package net.foreworld.yx.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.ProtocolModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class HeartbeatHandler extends
		SimpleChannelInboundHandler<ProtocolModel> {

	private static final Logger logger = LoggerFactory
			.getLogger(HeartbeatHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtocolModel msg)
			throws Exception {

		switch (msg.getMethod()) {
		case 2: {
			sendSelfChan(ctx);
			return;
		}
		case 6: {
			logger.info("method: {}", msg.getMethod());
			ctx.flush();
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
	 */
	private void sendSelfChan(ChannelHandlerContext ctx) {
		String chan_id = ctx.channel().id().asLongText();

		ctx.writeAndFlush(chan_id).addListener(f -> {
			if (!f.isSuccess()) {
			}
		});
	}

}
