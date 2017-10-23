package net.foreworld.yx.handler;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.util.StringUtil;
import net.foreworld.yx.model.ProtocolModel;
import net.foreworld.yx.util.ChannelUtil;
import net.foreworld.yx.util.Constants;
import net.foreworld.yx.util.RedisUtil;
import redis.clients.jedis.Jedis;

/**
 *
 * @author huangxin
 *
 */
@PropertySource("classpath:redis.properties")
@Component
@Sharable
public class LoginHandler extends SimpleChannelInboundHandler<ProtocolModel> {

	@Value("${sha.token}")
	private String sha_token;

	@Value("${sha.token.expire}")
	private int sha_token_expire;

	@Value("${server.id}")
	private String server_id;

	@Value("${queue.channel.open}")
	private String queue_channel_open;

	@Value("${queue.channel.close.force}")
	private String queue_channel_close_force;

	@Value("${db.redis.database}")
	private String db_redis_database;

	@Resource(name = "jmsMessagingTemplate")
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Resource(name = "unRegChannelHandler")
	private UnRegChannelHandler unRegChannelHandler;

	private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, final ProtocolModel msg) throws Exception {
		logger.info("{}:{}", msg.getMethod(), msg.getTimestamp());

		JsonObject _jo = null;

		try {
			_jo = new JsonParser().parse(msg.getData()).getAsJsonObject();
		} catch (Exception ex) {
			logout(ctx);
			return;
		}

		JsonElement _joo = _jo.get("code");

		if (null == _joo) {
			logout(ctx);
			return;
		}

		String code = _joo.getAsString();

		final Channel channel = ctx.channel();

		final String channel_id = channel.id().asLongText();

		if (!verify(code, channel_id)) {
			logout(ctx);
			return;
		}

		ctx.pipeline().replace(this, "unReg", unRegChannelHandler);

		ChannelUtil.getDefault().putChannel(channel_id, channel);

		jmsMessagingTemplate.convertAndSend(queue_channel_open, server_id + "::" + channel_id);
		logger.info("channel open: {}:{}", server_id, channel_id);

		ctx.flush();
	}

	/**
	 * 
	 * @param ctx
	 */
	private void logout(ChannelHandlerContext ctx) {
		ChannelFuture future = ctx.close();

		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				SocketAddress addr = ctx.channel().remoteAddress();

				if (future.isSuccess()) {
					logger.info("ctx close: {}", addr);
					return;
				}

				logger.info("ctx close failure: {}", addr);
				ctx.close();
			}
		});
	}

	/**
	 *
	 * @param code
	 * @param channel_id
	 * @return
	 */
	private boolean verify(String code, String channel_id) {

		code = StringUtil.isEmpty(code);

		if (null == code) {
			return false;
		}

		List<String> s = new ArrayList<String>();
		s.add(db_redis_database);
		s.add(server_id);
		s.add(channel_id);
		s.add(code);

		List<String> b = new ArrayList<String>();
		b.add(String.valueOf(sha_token_expire));
		b.add(String.valueOf(System.currentTimeMillis()));

		Jedis j = RedisUtil.getDefault().getJedis();

		if (null == j)
			return false;

		Object o = j.evalsha(sha_token, s, b);
		j.close();

		String str = o.toString();

		switch (str) {
		case Constants.INVALID_CODE:
			return false;
		case Constants.OK:
			return true;
		}

		String[] text = str.split("::");

		jmsMessagingTemplate.convertAndSend(queue_channel_close_force + "." + text[0], text[1]);

		return true;
	}

	public static void main(String[] args) {
		String str = "{code:1234}";

		JsonObject jo = new JsonParser().parse(str).getAsJsonObject();

		System.err.println(jo);
		System.err.println(jo.get("code").getAsString());
	}
}
