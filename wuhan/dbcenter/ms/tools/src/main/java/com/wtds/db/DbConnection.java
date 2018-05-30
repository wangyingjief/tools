package com.wtds.db;

import java.sql.Connection;

/**
 * 连接对象
 * @author wangyingjie
 */
public class DbConnection{
	
	@SuppressWarnings("unused")
	private DbConnection(){}
	
	DbConnection(DbConnBean dbConnBean,Connection connection){
		this.dbConnBean = dbConnBean;
		this.connection = connection;
	}
	
	/**
	 * 归还连接至连接池
	 */
	public void backPool(){
		dbConnBean.setCreateTime(System.currentTimeMillis());
		dbConnBean.setStatus(true);
	}
	
	/**
	 * 连接信息
	 */
	private DbConnBean dbConnBean;
	/**
	 * 连接实体
	 */
	private Connection connection;

	public DbConnBean getDbConnBean() {
		return dbConnBean;
	}

	public void setDbConnBean(DbConnBean dbConnBean) {
		this.dbConnBean = dbConnBean;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	
}
