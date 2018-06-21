package com.nova.zookeeper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.wtds.tools.ByteUtil;
import com.wtds.tools.FileUtil;
import com.wtds.tools.Lz4Util;

public class Test {
	public static void main(String[] args) throws Exception {
		t1();

	}

	public static void t2() throws UnsupportedEncodingException {
		byte []  fileByte = FileUtil.readFileToByte("D:\\svn_home\\wuhan\\novaDataClean-Manager\\cleanXML\\aa.txt");
		String data = new String(fileByte,"UTF-8");
		//String data = FileUtil.readFileToString("D:\\svn_home\\wuhan\\novaDataClean-Manager\\cleanXML\\aa.txt");
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
		byte []  fileByte = FileUtil.readFileToByte("D:\\svn_home\\wuhan\\novaDataClean-Manager\\cleanXML\\aa.txt");
		String data = new String(fileByte,"UTF-8");
		System.out.println(data.length());
		ZookeeperClient client = new ZookeeperClient("114.55.136.158:2181,114.55.136.158:2182,114.55.136.158:2183");
		try {
			// client.getCuratorFramework().delete().forPath("/txt2");
			client.getCuratorFramework().delete().deletingChildrenIfNeeded().forPath("/txt2");
			client.create("/txt2", data);

			String data2 = client.getData("/txt2");
			//System.out.println(data2);
			List<String> nodes0 = client.getCuratorFramework().getChildren().forPath("/");
			System.out.println("-->");
			System.out.println(JSON.toJSONString(nodes0));

			List<String> nodes = client.getCuratorFramework().getChildren().forPath("/txt2");
			System.out.println("-->");
			System.out.println(JSON.toJSONString(nodes));
			FileUtil.writeStringToFile(new File("D:\\svn_home\\wuhan\\novaDataClean-Manager\\cleanXML\\aa2.txt"), data2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
