package com.wtds.tools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具类
 * @author wyj
 *
 */
public class ReflectUtil {
	
	static Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
	static Map<String, Method> methodCache = new HashMap<String, Method>();
	
	/**
	 * 根据属性名称拼接set方法名称
	 * @param fieldName
	 * @return
	 */
	public static String joinSetMethodName(String fieldName) {
		return "set" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);
	}
	
	/**
	 * 根据属性名称拼接get方法名称
	 * @param fieldName
	 * @return
	 */
	public static String joinGetMethodName(String fieldName) {
		return "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);
	}
	
	/**
	 * 根据属性名称获取get
	 * @param clazz
	 * @param fieldName
	 * @param parameterTypes
	 * @return
	 */
	public static Method getMethodByName(Class<?> clazz,String fieldName,Class<?> ... parameterTypes) {
		Method m = null;
		try {
			Class<?> c = classCache.get(clazz.getName());
			if(c == null){
				classCache.put(clazz.getName(), clazz);
				Method [] ms = clazz.getDeclaredMethods();
				for(Method m1 : ms){
					methodCache.put(clazz.getName() + m1.getName(), m1);
				}
			}
			if(methodCache.get(clazz.getName() + ReflectUtil.joinGetMethodName(fieldName)) != null){
				m = clazz.getMethod(ReflectUtil.joinGetMethodName(fieldName),parameterTypes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}
	
	/**
	 * 执行方法
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 */
	public static Object invokeMethod(Object obj,Method method,Object ... args) {
		Object value = null;
		try {
			value = method.invoke(obj, args);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 根据属性名称执行get方法
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Object invokeGetMethodByFieldName(Object obj,String fieldName) {
		Object value = null;
		try {
			Method method = getMethodByName(obj.getClass(),fieldName);
			if(method != null) {
				value = method.invoke(obj);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
