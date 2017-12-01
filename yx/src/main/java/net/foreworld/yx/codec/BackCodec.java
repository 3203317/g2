package net.foreworld.yx.codec;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import java.net.SocketAddress;
import java.util.List;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.foreworld.yx.model.BackModel;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class BackCodec extends MessageToMessageCodec<BinaryWebSocketFrame, String> {

	private static final Logger logger = LoggerFactory.getLogger(BackCodec.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		out.add(new BinaryWebSocketFrame(wrappedBuffer(msg.getBytes())));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
		ByteBuf _bf = msg.content();

		byte[] _bytes = new byte[_bf.capacity()];
		_bf.readBytes(_bytes);

		String _text = new String(_bytes, CharEncoding.UTF_8);

		JsonArray _ja = null;

		try {
			_ja = new JsonParser().parse(_text).getAsJsonArray();
		} catch (Exception ex) {
			logout(ctx);
			return;
		}

		int _size = _ja.size();

		BackModel model = new BackModel();

		model.setMethod(_ja.get(0).getAsInt());

		if (1 < _size) {
			JsonElement _je_1 = _ja.get(1);

			if (!_je_1.isJsonNull()) {
				model.setReceiver(_je_1.getAsString());
			}
		}

		if (2 < _size) {
			JsonElement _je_2 = _ja.get(2);

			if (!_je_2.isJsonNull()) {
				model.setData(_je_2.getAsString());
			}
		}

		out.add(model);
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
					logger.info("back close: {}", addr);
					return;
				}

				logger.info("back close failure: {}", addr);
				ctx.close();
			}
		});
	}

}
