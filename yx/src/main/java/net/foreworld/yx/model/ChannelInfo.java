package net.foreworld.yx.model;

import io.netty.channel.Channel;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public class ChannelInfo {

	private Channel channel;

	private Long loginTime;

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

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
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
