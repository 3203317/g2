package net.foreworld.yx.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.BaseModel;

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
		if (6 == msg.getMethod()) {
			logger.info("method: {}", msg.getMethod());
			ctx.flush();
			return;
		}

		ctx.fireChannelRead(msg);
	}

}
