package com.wtds.zk.simplemq.bean;
/**
 * 通知实体
 * @author wyj
 */
public class Notice {
	/**
	 * 主题
	 */
	private String topic;
	/**
	 * 消息类型 1:只有一个接收者收到 2:所有接收者都收到
	 */
	private int type;
	/**
	 * 消息的uuid
	 */
	private String messageUuid;
	
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMessageUuid() {
		return messageUuid;
	}
	public void setMessageUuid(String messageUuid) {
		this.messageUuid = messageUuid;
	}
	
	
}
