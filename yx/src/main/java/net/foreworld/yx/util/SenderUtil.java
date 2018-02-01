package net.foreworld.yx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
	 * @param c
	 * @param data
	 * @throws InterruptedException
	 */
	public static void send(Channel c, Object data) throws InterruptedException {
		if (null == c || !c.isOpen() || !c.isActive())
			return;

		if (c.isWritable()) {
			c.writeAndFlush(data.toString()).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess())
						return;

					logger.error("data: {}", data);

					Throwable cause = future.cause();
					if (null != cause)
						throw new Exception(cause);
				}
			});

			return;
		}
		
		logger.error("sync");

//		c.writeAndFlush(data.toString()).sync().addListener(new ChannelFutureListener() {
//
//			@Override
//			public void operationComplete(ChannelFuture future) throws Exception {
//				if (future.isSuccess())
//					return;
//
//				logger.error("sync data: {}", data);
//
//				Throwable cause = future.cause();
//				if (null != cause)
//					throw new Exception(cause);
//			}
//		});
	}

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

			send(ci.getChannel(), data);
		}
	}

}
