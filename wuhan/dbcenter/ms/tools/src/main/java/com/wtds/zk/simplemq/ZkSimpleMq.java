package com.wtds.zk.simplemq;

import org.apache.zookeeper.CreateMode;

import com.alibaba.fastjson.JSON;
import com.wtds.zk.ZookeeperClient;
import com.wtds.zk.simplemq.bean.MqInfo;
import com.wtds.zk.simplemq.config.MqZkPathConfig;
import com.wtds.zk.simplemq.service.MessageCallback;
import com.wtds.zk.simplemq.service.MqService;

/**
 * zookeeper实现的简单MQ
 * 
 * @author wyj
 *
 */
public class ZkSimpleMq {

	private MqZkPathConfig pathConfig = new MqZkPathConfig();;

	private MqInfo mqBean = new MqInfo();

	private ZookeeperClient client;

	private MqService service;

	public ZkSimpleMq(ZookeeperClient client) {
		this.client = client;
	}

	public ZkSimpleMq(String group, ZookeeperClient client) {
		this.client = client;
		this.pathConfig.setGroup(group);
	}

	/**
	 * 运行客户端
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void run(String id) throws Exception {
		this.mqBean.setId(id);
		init();
	}

	/**
	 * 注册当前客户端至zookeeper
	 * 
	 * @throws Exception
	 */
	public void register() throws Exception {
		String path = pathConfig.getMq_client_path() + "/" + mqBean.getId();
		if (client.check(path)) {
			throw new Exception("客户端:" + mqBean.getId() + "已经存在!");
		}
		client.create(path, JSON.toJSONString(mqBean), CreateMode.EPHEMERAL);
		System.out.println("register-client:" + path);
	}

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		initZkDir();
		register();
		service = new MqService(pathConfig, client);
	}

	/**
	 * 初始化zookeeper消息要用的目录
	 */
	public void initZkDir() {
		try {
			if (!client.check(pathConfig.getMq_home_path())) {
				client.create(pathConfig.getMq_home_path());
			}
			if (!client.check(pathConfig.getMq_client_path())) {
				client.create(pathConfig.getMq_client_path());
			}
			if (!client.check(pathConfig.getMq_message_path())) {
				client.create(pathConfig.getMq_message_path());
			}
			if (!client.check(pathConfig.getMq_notice_path())) {
				client.create(pathConfig.getMq_notice_path());
			}
			if (!client.check(pathConfig.getMq_consumer_path())) {
				client.create(pathConfig.getMq_consumer_path());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送消息<br>
	 * 只会有一个注册终端收到
	 * 
	 * @param topic
	 *            主题
	 * @param content
	 *            内容
	 */
	public void sendToOne(String topic, String content) {
		service.send(topic, content, 1);
	}

	/**
	 * 发送消息<br>
	 * 所有注册终端都会收到
	 * 
	 * @param topic
	 *            主题
	 * @param content
	 *            内容
	 */
	public void sendToMany(String topic, String content) {
		service.send(topic, content, 2);
	}

	/**
	 * 接收消息
	 * 
	 * @param topic
	 * @param callback
	 */
	public void receive(String topic, MessageCallback callback) {
		service.listenGetMessage(mqBean.getId(), topic, callback);
	}

}
