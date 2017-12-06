package net.foreworld.yx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
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
	 * @param receiver
	 * @param data
	 * @throws InterruptedException
	 */
	public static void backSend(String receiver, String data) throws InterruptedException {
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

			if (c.isWritable()) {
				c.writeAndFlush(data).addListener(f -> {
					if (!f.isSuccess()) {
						logger.error("data: {}", data);
					}
				});

				continue;
			}

			c.writeAndFlush(data).sync().addListener(f -> {
				if (!f.isSuccess()) {
					logger.error("data: {}", data);
				}
			});

		}
	}

}
