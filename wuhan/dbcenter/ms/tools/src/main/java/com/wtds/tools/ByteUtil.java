package com.wtds.tools;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 二进制转换工具类
 * <ul>
 * <li>将二进制转换成八进制</li>
 * <li>将二进制转换成十进制</li>
 * <li>将二进制转换成十六进制</li>
 * <li>将十进制转换成二进制</li>
 * </ul>
 * 
 * @author InJavaWeTrust
 */
public class ByteUtil {
	
	/**
	 * 将二进制整数部分转换成十进制
	 * @param inteter 二进制整数部分字符串
	 * @return 转换后的十进制数值
	 */
	public static int binaryIntToDecimalism(String inteter) {
		int inteterSum = 0;
		for (int i = inteter.length(); i > 0; i--) {
			int scale = 2;
			if (inteter.charAt(-(i - inteter.length())) == '1') {
				if (i != 1) {
					for (int j = 1; j < i - 1; j++) {
						scale *= 2;
					}
				} else {
					scale = 1;
				}
			} else {
				scale = 0;
			}
			inteterSum += scale;
		}
		return inteterSum;
	}
	
	/**
	 * 将二进制小数部分转换成十进制
	 * @param decimals 二进制小数部分字符串
	 * @return 转换后的十进制数值
	 */
	public static double binaryDecToDecimalism(String decimals) {
		double decimalsSum = 0f;
		for (int i = 0; i < decimals.length(); i++) {
			double scale = 2;
			if (decimals.charAt(i) == '1') {
				if (i == 0) {
					scale = 1 / scale;
				} else {
					for (int j = 1; j <= i; j++) {
						scale *= 2;
					}
					scale = 1 / scale;
				}
			} else {
				scale = 0;
			}
			decimalsSum += scale;
		}
		return decimalsSum;
	}

	/**
	 * 将二进制转换成十进制
	 * @param binary 二进制字符串
	 * @return 转换后的十进制字符串
	 */
	public static String binaryToDecimalism(String binary) {
		String sum = "";
		String integer = "";     // 整数部分
		String decimals = "";    // 小数部分
		int integerSum = 0;      // 整数部分和
		double decimalsSum = 0d; // 小数部分和
		if (ByteUtil.isBinary(binary)) {
			if (ByteUtil.isContainsPoint(binary)) {
				integer = binary.substring(0, binary.indexOf("."));
				decimals = binary.substring(binary.indexOf(".") + 1,
						binary.length());
				integerSum = ByteUtil.binaryIntToDecimalism(integer);
				decimalsSum = ByteUtil.binaryDecToDecimalism(decimals);
				sum = String.valueOf(integerSum + decimalsSum);
			} else {
				integerSum = ByteUtil.binaryIntToDecimalism(binary);
				sum = String.valueOf(integerSum);
			}
		} else {
			try {
				throw new Exception(binary + " Illegal binary!");
			} catch (Exception be) {
				System.out.println(be.getMessage());
			}
		}
		return sum;
	}
	
	/**
	 * 将二进制整数部分转换成八进制
	 * @param integer 二进制字符串
	 * @return 转换后的八进制字符串
	 */
	public static String binaryIntToOctal(String integer) {
		StringBuilder integerSum = new StringBuilder();
		int loop = 0; // 循环次数
		if (integer.length() % 3 == 0) {
			loop = integer.length() / 3;
		} else {
			loop = integer.length() / 3 + 1;
		}
		String binary = "";
		for (int i = 1; i <= loop; i++) {
			if (i != loop) {
				binary = integer.substring(integer.length() - i * 3,
						integer.length() - i * 3 + 3);
			} else {
				binary = ByteUtil.appendZero(
						integer.substring(0, integer.length() - (i - 1) * 3),
						3, true);
			}
			integerSum.append(ByteUtil.binaryIntToDecimalism(binary));
		}
		return integerSum.reverse().toString();
	}
	
	/**
	 * 将二进制小数部分转换成八进制
	 * @param xs 二进制字符串
	 * @return 转换后的八进制字符串
	 */
	public static String binaryDecToOctal(String decimals) {
		StringBuilder decimalsSum = new StringBuilder();
		int loop = 0; // 循环次数
		if (decimals.length() % 3 == 0) {
			loop = decimals.length() / 3;
		} else {
			loop = decimals.length() / 3 + 1;
		}
		String binary = "";
		for (int i = 1; i <= loop; i++) {
			if (i != loop) {
				binary = decimals.substring(3 * (i - 1), 3 * (i - 1) + 3);
			} else {
				binary = ByteUtil.appendZero(decimals.substring(3 * (i - 1)),
						3, false);
			}
			decimalsSum.append(ByteUtil.binaryIntToDecimalism(binary));
		}
		return decimalsSum.toString();
	}
	
