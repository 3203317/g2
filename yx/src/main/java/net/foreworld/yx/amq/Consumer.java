package net.foreworld.yx.amq;

import java.net.SocketAddress;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

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
				ChannelUtil.getDefault().broadcast(_data);
				return;
			}

			Channel c = ChannelUtil.getDefault().getChannel(_receiver);

			if (null != c)
				c.writeAndFlush(_data);

		} catch (JMSException e) {
			logger.error("", e);
		}
	}

	@JmsListener(destination = "${queue.channel.close.force}.${server.id}")
	public void channel_close_force(BytesMessage msg) {

		try {
			byte[] data = new byte[(int) msg.getBodyLength()];
			msg.readBytes(data);

			String s = new String(data, Charsets.UTF_8);

			Channel c = ChannelUtil.getDefault().getChannel(s);

			if (null != c)
				logout(c);

		} catch (JMSException e) {
			logger.error("", e);
		}
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

}