package net.foreworld.yx.handler;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateHandler;
import net.foreworld.util.RedisUtil;
import net.foreworld.yx.codec.BinaryCodec;
import net.foreworld.yx.model.ChannelInfo;
import net.foreworld.yx.util.ChannelUtil;
import net.foreworld.yx.util.Constants;
import redis.clients.jedis.Jedis;

/**
 *
 * @author huangxin
 *
 */
@PropertySource("classpath:redis.properties")
@Component
@Sharable
public class LoginHandler extends SimpleChannelInboundHandler<String> {

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

	@Resource(name = "protocolSafeHandler")
	private ProtocolSafeHandler protocolSafeHandler;

	@Value("${server.idle.readerIdleTime:3}")
	private int readerIdleTime;

	@Value("${server.idle.writerIdleTime:7}")
	private int writerIdleTime;

	@Value("${server.idle.allIdleTime:10}")
	private int allIdleTime;

	@Resource(name = "binaryCodec")
	private BinaryCodec binaryCodec;

	private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, final String token) throws Exception {
		// logger.info("{}:{}", msg.getMethod(), msg.getTimestamp());
		//
		// JsonObject _jo = null;
		//
		// try {
		// _jo = new JsonParser().parse(msg.getData()).getAsJsonObject();
		// } catch (Exception ex) {
		// logout(ctx);
		// return;
		// }
		//
		// JsonElement _joo = _jo.get("code");
		//
		// if (null == _joo) {
		// logout(ctx);
		// return;
		// }
		//
		// String code = _joo.getAsString();

		final Channel channel = ctx.channel();

		final String channel_id = channel.id().asLongText();

		if (!verify(token, channel_id)) {
			logout(ctx);
			return;
		}

		ChannelPipeline pipe = ctx.pipeline();

		pipe.remove("loginTimeout");

		pipe.replace("loginCodec", "binaryCodec", binaryCodec);

		pipe.replace("defaIdleState", "newIdleState",
				new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));
		pipe.replace(this, "unReg", unRegChannelHandler);
		pipe.replace("httpSafe", "protocolSafe", protocolSafeHandler);

		ChannelInfo ci = new ChannelInfo();
		ci.setLoginTime(new Date());
		ci.setChannel(channel);

		ChannelUtil.getDefault().putChannel(channel_id, ci);

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
