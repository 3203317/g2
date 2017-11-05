package net.foreworld.yx.model;

import io.netty.channel.Channel;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = 6722528261341431862L;

	private Channel channel;

	private Date loginTime;

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
