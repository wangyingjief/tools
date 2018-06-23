package com.wtds.zk;

import java.nio.charset.Charset;

import net.sf.ehcache.config.ConfigError;

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
	 * 最大重试时间
	 */
	private int maxElapsedTimeMs;
	
	/**
	 * 每次重试间隔时间

	 */
	private int sleepMsBetweenRetries;
	
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
		config.setMaxElapsedTimeMs(5000);
		config.setSleepMsBetweenRetries(1000);
		return config;
	}

	public int getMaxElapsedTimeMs() {
		return maxElapsedTimeMs;
	}

	public void setMaxElapsedTimeMs(int maxElapsedTimeMs) {
		this.maxElapsedTimeMs = maxElapsedTimeMs;
	}

	public int getSleepMsBetweenRetries() {
		return sleepMsBetweenRetries;
	}

	public void setSleepMsBetweenRetries(int sleepMsBetweenRetries) {
		this.sleepMsBetweenRetries = sleepMsBetweenRetries;
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
