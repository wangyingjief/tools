package com.wtds.db;

/**
 * 数据库连接参数类
 * @author wangyingjie
 *
 */
 public class DbConfig {
	
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
	/**
	 * 最大连接
	 */
	private int maxconn = 20;
	/**
	 * 最小连接
	 */
	private int minconn = 5;
	/**
	 * 连接超时关闭时间(s)
	 */
	private int outtime = 120;
	
	/**
	 * 检查超时连接时间(s)
	 */
	private int checkTime = 10;

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

	public int getMaxconn() {
		return maxconn;
	}

	public void setMaxconn(int maxconn) {
		this.maxconn = maxconn;
	}

	public int getMinconn() {
		return minconn;
	}

	public void setMinconn(int minconn) {
		this.minconn = minconn;
	}

	public int getOuttime() {
		return outtime;
	}

	public void setOuttime(int outtime) {
		this.outtime = outtime;
	}

	public int getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(int checkTime) {
		this.checkTime = checkTime;
	}
	
	
}
