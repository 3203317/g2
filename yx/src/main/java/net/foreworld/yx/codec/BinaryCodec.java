package net.foreworld.yx.codec;

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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.foreworld.yx.model.ProtocolModel;
import net.foreworld.yx.util.SenderUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class BinaryCodec extends MessageToMessageDecoder<BinaryWebSocketFrame> {

	@Value("${msg.body.max:512}")
	private int msg_body_max;

	private static final Logger logger = LoggerFactory.getLogger(BinaryCodec.class);

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
		_bf.clear();

		JsonArray _ja = null;

		try {
			String _text = new String(_bytes, CharEncoding.UTF_8);
			_ja = new JsonParser().parse(_text).getAsJsonArray();
		} catch (Exception ex) {
			logout(ctx);
			return;
		}

		int _size = _ja.size();

		if (3 > _size || 6 < _size) {
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
