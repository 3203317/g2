package net.foreworld.yx.util;

import io.netty.channel.Channel;
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
	 * @param c
	 * @param data
	 * @throws InterruptedException
	 */
	public static void send(Channel c, Object data) throws InterruptedException {
		if (c.isWritable()) {
			c.writeAndFlush(data.toString()).addListener(f -> {
				if (!f.isSuccess()) {
					logger.error("data: {}", data);
				}
			});

			return;
		}

		c.writeAndFlush(data.toString()).sync().addListener(f -> {
			if (!f.isSuccess()) {
				logger.error("sync data: {}", data);
			}
		});
	}

	/**
	 *
	 * @param receiver
	 * @param data
	 * @throws InterruptedException
	 */
	public static void backSend(String receiver, String data)
			throws InterruptedException {
		if (Constants.ALL.equals(receiver)) {
			ChannelUtil.getDefault().broadcast(data).addListener(f -> {
				if (!f.isSuccess()) {
					logger.error("data: {}", data);
				}
			});
			return;
		}

		String[] recs = receiver.split(",");

		for (int i = 0; i < recs.length; i++) {
			ChannelInfo ci = ChannelUtil.getDefault().getChannel(recs[i]);

			if (null == ci)
				continue;

			Channel c = ci.getChannel();

			if (null == c)
				continue;

			send(c, data);
		}
	}

}
