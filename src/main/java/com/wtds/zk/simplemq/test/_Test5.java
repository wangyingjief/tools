package com.wtds.zk.simplemq.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.wtds.tools.Udp;
import com.wtds.zk.ZookeeperClient;

public class _Test5 {

	static int i = 1;
	static long s1 = 0;

	public static void main(String[] args) throws IOException {
		ZookeeperClient client = new ZookeeperClient("114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");
		
		try {
			//client.getCuratorFramework().delete().deletingChildrenIfNeeded().forPath("/test/0");
			for (int i = 0; i < 10000; i++) {
				client.getCuratorFramework().delete().deletingChildrenIfNeeded().forPath("/test_" + i);
//				client.getCuratorFramework().create().forPath("/test_" + i, "ttt".getBytes());
//				System.out.println("/test/" + i);
			}
			System.out.println("Ok");
			TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Udp udp = new Udp(1258);
		// udp.receiveMessage((String msg, DatagramPacket pack) -> {
		// if (i == 1) {
		// s1 = System.currentTimeMillis();
		// }
		// i++;
		// if (i == 10000) {
		// long s2 = System.currentTimeMillis();
		// System.err.println("time======>" + (s2 - s1));
		// i = 1;
		// }
		// //System.out.println(msg);
		// });
		//
		// for (int i = 0; i < 50000; i++) {
		// udp.sendMessage("127.0.0.1", "aaaaaaaaaa", 0);
		// }

	}
}
