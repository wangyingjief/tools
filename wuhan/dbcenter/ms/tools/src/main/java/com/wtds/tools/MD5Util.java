package com.wtds.tools;

public class MD5Util {
	
	public static String getMd5(String str){
		try {
			Md5 md5 = new Md5(str);
			byte b[] = md5.getDigest();
			return Md5.stringify(b);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
}
