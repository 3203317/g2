package net.foreworld.yx.handler;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.foreworld.yx.util.SenderUtil;

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

		logout(ctx);
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

		if (SenderUtil.canClose(chan))
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
