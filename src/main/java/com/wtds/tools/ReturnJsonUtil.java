package com.wtds.tools;

import com.alibaba.fastjson.JSON;

/**
 * 用于返回json数据帮助类
 * @author joymting
 */
public class ReturnJsonUtil {
	/**
	 * 返回json
	 * @param code
	 * @param msg
	 * @return
	 */
	public static String toJson(Code code,String msg){
		
		return JSON.toJSONString(new ReturnJsonBean(code.toString(), msg, null));
	}
	
	/**
	 * 返回json
	 * @param code
	 * @param msg
	 * @param data
	 * @return
	 */
	public static String toJson(Code code,String msg,String data){
		return JSON.toJSONString(new ReturnJsonBean(code.toString(), msg, data));
	}
	
	/**
	 * 返回json
	 * @param code
	 * @param msg
	 * @param obj
	 * @return
	 */
	public static String toJson(Code code,String msg,Object obj){
		return JSON.toJSONString(new ReturnJsonBean(code.toString(), msg, obj));
	}
	
	/**
	 * 直接输入对象
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){
		return JSON.toJSONString(obj);
	}
	
	public static String toJsonString(Object obj){
		return JSON.toJSONString(obj);
	}
	
}
