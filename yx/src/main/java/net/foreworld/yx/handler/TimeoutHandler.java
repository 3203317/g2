package net.foreworld.yx.handler;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class TimeoutHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(TimeoutHandler.class);

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
		if (!(evt instanceof IdleStateEvent))
			return;

		IdleStateEvent event = (IdleStateEvent) evt;

		switch (event.state()) {
		case READER_IDLE:
			logout(ctx);
			break;
		case WRITER_IDLE:
		case ALL_IDLE:
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("", cause);
		logout(ctx);
	}

	/**
	 * 
	 * @param ctx
	 */
	private void logout(ChannelHandlerContext ctx) {
		Channel chan = ctx.channel();

		if (null == chan || !chan.isOpen() || !chan.isActive())
			return;

		ctx.close().addListener(f -> {
			SocketAddress addr = chan.remoteAddress();

			if (f.isSuccess()) {
				logger.info("ctx close: {}", addr);
				return;
			}

			logger.error("ctx close: {}", addr);
		});
	}

}
