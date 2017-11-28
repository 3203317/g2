package net.foreworld.yx.handler;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class LoginTimeoutHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(LoginTimeoutHandler.class);

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
		removeChannel(addr.getAddress().getHostAddress());
		super.channelUnregistered(ctx);
	}

	private void removeChannel(String ip) {
		logger.info("channel close: {}", ip);
	}
}