	/**
	 * 将二进制转换成八进制
	 * @param binary 二进制字符串
	 * @return 转换后的八进制字符串
	 */
	public static String binaryToOctal(String binary) {
		String integer = "";
		String point = "";
		String decimals = "";
		String integerSum = "";
		String decimalsSum = "";
		if (ByteUtil.isBinary(binary)) {
			if (ByteUtil.isContainsPoint(binary)) {
				integer = binary.substring(0, binary.indexOf("."));
				point = ".";
				decimals = binary.substring(binary.indexOf(".") + 1,
						binary.length());
				integerSum = ByteUtil.binaryIntToOctal(integer);
				decimalsSum = ByteUtil.binaryDecToOctal(decimals);
			} else {
				integerSum = ByteUtil.binaryIntToOctal(binary);
			}
		} else {
			try {
				throw new Exception(binary + " Illegal binary!");
			} catch (Exception be) {
				System.out.println(be.getMessage());
			}
		}
		StringBuilder sum = new StringBuilder();
		sum = sum.append(integerSum).append(point).append(decimalsSum);
		return sum.toString();
	}
	
	/**
	 * 将二进制整数部分转换成十六进制
	 * @param integer 二进制整数部分字符串
	 * @return 转换后的十六进制字符串
	 */
	public static String binaryIntToHexadecimal(String integer) {
		StringBuffer integerSum = new StringBuffer();
		int loop = 0; // 循环次数
		if (integer.length() % 4 == 0) {
			loop = integer.length() / 4;
		} else {
			loop = integer.length() / 4 + 1;
		}
		String binary = "";
		for (int i = 1; i <= loop; i++) {
			if (i != loop) {
				binary = integer.substring(integer.length() - i * 4,
						integer.length() - i * 4 + 4);
			} else {
				binary = ByteUtil.appendZero(
						integer.substring(0, integer.length() - (i - 1) * 4),
						4, true);
			}
			integerSum.append(ByteUtil.toHex(String.valueOf(ByteUtil
					.binaryIntToDecimalism(binary))));
		}
		return integerSum.reverse().toString();
	}
	
	/**
	 * 将二进制小数部分转换成十六进制
	 * @param xs 二进制字符串
	 * @return 转换后的十六进制字符串
	 */
	public static String binaryDecToHexadecimal(String decimals) {
		StringBuffer decimalsSum = new StringBuffer();
		int loop = 0;
		if (decimals.length() % 3 == 0) {
			loop = decimals.length() / 3;
		} else {
			loop = decimals.length() / 3 + 1;
		}
		String binary = "";
		for (int i = 1; i <= loop; i++) {
			if (i != loop) {
				binary = decimals.substring(4 * (i - 1), 4 * (i - 1) + 4);
			} else {
				binary = ByteUtil.appendZero(decimals.substring(4 * (i - 1)),
						4, false);
			}
			decimalsSum.append(ByteUtil.toHex(String.valueOf(ByteUtil
					.binaryIntToDecimalism(binary))));
		}
		return decimalsSum.toString();
	}
	
	/**
	 * 将二进制转换成十六进制
	 * @param binary 二进制字符串
	 * @return 转换后的十六进制字符串
	 */
	public static String binaryToHexadecimal(String binary) {
		String integer = "";
		String point = "";
		String decimals = "";
		String integerSum = "";
		String decimalsSum = "";
		if (ByteUtil.isBinary(binary)) {
			if (ByteUtil.isContainsPoint(binary)) {
				integer = binary.substring(0, binary.indexOf("."));
				point = ".";
				decimals = binary.substring(binary.indexOf(".") + 1,
						binary.length());
				integerSum = ByteUtil.binaryIntToHexadecimal(integer);
				decimalsSum = ByteUtil.binaryDecToHexadecimal(decimals);
			} else {
				integerSum = ByteUtil.binaryIntToHexadecimal(binary);
			}
		} else {
			try {
				throw new Exception(binary + " Illegal binary!");
			} catch (Exception be) {
				System.out.println(be.getMessage());
			}
		}
		StringBuilder sum = new StringBuilder();
		sum = sum.append(integerSum).append(point).append(decimalsSum);
		return sum.toString();
	}
	
