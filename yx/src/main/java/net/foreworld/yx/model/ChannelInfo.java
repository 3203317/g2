package net.foreworld.yx.model;

import java.io.Serializable;

import io.netty.channel.Channel;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = -3203250186246477775L;

	private String backendId;

	private Channel channel;

	public String getBackendId() {
		return backendId;
	}

	public void setBackendId(String backendId) {
		this.backendId = backendId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
