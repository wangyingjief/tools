package com.nova.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.alibaba.fastjson.JSON;
import com.wtds.tools.Lz4Util;
import com.wtds.tools.StringUtil;

/**
 * zookeeper客户端
 * 
 * @author wyj
 *
 */
public class ZookeeperClient {

	@SuppressWarnings("unused")
	private ZookeeperClient() {
	}

	private CuratorFramework curatorFramework;

	private RetryPolicy retryPolicy;

	private ZookeeperConfig config;

	/**
	 * 实例化zookeeper client
	 * 
	 * @param connectString
	 *            zookeeper连接地址
	 */
	public ZookeeperClient(String connectString) {
		config = ZookeeperConfig.getDefaultConfig(connectString);
		instantiation();
	}

	/**
	 * 根据配置文件实例化zookeeper client
	 * 
	 * @param zookeeperConfig
	 */
	public ZookeeperClient(ZookeeperConfig zookeeperConfig) {
		instantiation();
	}

	/**
	 * 初始化zookeeper client
	 */
	private void instantiation() {
		retryPolicy = new ExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxRetries());
		curatorFramework = CuratorFrameworkFactory.builder().connectString(config.getConnectString())
				.sessionTimeoutMs(config.getSessionTimeoutMs()).connectionTimeoutMs(config.getConnectionTimeoutMs())
				.retryPolicy(retryPolicy).build();

		// 创建会话
		curatorFramework.start();
	}

	private static ZookeeperClient defaultZookeeperClient;

	/**
	 * 获取默认实例
	 * 
	 * @return
	 */
	public static ZookeeperClient getDefaultZookeeperClient() {
		return defaultZookeeperClient;
	}

	/**
	 * 初始化默认实例
	 * 
	 * @param zookeeperConfig
	 */
	public static void instantiationDefaultZookeeperClient(ZookeeperConfig zookeeperConfig) {
		defaultZookeeperClient = new ZookeeperClient(zookeeperConfig);
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public void create(String path, String data) throws Exception {
		if (!StringUtil.isEmpty(path)) {
			if (StringUtil.isEmpty(data)) {
				curatorFramework.create().forPath(path);
			} else {
				List<ZkNodeDataModel> list = ZkNodeDataModel.enDataModel(path, data);
				if (list != null && list.size() > 0) {
					if (list.size() == 1) {
						ZkNodeDataModel m = list.get(0);
						curatorFramework.create().forPath(path, m.toBytes());
					} else {
						String uuid = "";
						for (int i = 0; i < list.size(); i++) {
							ZkNodeDataModel m = list.get(i);
							if (m.getIsb() == 1) {
								uuid = m.getUuid();
								curatorFramework.create().forPath(path, m.toBytes());
							} else {
								String subPath = path + "/sd-" + uuid + "-" + i;
								System.out.println(m.toBytes().length);
								curatorFramework.create().forPath(subPath, m.toBytes());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取数据
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String getData(String path) throws Exception {
		String data = null;
		byte[] mData = curatorFramework.getData().forPath(path);
		if (mData != null) {
			String jsonStr = new String(mData, ZookeeperConfig.charset);
			ZkNodeDataModel m = JSON.parseObject(jsonStr, ZkNodeDataModel.class);
			// 判断是否独立数据
			if (m.getIsb() == 0) {
				byte[] byteData = null;
				// 判断是否压缩
				if (m.isLz4()) {
					byteData = Lz4Util.decompressorByte(m.getData(), m.getLz4BeforeSize());
				} else {
					byteData = m.getData();
				}
				System.out.println("2-"+byteData.length);
				data = new String(byteData, ZookeeperConfig.charset);
			} else if (m.getIsb() == 1) {
				List<ZkNodeDataModel> submList = new ArrayList<ZkNodeDataModel>();
				submList.add(m);
				for (int i = 1; i <= m.getMaxSubNode(); i++) {
					String subPath = path + "/sd-" + m.getUuid() + "-" + i;
					byte[] subData = curatorFramework.getData().forPath(subPath);
					String subJson = new String(subData, ZookeeperConfig.charset);
					ZkNodeDataModel subm = JSON.parseObject(subJson, ZkNodeDataModel.class);
					submList.add(subm);
				}
				data = ZkNodeDataModel.deDataModel(submList);
			}
		}
		return data;
	}

	public CuratorFramework getCuratorFramework() {
		return curatorFramework;
	}

	public void setCuratorFramework(CuratorFramework curatorFramework) {
		this.curatorFramework = curatorFramework;
	}

	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	public ZookeeperConfig getConfig() {
		return config;
	}

	public void setConfig(ZookeeperConfig config) {
		this.config = config;
	}

}
