package com.wtds.zk.simplemq.test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.wtds.tools.UUIDUtil;
import com.wtds.zk.ZookeeperClient;
import com.wtds.zk.simplemq.bean.Message;
import com.wtds.zk.simplemq.bean.Notice;

public class _Test3 {
	public static void main(String[] args) throws Exception {
		ZookeeperClient client = new ZookeeperClient("114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");

		client.listen("/test/1/2/3/4", (TreeCacheEvent event) -> {
			ChildData data = event.getData();
			if (data != null) {
				switch (event.getType()) {
				case NODE_ADDED:
					System.out.println("NODE_ADDED : " + data.getPath() + " 数据:" + new String(data.getData()));
					break;
				case NODE_REMOVED:
					System.out.println("NODE_REMOVED : " + data.getPath() + " 数据:" + new String(data.getData()));
					break;
				case NODE_UPDATED:
					System.out.println("NODE_UPDATED : " + data.getPath() + " 数据:" + new String(data.getData()));
					break;

				default:
					break;
				}
			} else {
				System.out.println("data is null : " + event.getType());
			}
		});
		
		TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
	}
}
