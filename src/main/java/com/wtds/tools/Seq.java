package com.wtds.tools;

public class Seq {
	private static long mark = 100000000000L;

	/*
	 * dcr动作数据序号 private static int dcrSeq = 0; private static String
	 * dcrSeqTable = "CRH2_RT_PRIMFAULT_OPER"; DI动作数据序号 private static int DISeq =
	 * 0; private static String DISeqTable = "CRH2_RT_PRIMFAULT_DICHANGE";
	 * 实时运行状态数据序号 private static int runInfoSeq = 18223456; private static
	 * String runInfoSeqTable = "CRH2_RT_NEWDATA"; 实时故障数据序号 private static int
	 * faultSeq = 0; private static String faultSeqTable = "CRH2_RT_PRIMFAULT";
	 * 司控室画面故障数据序号 private static int dcrPicSeq = 0; private static String
	 * dcrPicSeqTable = "CRH2_RT_CAB_FAULTINFO"; 自检信息数据序号 private static int
	 * checkSelfSeq = 0; private static String checkSelfSeqTable =
	 * "CRH2_RT_CHECKBYSELF"; 地面发送数据序号
	 * 
	 * private static int groundInfoSeq = 0; private static String
	 * groundInfoSeqTable = "";
	 */

	public static  long getUID() {
		long time = System.currentTimeMillis();
		long randomCount = Double.doubleToLongBits(Math.random())
				+ Double.doubleToLongBits(Math.random());
		long uid = (randomCount % 1000) * mark + time;
		return uid;
	}

	public static  String getUID_String() {
		long time = System.currentTimeMillis();
		long randomCount = Double.doubleToLongBits(Math.random())
				+ Double.doubleToLongBits(Math.random());
		String uid = String.valueOf(randomCount % 1000000000)
				+ String.valueOf(time).substring(2);
		return uid;
	}

	public static void setFCS(byte[] info, int start, int end) {
		for (int i = start; i < end; i++) {
			info[end] ^= info[i];
		}
	}
	/*
	 * public static synchronized int getDcrSeq() { return dcrSeq++; }
	 * 
	 * public static synchronized int getDISeq() { return DISeq++; }
	 * 
	 * public static synchronized int getRunInfoSeq() { return runInfoSeq++; }
	 * 
	 * public static synchronized int getFaultSeq() { return faultSeq++; }
	 * 
	 * public static synchronized int getDcrPicSeq() { return dcrPicSeq++; }
	 * 
	 * public static synchronized int getCheckSelfSeq() { return checkSelfSeq++; }
	 * 
	 * public static synchronized int getGroundInfoSeq() { return
	 * groundInfoSeq++; }
	 * 
	 * 用数据库中的序号初始化 public void initSeq() { Connection dbConection; ResultSet
	 * result; DBConnectionManager dbManager =
	 * DBConnectionManager.getInstance(); if (dbManager != null) { dbConection =
	 * dbManager .getConnection(dbManager.poolName, 60 * 1000); String sql =
	 * "select max(I_RECORDID) from " + runInfoSeqTable; try { Statement stmt =
	 * dbConection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
	 * ResultSet.CONCUR_READ_ONLY); stmt.execute(sql); System.out.println(sql); //
	 * stmt.setString(1, runInfoSeqTable); result = stmt.executeQuery(sql);
	 * result.absolute(1); runInfoSeq = result.getInt(1); //
	 * (result.getRow())?result.getInt(1):0;
	 * 
	 * 
	 * stmt.setString(1, dcrSeqTable); result=stmt.executeQuery(sql);
	 * dcrSeq=(result.first())?result.getInt(1):0; stmt.setString(1,
	 * DISeqTable); result=stmt.executeQuery(sql);
	 * DISeq=(result.first())?result.getInt(1):0;
	 * 
	 * stmt.setString(1, faultSeqTable); result=stmt.executeQuery(sql);
	 * faultSeq=(result.first())?result.getInt(1):0; stmt.setString(1,
	 * dcrPicSeqTable); result=stmt.executeQuery(sql);
	 * dcrPicSeq=(result.first())?result.getInt(1):0; stmt.setString(1,
	 * checkSelfSeqTable); result=stmt.executeQuery(sql);
	 * checkSelfSeq=(result.first())?result.getInt(1):0;
	 * 
	 * 
	 * stmt.setString(1, groundInfoSeqTable); result=stmt.executeQuery(sql);
	 * groundInfoSeq=(result.first())?result.getInt(1):0;
	 *  } catch (SQLException e) {
	 * 
	 * e.printStackTrace(); } } else {
	 *  } }
	 */
}
