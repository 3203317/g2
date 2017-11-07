package net.foreworld.yx.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@PropertySource("classpath:activemq.properties")
@Component
@Sharable
public class LoginTimeoutHandler extends ChannelInboundHandlerAdapter {

	@Resource(name = "jmsMessagingTemplate")
	private JmsMessagingTemplate jmsMessagingTemplate;

	private static final Logger logger = LoggerFactory
			.getLogger(LoginTimeoutHandler.class);

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		removeChannel(addr.getAddress().getHostAddress());
		super.channelUnregistered(ctx);
	}

	private void removeChannel(String ip) {
		logger.info("channel close: {}", ip);
	}
}
