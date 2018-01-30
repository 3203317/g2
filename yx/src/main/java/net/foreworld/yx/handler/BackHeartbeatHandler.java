package net.foreworld.yx.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.BackModel;
import net.foreworld.yx.util.SenderUtil;

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
public class BackHeartbeatHandler extends
		SimpleChannelInboundHandler<BackModel> {

	private static final Logger logger = LoggerFactory
			.getLogger(BackHeartbeatHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BackModel msg)
			throws Exception {
		switch (msg.getMethod()) {
		case 2: {
			Channel c = ctx.channel();
			SenderUtil.send(c, "[2,\"" + c.id().asLongText() + "\"]");
			return;
		}
		}

		ctx.fireChannelRead(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error("", cause);
		ctx.close();
	}

}
