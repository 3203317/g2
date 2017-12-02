package net.foreworld.yx.codec;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
public class OutDecoder extends MessageToMessageEncoder<String> {

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		out.add(new BinaryWebSocketFrame(wrappedBuffer(msg.getBytes())));
	}

}
