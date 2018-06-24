package com.wtds.zk.simplemq.test;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.tomcat.jni.Thread;

import com.wtds.tools.ThreadPoolUtil;
import com.wtds.zk.ZookeeperClient;

public class _Test4 {
	public static void main(String[] args) throws Exception {

		ThreadPoolExecutor pool = ThreadPoolUtil.newThreadPoolExecutor(128, 512 * 4, 30);

		/// nova/zk/mq/simple/message/test/91668325954282624264392041782825
		ZookeeperClient client = new ZookeeperClient("114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");
		// client.delete("/nova/zk/mq/simple/message/test");
		// client.getCuratorFramework().delete().deletingChildrenIfNeeded().forPath("/nova/zk/mq/simple/message/test");
		List<String> rList = client.getCuratorFramework().getChildren().forPath("/nova/zk/mq/simple/message/test");
		//List<String> rList = client.getCuratorFramework().getChildren().forPath("/nova/zk/mq/simple/consumer");

		for (int i = 0; i < rList.size(); i++) {
			String string = rList.get(i);
			pool.execute(() -> {
				try {
					System.out.println("UUID:" + string);
					client.getCuratorFramework().delete().deletingChildrenIfNeeded()
							.forPath("/nova/zk/mq/simple/message/test/" + string);
				} catch (Exception e) {
					// TODO: handle exception
				}

			});
			// new java.lang.Thread(() -> {
			// try {
			//
			// client.getCuratorFramework().delete().deletingChildrenIfNeeded()
			// .forPath("/nova/zk/mq/simple/message/test/" + string);
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			//
			// }).start();

		}
		System.out.println("Count:" + rList.size());
		TimeUnit.SECONDS.sleep(Long.MAX_VALUE);

		client.getCuratorFramework().close();
		// client.delete("/test/1/2/3/4");
		// TimeUnit.SECONDS.sleep(1);
		// client.create("/test/2/2/3/4");
		// TimeUnit.SECONDS.sleep(1);
		// client.create("/test/1/2/3/4/5");
		// TimeUnit.SECONDS.sleep(1);
		// client.create("/test/1/2/3/4/5/6");
		// TimeUnit.SECONDS.sleep(1);
		// client.create("/test/1/2/3/4", "tt");
	}
}
