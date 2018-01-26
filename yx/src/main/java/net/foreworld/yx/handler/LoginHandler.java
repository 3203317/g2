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
import net.foreworld.yx.codec.BackCodec;
import net.foreworld.yx.codec.BinaryCodec;
import net.foreworld.yx.model.ChannelInfo;
import net.foreworld.yx.model.ChannelInfo.Type;
import net.foreworld.yx.util.ChannelUtil;
import net.foreworld.yx.util.Constants;
import net.foreworld.yx.util.SenderUtil;
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

	@Resource(name = "backCodec")
	private BackCodec backCodec;

	@Resource(name = "timeHandler")
	private TimeHandler timeHandler;

	@Resource(name = "backTimeHandler")
	private BackTimeHandler backTimeHandler;

	@Resource(name = "heartbeatHandler")
	private HeartbeatHandler heartbeatHandler;

	@Resource(name = "backHeartbeatHandler")
	private BackHeartbeatHandler backHeartbeatHandler;

	private String first = "[1]";

	private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, final String token) throws Exception {
		/*
		 * logger.info("{}:{}", msg.getMethod(), msg.getTimestamp());
		 *
		 * JsonObject _jo = null;
		 *
		 * try { _jo = new JsonParser().parse(msg.getData()).getAsJsonObject();
		 * } catch (Exception ex) { logout(ctx); return; }
		 *
		 * JsonElement _joo = _jo.get("code");
		 *
		 * if (null == _joo) { logout(ctx); return; }
		 *
		 * String code = _joo.getAsString();
		 */

		final Channel channel = ctx.channel();

		final String chan_id = channel.id().asLongText();

		String str = verify(token, chan_id);

		if (null == str) {
			logout(ctx);
			return;
		}

		String[] text = str.split(",");

		Type chan_type = Type.valueOf(text[0]);

		ChannelPipeline pipe = ctx.pipeline();

		pipe.remove(this);

		pipe.replace("loginTimeout", "unReg", unRegChannelHandler);

		pipe.replace("loginCodec", "binaryCodec", Type.USER == chan_type ? binaryCodec : backCodec);

		pipe.replace("defaIdleState", "newIdleState", new IdleStateHandler(Type.USER == chan_type ? readerIdleTime : 60,
				writerIdleTime, allIdleTime, TimeUnit.SECONDS));

		pipe.replace("httpSafe", "protocolSafe", protocolSafeHandler);

		pipe.addLast(heartbeatHandler);

		if (Type.BACK == chan_type)
			pipe.addLast(backHeartbeatHandler);

		pipe.addLast(Type.USER == chan_type ? timeHandler : backTimeHandler);

		ChannelInfo ci = new ChannelInfo();
		ci.setLoginTime(new Date().getTime());
		ci.setChannel(channel);
		ci.setType(chan_type);
		ci.setUserId(text[1]);

		ChannelUtil.getDefault().putChannel(chan_id, ci);

		// 登陆成功，发送成功标记
		SenderUtil.send(channel, first);

		if (Type.USER == chan_type)
			jmsMessagingTemplate.convertAndSend(queue_channel_open,
					server_id + "::" + chan_id + "::" + ci.getUserId() + "::" + ci.getLoginTime());

		logger.info("channel open: {}:{}", server_id, chan_id);

		ctx.flush();
	}

	/**
	 *
	 * @param code
	 * @param chan_id
	 * @return
	 */
	private String verify(String code, String chan_id) {
		List<String> s = new ArrayList<String>();
		s.add(db_redis_database);
		s.add(server_id);
		s.add(chan_id);
		s.add(code);

		List<String> b = new ArrayList<String>();
		b.add(String.valueOf(sha_token_expire));
		b.add(String.valueOf(System.currentTimeMillis()));

		Jedis j = RedisUtil.getDefault().getJedis();

		if (null == j)
			return null;

		Object o = j.evalsha(sha_token, s, b);
		j.close();

		if (null == o)
			return null;

		String str = o.toString();

		if (Constants.INVALID_CODE.equals(str))
			return null;

		String[] text = str.split(":");

		if (2 < text.length)
			jmsMessagingTemplate.convertAndSend(queue_channel_close_force + "." + text[1], text[2]);

		return text[0];
	}

	/**
	 *
	 * @param ctx
	 */
	private void logout(ChannelHandlerContext ctx) {
		ctx.close().addListener(new ChannelFutureListener() {

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

	public static void main(String[] args) {
		String str = "{code:1234}";

		JsonObject jo = new JsonParser().parse(str).getAsJsonObject();

		System.err.println(jo);
		System.err.println(jo.get("code").getAsString());

		String s = "a:b:c";

		System.err.println(s.split(":").length);

		System.err.println(Type.valueOf("BACK"));
	}
}
