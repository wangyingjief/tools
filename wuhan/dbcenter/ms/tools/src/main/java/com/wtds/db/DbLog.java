package com.wtds.db;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据库日志打印类
 * 
 * @author wangyingjie
 */
public class DbLog {

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");

	FileWriter fw = null;

	String consoleLogPath = System.getProperty("user.dir") + "/log";

	String logName = "db_logger";

	/**
	 * 打印日志
	 * 
	 * @param info
	 * @param add
	 */
	private void sendLogger(String info, boolean addflag) {
		String filePath = consoleLogPath;
		Date currentTime = new Date();
		String dayStr = formatter2.format(currentTime);
		String dateString = formatter.format(currentTime);
		String fileName = filePath + "/" + logName + ".log";

		try {
			File path = new File(filePath);
			// 如果文件夹不存在则创建
			if (!path.exists() && !path.isDirectory()) {
				path.mkdir();
			}

			File f = new File(fileName);
			String lts = formatter2.format(f.lastModified());
			if (!dayStr.equals(lts)) {
				String rfileName = filePath + "/" + logName + "_" + lts + ".log";
				f.renameTo(new File(rfileName));
				f = new File(fileName);
			}

			fw = new FileWriter(f, true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		synchronized (fw) {
			try {
				PrintWriter pw = new PrintWriter(fw);
				if (addflag) {
					pw.print(info);
				} else {
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

	// 实例一个线程池，用于处理日志
	static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

	/**
	 * 打印日志
	 * 
	 * @param info
	 * @param addflag
	 *            true:追加打印
	 */
	public static void logger(String info, boolean addflag) {
		final String msg = info;
		final boolean flag = addflag;
		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					new DbLog().sendLogger(msg, flag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 打印日志
	 * 
	 * @param info
	 */
	public static void logger(String info) {
		System.out.println(info);
		logger(info, false);
	}

}
