package org.distributed.gateway.bean.vo;

public class ResultVO<T> {

	/**
	 * 状态码
	 */
	private Integer code;

	/**
	 * 信息
	 */
	private String message;

	/**
	 * 返回数据
	 */
	private T data;

	public ResultVO() {
	}

	public ResultVO(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public ResultVO(int code, String message) {
		this.code = code;
		this.message = message;

	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
