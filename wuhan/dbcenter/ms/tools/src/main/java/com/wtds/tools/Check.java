package com.wtds.tools;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Check {
	private static final ArrayList<Integer> Fault_Code = new ArrayList<Integer>(10);;

	Check() {
		Fault_Code.add(Integer.valueOf("2F2B", 16));
		Fault_Code.add(Integer.valueOf(0));
	}

	/* Fcs检验 start为数组初始游标，end为结束游标 checksum为content[end+1] */
	public static boolean fcsCheck(byte[] content, byte checkSum, int start, int end) {
		byte tempSum = 0;
		while (start <= end) {
			tempSum ^= content[start++];
		}
		return ((tempSum ^ checkSum) == 0) ? true : false;

	}

	/* 检查数据是否复合协议要求 各车型长度要求不同 */
	public static boolean dataCheck(byte[] data) {
		try {

			if ((data.length > 21) && (data[15 + Transfer.byteToShort(data, 13) + 2 - 1] == (byte) 0xBA) ? true
					: false) {
				switch (data[11]) {
				case 0x01:
					return crh1Datacheck(data[14 + 7], data);
				case 0x02:
					return crh2Datacheck(data[14 + 7], data);
				case 0x03:
					return crh3Datacheck(data[14 + 7], data);
				case 0x05:
					return crh5Datacheck(data[14 + 7], data);
				}
			}
			return false;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private static boolean crh1Datacheck(byte infoType, byte[] data) {
		return false;
	}

	public static boolean bufferCheck(ByteBuffer buffer, byte flag) {
		if (buffer.hasRemaining()) {
			return (buffer.get() == flag) ? true : false;
		} else
			return false;
	}

	public static boolean byteCheck(byte[] bytes, int offset, byte flag) {
		if (bytes.length > offset) {
			return (bytes[offset] == flag) ? true : false;
		} else
			return false;
	}

	// 文件移动
	public static void backupFile(File messageFile, File backupDirectory, String sonDirectory) {
		String backupPath = backupDirectory.getAbsolutePath() + sonDirectory;
		File backupDicSon = new File(backupPath);
		if (backupDicSon.exists() != true)
			backupDicSon.mkdirs();
		File backFile = new File(backupPath + messageFile.getName());
		if (backFile.exists())
			backFile.delete();
		if (backFile != null)
			messageFile.renameTo(backFile);
		messageFile.delete();
	}

	/*
	 * 0x31主故障数据不定长，0x32 实时运行数据 共15+28+20+207+1+3个字节，0x33 实时运行数据
	 * 共15+28+20+227+1+3个字节
	 */
	private static boolean crh2Datacheck(byte infoType, byte[] data) {
		try {
			switch (infoType) {
			case 0x31:
				return (data.length == 15 + 28 + 158 + 500 + 20 + 1 + 3) ? true : false;
			case 0x32:
				return (data.length == 15 + 28 + 207 + 20 + 1 + 3) ? true : false;
			case 0x33:
				return (data.length == 15 + 28 + 227 + 20 + 1 + 3) ? true : false;
			case 0x34:
				return (data.length == 15 + 28 + 25 + 20 + 1 + 3) ? true : false;
			case 0x35:
				return false;
			case 0x36:
				return (data.length == 15 + 28 + 100 + 20 + 1 + 3) ? true : false;
			case 0x37:
				return (data.length == 15 + 28 + 44 + 20 + 1 + 3) ? true : false;
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private static boolean crh3Datacheck(byte infoType, byte[] data) {
		return true;
	}

	private static boolean crh5Datacheck(byte infoType, byte[] data) {
		return false;
	}

	public static boolean checkFaultCode(int sourceCode) {
		for (int i = 0; i < Fault_Code.size(); i++) {
			if (Fault_Code.get(i).compareTo(sourceCode) == 0)
				return false;
		}
		return true;
	}

	public static boolean checkGPSInfo(double Latitude, double Longitude) {

		if (Latitude < 0.1)
			return false;
		if (Longitude < 0.1)
			return false;
		return true;

	}
	/**
	 * 检查内容是否为IP地址
	 * @param addr
	 * @return
	 */
	public static boolean isIP(String addr) {
		if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		Pattern pat = Pattern.compile(rexp);

		Matcher mat = pat.matcher(addr);

		boolean ipAddress = mat.find();

		return ipAddress;
	}

}
