package com.wtds.zk.simplemq.test;

import java.util.concurrent.TimeUnit;

import com.wtds.zk.ZookeeperClient;

public class _Test4 {
	public static void main(String[] args) throws Exception {
		ZookeeperClient client = new ZookeeperClient("localhost:2181");
		TimeUnit.SECONDS.sleep(1);
		client.delete("/test/1/2/3/4");
//		TimeUnit.SECONDS.sleep(1);
//		client.create("/test/2/2/3/4");
//		TimeUnit.SECONDS.sleep(1);
//		client.create("/test/1/2/3/4/5");
//		TimeUnit.SECONDS.sleep(1);
//		client.create("/test/1/2/3/4/5/6");
		TimeUnit.SECONDS.sleep(1);
		client.create("/test/1/2/3/4", "tt");
	}
}
