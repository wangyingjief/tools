package com.wtds.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.wtds.tools.UUIDUtil;

/**
 * 连接池监听类 用于监听连接池的连接数量
 * 
 * @author wangyingjie
 *
 */
class DbPoolsListen extends Thread {
	
	ConcurrentHashMap<DbConnBean,Connection> pools;
	
	DbPool dbpool;
	
	public DbPoolsListen(DbPool dbpool){
		this.dbpool = dbpool;
		this.pools = dbpool.pools;
	}
	
	
	/**
	 * 每10秒检查一次超时的连接
	 */
	private void checkOutTimePool() {
		try {
			while (true) {
				List<DbConnBean> rmKey = new ArrayList<DbConnBean>();
				Iterator<DbConnBean> it1 = pools.keySet().iterator();
				while (it1.hasNext()) {
					DbConnBean connBean = it1.next();
					long reCt = connBean.getCreateTime() + (dbpool.dbConfig.getOuttime() * 1000);
					//使用中的连接
					if(!connBean.isStatus()){
						//使用中的连接如果被关闭了，清除
						if(pools.get(connBean).isClosed())rmKey.add(connBean);
					}else if (pools.size() > dbpool.dbConfig.getMinconn() && connBean.isStatus() && reCt < System.currentTimeMillis()) {
						// 待命中的连接小于最小连接，并且超过超时时间，去除连接
						rmKey.add(connBean);
					} else {
						// 如果连接被关闭或者报错，加入去除连接集合
						if (this.isRmConn(pools.get(connBean))) {
							rmKey.add(connBean);
						}
					}
				}

				// 删除连接
				for (DbConnBean connBean : rmKey) {
					Connection conn = pools.get(connBean);
					try {
						// 删除连接同时关闭连接
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					pools.remove(connBean);
				}
				//System.out.println("杀死连接"+rmKey.size()+"个");
				// System.out.print("1==>连接池数量"+DbPool.pools.size()+" ");

				// 如果连接小于最小连接，则创建连接
				if (pools.size() < dbpool.dbConfig.getMinconn()) {
					try {
						
						int size = pools.size();
						for (int i = 0; i < dbpool.dbConfig.getMinconn() - size; i++) {
							DbConnBean dcb = new DbConnBean(UUIDUtil.getUUID(), true, System.currentTimeMillis(),
									dbpool.dbConfig.getUrl(), dbpool.dbConfig.getName(), dbpool.dbConfig.getPassword());
							Connection conn = DriverManager.getConnection(dbpool.dbConfig.getUrl(), dbpool.dbConfig.getName(),
									dbpool.dbConfig.getPassword());
							pools.put(dcb, conn);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				// 等待
				Thread.sleep(dbpool.dbConfig.getCheckTime() * 1000);
				//System.out.println("连接池数量"+pools.size());
			}

		} catch (Exception e) {
			DbLog.logger("com.wdts.db.DbPoolsListen.checkOutTimePool 连接池监听出错!!!");
			DbLog.logger("ERROR-INFO:" + e.toString(), true);
			e.printStackTrace();
			checkOutTimePool();
		}
	}

	/**
	 * 是否连接无效，或者报错
	 * 
	 * @param conn
	 * @return
	 */
	public boolean isRmConn(Connection conn) {
		try {
			if (conn.isClosed()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		DbLog.logger("开始监听连接池("+dbpool.dbConfig.getUrl()+") 监听间隔时间"+dbpool.dbConfig.getOuttime()+"s");
		checkOutTimePool();
	}

}
