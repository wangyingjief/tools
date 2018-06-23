package com.wtds.zk.simplemq.config;

/**
 * 消息的目录配置
 * 
 * @author wyj
 *
 */
public class MqZkPathConfig {
	
	/**
	 * 消息组
	 */
	private String group = "/simple";

	/**
	 * mq主目录
	 */
	private String mq_home_path = "/nova/zk/mq" + group;

	/**
	 * 消息目录<br>
	 * /nova/zk/mq/{group}/message/{topic}/{message-uuid}@(content)
	 */
	private String mq_message_path = mq_home_path + "/message";

	/**
	 * 客户端目录<br>
	 * /nova/zk/mq/{group}/client/{id}
	 */
	private String mq_client_path = mq_home_path + "/client";

	/**
	 * 通知目录<br>
	 * /nova/zk/mq/{group}/notice/new/{topic}/{id}/{message-uuid}
	 */
	private String mq_notice_path = mq_home_path + "/notice";
	
	/**
	 * 消费者目录<br>
	 * /nova/zk/mq/{group}/consumer/client_receive_{topic}_{id}
	 */
	private String mq_consumer_path = mq_home_path + "/consumer";
	/**
	 * 消息组
	 */
	public String getGroup() {
		return group;
	}
	/**
	 * 消息组
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	/**
	 * mq主目录
	 */
	public String getMq_home_path() {
		return mq_home_path;
	}
	/**
	 * mq主目录
	 */
	public void setMq_home_path(String mq_home_path) {
		this.mq_home_path = mq_home_path;
	}
	/**
	 * 消息目录
	 */
	public String getMq_message_path() {
		return mq_message_path;
	}
	/**
	 * 消息目录
	 */
	public void setMq_message_path(String mq_message_path) {
		this.mq_message_path = mq_message_path;
	}
	/**
	 * 客户端目录
	 */
	public String getMq_client_path() {
		return mq_client_path;
	}
	/**
	 * 客户端目录
	 */
	public void setMq_client_path(String mq_client_path) {
		this.mq_client_path = mq_client_path;
	}
	/**
	 * 通知目录
	 */
	public String getMq_notice_path() {
		return mq_notice_path;
	}
	/**
	 * 通知目录
	 */
	public void setMq_notice_path(String mq_notice_path) {
		this.mq_notice_path = mq_notice_path;
	}
	/**
	 * 消费者目录<br>
	 * /nova/zk/mq/{group}/consumer/client_receive_{topic}_{id}
	 */
	public String getMq_consumer_path() {
		return mq_consumer_path;
	}
	/**
	 * 消费者目录<br>
	 * /nova/zk/mq/{group}/consumer/client_receive_{topic}_{id}
	 */
	public void setMq_consumer_path(String mq_consumer_path) {
		this.mq_consumer_path = mq_consumer_path;
	}
	
	
}
