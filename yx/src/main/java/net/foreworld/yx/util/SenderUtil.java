package net.foreworld.yx.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;

import net.foreworld.yx.model.ChannelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public final class SenderUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(SenderUtil.class);

	/**
	 *
	 * @param ctx
	 * @param data
	 */
	public static void send(ChannelHandlerContext ctx, Object data) {
		Channel chan = ctx.channel();

		if (null == chan || !chan.isOpen() || !chan.isActive())
			return;

		if (chan.isWritable()) {
			ctx.writeAndFlush(data.toString()).addListener(f -> {
				if (!f.isSuccess())
					logger.error("data: {}", data);
			});

			return;
		}

		logger.error("sync data: {}", data);

		ctx.close().addListener(f -> {
			SocketAddress addr = chan.remoteAddress();

			if (f.isSuccess()) {
				logger.info("ctx close: {}", addr);
				return;
			}

			logger.error("ctx close: {}", addr);
		});
	}

	/**
	 *
	 * @param chan
	 * @param data
	 */
	public static void send(Channel chan, Object data) {
		if (null == chan || !chan.isOpen() || !chan.isActive())
			return;

		if (chan.isWritable()) {
			chan.writeAndFlush(data.toString()).addListener(f -> {
				if (!f.isSuccess())
					logger.error("data: {}", data);
			});

			return;
		}

		logger.error("sync data: {}", data);

		chan.close().addListener(f -> {
			SocketAddress addr = chan.remoteAddress();

			if (f.isSuccess()) {
				logger.info("ctx close: {}", addr);
				return;
			}

			logger.error("ctx close: {}", addr);
		});
	}

	/**
	 *
	 * @param receiver
	 * @param data
	 */
	public static void backSend(String receiver, String data) {
		if (Constants.ALL.equals(receiver)) {
			ChannelUtil.getDefault().broadcast(data).addListener(f -> {
				if (!f.isSuccess())
					logger.error("data: {}", data);
			});
			return;
		}

		String[] recs = receiver.split(",");

		for (int i = 0; i < recs.length; i++) {
			ChannelInfo ci = ChannelUtil.getDefault().getChannel(recs[i]);

			if (null != ci)
				send(ci.getChannel(), data);
		}
	}

}
