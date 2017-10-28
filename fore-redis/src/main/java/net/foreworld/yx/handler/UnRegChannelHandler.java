package net.foreworld.yx.handler;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.foreworld.yx.util.ChannelUtil;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
@PropertySource("classpath:activemq.properties")
@Component
@Sharable
public class UnRegChannelHandler extends ChannelInboundHandlerAdapter {

	@Value("${server.id}")
	private String server_id;

	@Value("${queue.channel.close}")
	private String queue_channel_close;

	@Resource(name = "jmsMessagingTemplate")
	private JmsMessagingTemplate jmsMessagingTemplate;

	private static final Logger logger = LoggerFactory.getLogger(UnRegChannelHandler.class);

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		removeChannel(ctx.channel().id().asLongText());
		super.channelUnregistered(ctx);
	}

	private void removeChannel(String channel_id) {
		ChannelUtil.getDefault().removeChannel(channel_id);

		jmsMessagingTemplate.convertAndSend(queue_channel_close, server_id + "::" + channel_id);
		logger.info("channel close: {}:{}", server_id, channel_id);
	}
}
