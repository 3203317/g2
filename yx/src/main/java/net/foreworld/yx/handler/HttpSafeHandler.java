package net.foreworld.yx.handler;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.foreworld.yx.util.SenderUtil;

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
