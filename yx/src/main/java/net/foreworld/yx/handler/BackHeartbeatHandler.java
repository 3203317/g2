package net.foreworld.yx.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.BackModel;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class BackHeartbeatHandler extends SimpleChannelInboundHandler<BackModel> {

	private static final Logger logger = LoggerFactory.getLogger(BackHeartbeatHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BackModel msg) throws Exception {
		switch (msg.getMethod()) {
		case 2: {
			sendSelfChan(ctx);
			return;
		}
		}

		ctx.fireChannelRead(msg);
	}

	/**
	 *
	 * 获取当前的通道号
	 *
	 * @param ctx
	 * @throws InterruptedException
	 */
	private void sendSelfChan(ChannelHandlerContext ctx) throws InterruptedException {
		Channel c = ctx.channel();

		String chan_id = c.id().asLongText();

		if (c.isWritable()) {
			c.writeAndFlush(chan_id).addListener(f -> {
				if (!f.isSuccess()) {
					logger.error("data: {}", chan_id);
				}
			});
			return;
		}

		c.writeAndFlush(chan_id).sync().addListener(f -> {
			if (!f.isSuccess()) {
				logger.error("data: {}", chan_id);
			}
		});
	}

}
