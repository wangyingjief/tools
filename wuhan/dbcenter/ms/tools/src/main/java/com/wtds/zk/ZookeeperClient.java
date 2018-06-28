package com.wtds.zk;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import com.alibaba.fastjson.JSON;
import com.wtds.tools.Lz4Util;
import com.wtds.tools.ReflectUtil;
import com.wtds.tools.StringUtil;
import com.wtds.zk.ListenData.Type;

/**
 * zookeeper客户端
 * 
 * @author wyj
 *
 */
public class ZookeeperClient {

	private static final String ZKCONFIG_HOME_PATH = "/nova/data/clean/config";

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
		retryPolicy = new RetryUntilElapsed(config.getMaxElapsedTimeMs(), config.getSleepMsBetweenRetries());
		// retryPolicy = new ExponentialBackoffRetry(config.getBaseSleepTimeMs(),
		// config.getMaxRetries());
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
	 * @throws Exception
	 */
	public void create(String path) throws Exception {
		create(path, null, CreateMode.PERSISTENT);
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param createMode
	 * @throws Exception
	 */
	public void create(String path, CreateMode createMode) throws Exception {
		create(path, null, createMode);
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param obj
	 * @throws Exception
	 */
	public void create(String path, Object obj) throws Exception {
		create(path, JSON.toJSONString(obj), CreateMode.PERSISTENT);
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public void create(String path, String data) throws Exception {
		create(path, data, CreateMode.PERSISTENT);
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param obj
	 * @param createMode
	 * @throws Exception
	 */
	public void create(String path, Object obj, CreateMode createMode) throws Exception {
		create(path, JSON.toJSONString(obj), createMode);
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param data
	 * @param createMode
	 * @throws Exception
	 */
	public void create(String path, String data, CreateMode createMode) throws Exception {
		if (!StringUtil.isEmpty(path)) {
			Stat stat = curatorFramework.checkExists().forPath(path);
			if (stat == null) {
				curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path);
				if (!StringUtil.isEmpty(data)) {
					setData(path, data);
				}
			} else {
				throw new Exception("节点:" + path + "已经存在");
			}
		}
	}

	/**
	 * 写入数据
	 * 
	 * @param path
	 * @param obj
	 * @throws Exception
	 */
	public void setData(String path, Object obj) throws Exception {
		setData(path, JSON.toJSONString(obj));
	}

	/**
	 * 写入数据
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public void setData(String path, String data) throws Exception {
		List<ZkNodeDataModel> list = ZkNodeDataModel.enDataModel(path, data);
		if (list != null && list.size() > 0) {
			if (list.size() == 1) {
				ZkNodeDataModel m = list.get(0);
				curatorFramework.setData().forPath(path, m.toBytes());
			} else {
				String uuid = "";
				for (int i = 0; i < list.size(); i++) {
					ZkNodeDataModel m = list.get(i);
					if (m.getIsb() == 1) {
						uuid = m.getUuid();
						curatorFramework.setData().forPath(path, m.toBytes());
					} else {
						String subPath = path + "/sd-" + uuid + "-" + i;
						System.out.println(m.toBytes().length);
						curatorFramework.setData().forPath(subPath, m.toBytes());
					}
				}
			}
		}
	}

	/**
	 * 获取数据
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String getData(String path) throws Exception {
		String data = null;
		if (curatorFramework.checkExists().forPath(path) == null) {
			return data;
		}

		byte[] mData = curatorFramework.getData().forPath(path);
		data = analysisData(path, mData);
		return data;
	}

	/**
	 * 解析封装后的数据
	 * 
	 * @param path
	 * @param mData
	 * @return
	 * @throws Exception
	 */
	private String analysisData(String path, byte[] mData) throws Exception {
		String data = null;
		if (mData != null) {
			String jsonStr = new String(mData, ZookeeperConfig.charset);
			if (!StringUtil.isEmpty(jsonStr)) {
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
					System.out.println("2-" + byteData.length);
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
		}
		return data;
	}

	/**
	 * 删除节点,包含节点下所有数据
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void delete(String path) throws Exception {
		if (curatorFramework.checkExists().forPath(path) == null) {
			return;
		}
		curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
	}

	/**
	 * 判断节点是否存在<br>
	 * 存在[true] 不存在[false]
	 * 
	 * @param path
	 * @return
	 */
	public boolean check(String path) {
		Stat stat = null;
		try {
			stat = curatorFramework.checkExists().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (stat == null)
			return false;
		else
			return true;
	}

	/**
	 * 监听zookeeper原始数据
	 * 
	 * @param path
	 *            路径
	 * @param listen
	 *            监听类 <br>
	 *            -----------------------------<br>
	 *            使用方式 <br>
	 *            client.listen("/txt2", (TreeCacheEvent event) -> { ChildData data
	 *            = event.getData(); if (data != null) { switch (event.getType()) {
	 *            case NODE_ADDED: System.out.println("NODE_ADDED : " +
	 *            data.getPath() + " 数据:" + new String(data.getData())); break; case
	 *            NODE_REMOVED: System.out.println("NODE_REMOVED : " +
	 *            data.getPath() + " 数据:" + new String(data.getData())); break; case
	 *            NODE_UPDATED: System.out.println("NODE_UPDATED : " +
	 *            data.getPath() + " 数据:" + new String(data.getData())); break;
	 * 
	 *            default: break; } } else { System.out.println("data is null : " +
	 *            event.getType()); } });
	 */
	public void listen(String path, ZookeeperListen listen) {
		// 设置节点的cache
		@SuppressWarnings("resource")
		TreeCache treeCache = new TreeCache(curatorFramework, path);
		// 设置监听器和处理过程
		treeCache.getListenable().addListener(new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				listen.event(event);
			}
		});
		// 开始监听
		try {
			treeCache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 监控封装后的数据
	 * 
	 * @param path
	 * @param listen
	 */
	public void listenData(String path, ZookeeperListenData listen) {
		ZookeeperClient _this = this;
		// 设置节点的cache
		@SuppressWarnings("resource")
		TreeCache treeCache = new TreeCache(curatorFramework, path);
		// 设置监听器和处理过程
		treeCache.getListenable().addListener(new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				ChildData data = event.getData();
				if (data != null) {
					ListenData lData = new ListenData();
					byte[] resultData = null;
					switch (event.getType()) {
					case NODE_ADDED:
						lData.setType(Type.add);
						lData.setPath(data.getPath());
						resultData = data.getData();
						break;
					case NODE_REMOVED:
						lData.setType(Type.delete);
						lData.setPath(data.getPath());
						resultData = data.getData();
						break;
					case NODE_UPDATED:
						lData.setType(Type.update);
						lData.setPath(data.getPath());
						resultData = data.getData();
						break;
					default:
						break;
					}

					if (resultData != null) {
						String value = _this.analysisData(lData.getPath(), resultData);
						lData.setData(value);
					}

					listen.event(lData);
				}
			}
		});
		// 开始监听
		try {
			treeCache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取配置
	 * 
	 * @param group
	 *            组名(类名)
	 * @param key
	 *            主键(字段名)
	 * @return
	 */
	public String getConfig(String group, String key) {
		String result = null;
		String path = ZKCONFIG_HOME_PATH + "/" + group + "/" + key;
		try {
			result = this.getData(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 添加Class中所有的属性到zookeeper中
	 * 
	 * @param clazz
	 * @throws Exception
	 */
	public void addClassConfig(Class<?> clazz) throws ZookeeperConfigException {
		String gPath = ZKCONFIG_HOME_PATH + "/" + clazz.getSimpleName();
		if (this.check(gPath)) {
			throw new ZookeeperConfigException("配置" + gPath + "已经存在");
		} else {
			Field[] fields = clazz.getFields();
			for (Field f : fields) {
				Object object = ReflectUtil.getFieldValue(f.getName(), clazz);
				if (object != null) {
					String path = ZKCONFIG_HOME_PATH + "/" + clazz.getSimpleName() + "/" + f.getName();
					try {
						if (this.check(path)) {
							this.delete(path);
						}
						this.create(path, object.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 写入配置信息
	 * 
	 * @param group
	 * @param key
	 * @param config
	 * @throws Exception
	 */
	public void addConfig(String group, String key, String config) throws Exception {
		String path = ZKCONFIG_HOME_PATH + "/" + group + "/" + key;
		String configData = this.getData(path);
		if (!StringUtil.isEmpty(configData)) {
			throw new Exception("group:" + group + " key:" + key + "已经存在配置,不能覆盖");
		} else {
			this.create(path, config);
		}
	}

	/**
	 * 修改配置信息
	 * 
	 * @param group
	 * @param key
	 * @param config
	 * @throws Exception
	 */
	public void updateConfig(String group, String key, String config) throws Exception {
		String path = ZKCONFIG_HOME_PATH + "/" + group + "/" + key;
		if (this.check(path)) {
			this.delete(path);
		}
		this.create(path, config);
	}

	/**
	 * 根据group清除配置
	 * 
	 * @param group
	 * @param key
	 */
	public void clearConfigByGroup(String group) {
		String path = ZKCONFIG_HOME_PATH + "/" + group;
		if (this.check(path)) {
			try {
				this.delete(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 清除配置
	 * 
	 * @param group
	 * @param key
	 */
	public void clearConfig(String group, String key) {
		String path = ZKCONFIG_HOME_PATH + "/" + group + "/" + key;
		if (this.check(path)) {
			try {
				this.delete(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
