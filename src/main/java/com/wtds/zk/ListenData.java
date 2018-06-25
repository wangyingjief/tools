package com.wtds.zk;

public class ListenData {
	
	public Type type;
	
	private String path;
	
	private String data;
	
	public enum Type{
		add,update,delete
    }
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
