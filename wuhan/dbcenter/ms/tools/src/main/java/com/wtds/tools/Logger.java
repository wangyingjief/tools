package com.wtds.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 打印日志
 * 
 * @author wyj
 */
public class Logger {

	public Logger() {
		this.logName = "logger";
	}
	
	private boolean consolePrint = true;
	
	/**
	 * @param logName
	 *            日志名称
	 */
	public Logger(String logName) {
		this.logName = logName;
		pool = ThreadPoolUtil.newThreadPoolExecutor(0, 1, 60);
	}

	String consoleLogPath = System.getProperty("user.dir") + "/log";
	String logName;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");

	// 实例一个线程池，用于处理日志
	private ThreadPoolExecutor pool;

	FileWriter fw = null;

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

	/**
	 * 打印日志
	 * 
	 * @param info
	 * @param addflag
	 *            true:追加打印
	 */
	public void info(String info, boolean addflag) {
		final String msg = info;
		final boolean flag = addflag;
		pool.execute(new Runnable() {
			public void run() {
				try {
					sendLogger(msg, flag);
					// new Logger(logName).sendLogger(msg, flag);
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
	public void info(String info) {
		if(consolePrint){
			System.out.println(info);
		}
		info(info, false);
	}

	public boolean isConsolePrint() {
		return consolePrint;
	}

	public void setConsolePrint(boolean consolePrint) {
		this.consolePrint = consolePrint;
	}
	
	

}