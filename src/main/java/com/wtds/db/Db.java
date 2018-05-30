package com.wtds.db;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库操作抽象类
 * 如果要实现多个数据库连接操作，需要继承此类
 * @author wangyingjie
 */
public abstract class Db {
	
	DbConfig dbConfig;
	
	DbPool dbPool;
	
	/**
	 * 所有Db类的实例集合,用于控制Db单列模式
	 */
	static Map<Class<?>, Db> dbMap = new HashMap<Class<?>, Db>();
	
	/**
	 * 不建议使用此方法新建数据库操作
	 */
	@Deprecated
	public Db(){
		//初始数据库连接，加载连接池
		dbConfig = this.initConfig();
		try {
			//加载驱动
			Class.forName(this.LoadDriverName());
			//创建连接池
			dbPool = new DbPool(dbConfig);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取单列数据库操作类
	 * @param clazz 继承Db并且实现Db类的class
	 * @return Db
	 */
	public static synchronized Db instance(Class<?> clazz){
		if(dbMap.get(clazz) == null){
			try {
				Db db = (Db) clazz.newInstance();
				if(db != null) {
					dbMap.put(clazz, db);
				}
				return db;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}else{
			return dbMap.get(clazz);
		}
		return null;
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	public DbConnection getDbConn(){
		return dbPool.getConnection();
	}
	
	/**
	 * 配置信息
	 * @return
	 */
	public abstract DbConfig initConfig();
	
	/**
	 * 驱动全名称
	 * @return
	 */
	public abstract String LoadDriverName();

	public DbConfig getDbConfig() {
		return dbConfig;
	}

	public void setDbConfig(DbConfig dbConfig) {
		this.dbConfig = dbConfig;
	}

	public DbPool getDbPool() {
		return dbPool;
	}

	public void setDbPool(DbPool dbPool) {
		this.dbPool = dbPool;
	}
	
	
}
