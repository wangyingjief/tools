package com.wtds.tools;
/**
 * 16进制帮助类
 * @author wyj
 */
public class HexUtil {
	// 将指定byte数组以16进制的形式打印到控制台
	public static String hexString(byte[] b) {
		String result = "";
		int ln = 1;
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result += " " + hex.toUpperCase();
			if (ln == 8) {
				result += "[" + (i - 7) + "~" + (i) + "]\r\n";
				ln = 0;
			}
			ln++;
		}
		return result;
	}
}
