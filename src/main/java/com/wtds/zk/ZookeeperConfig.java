package com.wtds.zk;

import java.nio.charset.Charset;

public class ZookeeperConfig {
	
	/**
	 * zookeeper服务连接信息
	 */
	private String connectString;
	
	/**
	 * session超时时间
	 */
	private int sessionTimeoutMs;
	
	/**
	 * 连接超时时间
	 */
	private int connectionTimeoutMs;
	
	/**
	 * 初始sleep时间
	 */
	private int baseSleepTimeMs;
	
	/**
	 * 最大重试次数
	 */
	private int maxRetries;
	
	/**
	 * 字符集
	 */
	public static Charset charset = Charset.forName("UTF-8");
	
	/**
	 * 获取默认配置<br>
	 * @param connectString zookeeper服务连接信息<br>
	 * sessionTimeoutMs session超时时间 3s<br>
	 * connectionTimeoutMs 连接超时时间 5s<br>
	 * baseSleepTimeMs 初始sleep时间 1s<br>
	 * maxRetries 最大重试次数
	 * @return ZookeeperConfig
	 */
	public static ZookeeperConfig getDefaultConfig(String connectString) {
		ZookeeperConfig config = new ZookeeperConfig();
		config.setConnectString(connectString);
		config.setSessionTimeoutMs(3000);
		config.setConnectionTimeoutMs(5000);
		config.setBaseSleepTimeMs(1000);
		config.setMaxRetries(3);
		return config;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public void setSessionTimeoutMs(int sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
	}

	public int getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}

	public void setConnectionTimeoutMs(int connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}

	public int getBaseSleepTimeMs() {
		return baseSleepTimeMs;
	}

	public void setBaseSleepTimeMs(int baseSleepTimeMs) {
		this.baseSleepTimeMs = baseSleepTimeMs;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}
	
	
}
