package net.foreworld.yx.model;

import java.util.Date;

import io.netty.channel.Channel;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public class ChannelInfo {

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
