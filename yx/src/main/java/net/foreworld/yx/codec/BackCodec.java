package net.foreworld.yx.codec;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import java.net.SocketAddress;
import java.util.List;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import net.foreworld.yx.model.ProtocolModel;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class BackCodec extends MessageToMessageCodec<BinaryWebSocketFrame, String> {

	@Value("${msg.body.max:512}")
	private int msg_body_max;

	private static final Logger logger = LoggerFactory.getLogger(BackCodec.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		out.add(new BinaryWebSocketFrame(wrappedBuffer(msg.getBytes())));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
		ByteBuf _bf = msg.content();

		int _len = _bf.capacity();

		if (msg_body_max < _len || 1 > _len) {
			logout(ctx);
			return;
		}

		byte[] _bytes = new byte[_len];
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

		if (null == _ja || 3 > _size || 6 < _size) {
			logout(ctx);
			return;
		}

		ProtocolModel model = new ProtocolModel();

		try {
			model.setSignature(_ja.get(0).getAsString().trim());
			model.setMethod(_ja.get(1).getAsInt());
			model.setTimestamp(_ja.get(2).getAsLong());

			if (3 < _size) {
				JsonElement _je_3 = _ja.get(3);

				if (!_je_3.isJsonNull()) {
					model.setData(_je_3.getAsString().trim());
				}
			}

			if (4 < _size) {
				JsonElement _je_4 = _ja.get(4);

				if (!_je_4.isJsonNull()) {
					model.setSeqId(_je_4.getAsInt());
				}
			}

			if (5 < _size) {
				JsonElement _je_5 = _ja.get(5);

				if (!_je_5.isJsonNull()) {
					model.setBackId(_je_5.getAsString().trim());
				}
			}

		} catch (Exception ex) {
			logout(ctx);
			return;
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
					logger.info("ctx close: {}", addr);
					return;
				}

				logger.info("ctx close failure: {}", addr);
				ctx.close();
			}
		});
	}

	public static void main(String[] args) {
		String arrStr = "['a', 'b', 3, , ]";

		System.err.println(arrStr);

		JsonArray jo = new JsonParser().parse(arrStr).getAsJsonArray();

		System.err.println(jo);

		System.err.println(jo.size());

		System.err.println(jo.get(0).getAsString());

		System.err.println(jo.get(2).getAsString());

		System.err.println(jo.get(3).isJsonNull());

		System.err.println(jo.get(4).isJsonNull());
	}

}
