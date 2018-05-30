package com.wtds.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ConcurrentHashMap;

import com.wtds.tools.UUIDUtil;

/**
 * 连接池类
 * @author wangyingjie
 */
public class DbPool {
	
	DbConfig dbConfig;
	
	/**
	 * 连接池存储对象
	 */
	ConcurrentHashMap<DbConnBean,Connection> pools = new ConcurrentHashMap<DbConnBean,Connection>();
	
	/**
	 * 获取连接
	 * @return
	 */
	public synchronized DbConnection getConnection(){
		try{
			//获取连接
			while (true) {
				
				for (DbConnBean connBean : pools.keySet()) {
					if(connBean.isStatus()){
						Connection connection = pools.get(connBean);
						//判断连接是否被关闭
						if(connection.isClosed())continue;
						
						//设置连接正在被使用
						connBean.setStatus(false);
						return new DbConnection(connBean, connection);
					}
				}
				
				//获取增量连接
				DbConnection connection = incrementConnectionn();
				if(connection != null)return connection;
				
				// 等待1秒
				Thread.sleep(1 * 1000);
			}
		
		}catch (Exception e) {
			DbLog.logger("com.wdts.db.getConnection 获取数据库连接出错!!!");
			DbLog.logger("ERROR-INFO:"+e.toString(),true);
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 创建数据库连接
	 * @param status
	 */
	private DbConnection createConn(boolean status){
		DbConnection dbConnection = null;
		try {
			DbConnBean dbConnBean = new DbConnBean(UUIDUtil.getUUID(),status,System.currentTimeMillis(),dbConfig.getUrl(),dbConfig.getName(),dbConfig.getPassword());
			Connection connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getName(),dbConfig.getPassword());
			pools.put(dbConnBean, connection);
			dbConnection = new DbConnection(dbConnBean, connection);
			//System.out.println("创建数据库连接："+dbConnBean.getId());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return dbConnection;
	}
	
	/**
	 * 添加增量连接，如果增量达到了最大连接池的设置，则返回null
	 * @return
	 */
	private DbConnection incrementConnectionn(){
		if(pools.size() < dbConfig.getMaxconn()){
			return createConn(false);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private DbPool(){
	}
	
	public DbPool(DbConfig dbConfig){
		this.dbConfig = dbConfig;
		DbLog.logger("数据库连接URL：" + dbConfig.getUrl());
		DbLog.logger("用户名：" + dbConfig.getName());
		DbLog.logger("密码：*******");
		DbLog.logger("连接池最大连接数： " + dbConfig.getMaxconn());
		DbLog.logger("连接池最小连接数：" + dbConfig.getMinconn());
		DbLog.logger("清理连接时间间隔（秒）：" + dbConfig.getOuttime());
		
		this.initDbPool(dbConfig.getUrl(), dbConfig.getName(), dbConfig.getPassword(), dbConfig.getMaxconn(), dbConfig.getMinconn());
	}
	
	/**
	 * 初始连接池
	 * @param url
	 * @param name
	 * @param password
	 * @param maxconn
	 * @param minconn
	 */
	private void initDbPool(String url, String name, String password,int maxconn,int minconn){
		try {
			for(int i = 0;i < minconn;i++)createConn(true);
			DbLog.logger(url+" 初始连接池("+pools.size()+")");
			//开启连接池监听
			new DbPoolsListen(this).start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
