package com.wtds.tools.des;

import java.sql.Timestamp;

import com.wtds.tools.DateUtil;

/**
 * DES帮助类
 * @author snack
 *
 */
public class DesUtil {
	static DesEncrypterAsPassword des = new DesEncrypterAsPassword();
	/**
	 * 解密DES
	 * @param value
	 * @return
	 */
	public static String decStr(String value){
		String desStr = "";
		try {
			desStr = des.decryptStr(value);
		} catch (Exception e) {
		}
		return desStr;
	} 
	
	/**
	 * 加密DES
	 * @param value
	 * @return
	 */
	public static String encStr(String value){
		return des.encryptStr(value);
	} 
	
	/**
	 * Session解密DES
	 * @param value
	 * @return
	 */
	public static String decSession(String value){
		value = value.replaceAll("\\.", "/").replaceAll("\\*", "+").replaceAll(";", "=");
		String desStr = "";
		try {
			desStr = des.decryptStr(value);
		} catch (Exception e) {
		}
		return desStr;
	} 
	
	/**
	 * Session加密DES
	 * @param value
	 * @return
	 */
	public static String encSession(String value){
		String desStr = des.encryptStr(value);
		//desStr = desStr.replaceAll("\\.", "~DO~").replaceAll("*", "~XIN~");
		desStr = desStr.replaceAll("/", ".").replaceAll("\\+", "*").replaceAll("=", ";");
		desStr = desStr.replaceAll("\r\n","");//windows换行
		desStr = desStr.replaceAll("\n","");//linux换行
		return desStr;
	} 
	
	/**
	 * SQL解密DES
	 * @param value
	 * @return
	 */
	public static String decSql(String value){
		value = value.replaceAll("\\.", "/").replaceAll("\\*", "+").replaceAll(";", "=");
		String desStr = "";
		try {
			desStr = des.decryptStr(value);
		} catch (Exception e) {
		}
		return desStr;
	} 
	
	/**
	 * SQL加密DES
	 * @param value
	 * @return
	 */
	public static String encSql(String value){
		String desStr = des.encryptStr(value);
		//desStr = desStr.replaceAll("\\.", "~DO~").replaceAll("*", "~XIN~");
		desStr = desStr.replaceAll("/", ".").replaceAll("\\+", "*").replaceAll("=", ";");
		desStr = desStr.replaceAll("\r\n","");//windows换行
		desStr = desStr.replaceAll("\n","");//linux换行
		return desStr;
	} 
	
	
	/**
	 * 获取加密key
	 * @return
	 */
	public static String getRule(){
		String value = "";
		Timestamp time = DateUtil.getSysDateTimestamp();
		int yeas = DateUtil.getYearOfTimestamp(time);
		int minute = DateUtil.getMinuteOfTimestamp(time);
		int day = DateUtil.getDayOfTimestamp(time);
		value = (yeas%3*6)+(minute/2-8)*(day*17-minute%3)+"";
		return DesUtil.encStr(value);
	}
	public static void main(String[] args) {
//		String value = DesUtil.encSql("from NyhGoods where storeId='<@storeId@>' and storeName='_<@storeName@>_' ");
//		System.out.println(value);
//		System.out.println(DesUtil.decSql(".XEniRniWG8LUdCoi*h3fUj2vbYQ5OUK9fZG1olcstGzGjdfLox0J5nG72DOvf3VdstH7mXaoyosNUYqeoGJmwEpvkCcHmKctn6A4IsPv0Y;"));
	}
}
