package com.wtds.db.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.wtds.db.Db;
import com.wtds.db.DbConfig;
import com.wtds.db.DbLog;

/**
 * @author wyj
 *
 */
@SuppressWarnings("deprecation")
public class DbOracleManagerImpl extends Db{

	@Override
	public DbConfig initConfig() {
		DbConfig config = new DbConfig();
		Properties properties = new Properties();
			try {
				InputStream is = null;
				try {
					is = new FileInputStream(System.getProperty("user.dir") + "/config/db.properties");
				} catch (Exception e) {
				}

				if (is == null) {
					// 从jar包中读取(因为jar包中的资源不能使用文件目录获取)
					is = this.getClass().getResourceAsStream("/config/db.properties");
					DbLog.logger("!!!连接数据库使用默认配置文件!!!");
				}

				properties.load(is);
				
				config.setUrl(properties.get("URL").toString());
				config.setName(properties.get("NAME").toString());
				config.setPassword(properties.get("PASSWORD").toString());
				config.setMaxconn(Integer.parseInt(properties.get("MAXCONN").toString()));
				config.setMinconn(Integer.parseInt(properties.get("MINCONN").toString()));
				config.setOuttime(Integer.parseInt(properties.get("OUTTIME").toString()));
				
			} catch (Exception e) {
				e.printStackTrace();
				DbLog.logger("！！！！！！！！！！读取配置文件db.properties失败！！！！！！！！！！！");
			}
		
		return config;
	}

	@Override
	public String LoadDriverName() {
		return "oracle.jdbc.driver.OracleDriver";
	}
	
}
