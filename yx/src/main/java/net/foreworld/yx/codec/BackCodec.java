package net.foreworld.yx.codec;

import java.net.SocketAddress;
import java.util.List;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.foreworld.yx.model.BackModel;
import net.foreworld.yx.util.SenderUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class BackCodec extends MessageToMessageDecoder<BinaryWebSocketFrame> {

	private static final Logger logger = LoggerFactory.getLogger(BackCodec.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
		ByteBuf _bf = msg.content();

		byte[] _bytes = new byte[_bf.capacity()];
		_bf.readBytes(_bytes);
		_bf.clear();

		JsonArray _ja = null;

		try {
			String _text = new String(_bytes, CharEncoding.UTF_8);
			_ja = new JsonParser().parse(_text).getAsJsonArray();
		} catch (Exception ex) {
			logout(ctx);
			return;
		}

		BackModel model = new BackModel();

		model.setReceiver(_ja.get(0).getAsString().trim());
		model.setMethod(_ja.get(1).getAsInt());
		model.setData(_ja.get(2).getAsString().trim());

		out.add(model);
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

		if (SenderUtil.canClose(chan))
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
