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
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class HttpSafeHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HttpSafeHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof BinaryWebSocketFrame || msg instanceof FullHttpRequest) {
			ctx.fireChannelRead(msg);
			return;
		}

		logout(ctx);
	}

	private void logout(ChannelHandlerContext ctx) {
		ChannelFuture future = ctx.close();

		future.addListener(new ChannelFutureListener() {

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
