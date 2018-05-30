package com.wtds.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Properties配置文件帮助类
 * @author wyj
 *
 */
public class PropertiesUtil {
	
	public static Map<String,Properties> propertiesMap = new ConcurrentHashMap<String,Properties>();
	
	public static Properties getProperties(String path){
		if(propertiesMap.get(path) == null){
			Properties p  = readProperties(path);
			return p;
		}else{
			return propertiesMap.get(path);
		}
	}
	/**
	 * 读取配置文件
	 * @param filePath
	 * @return
	 */
	public static Properties readProperties(String filePath){
		Properties config;
		if(propertiesMap.get(filePath) == null){
			config = new Properties();
			try {
				InputStream is = null;
				try {
					is = new FileInputStream(filePath);
				} catch (Exception e) {
				}
				
				if (is == null) {
					// 从jar包中读取(因为jar包中的资源不能使用文件目录获取)
					is = PropertiesUtil.class.getResourceAsStream(filePath);
					System.out.println("!!!使用默认配置文件!!!");
				}
				config.load(is);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("！！！！！！！！！！读取配置文件"+filePath+"失败！！！！！！！！！！！");
			}
			System.out.println("读取配置文件"+filePath);
		}else{
			config = propertiesMap.get(filePath);
		}
		
		return config;
	}
	
	/**
     * 更新（或插入）一对properties信息(主键及其键值) 如果该主键已经存在，更新该主键的值； 如果该主键不存在，则插件一对键值。
     *
     * @param keyname 键名
     * @param keyvalue 键值
     */
    public void writeProperties(String keyname, String keyvalue,String path) {
    	Properties config = readProperties(path);
        try {
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。   
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。   
            OutputStream fos = new FileOutputStream(path);
            config.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，   
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流   
            config.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
    }
    
    /**
     * 每次都读取
     * @param filePath
     * @return
     */
    public static Properties readEveryProperties(String filePath){
		Properties config = new Properties();
			try {
				InputStream is = null;
				try {
					is = new FileInputStream(filePath);
				} catch (Exception e) {
				}
				
				if (is == null) {
					// 从jar包中读取(因为jar包中的资源不能使用文件目录获取)
					is = PropertiesUtil.class.getResourceAsStream(filePath);
					System.out.println("!!!使用默认配置文件!!!");
				}
				config.load(is);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("！！！！！！！！！！读取配置文件"+filePath+"失败！！！！！！！！！！！");
			}
			System.out.println("读取配置文件"+filePath);
		
		return config;
	}
	
}
