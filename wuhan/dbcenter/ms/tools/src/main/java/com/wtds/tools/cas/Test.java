package com.wtds.tools.cas;

public class Test {
	public static void main(String[] args) {
		/*
		 * 复杂切分
		 */
		System.out.println(ChineseAddressParser.parse("重庆市石柱县下路镇上进村锅圈组28号"));
		/*
		 * 简单切分
		 */
		System.out.println(SimpleSplitter.getCountryMap("江西省吉安市安福县洲湖镇花门村瑶溪下山2号"));
	}
}
