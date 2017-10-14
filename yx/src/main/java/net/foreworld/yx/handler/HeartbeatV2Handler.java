package net.foreworld.yx.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.ProtocolModel;

/**
 *
 * @author huangxin
 *
 */
@Component
@Sharable
public class HeartbeatV2Handler extends SimpleChannelInboundHandler<ProtocolModel> {

	private static final Logger logger = LoggerFactory.getLogger(HeartbeatV2Handler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtocolModel msg) throws Exception {
		logger.info("method: {}", msg.getMethod());
		logger.info("method: {}", msg.getMethod());

		// ChannelUtil.getDefault().broadcast("asa");

		String str = "%5B%7B%2s啥宋德福2lastUpdateTime%22%3A%222011-10-28+9%3A39%3A41%22%2C%22smsList%22%3A%5B%7B%22liveState%22%3A%221啥h打饭";
		// System.out.println("原长度：" + str.length());

		ctx.writeAndFlush(str.getBytes());

		// if (666 == msg.getMethod()) {
		// ctx.flush();
		// return;
		// }

		// ctx.fireChannelRead(msg);
	}

}
