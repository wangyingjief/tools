package com.wtds.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wtds.db.impl.DbOracleManagerImpl;

/**
 * 连接池测试类
 * @author wangyingjie
 */
public class DbTest {
	public static void main(String[] args) throws Exception {
		//dbTest();
		System.out.println(System.currentTimeMillis());
	}
	
	public static void dbTest() throws SQLException {

//		Db db = Db.instance(DbOracleManagerImpl.class);
//		
//		DbConnection dbc = db.getDbConn();
//		Connection connection = dbc.getConnection();
//		
//		Statement s = connection.createStatement();
//		ResultSet  rs = s.executeQuery("select * from CRH_ROUT_INFO");
//		if(rs != null){
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			while(rs.next()){
//				String r1 = rs.getString(1);
//				String r2 = rs.getString(2);
//				Date r3 = rs.getDate(3);
//				
//				System.out.println(formatter.format(r3));
//			}
//		}
//		dbc.backPool();
		//System.out.println(db.getDbConn().getConnection());
		
//		Db db2 = new DbMysqlManagerImpl();
//		System.err.println(db2.getDbConn().getConnection());
		
		//insert into mysql.user(host,user,password) values('localhost','deepin','deepin')
		TestBean t = new TestBean();
//		t.setUuid("1");
//		t.setAddress("新建2");
//		t.setCreateTime(new java.util.Date());
//		t.setProjectName("fff2");
		DbManager dbManager = new DbManager();
		
		OraclePage page = new OraclePage(5,2);
		
		List<TestBean> list = dbManager.queryGenericityPage(t, page);
		System.out.println(page.getTotalRow());
		for(TestBean bean : list) {
			System.out.println(bean.getUuid());
		}
		//插入
//		System.out.println(dbManager.insert(t));
		//查询列表
//		List<TestBean> list = dbManager.query(t);
		//查询总数
//		System.out.println(dbManager.count(t));
//		t.setAddress("修改");
		//修改
//		System.out.println(dbManager.update(t, "UUID = ?", "fff"));
		//删除
//		System.out.println(dbManager.delete(t));
//		dbManager.setReadBlob(true);
//		List<TestBean> r = dbManager.query("select * from T_SDR where uuid=?","a8785145ef744e85bc895bbea0a60c4e").toObject(TestBean.class);
//		System.out.println(r);
		
	}
}
