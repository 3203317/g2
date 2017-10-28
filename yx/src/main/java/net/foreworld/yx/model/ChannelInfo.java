package net.foreworld.yx.model;

import java.io.Serializable;

import io.netty.channel.Channel;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = 6722528261341431862L;

	private Channel channel;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
