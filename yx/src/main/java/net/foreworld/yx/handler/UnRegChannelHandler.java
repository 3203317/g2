package net.foreworld.yx.handler;

import java.net.SocketAddress;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.foreworld.yx.model.ChannelInfo;
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

	private void removeChannel(String chan_id) {
		ChannelInfo ci = ChannelUtil.getDefault().getChannel(chan_id);

		if (null == ci) {
			jmsMessagingTemplate.convertAndSend(queue_channel_close,
					server_id + "::" + chan_id + "::" + new Date().getTime());
			logger.info("channel close: {}:{}", server_id, chan_id);
			return;
		}

		jmsMessagingTemplate.convertAndSend(queue_channel_close,
				server_id + "::" + chan_id + "::" + ci.getUserId() + "::" + new Date().getTime());
		logger.info("channel close: {}:{}:{}", server_id, chan_id, ci.getUserId());
		ChannelUtil.getDefault().removeChannel(ci.getChannel(), chan_id);
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
