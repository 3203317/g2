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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.foreworld.yx.model.ChannelInfo;
import net.foreworld.yx.util.ChannelUtil;
import net.foreworld.yx.util.Constants;

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

			if (Constants.ALL.equals(_receiver)) {
				ChannelUtil.getDefault().broadcast(_data).addListener(f -> {
					if (!f.isSuccess()) {
						logger.error("data: {}", _data);
					}
				});
				return;
			}

			String[] recs = _receiver.split(",");

			for (int i = 0; i < recs.length; i++) {
				ChannelInfo ci = ChannelUtil.getDefault().getChannel(recs[i]);

				if (null == ci)
					continue;

				Channel c = ci.getChannel();

				if (null == c)
					continue;

				if (!c.isWritable()) {
					c.writeAndFlush(_data).sync().addListener(f -> {
						if (!f.isSuccess()) {
							logger.error("data: {}", _data);
						}
					});

					continue;
				}

				c.writeAndFlush(_data).addListener(f -> {
					if (!f.isSuccess()) {
						logger.error("data: {}", _data);
					}
				});
			}

		} catch (InterruptedException | JMSException e) {
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

		if (null == ci)
			return;

		Channel c = ci.getChannel();

		if (null == c)
			return;

		logout(c);
	}

	/**
	 *
	 * @param channel
	 */
	private void logout(Channel channel) {
		ChannelFuture future = channel.close();

		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				SocketAddress addr = channel.remoteAddress();

				if (future.isSuccess()) {
					logger.info("channel close: {}", addr);
					return;
				}

				logger.info("channel close failure: {}", addr);
				channel.close();
			}
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