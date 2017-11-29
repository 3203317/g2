package net.foreworld.yx.handler;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 *
 * @author huangxin
 *
 */
@Component
@Sharable
public class ProtocolSafeHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ProtocolSafeHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		// if (msg instanceof PingWebSocketFrame) {
		// logger.info("client ping: {}", new Date());
		// ctx.channel().write(new PongWebSocketFrame(((WebSocketFrame)
		// msg).content().retain()));
		// return;
		// }

		if (msg instanceof BinaryWebSocketFrame) {
			ctx.fireChannelRead(msg);
			return;
		}

		// logger.error("protocol error: {}", msg);

		logout(ctx);
	}

	private void logout(ChannelHandlerContext ctx) {

		ctx.close().addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				SocketAddress addr = ctx.channel().remoteAddress();

				if (future.isSuccess()) {
					logger.info("ctx close: {}", addr);
					return;
				}

				logger.info("ctx close failure: {}", addr);
				ctx.close();
			}
		});
	}
}
