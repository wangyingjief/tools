package com.wtds.zk;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.alibaba.fastjson.JSON;
import com.wtds.tools.ByteUtil;
import com.wtds.tools.FileUtil;
import com.wtds.tools.Lz4Util;

public class Test {
	public static void main(String[] args) throws Exception {
		t3();
	}

	public static void t3() throws Exception {
		ZookeeperClient client = new ZookeeperClient("114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");
		List<String> nodes0 = client.getCuratorFramework().getChildren().forPath("/");
		System.out.println("-->");
		System.out.println(JSON.toJSONString(nodes0));

		client.listen("/txt2/a/c/d", (TreeCacheEvent event) -> {
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

		client.delete("/txt2/a/c/d");
		client.create("/txt2/a/c/d", "测试");
		client.create("/txt2/a/c/d", "测试2");
		System.out.println("-->" + client.getData("/txt2/a/c/d"));

		client.listenData("", (ListenData data) -> {
			
		});
	}

	public static void t2() throws UnsupportedEncodingException {
		byte[] fileByte = FileUtil.readFileToByte("D:\\svn_home\\wuhan\\novaDataClean-Manager\\cleanXML\\aa.txt");
		String data = new String(fileByte, "UTF-8");
		// String data =
		// FileUtil.readFileToString("D:\\svn_home\\wuhan\\novaDataClean-Manager\\cleanXML\\aa.txt");
		long l1 = System.currentTimeMillis();
		byte[] y = data.getBytes(ZookeeperConfig.charset);
		byte[] b = Lz4Util.compressedByte(y);
		List<byte[]> bl = ByteUtil.splitByte(b, 869);

		byte[] zl = ByteUtil.joinByte(bl);

		byte[] j = Lz4Util.decompressorByte(zl, y.length);
		System.out.println(new String(j, ZookeeperConfig.charset));
		long l2 = System.currentTimeMillis();
		System.out.println("TIME:" + ((l2 - l1)));
	}

	public static void t1() throws Exception {
		byte[] fileByte = FileUtil.readFileToByte("/Users/joymting/Downloads/yuncong_2018-06-22.sql");
		String data = new String(fileByte, "UTF-8");
		System.out.println(data.length());
		ZookeeperClient client = new ZookeeperClient("114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");
		try {
			List<String> nodes0 = client.getCuratorFramework().getChildren().forPath("/");
			System.out.println("-->");
			System.out.println(JSON.toJSONString(nodes0));

			// client.getCuratorFramework().delete().forPath("/txt2");
			if (client.getCuratorFramework().checkExists().forPath("/txt2") != null)
				client.getCuratorFramework().delete().deletingChildrenIfNeeded().forPath("/txt2");
			client.create("/txt2", data);

			String data2 = client.getData("/txt2");
			// System.out.println(data2);

			List<String> nodes = client.getCuratorFramework().getChildren().forPath("/txt2");
			System.out.println("-->");
			System.out.println(JSON.toJSONString(nodes));
			FileUtil.writeStringToFile(new File("D:\\svn_home\\wuhan\\novaDataClean-Manager\\cleanXML\\aa2.txt"),
					data2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
