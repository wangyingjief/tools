package com.wtds.zk.simplemq.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.wtds.zk.ZookeeperClient;
import com.wtds.zk.simplemq.ZkSimpleMq;

public class _Test2 {
	
	static int j = 1;
	static long s1 = 0;
	public static void main(String[] args) throws Exception {
		ZookeeperClient client = new ZookeeperClient("114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");
		ZkSimpleMq mq = new ZkSimpleMq(client);
		mq.run("002");
		for (int i = 0; i < 10000; i++) {
			final int x = i;
			new Thread(()->{
				mq.sendToOne("test", "tccccc" + x);
				
				if (j == 1) {
					s1 = System.currentTimeMillis();
				}
				j++;
				if (j == 10000) {
					long s2 = System.currentTimeMillis();
					System.err.println("time======>" + (s2 - s1));
					j = 1;
				}
			}).start(); ;
		}
	}
}
