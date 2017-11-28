package net.foreworld.yx.model;

import java.io.Serializable;
import java.util.Date;

import io.netty.channel.Channel;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = 6722528261341431862L;

	private Channel channel;

	private Date loginTime;

	private Type type;

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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public enum Type {
		BACK("back"), USER("user");

		private String value = "user";

		private Type(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
}
