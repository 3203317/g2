package net.foreworld.yx.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

import net.foreworld.yx.model.BaseModel;
import net.foreworld.yx.util.SenderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<BaseModel> {

	private static final Logger logger = LoggerFactory
			.getLogger(HeartbeatHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BaseModel msg)
			throws Exception {

		switch (msg.getMethod()) {
		case 6: {
			logger.info("method: {}", msg.getMethod());
			ctx.flush();
			return;
		}
		case 7: {
			SenderUtil.send(ctx.channel(), new Date().getTime());
			return;
		}
		}

		ctx.fireChannelRead(msg);
	}

	public static void main(String[] args) {
		System.err.println(new Date().getTime());
	}
}
