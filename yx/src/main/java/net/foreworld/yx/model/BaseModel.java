package net.foreworld.yx.model;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public abstract class BaseModel {

	private Integer method;
	private String data;

	public Integer getMethod() {
		return method;
	}

	public void setMethod(Integer method) {
		this.method = method;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
