package com.wtds.db;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wtds.db.annotation.DBField;
import com.wtds.db.annotation.DBTable;
import com.wtds.tools.ReflectUtil;


/**
 * Oracle 数据库操作类
 * 
 * @author wyj
 */
public class DbManager {
	private Db db;

	/**
	 * 默认实现
	 */
	public DbManager() {
		try {
			db = Db.instance(Class.forName("com.wtds.db.impl.DbOracleManagerImpl"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public DbManager(Db db){
		this.db = db;
	}
	
	/**
	 * 自定义配置
	 * @param clazz
	 */
	public DbManager(Class<?> clazz) {
		db = Db.instance(clazz);
	}
	
	/**
	 * 是否读取blob,默认不读取
	 */
	private boolean readBlob = false;

	/**
	 * 执行占位符sql
	 * 
	 * @param sql
	 * @param param <br> 支持类型 	<br>
	 * 						String<br>
	 * 						Integer<br>
	 * 						Double<br>
	 * 						Long<br>
	 * 						Boolean<br>
	 * 						java.sql.Date<br>
	 * 						java.util.Date
	 * @return
	 */
	public int execute(String sql, Object ... param) throws SQLException{
		DbConnection dbc = db.getDbConn();
		Connection connection = dbc.getConnection();
		PreparedStatement ps = null;
		int flag = 0;
		try {
			ps = connection.prepareStatement(sql);
			if (param != null && param.length > 0) {
				for (int i = 0; i < param.length; i++) {
					setParam(ps, i, param[i]);
				}
			}
			flag = ps.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			ps.close();
			dbc.backPool();
		}
		return flag;
	}
	
	public final String INT_NAME = "int";
	public final String STRING_NAME = "java.lang.String";
	public final String INTEGER_NAME = "java.lang.Integer";
	public final String DOUBLE_NAME = "java.lang.Double";
	public final String LONG_NAME = "java.lang.Long";
	public final String FLOAT_NAME = "java.lang.Float";
	public final String BOOLEAN_NAME = "java.lang.Boolean";
	public final String SQL_DATE_NAME = "java.sql.Date";
	public final String DATE_NAME = "java.util.Date";
	
	/**
	 * 根据Object类型写入ps值
	 * @param ps
	 * @param i 从 0 开始
	 * @param value
	 * @throws SQLException
	 */
	private void setParam(PreparedStatement ps,int i,Object value) throws SQLException{
		String typeName = value.getClass().getTypeName();
		if(STRING_NAME.equals(typeName)) {
			ps.setString(i + 1, value.toString());
		}else if(INTEGER_NAME.equals(typeName) || INT_NAME.equals(typeName)) {
			ps.setInt(i + 1, (Integer)value);
		}else if(DOUBLE_NAME.equalsIgnoreCase(typeName)) {
			ps.setDouble(i + 1, (Double)value);
		}else if(LONG_NAME.equalsIgnoreCase(typeName)) {
			ps.setLong(i + 1, (Long)value);
		}else if(FLOAT_NAME.equalsIgnoreCase(typeName)) {
			ps.setFloat(i + 1, (Float)value);
		}else if(BOOLEAN_NAME.equalsIgnoreCase(typeName)) {
			ps.setBoolean(i + 1, (Boolean)value);
		}else if(SQL_DATE_NAME.equals(typeName)) {
			ps.setDate(i + 1, (java.sql.Date)value);
		}else if(DATE_NAME.equals(typeName)) {
			Date date = (Date)value;
			//java.sql.Date sDate = new java.sql.Date(date.getTime());
			//ps.setDate(i + 1, sDate);
			ps.setTimestamp(i + 1, new Timestamp(date.getTime()));
		}
	}
	
	/**
	 * 查询
	 * @param t
	 * @return
	 * @throws SQLException 
	 */
	public <T> java.util.List<T> queryGenericityList(T t) throws SQLException {
		String tableName = this.getTableName(t);
		Map<String, Object> param = this.getTableValues(t);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(tableName);
		
		if(param.size() > 0) {
			sql.append(" WHERE ");
		}
		
		Object [] params = new Object[param.size()]; 

		int mapCount = param.size();
		int mapIndex = 0;
		
		for(String key : param.keySet()) {
			sql.append(key).append(" = ?");
			params[mapIndex] = param.get(key);
			if(mapIndex < (mapCount - 1)) {
				sql.append(" and ");
			}
			mapIndex++;
		}
		
		DbLog.logger(sql.toString());
		
		return this.query(sql.toString(),params).toObject(t.getClass());
	} 
	
	/**
	 * 分页查询 
	 * @param t
	 * @param page
	 * @return
	 * @throws SQLException 
	 */
	public <T> java.util.List<T> queryGenericityPage(T t,OraclePage page) throws SQLException {
		
		String tableName = this.getTableName(t);
		Map<String, Object> param = this.getTableValues(t);
		
		Object [] params = new Object[param.size()]; 
		int mapIndex = 0;
		
		//查询条件sql
		StringBuilder whereSql = new StringBuilder();
		for(String key : param.keySet()) {
			whereSql.append(" AND ").append(key).append(" = ?");
			params[mapIndex] = param.get(key);
			
			mapIndex++;
		}
		
		//分页查询sql
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM (");
			sql.append("SELECT ROWNUM rn,(SELECT COUNT(*) FROM ").append(tableName).append(") PAGE_AUTO_COUNT_243,c.* ");
			sql.append("FROM ").append(tableName).append(" c ");
			sql.append("WHERE ROWNUM <= ").append(page.getEndRow()).append(whereSql);
		sql.append(") WHERE rn > ").append(page.getStartRow());
		
		DbLog.logger(sql.toString());
		
		return this.query(sql.toString(),page,params).toObject(t.getClass());
	} 

	/**
	 * 查询
	 * @param sql
	 * @param param
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String, Object>> query(String sql, Object ... param) throws SQLException {
		return this.queryPage(sql,null,param);
	}
	
	/**
	 * 分页查询<br/>
	 * 分页查询SQL请遵守以下语句规则：<br/>
	 * <b>SELECT * FROM (SELECT ROWNUM rn,(SELECT COUNT(*) FROM SYS_APIDOC_DOCUMENT_INFO) PAGE_AUTO_COUNT_243,c.* FROM SYS_APIDOC_DOCUMENT_INFO c WHERE ROWNUM <= 10 and 1 = 1 order by rn DESC) WHERE rn > 0</b>
	 * @param sql
	 * @param page
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> queryPage(String sql,OraclePage page, Object ... param) throws SQLException {
		List<Map<String, Object>> result = null;
		DbConnection dbc = db.getDbConn();
		Connection connection = dbc.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (param != null && param.length > 0) {
				for (int i = 0; i < param.length; i++) {
					setParam(ps, i, param[i]);
				}
			}
			rs = ps.executeQuery();
			if (rs != null) {
				result = new List<Map<String, Object>>();
				ResultSetMetaData rsm = rs.getMetaData();
				while (rs.next()) {
					if(page != null) {
						int count = rs.getInt("PAGE_AUTO_COUNT_243");
						page.setTotalRow(count);
					}
					Map<String, Object> valMap = new HashMap<String, Object>(rsm.getColumnCount());
					for (int i = 1; i <= rsm.getColumnCount(); i++) {
						String colName = rsm.getColumnName(i);
						Object value = rs.getObject(colName);
						if(value != null && "BLOB".equals(value.getClass().getSimpleName())) {
							if(this.readBlob){
								Blob blob = (Blob)value;
								byte [] b = blobToBytes(blob);
								valMap.put(colName, b);
							}
						}else {
							valMap.put(colName, value);
						}
					}
					result.add(valMap);
				}
				
			}
			
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			if(rs != null){
				rs.close();
			}
			if(ps != null){
				ps.close();
			}
			dbc.backPool();
		}
		return result;
	}
	
	/**
	 * 持久化对象
	 * @param bean
	 * @return
	 */
	public int insert(Object bean) throws SQLException{
		String tableName = this.getTableName(bean);
		Map<String, Object> param = this.getTableValues(bean);
		
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(tableName);
		sql.append(" (");
		
		//sql占位符
		StringBuilder placeholder = new StringBuilder();
		Object [] params = new Object[param.size()]; 

		int mapCount = param.size();
		int mapIndex = 0;
		
		for(String key : param.keySet()) {
			sql.append(key);
			placeholder.append("?");
			params[mapIndex] = param.get(key);
			if(mapIndex < (mapCount - 1)) {
				sql.append(",");
				placeholder.append(",");
			}
			mapIndex++;
		}
		
		sql.append(") values (");
		sql.append(placeholder);
		sql.append(")");
		
		DbLog.logger(sql.toString());
		
		return this.execute(sql.toString(), params);
	}
	
	/**
	 * 修改
	 * @param bean
	 * @param whereSql 条件SQL
	 * @param whereParam
	 * @return
	 * @throws SQLException 
	 */
	public int update(Object bean,String whereSql,String ... whereParam) throws SQLException {
		String tableName = this.getTableName(bean);
		Map<String, Object> param = this.getTableValues(bean);
		
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(tableName);
		sql.append(" SET ");
		
		Object [] params = new Object[param.size()]; 

		int mapCount = param.size();
		int mapIndex = 0;
		
		for(String key : param.keySet()) {
			sql.append(key).append(" = ?");
			params[mapIndex] = param.get(key);
			if(mapIndex < (mapCount - 1)) {
				sql.append(",");
			}
			mapIndex++;
		}
		
		Object [] params2;
		if(whereParam != null) {
			sql.append(" WHERE ").append(whereSql);
			params2 = concat(params,whereParam);
		}else {
			params2 = params;
		}
		
		DbLog.logger(sql.toString());
		
		return this.execute(sql.toString(), params2);
	}
	
	/**
	 * 根据对象删除
	 * @param bean
	 * @return
	 * @throws SQLException 
	 */
	public int delete (Object bean) throws SQLException {
		String tableName = this.getTableName(bean);
		Map<String, Object> param = this.getTableValues(bean);
		
		StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		
		Object [] params = new Object[param.size()]; 

		int mapCount = param.size();
		int mapIndex = 0;
		
		for(String key : param.keySet()) {
			sql.append(key).append(" = ?");
			params[mapIndex] = param.get(key);
			if(mapIndex < (mapCount - 1)) {
				sql.append(" and ");
			}
			mapIndex++;
		}
		
		DbLog.logger(sql.toString());
		
		return this.execute(sql.toString(), params);
	}
	
	/**
	 * SQL查询总数
	 * @param sql
	 * @param param
	 * @return
	 */
	public int count(String sql,Object ... param) throws SQLException{
		int count = 0;
		DbConnection dbc = db.getDbConn();
		Connection connection = dbc.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (param != null && param.length > 0) {
				for (int i = 0; i < param.length; i++) {
					setParam(ps, i, param[i]);
				}
			}
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					count = rs.getInt(1);
					break;
				}
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally{
			if(rs != null){
				rs.close();
			}
			if(ps != null){
				ps.close();
			}
			dbc.backPool();
		}
		return count;
	}
	
	/**
	 * 查询总数
	 * @param bean
	 * @return
	 * @throws SQLException 
	 */
	public int count(Object bean) throws SQLException {
		String tableName = this.getTableName(bean);
		Map<String, Object> param = this.getTableValues(bean);
		
		StringBuilder sql = new StringBuilder("SELECT count(*) FROM ");
		sql.append(tableName);
		
		if(param.size() > 0) {
			sql.append(" WHERE ");
		}
		
		Object [] params = new Object[param.size()]; 

		int mapCount = param.size();
		int mapIndex = 0;
		
		for(String key : param.keySet()) {
			sql.append(key).append(" = ?");
			params[mapIndex] = param.get(key);
			if(mapIndex < (mapCount - 1)) {
				sql.append(" and ");
			}
			mapIndex++;
		}
		
		DbLog.logger(sql.toString());
		
		return this.count(sql.toString(),params);
	} 
	
	/**
	 * 获取对象映射的表名称<b/>
	 * 获取规则：获取类@DBTable注解中name值，如果不存在，则以类大写分割添加下划线作为表名称
	 * @param bean
	 * @return
	 */
	private String getTableName(Object bean) {
		String name = null;
		DBTable table = bean.getClass().getAnnotation(DBTable.class);
		if(table != null) {
			name = table.name();
		}else {
			name = classFieldNameToOracleFieldName(bean.getClass().getSimpleName());
		}
		return name;
	}
	
	/**
	 * 获取实体中属性名称与值，转换为Map<k,v> (k为oracle规则的字段    v为值)，当属性值为null时不会获取
	 * @param bean
	 * @return
	 */
	private Map<String, Object> getTableValues(Object bean){
		Map<String, Object> values = new HashMap<String, Object>(bean.getClass().getFields().length / 2);
		Field [] fs = bean.getClass().getDeclaredFields();
		for(int i = 0;i < fs.length;i++) {
			Field f = fs[i];
			String key = null;
			Object value = ReflectUtil.invokeGetMethodByFieldName(bean, f.getName());
			
			if(value != null) {
				DBField dbf = f.getAnnotation(DBField.class);
				if(dbf != null) {
					key = dbf.name().toLowerCase();
				}else {
					key = classFieldNameToOracleFieldName(f.getName());
				}
				values.put(key, value);
			}
		}
		
		return values;
	}
	
	/**
	 * 合并数组
	 * @param first
	 * @param rest
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] concat(T[] first,T[] ... rest) {
		int totalLength = first.length;
		for(T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for(T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
	
	/**
	 * 类名转为oracle规则名<b/>
	 * 转换规则为：大写字母前添加下划线
	 * @return
	 */
	private static String classFieldNameToOracleFieldName(String name) {
		char [] className = name.toCharArray();
		java.util.List<Integer> index = new ArrayList<Integer>();
		for (int i = 0; i < className.length; i++) {
			if(Character.isUpperCase(className[i])) {
				index.add(i);
			}
		}
		StringBuilder sb = new StringBuilder(name);
		for(int i = (index.size() - 1); i >= 0; i--) {
			if(index.get(i) != 0) {
				sb.insert(index.get(i), "_");
			}
		}
		name = sb.toString();
		return name.toLowerCase();
	}
	
	/** 
     * 将blob转化为byte[]
     * @param blob 
     * @return 
     */  
    public static byte[] blobToBytes(Blob blob) {
        InputStream is = null;  
        byte[] b = null;  
        try {
            is = blob.getBinaryStream();  
            b = new byte[(int) blob.length()];  
            is.read(b);  
            return b;
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                is.close();  
                is = null;
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return b;  
    }

    /**
     * 是否读取Blob数据
     * @return
     */
	public boolean isReadBlob() {
		return readBlob;
	}
	
	/**
	 * 设置是否读取Blob数据
	 * @param readBlob
	 */
	public void setReadBlob(boolean readBlob) {
		this.readBlob = readBlob;
	}

	public Db getDb() {
		return db;
	}

	public void setDb(Db db) {
		this.db = db;
	}
    
}
