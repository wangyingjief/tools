package com.wtds.zk;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

public interface ZookeeperListen {
	
	/**
	 * 使用方式
	 * client.listen("/txt2", (TreeCacheEvent event) -> {
			ChildData data = event.getData();
			if (data != null) {
				switch (event.getType()) {
				case NODE_ADDED:
					System.out.println("NODE_ADDED : " + data.getPath() + "  数据:" + new String(data.getData()));
					break;
				case NODE_REMOVED:
					System.out.println("NODE_REMOVED : " + data.getPath() + "  数据:" + new String(data.getData()));
					break;
				case NODE_UPDATED:
					System.out.println("NODE_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
					break;

				default:
					break;
				}
			} else {
				System.out.println("data is null : " + event.getType());
			}
		});
	 * @param event
	 */
	public void event(TreeCacheEvent event);
}
