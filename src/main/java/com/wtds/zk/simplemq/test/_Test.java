package com.wtds.zk.simplemq.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.wtds.zk.ZookeeperClient;
import com.wtds.zk.simplemq.ZkSimpleMq;

public class _Test {
	static int i = 0;
	static long s1 = 0;
	static int count1 = 0;
	static int count2 = 0;

	public static void main(String[] args) throws Exception {
		t1();
		t2();
	}

	public static void t1() {

		new Thread(() -> {
			try {

				ZookeeperClient client = new ZookeeperClient(
						"114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");

				List<String> list = client.getCuratorFramework().getChildren().forPath("/nova/zk/mq/simple/client");
				System.out.println(list);
				List<String> list2 = client.getCuratorFramework().getChildren().forPath("/nova/zk/mq/simple/consumer");
				System.out.println(list2);

				// client.delete("/nova/zk/mq/simple/client");
				// client.delete("/nova/zk/mq/simple/consumer/client_receive_test_001");

				ZkSimpleMq mq = new ZkSimpleMq(client);
				mq.run("r001");

				mq.receive("test", (String content) -> {
					count1++;
					System.out.println("T11111|(" + count1 + ")========>" + content);
					if (i == 1) {
						s1 = System.currentTimeMillis();
					}
					i++;
					if (i == 10000) {
						long s2 = System.currentTimeMillis();
						System.err.println("time======>" + (s2 - s1));
						System.err.println("T11111Count========>" + i);
						i = 1;
					}

				});

				TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public static void t2() {
		new Thread(() -> {
			try {
				ZookeeperClient client = new ZookeeperClient(
						"114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");

				List<String> list = client.getCuratorFramework().getChildren().forPath("/nova/zk/mq/simple/client");
				System.out.println(list);
				List<String> list2 = client.getCuratorFramework().getChildren().forPath("/nova/zk/mq/simple/consumer");
				System.out.println(list2);

				// client.delete("/nova/zk/mq/simple/client");
				// client.delete("/nova/zk/mq/simple/consumer/client_receive_test_001");

				ZkSimpleMq mq = new ZkSimpleMq(client);
				mq.run("r002");

				mq.receive("test", (String content) -> {
					count2++;
					System.out.println("T22222|(" + count2 + ")========>" + content);
					if (i == 1) {
						s1 = System.currentTimeMillis();
					}
					i++;
					if (i == 10000) {
						long s2 = System.currentTimeMillis();
						System.err.println("time======>" + (s2 - s1));
						System.err.println("T2222Count========>" + i);
						i = 1;
					}

				});

				TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
