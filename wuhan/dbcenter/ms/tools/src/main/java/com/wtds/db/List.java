package com.wtds.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wtds.tools.StringUtil;

import oracle.sql.TIMESTAMP;

/**
 * 自定义数据库操作返回集合
 * 
 * @author wyj
 */
public class List<T> extends ArrayList<T> {

	/**
	 */
	private static final long serialVersionUID = 1001L;

	/**
	 * 转为json
	 * @return json
	 */
	public String toJson() {
		return JSON.toJSONString(this);
	}

	/**
	 * 转为对象
	 * @param clazz
	 * @return List<T>
	 */
	@SuppressWarnings({ "hiding", "rawtypes", "unchecked" })
	public <T> java.util.List<T> toObject(Class clazz) {
		java.util.List list = null;
		if (this.size() > 0) {
			list = new ArrayList(this.size());
			try {
				for (int i = 0; i < this.size(); i++) {
					Object obj = clazz.newInstance();
					Map<String, Object> row = (Map<String, Object>) this.get(i);
					Map<String, Object> reRow = new HashMap<String, Object>();
					for (String key : row.keySet()) {
						reRow.put(key.replaceAll("_", ""), row.get(key));
					}
					setFieldValue(obj, reRow);
					list.add(obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * set属性的值到Bean
	 * @param bean
	 * @param valMap
	 */
	public static void setFieldValue(Object bean, Map<String, Object> valMap) {
		Class<?> cls = bean.getClass();
		// 取出bean里的所有方法
		Method[] methods = cls.getDeclaredMethods();
		Field[] fields = cls.getDeclaredFields();

		for (Field field : fields) {
			try {
				String fieldSetName = parSetName(field.getName());
				
				if (!checkSetMet(methods, fieldSetName)) {
					if("boolean".equalsIgnoreCase(field.getType().getSimpleName())) {
						if("is".equals(field.getName().substring(0,2).toLowerCase())) {
							fieldSetName = parSetName(field.getName().substring(2));
							if (!checkSetMet(methods, fieldSetName)) {
								continue;
							}
						}
					}else {
						continue;
					}
				}
				Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
				String fieldKeyName = field.getName().toLowerCase();
				Object value = valMap.get(fieldKeyName);
				if (null != value && !"".equals(value)) {
					String fieldType = field.getType().getSimpleName();
					if ("String".equals(fieldType)) {
						fieldSetMet.invoke(bean, value);
					} else if ("Date".equals(fieldType)) {
						if("oracle.sql.TIMESTAMP".equals(value.getClass().getName())){
							TIMESTAMP otamp = (TIMESTAMP)value;
							Date date = otamp.dateValue();
							fieldSetMet.invoke(bean, date);
						}else{
							Date temp = (Date) value;
							fieldSetMet.invoke(bean, temp);
						}
					} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
						if("BigDecimal".equals(value.getClass().getSimpleName())) {
							BigDecimal bd = (BigDecimal)value;
							Integer intval = bd.intValue();
							fieldSetMet.invoke(bean, intval);
						}else {
							Integer intval = (Integer) value;
							fieldSetMet.invoke(bean, intval);
						}
					} else if ("Long".equalsIgnoreCase(fieldType)) {
						if("BigDecimal".equals(value.getClass().getSimpleName())) {
							BigDecimal bd = (BigDecimal)value;
							Long intval = bd.longValue();
							fieldSetMet.invoke(bean, intval);
						}else {
							Long temp = (Long) value;
							fieldSetMet.invoke(bean, temp);
						}
					} else if ("Double".equalsIgnoreCase(fieldType)) {
						if("BigDecimal".equals(value.getClass().getSimpleName())) {
							BigDecimal bd = (BigDecimal)value;
							Double intval = bd.doubleValue();
							fieldSetMet.invoke(bean, intval);
						}else {
							Double temp = (Double) value;
							fieldSetMet.invoke(bean, temp);
						}
					} else if ("Boolean".equalsIgnoreCase(fieldType)) {
						if("BigDecimal".equals(value.getClass().getSimpleName())) {
							boolean flag = false;
							BigDecimal bd = (BigDecimal)value;
							if(bd.intValue() == 1) {
								flag = true;
							}
							fieldSetMet.invoke(bean, flag);
						}else if("String".equals(value.getClass().getSimpleName())){
							boolean flag = false;
							String temp = value + "";
							if(!StringUtil.isEmpty(temp)) {
								if("true".equals(temp.trim()) || "1".equals(temp.trim())) {
									flag = true;
								}
							}
							fieldSetMet.invoke(bean, flag);
						}
					} else if("Timestamp".equals(fieldType)){
						if("oracle.sql.TIMESTAMP".equals(value.getClass().getName())){
							TIMESTAMP otamp = (TIMESTAMP)value;
							Timestamp time = new Timestamp(otamp.longValue());
							fieldSetMet.invoke(bean, time);
						}else if("Date".equals(value.getClass().getSimpleName())) {
							Date date = (Date) value;
							Timestamp time = new Timestamp(date.getTime());
							fieldSetMet.invoke(bean, time);
						}else{
							Timestamp time = (Timestamp)value;
							fieldSetMet.invoke(bean, time);
						}
					} else if("byte[]".equals(fieldType)){
						fieldSetMet.invoke(bean, value);
					}else {
						System.out.println("not supper type:" + fieldType);
					}
				}
			} catch (Exception e) {
				System.out.println("类型转换错误:");
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * 拼接在某属性的 set方法
	 * 
	 * @param fieldName
	 * @return String
	 */
	public static String parSetName(String fieldName) {
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')
			startIndex = 1;
		return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
				+ fieldName.substring(startIndex + 1);
	}

	/**
	 * 判断是否存在某属性的 set方法
	 * 
	 * @param methods
	 * @param fieldSetMet
	 * @return boolean
	 */
	public static boolean checkSetMet(Method[] methods, String fieldSetMet) {
		for (Method met : methods) {
			if (fieldSetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 格式化string为Date
	 * 
	 * @param datestr
	 * @return date
	 */
	public static Date parseDate(String datestr) {
		if (null == datestr || "".equals(datestr)) {
			return null;
		}
		try {
			String fmtstr = null;
			if (datestr.indexOf(':') > 0) {
				fmtstr = "yyyy-MM-dd HH:mm:ss";
			} else {
				fmtstr = "yyyy-MM-dd";
			}
			SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
			return sdf.parse(datestr);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(TIMESTAMP.class.getName());
	}
}
