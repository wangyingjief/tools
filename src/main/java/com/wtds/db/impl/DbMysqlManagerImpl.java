package com.wtds.db.impl;

import com.wtds.db.Db;
import com.wtds.db.DbConfig;

@SuppressWarnings("deprecation")
public class DbMysqlManagerImpl extends Db {

	@Override
	public DbConfig initConfig() {
		DbConfig config = new DbConfig();
		config.setUrl("jdbc:mysql://localhost:3306/mysql");
		config.setName("root");
		config.setPassword("root");
		
		return config;
	}

	@Override
	public String LoadDriverName() {
		return "com.mysql.jdbc.Driver";
	}

}
