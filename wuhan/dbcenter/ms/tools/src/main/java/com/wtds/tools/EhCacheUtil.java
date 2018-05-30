/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wtds.tools;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Cache工具类
 * @author ThinkGem
 * @version 2013-5-29
 */

public class EhCacheUtil {
	
	private static String configPath = "";
	
	private static Map<String, CacheManager> cacheManagerMap = new HashMap<String, CacheManager>();
	
	private CacheManager cacheManager;
	
	private String CACHE_NAME = "sysCache";
	
	/**
	 * 默认ehcache.xml配置
	 */
	public EhCacheUtil(){
		configPath = System.getProperty("user.dir") + "/config/ehcache.xml";
		getCacheManager(configPath);
	}
	
	public EhCacheUtil(String configPath){
		getCacheManager(configPath);
	}
	
	private void getCacheManager(String configPath){
		if(cacheManagerMap.get(configPath) == null){
			cacheManager = CacheManager.newInstance(configPath);
			cacheManagerMap.put(configPath, cacheManager);
		}else{
			cacheManager = cacheManagerMap.get(configPath);
		}
	}
	
	/**
	 * 设置cache名称
	 * @param name
	 */
	public void setCacheName(String name){
		this.CACHE_NAME = name;
	}

	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return get(CACHE_NAME, key);
	}
	
	/**
	 * 写入缓存
	 * @param key
	 * @return
	 */
	public void put(String key, Object value) {
		put(CACHE_NAME, key, value);
	}
	
	/**
	 * 移除缓存
	 * @param key
	 * @return
	 */
	public void remove(String key) {
		remove(CACHE_NAME, key);
	}
	
	/**
	 * 获取缓存
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public Object get(String cacheName, String key) {
		Element element = getCache(cacheName).get(key);
		return element==null?null:element.getObjectValue();
	}

	/**
	 * 写入缓存
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cacheName).put(element);
	}

	/**
	 * 从缓存中移除
	 * @param cacheName
	 * @param key
	 */
	public void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}
	
	/**
	 * 获得一个Cache，没有则创建一个。
	 * @param cacheName
	 * @return
	 */
	private Cache getCache(String cacheName){
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null){
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(true);
		}
		return cache;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}
	
}
