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

	private Type type;

	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

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

	public enum Type {
		BACK, USER
	}

}
