package net.foreworld.yx.handler;

import java.net.SocketAddress;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.BaseModel;
import net.foreworld.yx.util.SenderUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<BaseModel> {

	private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BaseModel msg) throws Exception {

		switch (msg.getMethod()) {
		case 6: {
			logger.info("method: {}", msg.getMethod());

			Channel chan = ctx.channel();
			if (null != chan && chan.isOpen() && chan.isActive())
				ctx.flush();

			return;
		}
		case 7: {
			SenderUtil.send(ctx, "[7," + new Date().getTime() + "]");
			return;
		}
		}

		ctx.fireChannelRead(msg);
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

	public static void main(String[] args) {
		System.err.println(new Date().getTime());
	}
}
