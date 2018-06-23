package com.wtds.zk.simplemq.bean;

/**
 * mq的实体信息
 * @author wyj
 *
 */
public class MqInfo {
	/**
	 * 客户端ID
	 */
	private String id;
	
	/**
	 * 此客户端创建时间
	 */
	private long create_time = System.currentTimeMillis();
	
	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
}
