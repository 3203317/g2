package net.foreworld.yx.handler;

import java.net.SocketAddress;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.util.StringUtil;
import net.foreworld.yx.model.ProtocolModel;
import net.foreworld.yx.util.Constants;

/**
 *
 * @author huangxin
 *
 */
@PropertySource("classpath:activemq.properties")
@Component
@Sharable
public class TimeHandler extends SimpleChannelInboundHandler<ProtocolModel> {

	@Resource(name = "jmsMessagingTemplate")
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Value("${server.id}")
	private String server_id;

	@Value("${allow.queue:}")
	private String allow_queue;

	@Resource(name = "gson")
	private Gson gson;

	private static final Logger logger = LoggerFactory.getLogger(TimeHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtocolModel msg) throws Exception {
		logger.info("{}:{}", msg.getMethod(), msg.getTimestamp());

		String destName = msg.getMethod().toString();

		String sb = StringUtil.isEmpty(msg.getBackId());

		if (null != sb) {
			destName += '.' + sb;
		}

		if (0 > allow_queue.indexOf("," + destName + ",")) {
			logout(ctx);
			return;
		}

		msg.setServerId(server_id);
		msg.setChannelId(ctx.channel().id().asLongText());

		jmsMessagingTemplate.convertAndSend(Constants.QUEUE_PREFIX + destName, gson.toJson(msg));
		ctx.flush();
	}

	/**
	 *
	 * @param ctx
	 */
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

	public static void main(String[] args) {
		System.err.println(",111,222,".indexOf(",333,"));

		System.err.println(":a:bc".split(":").length);
	}

}
