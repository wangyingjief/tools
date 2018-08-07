package com.wtds.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.fastjson.JSON;

/**
 * 打印日志<br>
 * 
 * @author wyj
 * @version 0.1
 * @version 1.0 打印String日志
 * @version 2.0 (2017-07-21) 支持打印json格式日志
 */
public class Logger {

	public enum PrintFormat {
		normal/* 正常格式 */, json/* JSON格式 */
	}

	/**
	 * 打印格式
	 */
	private PrintFormat printFormat = PrintFormat.normal;

	/**
	 * 后缀
	 */
	private String suffix = "log";

	/**
	 * 日志文件名
	 */
	String logName;

	/**
	 * 是否打印控制台
	 */
	private boolean consolePrint = true;

	/**
	 * 日志目录
	 */
	String consoleLogPath = System.getProperty("user.dir") + "/log";

	FileWriter fw = null;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");

	// 实例一个线程池，用于处理日志
	private ThreadPoolExecutor pool;

	public Logger() {
		this.logName = "logger";
		init();
	}

	/**
	 * @param logName
	 *            日志名称
	 */
	public Logger(String logName) {
		this.logName = logName;
		init();
	}

	/**
	 * 
	 * @param logName
	 *            日志名称
	 * @param printFormat
	 *            打印格式
	 */
	public Logger(String logName, PrintFormat printFormat) {
		this.printFormat = printFormat;
		this.logName = logName;
		init();

	}

	/**
	 * 
	 * @param printFormat
	 *            打印格式
	 */
	public Logger(PrintFormat printFormat) {
		this.printFormat = printFormat;
		this.logName = "logger";
		init();
	}

	private void init() {
		switch (this.printFormat) {
		case normal:
			suffix = "log";
			break;
		case json:
			suffix = "json.log";
			break;
		}
		checkPath();
		pool = ThreadPoolUtil.newThreadPoolExecutor(0, 1, 60);
	}

	/**
	 * 检查文件夹是否存在
	 */
	public void checkPath() {
		String filePath = consoleLogPath;
		String fileName = filePath + "/" + logName + "." + suffix;
		File f = new File(fileName);
		File path = new File(f.getParent());
		// 如果文件夹不存在则创建
		if (!path.exists() && !path.isDirectory()) {
			path.mkdirs();
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param info
	 * @param add
	 */
	private void sendLogger(Object info, boolean addflag) {
		String filePath = consoleLogPath;
		Date currentTime = new Date();
		String dayStr = formatter2.format(currentTime);
		String dateString = formatter.format(currentTime);
		String fileName = filePath + "/" + logName + "." + suffix;

		try {
			File f = new File(fileName);

			String lts = formatter2.format(f.lastModified());
			if (!dayStr.equals(lts)) {
				String rfileName = filePath + "/" + logName + "_" + lts + "." + suffix;
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

				switch (printFormat) {
				case normal:
					if (addflag) {
						pw.print(info);
					} else {
						pw.println("[" + dateString + "]" + info);
					}
					break;
				case json:
					pw.println(JSON.toJSONString(new Info(dateString, info)));
					break;
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
	public void info(Object info, boolean addflag) {
		final Object msg = info;
		final boolean flag = addflag;
		// 控制台打印
		if (consolePrint) {
			if ((info + "").length() > 256) {
				System.out.println((info + "").substring(0, 256) + "...");
			} else {
				System.out.println((info + ""));
			}
		}

		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					sendLogger(msg, flag);
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
	public void info(Object info) {
		info(info, false);
	}

	public boolean isConsolePrint() {
		return consolePrint;
	}

	public void setConsolePrint(boolean consolePrint) {
		this.consolePrint = consolePrint;
	}

	class Info {
		public Info(String time, Object content) {
			this.time = time;
			this.content = content;
		}

		private String time;
		private Object content;

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public Object getContent() {
			return content;
		}

		public void setContent(Object content) {
			this.content = content;
		}

	}
}