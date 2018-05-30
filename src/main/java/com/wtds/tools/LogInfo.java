package com.wtds.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogInfo {
	private static PrintWriter log = null;
	static private LogInfo logInfo;
	private static String logFile;
	private final static String recordPath = "D:\\CRH380C\\";
	private final static String recordPathtemp = "E:\\crh3\\";
	private final static String recordPath380BL = "E:\\DCLD\\";
	private static String tempfilename;
	private static long nowTime = System.currentTimeMillis();
	private static long logtime = 30 * 60 * 1000;
	private static SimpleDateFormat formatDate = new SimpleDateFormat(
			"yyyyMMdd");
	static// private static long logtime=10*60*1000;
	FileOutputStream fileStream;

	private LogInfo() {
	}

	private static void init() {
		try {
			
			logFile = System.getProperty("user.dir") + "/loggin.log";
			
			log = new PrintWriter(new FileWriter(logFile, true), true);
		} catch (IOException e) {
			System.err.println("无法打开日志文件: " + logFile);
			// log = new PrintWriter(System.err);
		}
	}

	/* 将文本信息写入日志文件 */

	public static void log(String msg) {
		if (log != null) {
			log.println(new Date() + ": " + msg);
			log.close();
		} else {
			init();
			log.println(new Date() + ": " + msg);
			log.close();
		}

	}

	/* 将文本信息与异常写入日志文件 */
	public static void log(Throwable e, String msg) {
		if (log != null) {
			log.println(new Date() + ": " + msg);
			log.close();
		} else {
			init();
			log.println(new Date() + ": " + msg);
			e.printStackTrace(log);
			log.close();
		}
	}

	public static void record2Disk(byte[] newInfobyte) {
		try {
			FileOutputStream fileStream = new FileOutputStream(new File(
					recordPath + String.valueOf(Seq.getUID())));
			fileStream.write(newInfobyte);
			fileStream.close();
		} catch (IOException e) {
			System.err.println("无法打开日志文件: " + logFile);
			e.printStackTrace();
			// log = new PrintWriter(System.err);
		}
	}

	public static void record2DiskTemp(byte[] newInfobyte) {
		try {
			FileOutputStream fileStream = new FileOutputStream(new File(
					recordPathtemp + String.valueOf(Seq.getUID())));
			fileStream.write(newInfobyte);
			fileStream.close();
		} catch (IOException e) {
			System.err.println("无法打开日志文件: " + logFile);
			e.printStackTrace();
			// log = new PrintWriter(System.err);
		}
	}

	/* 记录到硬盘 */
	public static void record2DiskA(byte[] newInfobyte) {

		try {
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(
					"yyyy_MM_dd_HH_mm");
			tempfilename = bartDateFormat.format(new Date(System
					.currentTimeMillis()));
			String filepath = recordPath + tempfilename + "_"
					+ String.valueOf(newInfobyte.length);
			File newfile = new File(filepath);
			if (newfile.exists()) {
				fileStream = new FileOutputStream(newfile, true);
				fileStream.write(newInfobyte);
				fileStream.flush();
				fileStream.close();
			} else if (newfile.createNewFile()) {
				fileStream = new FileOutputStream(newfile, true);
				fileStream.write(newInfobyte);
				fileStream.flush();
				fileStream.close();
			}
		} catch (Exception e) {
			System.err.println("无法打开日志文件: " + logFile);
			e.printStackTrace();
			try {
				fileStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/* 记录到硬盘 */
	public static void record2DiskALL(ArrayList<byte[]> dataArray) {

		try {
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(
					"yyyy_MM_dd_HH_mm");
			for (int i = 0; i < dataArray.size(); i++) {
				tempfilename = bartDateFormat.format(new Date(System
						.currentTimeMillis()));
				String filepath = recordPath + tempfilename + "_"
						+ String.valueOf(dataArray.get(i).length);
				File newfile = new File(filepath);
				if (newfile.exists()) {
					fileStream = new FileOutputStream(newfile, true);
					fileStream.write(dataArray.get(i));
					fileStream.flush();
					fileStream.close();
				} else if (newfile.createNewFile()) {
					fileStream = new FileOutputStream(newfile, true);
					fileStream.write(dataArray.get(i));
					fileStream.flush();
					fileStream.close();
				}
			}

		} catch (Exception e) {
			System.err.println("无法打开日志文件: " + logFile);
			e.printStackTrace();
			try {
				fileStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/* 记录到硬盘 */
	public static void record2DiskALL_380C(ArrayList<byte[]> dataArray) {

		try {
			String backUpPath = "D:\\CRH380C\\";
			String subPath = formatDate.format(new Date()) + "\\";
			File backPath = new File(backUpPath + subPath);
			if (backPath.exists() != true)
				backPath.mkdirs();
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(
					"yyyy_MM_dd_HH_mm");
			for (int i = 0; i < dataArray.size(); i++) {
				tempfilename = bartDateFormat.format(new Date(System
						.currentTimeMillis()));
				String filepath = backUpPath + subPath + tempfilename + "_"
						+ String.valueOf(dataArray.get(i).length);
				File newfile = new File(filepath);
				if (newfile.exists()) {
					fileStream = new FileOutputStream(newfile, true);
					fileStream.write(dataArray.get(i));
					fileStream.flush();
					fileStream.close();
				} else if (newfile.createNewFile()) {
					fileStream = new FileOutputStream(newfile, true);
					fileStream.write(dataArray.get(i));
					fileStream.flush();
					fileStream.close();
				}
			}

		} catch (Exception e) {
			System.err.println("无法打开日志文件: " + logFile);
			e.printStackTrace();
			try {
				fileStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/* 记录文本到硬盘 */
	public static void record2DiskTxT(String content, String filePath) {
		PrintWriter out = null;
		try {

			// File newfile=new File(filePath);
			out = new PrintWriter(new FileWriter(filePath));
			out.println(content);
			out.close();
		} catch (Exception e) {
			System.err.println("无法打开日志文件: " + logFile);
			e.printStackTrace();
			out.close();
		}
	}

	/* 记录CRH380BL文本到硬盘 */
	public static void record2Disk380BL(ArrayList<String> dataArrayTxT) {
		try {
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(
					"yyyy_MM_dd_HH_mm");
			for (int i = 0; i < dataArrayTxT.size(); i++) {
				tempfilename = bartDateFormat.format(new Date(System
						.currentTimeMillis()));
				String filepath = recordPath380BL + tempfilename + "_" + "3800";
				File newfile = new File(filepath);
				if (newfile.exists()) {
					fileStream = new FileOutputStream(newfile, true);
					fileStream.write(dataArrayTxT.get(i).getBytes());
					fileStream.flush();
					fileStream.close();
				} else if (newfile.createNewFile()) {
					fileStream = new FileOutputStream(newfile, true);
					fileStream.write(dataArrayTxT.get(i).getBytes());
					fileStream.flush();
					fileStream.close();
				}
			}

		} catch (Exception e) {
			System.err.println("无法打开日志文件: " + logFile);
			e.printStackTrace();
			try {
				fileStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 文件转化为字节数组
	 */
	public static byte[] getBytesFromFile(File file) {
		if (file == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}
	
	private static String logFilePath = System.getProperty("user.dir");
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
	
	
	
	FileWriter fw = null;
	/**
	 * 打印日志
	 * @param info
	 * @param add
	 */
	private void sendLogger(String info,boolean addflag){
		String filePath = logFilePath + "/log";
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		String fileName = filePath + "/logger_" + formatter2.format(currentTime) + ".log";
		
		try {
			File path = new File(filePath);
			// 如果文件夹不存在则创建
			if (!path.exists() && !path.isDirectory()) {
				path.mkdir();
			}
			
			File f = new File(fileName);
			fw = new FileWriter(f, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		synchronized(fw){
			try {
				
				PrintWriter pw = new PrintWriter(fw);
				
				if(addflag){
					pw.print(info);
				}else{
					pw.println("[" + dateString + "]" + info);
				}
				
				pw.flush();
				fw.flush();
				
				pw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//实例一个线程池，用于处理日志
	static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
	
	/**
	 * 打印日志
	 * @param info
	 * @param addflag
	 */
	public static void logger(String info,boolean addflag){
		final String msg = info; 
		final boolean flag = addflag;
		fixedThreadPool.execute(new Runnable() {
			public void run() {
				try {
					new LogInfo().sendLogger(msg,flag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 打印日志
	 * @param info
	 */
	public static void logger(String info){
		logger(info,false);
	}
	
//	public static void main(String[] args) throws InterruptedException  {
//		String  a = "日志";
//		int i = 0;
//		while(true){
//			LogInfo.logging(a+(++i));
//			System.out.println(i);
//			if(i == 10000)break;
//		}
//		
//	}
}
