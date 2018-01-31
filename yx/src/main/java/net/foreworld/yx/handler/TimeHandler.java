package net.foreworld.yx.handler;

import java.net.SocketAddress;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.util.StringUtil;
import net.foreworld.yx.model.ChannelInfo;
import net.foreworld.yx.model.ProtocolModel;
import net.foreworld.yx.util.ChannelUtil;
import net.foreworld.yx.util.Constants;
import net.foreworld.yx.util.MethodUtil;
import net.foreworld.yx.util.SenderUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
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

	@Resource(name = "gson")
	private Gson gson;

	private static final Logger logger = LoggerFactory.getLogger(TimeHandler.class);

	/**
	 *
	 * 方法:后置机 MQ
	 *
	 * :方法:后置机 MQ
	 *
	 * 方法:后置机:前置机 通道号
	 *
	 * :方法:后置机:前置机 通道号
	 *
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtocolModel msg) throws Exception {
		logger.info("{}:{}", msg.getMethod(), msg.getTimestamp());

		String backId = StringUtil.isEmpty(msg.getBackId());

		String _method = msg.getMethod().toString();

		if (null != backId) {
			_method += ":" + backId;
		}

		String chan_id = MethodUtil.getDefault().get(_method);

		if (null == chan_id) {
			logout(ctx);
			return;
		}

		msg.setServerId(server_id);
		msg.setChannelId(ctx.channel().id().asLongText());
		msg.setUserId(ChannelUtil.getDefault().getChannel(msg.getChannelId()).getUserId());

		String _data = gson.toJson(msg);

		if (Constants.MQ.equals(chan_id)) {
			jmsMessagingTemplate.convertAndSend(Constants.QUEUE_PREFIX + _method, _data);
			ctx.flush();
			return;
		}

		ChannelInfo ci = ChannelUtil.getDefault().getChannel(chan_id);

		if (null == ci) {
			if (MethodUtil.getDefault().contains(_method)) {
				jmsMessagingTemplate.convertAndSend(Constants.QUEUE_PREFIX + _method, _data);
				ctx.flush();
				return;
			}

			logout(ctx);
			return;
		}

		Channel c = ci.getChannel();

		if (null == c) {
			if (MethodUtil.getDefault().contains(_method)) {
				jmsMessagingTemplate.convertAndSend(Constants.QUEUE_PREFIX + _method, _data);
				ctx.flush();
				return;
			}

			logout(ctx);
			return;
		}

		SenderUtil.send(c, _data);
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("", cause);
		ctx.close();
	}

	public static void main(String[] args) {
		System.err.println(",111,222,".indexOf(",333,"));
		System.err.println(":a:bc".split(":").length);
		System.err.println(-1 < "MQ".indexOf("MQ"));

		Random random = new Random();
		int s = random.nextInt(3) + 1;
		System.out.println(":" + s);

		System.err.println("111,MQ".split(",")[0]);
	}

}
