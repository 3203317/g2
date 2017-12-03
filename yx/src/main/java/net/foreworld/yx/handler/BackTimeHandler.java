package net.foreworld.yx.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.BackModel;
import net.foreworld.yx.util.SendUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author huangxin
 *
 */
@Component
@Sharable
public class BackTimeHandler extends SimpleChannelInboundHandler<BackModel> {

	private static final Logger logger = LoggerFactory
			.getLogger(BackTimeHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BackModel msg)
			throws Exception {
		logger.info("{}", msg.getReceiver());

		SendUtil.backSend(msg.getReceiver(), msg.getData());
		ctx.flush();
	}

}
