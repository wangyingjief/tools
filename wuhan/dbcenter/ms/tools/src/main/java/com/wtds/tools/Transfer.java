package com.wtds.tools;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class Transfer {
	public static final SimpleDateFormat bartDateFormat = new SimpleDateFormat(
			"yyyyMMdd");
	public static final SimpleDateFormat crh3BartDateFormat = new SimpleDateFormat(
	"yyyyMMddHHmmss");
	public static final SimpleDateFormat crh380ABartDateFormat = new SimpleDateFormat(
	"yyyyMMddHHmmss");
	public static final SimpleDateFormat crh3BartDateFormatMIN = new SimpleDateFormat(
	"yyyyMMddHHmm");
	
	/* 小端序数组转换成int */
	public static long byteToInt(byte[] data, int offset) {
		long r = toUnsigned(data[offset + 3]);
		r = (r << 8) + toUnsigned(data[offset + 2]);
		r = (r << 8) + toUnsigned(data[offset + 1]);
		r = (r << 8) + toUnsigned(data[offset]);
		return r;
	}

	/* 小端序数组转换成short */
	public static int byteToShort(byte[] data, int offset) {
		int r = toUnsigned(data[offset + 1]);
		r = (r << 8) + toUnsigned(data[offset]);
		return r;
	}

	/* 大端序数组转换成int */
	public static long byteToIntbig(byte[] data, int offset) {
		long r = toUnsigned(data[offset++]);
		r = (r << 8) + toUnsigned(data[offset++]);
		r = (r << 8) + toUnsigned(data[offset++]);
		r = (r << 8) + toUnsigned(data[offset++]);
		return r;
	}

	/* 大端序数组转换成short */
	public static int byteToShortbig(byte[] data, int offset) {
		int r = toUnsigned(data[offset++]);
		r = (r << 8) + toUnsigned(data[offset]);
		return r;
	}

	/* BCD编码字节转化成数值 */
	public static int bcdToint(byte[] num, int offset) {
		int ten = (toUnsigned(num[offset]) / 16);
		int ge = toUnsigned(num[offset]) % 16;
		int number = ten * 10 + ge;
		if (number < 100)
			return number;
		return -1;
	}

	/* short转换成小端序数组 */
	public static byte[] ShortToByte(Short i) {
		byte[] result = new byte[2];
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

	/* int转换成小端序数组 */
	public static byte[] IntToByte(int i) {
		byte[] result = new byte[4];
		result[3] = (byte) ((i >> 24) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

	/* 将时间转化成 oracle date类型 */
	public static Timestamp getDate(int year, int month, int day, int hour,
			int min, int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, (month - 1 < 0) ? 0 : month - 1);
		cal.set(Calendar.DAY_OF_MONTH, (day == 0) ? 1 : day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, second);
		java.sql.Timestamp sqlTime = new java.sql.Timestamp(cal
				.getTimeInMillis());
		// 重新设置时间戳， 如不重设置没有时分秒信息
		sqlTime.setTime(cal.getTimeInMillis());
		return sqlTime;
	}

	/* 将时间转化成String类型 形如：20100303 */
	public static String Date2String(int year, int month, int day) {
		StringBuilder strDate = new StringBuilder();
		strDate.append(year);
		if (month < 10) {
			strDate.append(0).append(month);
		} else
			strDate.append(month);
		if (day < 10) {
			strDate.append(0).append(day);
		} else
			strDate.append(day);
		return strDate.toString();
	}
	public static String getSonDicPath(File uploadFile){ 
		String[] splitStrs=uploadFile.getName().split("_");
		return "\\"+splitStrs[2].substring(0, 6)+"\\"+splitStrs[2].substring(6, 8)+"\\"+splitStrs[0].substring((splitStrs[0].length()-4), splitStrs[0].length())+"\\";
	}
	public static String getSonDicPath(String uploadFileStr){
		String[] splitStrs=uploadFileStr.split("_");
		return "\\"+splitStrs[2].substring(0, 6)+"\\"+splitStrs[2].substring(6, 8)+"\\"+splitStrs[0].substring((splitStrs[0].length()-4), splitStrs[0].length())+"\\";
	}

	// 获取无符号byte型
	public static short toUnsigned(byte s) {
		short a;
		a = (short) (s & 0xFF);
		return a;
	}

	// 获取无符号short型
	public static int toUnsigned(short s) {
		int a;
		a = s & 0xFFFF;
		return a;
	}

	// 获取无符号int型
	public static long toUnsigned(int s) {
		long a;
		a = s & 0xFFFFFFFF;
		return a;
	}
	public  ByteBuffer Bytes2Buffer(byte[] content) {
		int i = 0;
		ByteBuffer _buffer=ByteBuffer.allocate(content.length);
		_buffer.put(content);
		_buffer.flip();
		return _buffer;
	}
	public static int get2power(int i) {
		return (i > 0) ? (1 << i) : 1;
	}
	/* 得到2的N次幂 N必须大于0*/
	public static int get2N(int N) {
		int result=1;
		return result<<N;
	}
	public static void append_int(StringBuilder strBuild,double value){
		
		strBuild.append(value).append('|');
	}
	public static void append_str(StringBuilder strBuild,double value){
		
		strBuild.append(value).append('|');
	}
    public static void append_str(StringBuilder strBuild,int value){
		
		strBuild.append(value).append('|');
	}
	public static void append_str(StringBuilder strBuild,String value){
		
		strBuild.append(value).append('|');
	}
	
}
