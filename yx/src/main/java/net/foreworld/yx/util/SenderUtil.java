package net.foreworld.yx.util;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.foreworld.yx.model.ChannelInfo;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public final class SenderUtil {

	private static final Logger logger = LoggerFactory.getLogger(SenderUtil.class);

	/**
	 *
	 * @param chan
	 * @return
	 */
	public static boolean canClose(Channel chan) {
		return (null != chan && chan.isOpen());
	}

	/**
	 * 
	 * @param chan
	 * @return
	 */
	public static boolean canSend(Channel chan) {
		return (null != chan && chan.isActive());
	}

	/**
	 *
	 * @param ctx
	 * @param data
	 */
	public static void send(ChannelHandlerContext ctx, Object data) {
		Channel chan = ctx.channel();

		if (!canSend(chan))
			return;

		if (chan.isWritable()) {
			ctx.writeAndFlush(data.toString()).addListener(f -> {
				if (!f.isSuccess())
					logger.error("data: {}", data);
			});

			return;
		}

		if (canClose(chan))
			ctx.close().addListener(f -> {
				SocketAddress addr = chan.remoteAddress();

				if (f.isSuccess()) {
					logger.info("sync ctx close: {}", addr);
					return;
				}

				logger.error("sync ctx close: {}", addr);
			});
	}

	/**
	 *
	 * @param chan
	 * @param data
	 */
	public static void send(Channel chan, Object data) {
		if (!canSend(chan))
			return;

		if (chan.isWritable()) {
			chan.writeAndFlush(data.toString()).addListener(f -> {
				if (!f.isSuccess())
					logger.error("data: {}", data);
			});

			return;
		}

		if (canClose(chan))
			chan.close().addListener(f -> {
				SocketAddress addr = chan.remoteAddress();

				if (f.isSuccess()) {
					logger.info("sync chan close: {}", addr);
					return;
				}

				logger.error("sync chan close: {}", addr);
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
