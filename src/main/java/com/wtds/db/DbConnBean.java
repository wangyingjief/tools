package com.wtds.db;

public class DbConnBean {
	
	@SuppressWarnings("unused")
	private DbConnBean(){}
	
	public DbConnBean(String id,boolean status,long createTime,String url,String name,String password){
		this.id = id;
		this.status = status;
		this.url = url;
		this.name = name;
		this.password = password;
		this.createTime = createTime;
	}
	/**
	 * 连接主键
	 */
	private String id;
	
	/**
	 * 连接状态 true:待命  false:使用中
	 */
	private boolean status;
	
	/**
	 * 连接创建时间
	 */
	private long createTime;
	/**
	 * 连接URL
	 */
	private String url;
	/**
	 * 连接名称
	 */
	private String name;
	/**
	 * 连接密码
	 */
	private String password;

	
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
