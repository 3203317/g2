package net.foreworld.yx.model;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public class MethodInfo {

	private Type type;

	private Integer name;

	private String serverId;
	private String channelId;

	private String backId;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Integer getName() {
		return name;
	}

	public void setName(Integer name) {
		this.name = name;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getBackId() {
		return backId;
	}

	public void setBackId(String backId) {
		this.backId = backId;
	}

	public enum Type {
		MQ, CHAN
	}
}
