package net.foreworld.yx.codec;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.util.List;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.foreworld.util.StringUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
@Sharable
public class LoginCodec extends MessageToMessageDecoder<BinaryWebSocketFrame> {

	private static final Logger logger = LoggerFactory.getLogger(LoginCodec.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) {
		ByteBuf _bf = msg.content();

		int _len = _bf.capacity();

		if (32 != _len) {
			logout(ctx);
			return;
		}

		byte[] _bytes = new byte[_len];
		_bf.readBytes(_bytes);
		_bf.clear();

		String _text = null;

		try {
			_text = new String(_bytes, CharEncoding.UTF_8);
		} catch (UnsupportedEncodingException e) {
			logout(ctx);
			return;
		}

		_text = StringUtil.isEmpty(_text);

		if (null == _text || 32 != _text.length()) {
			logout(ctx);
			return;
		}

		out.add(_text);
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

		if (null == chan || !chan.isOpen() || !chan.isActive())
			return;

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
