package net.foreworld.yx.handler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.foreworld.util.RedisUtil;
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
public class BlacklistHandler extends ChannelInboundHandlerAdapter {

	@Value("${db.redis.database:1}")
	private String db_redis_database;

	@Value("${sha.blacklist:}")
	private String sha_blacklist;

	private static final Logger logger = LoggerFactory.getLogger(BlacklistHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
		String incoming = addr.getAddress().getHostAddress();

		if (!check(incoming)) {
			logout(ctx);
			return;
		}

		ctx.pipeline().remove(this);

		logger.info("client ip: {}", incoming);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("", cause);
		logout(ctx);
	}

	/**
	 * 
	 * @param ip
	 * @return
	 */
	private boolean check(String ip) {
		List<String> s = new ArrayList<String>();
		s.add(db_redis_database);
		s.add(ip);

		List<String> b = new ArrayList<String>();

		Jedis j = RedisUtil.getDefault().getJedis();

		if (null == j)
			return false;

		Object o = j.evalsha(sha_blacklist, s, b);
		j.close();

		return Constants.OK.equals(o.toString());
	}

	/**
	 * 
	 * @param ctx
	 */
	private void logout(ChannelHandlerContext ctx) {
		Channel chan = ctx.channel();

		if (null == chan || !chan.isOpen() || !chan.isActive())
			return;

		ctx.close().addListener(f -> {
			SocketAddress addr = chan.remoteAddress();

			if (f.isSuccess()) {
				logger.info("ctx close: {}", addr);
				return;
			}

			logger.error("ctx close: {}", addr);
		});
	}

}
