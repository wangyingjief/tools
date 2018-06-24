package com.wtds.zk.simplemq.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.zookeeper.CreateMode;

import com.wtds.tools.StringUtil;
import com.wtds.tools.ThreadPoolUtil;
import com.wtds.tools.UUIDUtil;
import com.wtds.zk.ZookeeperClient;
import com.wtds.zk.simplemq.bean.Message;
import com.wtds.zk.simplemq.bean.Notice;
import com.wtds.zk.simplemq.config.MqZkPathConfig;

/**
 * 消息服务类
 * 
 * @author wyj
 *
 */
public class MqService {

	MqZkPathConfig config;

	ZookeeperClient client;

	ThreadPoolExecutor consumerPool;
	ThreadPoolExecutor sendPool;

	public MqService(MqZkPathConfig config, ZookeeperClient client) {
		this.config = config;
		this.client = client;
		
		consumerPool = ThreadPoolUtil.newThreadPoolExecutor(config.getReceiveMinThread(), config.getReceiveMaxThread(),
				config.getReceiveKeepAliveTime());
		sendPool = ThreadPoolUtil.newThreadPoolExecutor(config.getSendMinThread(), config.getSendMaxThread(),
				config.getSendKeepAliveTime());
	}

	/**
	 * 同步发送消息
	 * 
	 * @param topic
	 * @param content
	 * @param type
	 */
	public void send(String topic, String content, int type) {
		Message message = new Message();
		message.setN(content);

		Notice notice = new Notice();
		notice.setMessageUuid(UUIDUtil.getUUID());
		notice.setTopic(topic);
		notice.setType(type);

		saveMessageToZk(message, notice);
		sendNotice(notice);
	}

	/**
	 * 异步发送消息
	 * 
	 * @param topic
	 * @param content
	 * @param type
	 */
	public void syncSend(String topic, String content, int type) {
		sendPool.execute(() -> {
			send(topic, content, type);
		});
	}

	/**
	 * 存储信息到zNode
	 * 
	 * @param message
	 * @param notice
	 */
	private void saveMessageToZk(Message message, Notice notice) {
		try {
			// 消息存储路径
			String messagePath = config.getMq_message_path() + "/" + notice.getTopic() + "/" + notice.getMessageUuid();
			// 消息创建时间
			String messageCreateTimePath = messagePath + "/" + System.currentTimeMillis();
			// 存储消息
			client.create(messagePath, message);
			client.create(messageCreateTimePath);
			System.out.println("save-message:" + messagePath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送接收通知
	 * 
	 * @param notice
	 */
	private void sendNotice(Notice notice) {
		List<String> consumers = getAllConsumerByTopic(notice);
		if (consumers != null && consumers.size() > 0) {
			for (String id : consumers) {
				String noticePath = config.getMq_notice_path() + "/new/" + notice.getTopic() + "/" + id + "/"
						+ notice.getMessageUuid();
				try {
					client.create(noticePath);
					System.out.println("save-notice:" + noticePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取需要接收消息的客户端ID
	 * 
	 * @param notice
	 * @return
	 */
	private List<String> getAllConsumerByTopic(Notice notice) {
		String consumerPath = config.getMq_consumer_path();
		List<String> consumerIds = null;
		List<String> consumerStr = null;
		try {
			if (client.check(consumerPath)) {
				consumerIds = new ArrayList<String>();
				consumerStr = client.getCuratorFramework().getChildren().forPath(consumerPath);
			}
			for (String c : consumerStr) {
				String[] info = c.split("_");
				if (info != null && info.length == 4 && info[2].equals(notice.getTopic())) {
					consumerIds.add(info[3]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (consumerIds != null && consumerIds.size() > 0) {
			// 如果发送给当个，则随机发送至一个消费者
			if (consumerIds.size() > 1 && notice.getType() == 1) {
				Random random = new Random();
				int i = random.nextInt(consumerIds.size());
				String id = consumerIds.get(i);
				consumerIds = new ArrayList<String>();
				consumerIds.add(id);
			}
		}
		return consumerIds;
	}

	/**
	 * 获取消息
	 * 
	 * @param callback
	 */
	public void listenGetMessage(String id, String topic, MessageCallback callback) {
		registerConsumer(id, topic);
		listenOvertimeMessage();

		String listenPath = config.getMq_notice_path() + "/new/" + topic + "/" + id;
		System.out.println("listen:" + listenPath);
		client.listen(listenPath, (TreeCacheEvent event) -> {

			ChildData data = event.getData();
			if (data != null) {
				consumerPool.execute(() -> {
					// new Thread(() -> {
					switch (event.getType()) {
					case NODE_ADDED:
						// System.out.println("NODE_ADDED : " + data.getPath() + " 数据:" + new
						// String(data.getData()));

						// 监听消息
						String messageId = data.getPath().replaceAll(listenPath, "");
						if (!StringUtil.isEmpty(messageId)) {
							String messagePath = config.getMq_message_path() + "/" + topic + messageId;
							try {
								String content = client.getData(messagePath);
								if (!StringUtil.isEmpty(content)) {
									callback.result(content);
									// 接收到信息，删除通知，删除信息
									client.delete(data.getPath());
									try {
										client.delete(messagePath);
									} catch (Exception e) {
										System.out.println(e);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					default:
						break;
					}
				});

			} else {
				System.out.println("data is null : " + event.getType());
			}
		});
	}

	/**
	 * 注册为消费者
	 * 
	 * @param id
	 * @param topic
	 */
	public void registerConsumer(String id, String topic) {
		String ptah = config.getMq_consumer_path() + "/client_receive_" + topic + "_" + id;
		try {
			client.create(ptah, CreateMode.EPHEMERAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void listenOvertimeMessage() {

	}
}
