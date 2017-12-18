package net.foreworld.yx.handler;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.foreworld.yx.model.BackModel;
import net.foreworld.yx.util.SenderUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class BackHeartbeatHandler extends SimpleChannelInboundHandler<BackModel> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BackModel msg) throws Exception {
		switch (msg.getMethod()) {
		case 2: {
			Channel c = ctx.channel();
			SenderUtil.send(c, "[2,'" + c.id().asLongText() + "']");
			return;
		}
		}

		ctx.fireChannelRead(msg);
	}

}