	/**
	 * 将十进制整数部分转换成二进制
	 * @param integer 十进制整数部分
	 * @return 转换后的二进制
	 */
	public static String decimalismIntToBinary(String integer) {
		return Integer.toBinaryString(Integer.parseInt(integer)).toString();
	}
	
	/**
	 * 将十进制小数部分转换成二进制
	 * @param sxs 十进制整小数部分
	 * @return 转换后的二进制
	 */
	public static String decimalismDecToBinary(String decimals) {
		String pre = "0.";
		String all = pre + decimals;
		String sum = "";
		double dou = Double.parseDouble(all);
		while (!String.valueOf(dou).equals("0.0")) {
			dou = dou * 2;
			sum += String.valueOf(dou).substring(0,
					String.valueOf(dou).indexOf("."));
			dou = Double.parseDouble("0."
					+ String.valueOf(dou).substring(
							String.valueOf(dou).indexOf(".") + 1));
		}
		return sum;
	}
	
	/**
	 * 将十进制转换成二进制
	 * @param decimalism 十进制数字符串
	 * @return 转换后的二进制数字符串
	 */
	public static String decimalismToBinary(String decimalism) {
		String binary = "";
		String point = "";
		String integer = "";
		String decimals = "";
		if (ByteUtil.isNumber(decimalism)) {
			if (ByteUtil.isContainsPoint(decimalism)) {
				integer = decimalism.substring(0, decimalism.indexOf("."));
				integer = ByteUtil.decimalismIntToBinary(integer);
				point = ".";
				decimals = decimalism.substring(decimalism.indexOf(".") + 1);
				decimals = ByteUtil.decimalismDecToBinary(decimals);
			} else {
				integer = ByteUtil.decimalismIntToBinary(decimalism);
			}
		} else {
			try {
				throw new Exception(decimalism
						+ " Illegal number!");
			} catch (Exception be) {
				System.out.println(be.getMessage());
			}
		}
		binary = integer + point + decimals;
		return binary;
	}
	
	/**
	 * 将10~15转换成A~F
	 * @param binary 十六进制字符串
	 * @return 转换后的十六进制数值
	 */
	public static String toHex(String hex) {
		String str = "";
		switch(Integer.parseInt(hex)){
		case 10 : str = "A"; break;
		case 11 : str = "B"; break;
		case 12 : str = "C"; break;
		case 13 : str = "D"; break;
		case 14 : str = "E"; break;
		case 15 : str = "F"; break;
		default : str = hex;
		}
		return str;
	}
	
	/**
	 * 根据补位标志将源字符串补位到指定长度
	 * @param str 源字符串
	 * @param len 补位到指定长度
	 * @param flag 补位标志 true-左补;false-右补
	 * @return 补位后的字符串
	 */
	public static String appendZero(String str, int len, boolean flag) {
		String zero = "0";
		if (null == str || str.length() == 0) {
			return "";
		}
		if (str.length() >= len) {
			return str;
		}
		for (int i = str.length(); i < len; i++) {
			if (flag) {
				str = zero + str;
			} else {
				str += zero;
			}
		}
		return str;
	}
	
