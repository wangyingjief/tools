package com.wtds.zk;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.wtds.tools.ByteUtil;
import com.wtds.tools.Lz4Util;
import com.wtds.tools.UUIDUtil;

/**
 * zookeeper数据模型
 * 
 * @author wyj
 *
 */
public class ZkNodeDataModel {
	/**
	 * 数据
	 */
	private byte[] data;
	/**
	 * 是否开启压缩
	 */
	private boolean lz4 = false;
	/**
	 * 压缩之前的数据大小
	 */
	private int lz4BeforeSize;
	/**
	 * 压缩后的数据大小
	 */
	private int lz4AfterSize;

	private String uuid;

	private int maxSubNode;
	/**
	 * 数据状态:0-独立数据 ,1-分切数据主描述数据,2-分切数据子数据
	 * 
	 */
	private int isb = 0;

	private static int nodeSize = 768 * 1000;

	/**
	 * 封装json数据为多个ZkNodeDataModel
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public static List<ZkNodeDataModel> enDataModel(String path, String data) {
		List<ZkNodeDataModel> list = new ArrayList<ZkNodeDataModel>();
		ZkNodeDataModel model = new ZkNodeDataModel();

		// 1.json转为byte
		byte[] b = data.getBytes(ZookeeperConfig.charset);
		System.out.println("1-" + b.length);
		if (b.length > nodeSize) {
			// 压缩后的数据
			byte[] comByte = Lz4Util.compressedByte(b);
			// 判断压缩有的数据是否还是大于896KB
			if (comByte.length > nodeSize) {

				// 如果压缩后的数据，还是大于896KB则存储到子节点
				List<byte[]> byteList = ByteUtil.splitByte(comByte, nodeSize);
				String uuid = UUIDUtil.getUUID();

				model.setUuid(uuid);
				model.setMaxSubNode(byteList.size());
				model.setLz4(true);
				model.setIsb(1);
				model.setLz4BeforeSize(b.length);
				model.setLz4AfterSize(comByte.length);
				list.add(model);
				for (int i = 0; i < byteList.size(); i++) {
					ZkNodeDataModel subModel = new ZkNodeDataModel();
					subModel.setData(byteList.get(i));
					subModel.setIsb(2);
					list.add(subModel);
				}

			} else {
				// 压缩后的数据
				model.setLz4(true);
				model.setData(comByte);
				model.setLz4BeforeSize(b.length);
				model.setLz4AfterSize(comByte.length);
				list.add(model);
			}
		} else {
			// 未压缩的数据
			model.setData(b);
			list.add(model);
		}

		return list;
	}

	/**
	 * 拆箱ZkNodeDataModel 为 data
	 * 
	 * @param list
	 * @return
	 */
	public static String deDataModel(List<ZkNodeDataModel> list) {
		String data = "";
		byte[] b = null;
		if (list != null && list.size() > 0) {
			// 只有一条数据，并且为独立数据
			if (list.size() == 1 && list.get(0).getIsb() == 0) {
				ZkNodeDataModel zndm = list.get(0);
				// 是否压缩
				if (zndm.isLz4()) {
					b = Lz4Util.decompressorByte(zndm.getData(), zndm.getLz4BeforeSize());
				} else {
					b = zndm.getData();
				}
			} else {
				ZkNodeDataModel zndm = null;
				List<byte[]> byteList = new ArrayList<byte[]>();
				for (ZkNodeDataModel m : list) {
					if (m.getIsb() == 2) {
						byteList.add(m.getData());
					} else if (m.getIsb() == 1) {
						zndm = m;
					}
				}
				byte[] b2 = ByteUtil.joinByte(byteList);
				b = Lz4Util.decompressorByte(b2, zndm.getLz4BeforeSize());
			}
		}
		System.out.println("3-" + b.length);
		if (b != null) {
			data = new String(b, ZookeeperConfig.charset);
		}

		return data;
	}

	public int getIsb() {
		return isb;
	}

	public void setIsb(int isb) {
		this.isb = isb;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMaxSubNode() {
		return maxSubNode;
	}

	public void setMaxSubNode(int maxSubNode) {
		this.maxSubNode = maxSubNode;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isLz4() {
		return lz4;
	}

	public void setLz4(boolean lz4) {
		this.lz4 = lz4;
	}

	/**
	 * 压缩之前的数据大小
	 */
	public int getLz4BeforeSize() {
		return lz4BeforeSize;
	}

	/**
	 * 压缩之前的数据大小
	 */
	public void setLz4BeforeSize(int lz4BeforeSize) {
		this.lz4BeforeSize = lz4BeforeSize;
	}

	/**
	 * 压缩后的数据大小
	 */
	public int getLz4AfterSize() {
		return lz4AfterSize;
	}

	/**
	 * 压缩后的数据大小
	 */
	public void setLz4AfterSize(int lz4AfterSize) {
		this.lz4AfterSize = lz4AfterSize;
	}

	public byte[] toBytes() {
		return JSON.toJSONString(this).getBytes(ZookeeperConfig.charset);
	}
}
