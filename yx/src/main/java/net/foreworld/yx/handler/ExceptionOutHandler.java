package net.foreworld.yx.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;

/**
 *
 * @author huangxin
 *
 */
@Component
@Sharable
public class ExceptionOutHandler extends ChannelOutboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionOutHandler.class);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("", cause);

		Channel chan = ctx.channel();
		if (null != chan && chan.isOpen() && chan.isActive())
			ctx.close();
	}
}
