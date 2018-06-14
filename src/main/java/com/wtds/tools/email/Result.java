package com.wtds.tools.email;

public class Result {

	public Result(SendType type, String msg) {
		this.type = type;
		this.msg = msg;
	}

	private SendType type;

	private String msg;

	public SendType getType() {
		return type;
	}

	public void setType(SendType type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