	/**
	 * 是否合法二进制字符串
	 * @param binary 二进制字符串
	 * @return true-合法;false-不合法
	 */
	public static boolean isBinary(String binary) {
		boolean flag = true;
		if (binary.contains(".")) {
			if (binary.lastIndexOf(".") + 1 == binary.length()) {
				return false;
			} else if (binary.indexOf(".") == 0) {
				return false;
			}
			char[] c = binary.toCharArray();
			int sum = 0;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '.') {
					sum += 1;
				} else {
					if (c[i] != '0' && c[i] != '1') {
						return false;
					}
				}
				if (sum > 1) {
					return false;
				}
			}
		} else {
			char[] c = binary.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (c[i] != '0' && c[i] != '1') {
					return false;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 是否包含小数点
	 * @param number 字符串
	 * @return true-包含;false-不包含
	 */
	public static boolean isContainsPoint(String number) {
		return number.contains(".") ? true : false;
	}
	
	/**
	 * 判断是否数字
	 * @param number 要判断的数字 
	 * @return true-数字;false-非数字
	 */
	public static boolean isOToN(String number) {
		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(number);
		return m.matches();
	}
	
	/**
	 * 判断是否是一个合法的数字
	 * @param number 要判断是数字
	 * @return true-合法数字;false-非法数字
	 */
	public static boolean isNumber(String number) {
		boolean flag = true;
		if (number.contains(".")) {
			if (number.lastIndexOf(".") + 1 == number.length()) {
				return false;
			} else if (number.indexOf(".") == 0) {
				return false;
			}
			char[] c = number.toCharArray();
			int sum = 0;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '.') {
					sum += 1;
				} else {
					if (!ByteUtil.isOToN(String.valueOf(c[i]))) {
						return false;
					}
				}
				if (sum > 1) {
					return false;
				}
			}
		} else {
			char[] c = number.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (!ByteUtil.isOToN(String.valueOf(c[i]))) {
					return false;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 字节转为二进制字符
	 * @param b
	 * @return
	 */
	public static String byteToBinStr(byte b){
		StringBuffer result = new StringBuffer();  
		return result.append(Long.toString(b & 0xff, 2)).toString();  		
	}
	
	/**
	 * 字节转十六进制字符
	 * @param src
	 * @return
	 */
	public static String bytesToHexStr(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	} 
	
	// 将指定byte数组以16进制的形式打印到控制台
	public static void printHexString(byte[] b) {
		int ln = 1;
		System.out.println("");
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			
			System.out.print(" "+hex.toUpperCase());
			if(ln==8){
				System.out.println("["+(i-7)+"~"+(i)+"]");
				ln = 0;
			}
			ln++;
		}
		System.out.println("");
	}
	
	/** int -> byte[] */  
    public static byte[] intToBytes(int num) {  
       byte[] b = new byte[4];  
       for (int i = 0; i < 4; i++) {  
        b[i] = (byte) (num >>> (24 - i * 8));  
       }  
        
       return b;  
    }
	
	/**
	 * 拼接 byte []
	 * @param first
	 * @param rest
	 * @return
	 */
	public static byte[] concatAll(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * 分割byte[](与joinByte组合使用)
	 * @param data 需要被分割的data
	 * @param splitSize 分割为多大一个
	 * @return
	 */
	public static List<byte[]> splitByte(byte[] data, int splitSize) {
		List<byte[]> list = new ArrayList<byte[]>();
		int splitCount = data.length / splitSize + 1;
		ByteBuffer bb = ByteBuffer.wrap(data);
		for (int i = 0; i < splitCount; i++) {
			int len = splitSize;
			byte[] rb = new byte[len];
			if ((bb.limit() - bb.position()) < splitSize) {
				len = bb.limit() - bb.position();
			}
			bb.get(rb, 0, len);
			list.add(rb);
		}
		return list;
	}
	
	/**
	 * 组合byte[](与splitByte组合使用)
	 * @param list
	 * @return
	 */
	public static byte[] joinByte(List<byte[]> list) {
		int len = list.size() > 1 
				? (list.get(0).length * list.size() - 1) + (list.get(list.size() - 1).length)
				: list.get(0).length;
		byte[] b = new byte[len];
		ByteBuffer bb = ByteBuffer.wrap(b);
		for(byte [] sub : list) {
			bb.put(sub);
		}
		return b;
	}
	
	public static void main(String[] args) throws Exception {
//		String s = "";
//		byte[] t = s.getBytes();
//		List<byte[]> list = splitByte(t, 986);
//		byte [] g = joinByte(list);
//		System.out.println(new String(g));
		
		
		String binary = "110011";
		System.out.println(ByteUtil.binaryToDecimalism(binary));
		System.out.println(ByteUtil.binaryToOctal(binary));
		System.out.println(ByteUtil.binaryToHexadecimal(binary));
		String integer = "51";
		System.out.println(ByteUtil.decimalismToBinary(integer));
		
		String bin = "101011.101";
		System.out.println(ByteUtil.binaryToDecimalism(bin));
		System.out.println(ByteUtil.binaryToOctal(bin));
		System.out.println(ByteUtil.binaryToHexadecimal(bin));
		String inte = "43.625";
		System.out.println(ByteUtil.decimalismToBinary(inte));
	}
}
