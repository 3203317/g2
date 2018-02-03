package net.foreworld.yx.amq;

import java.net.SocketAddress;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import io.netty.channel.Channel;
import net.foreworld.yx.model.ChannelInfo;
import net.foreworld.yx.util.ChannelUtil;
import net.foreworld.yx.util.SenderUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@PropertySource("classpath:activemq.properties")
@Component
public class Consumer {

	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

	@JmsListener(destination = "${queue.back.send}.${server.id}")
	public void back_send(BytesMessage msg) {
		try {
			byte[] data = new byte[(int) msg.getBodyLength()];
			msg.readBytes(data);

			String s = new String(data, Charsets.UTF_8);

			JsonArray _ja = new JsonParser().parse(s).getAsJsonArray();

			String _receiver = _ja.get(0).getAsString();

			String _data = _ja.get(1).getAsString();

			// 开始发送
			SenderUtil.backSend(_receiver, _data);

		} catch (JMSException e) {
			logger.error("", e);
		}
	}

	@JmsListener(destination = "${queue.channel.close.force}.${server.id}")
	public void channel_close_force(TextMessage msg) {
		String id = null;

		try {
			id = msg.getText();
		} catch (JMSException e) {
			logger.error("", e);
			return;
		}

		ChannelInfo ci = ChannelUtil.getDefault().getChannel(id);

		if (null != ci)
			logout(ci.getChannel());
	}

	/**
	 * 
	 * @param chan
	 */
	private void logout(Channel chan) {
		if (SenderUtil.canClose(chan))
			chan.close().addListener(f -> {
				SocketAddress addr = chan.remoteAddress();

				if (f.isSuccess()) {
					logger.info("chan close: {}", addr);
					return;
				}

				logger.error("chan close: {}", addr);
			});
	}

	public static void main(String[] args) {
		String str = "ab";

		String[] strs = str.split(",");

		for (int i = 0; i < strs.length; i++) {
			System.err.println(strs[i]);
		}
	}

}