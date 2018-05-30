package com.wtds.tools;

/**
 * 主键帮助类
 * @author wangyingjie
 *
 */
public class UUIDUtil {
	/**
	 * 获取32位UUID
	 * @return
	 */
	public static String getUUID() {
		long time = System.currentTimeMillis();
		long randomCount = Double.doubleToLongBits(Math.random())
				+ Double.doubleToLongBits(Math.random());
		long randomCount2 = (Double.doubleToLongBits(Math.random())
				+ Double.doubleToLongBits(Math.random())) >> 10;
		String uid = String.valueOf(randomCount+randomCount2)
				+ String.valueOf(time + Double.doubleToLongBits(Math.random()) >> 20);
		return uid;
	}
	
}
