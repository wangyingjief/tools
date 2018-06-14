package com.wtds.tools.javaruninwindows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wtds.tools.CmdProcessor;
import com.wtds.tools.FileUtil;
import com.wtds.tools.MD5Util;
import com.wtds.tools.StringUtil;

/**
 * 在windows环境下运行.bat,.jar<br>
 * 1.设置运行环境
 *   - 拷贝jre至运行目录(文件夹名称为：sys_jre_xxx)
 *   - 生成运行bat文件(文件名称为：xxx.bat_sys.bat 或者 xxx.jar_sys.bat)
 *   - 生成日志文件夹(文件夹名称为:console_xxx.bat_log 或者 console_xxx.jar_log)
 * 2.调用生成的bat文件
 * 	 - 启动时获取当前jre的PID
 * 3.根据PID关闭进程    
 * @author wyj
 */
public class JavaRunInWindows {

	/**
	 * 所有运行中的java线程
	 */
	public static Map<String, String> runingThreads = new HashMap<String, String>();
	
	Config conf = new Config();
	
	public Config getConf() {
		return conf;
	}

	public void setConf(Config conf) {
		this.conf = conf;
	}

	// 需要启动的文件
	File runFile;
	
	private Logger logger;
	// 实例一个线程池，用于处理日志
	private ExecutorService fixedThreadPool;

	// 启动的线程
	private ExecutorService t;
	
	@SuppressWarnings("unused")
	private JavaRunInWindows() {
	}
	
	/**
	 * 执行bat或者jar,默认运行HOME为执行文件所在目录
	 * @param runPath 执行文件
	 */
	public JavaRunInWindows(String runPath) {
		conf.setRunPath(runPath);
		install();
	}
	
	/**
	 * 根据config加载
	 * @param config
	 */
	public JavaRunInWindows(Config config) {
		conf.setRunJavaPath(config.getJavaHome() + "\\bin\\java.exe");
		boot(config.getRunPath(),config.getRunHome(),config.getJavaHome());
	}
	
	/**
	 * 执行bat或者jar
	 * @param runPath 执行文件
	 * @param runPathHome 运行HOME
	 */
	public JavaRunInWindows(String runPath,String runPathHome) {
		boot(runPath,runPathHome,null);
	}
	
	/**
	 * 执行bat或者jar
	 * @param runPath 执行文件路径
	 * @param runPathHome 运行home
	 * @param javaHome 
	 */
	public JavaRunInWindows(String runPath,String runPathHome,String javaHome) {
		boot(runPath,runPathHome,javaHome);
	}
	
	public void boot(String runPath,String runPathHome,String javaHome){
		conf.setJavaHome(javaHome);
		conf.setRunPath(runPath);
		try {
			if (StringUtil.isEmpty(runPathHome)) {
				throw new Exception("runPathHome未设置");
			}
			conf.setRunHome(runPathHome);
			install();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 安载运行环境
	 */
	public void install(){
		config();
		conf.setInstallStatus(true);
	}
	
	/**
	 * 设置运行环境配置
	 * @param runPath
	 */
	private void config(){
		init();
		runtimeInit();
		System.out.println("----------------java run config----------------");
		System.out.println("  javaHome:" + conf.getJavaHome());
		System.out.println("  runHome:" + conf.getRunHome());
		System.out.println("  runPath:" + conf.getRunPath());
		System.out.println("  runName:" + conf.getRunName());
		System.out.println("  suffix:" + conf.getSuffix());
		System.out.println("  path:" + conf.getPath());
		System.out.println("  execFileName:" + conf.getExecFileName());
		System.out.println("  runJavaPath:" + conf.getRunJavaPath());
		if(conf.isPrintConsole()){
			System.out.println("  consoleLogPath:" + conf.getConsoleLogPath());
		}
		System.out.println("------------config setting success------------");
	}

	/**
	 * 运行环境初始化
	 */
	private void init() {
		try {
			conf.setJavaHome(initJavaHome());
			conf.setPath(System.getenv("path"));
			if (StringUtil.isEmpty(conf.getJavaHome())) {
				throw new Exception("JAVA_HOME未设置");
			}
			this.runFile = new File(conf.getRunPath());
			if (!this.runFile.isFile()) {
				throw new Exception("执行文件不存在");
			}
			if (conf.getRunPath().indexOf(".") > -1) {
				conf.setSuffix(conf.getRunPath().substring(conf.getRunPath().lastIndexOf(".") + 1));
			}
			
			if (!conf.getSuffixTag().contains(conf.getSuffix().toLowerCase())) {
				throw new Exception("执行文件只能为.bat或者.jar");
			}
			
			conf.setRunHome(this.runFile.getParent());
			conf.setRunName(this.runFile.getName());
			conf.setExecFileName(conf.getRunHome() + "\\" + runFile.getName() + "_sys.bat");
			
			if(conf.isPrintConsole()){
				conf.setConsoleLogPath(conf.getRunHome() + "\\console_" + conf.getRunName() + "_log");
				FileUtil.createDirectory(conf.getConsoleLogPath());
				
				// 实例一个线程池，用于处理日志
				fixedThreadPool = Executors.newFixedThreadPool(1);
				logger = new Logger();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String initJavaHome(){
		String javahome = conf.getJavaHome();
		if(StringUtil.isEmpty(javahome)){
			javahome = System.getenv("JAVA_HOME");
		}
		return javahome;
	}

	/**
	 * Java运行环境初始化
	 */
	private void runtimeInit() {
		
		try {
			// 1.复制JAVA_HOME jre到软件目录下
			String jre = conf.getJavaHome() + "\\jre";
			String rh = conf.getRunHome() + "\\sys_jre_" + MD5Util.getMd5(runFile.getName());
			String javapn = rh + "\\bin\\java.exe";
			FileUtil.copyDirectory(jre, rh);
			conf.setJavaHome(rh);
			File javapnFile = new File(javapn);
			if(!javapnFile.exists()){
				throw new Exception(javapn + " 不存在");
			}
			String newPath = conf.getJavaHome() + "\\bin\\;" + conf.getPath();
			conf.setPath(newPath);
			conf.setRunJavaPath(conf.getJavaHome() + "\\bin\\java.exe");
			runingThreads.put(conf.getRunJavaPath(), conf.getRunPath());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行cmd命令
	 * @param cmd
	 * @return
	 */
	public void shell(String cmd) throws Exception{
//		String errAll = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));
//            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream(),"GBK"));
            //从命令行打印出输出结果
            //System.out.println("标准输出命令\n");
        	String s = null;
            while ((s = stdInput.readLine()) != null) {
            	if(s!=null && !s.equals("")&&conf.isPrintConsole())logger.info(s);
            }
//            //System.out.println("标准错误的输出命令(如果存在):\n");
//            String err = "";
//            while ((err = stdError.readLine()) != null) {
//            	if(err!=null && !err.equals(""))errAll += err + "\n";
//            }
        } catch (IOException e) {
        	e.printStackTrace();
        	throw new Exception(e.toString());
        }
        stopDelayLoadPid();
	}
	
	boolean loadpidstatus = true;
	public void stopDelayLoadPid(){
		loadpidstatus = false;
	}
	
	/**
	 * 延时加载运行后的java进程PID
	 */
	public void delayLoadPid(){
		try {
			while (true) {
				Thread.sleep(1000);
				if(!loadpidstatus)break;
				String r = CmdProcessor.shell("wmic process where \"name='java.exe'\" get name,ProcessId,executablepath");
				r = r.replaceAll("  ", "|");
				String [] rs = r.split("\n");
				Map<String, String> m = new HashMap<String, String>();
				if(rs.length > 1){
					for(int i = 1;i < rs.length;i++){
						String [] d = rs[i].split("\\|");
						String path = d[0].trim();
						//String name = d[1].trim(); 
						String pid = d[2].trim(); 
						m.put(path, pid);
						if(conf.getRunJavaPath().equals(path)){
							conf.setPid(pid);
							System.out.println("PID:" + pid);
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据运行文件全路与进程名称径获取PID
	 * @param runFilePath 运行文件路径
	 * @param threadName 进程名称
	 * @return
	 */
	public static String searchPid(String runFilePath){
		File file = new File(runFilePath);
		String r = CmdProcessor.shell("wmic process where \"name='" + file.getName() + "'\" get name,ProcessId,executablepath");
		r = r.replaceAll("  ", "|");
		String [] rs = r.split("\n");
		Map<String, String> m = new HashMap<String, String>();
		if(rs.length > 1){
			for(int i = 1;i < rs.length;i++){
				String [] d = rs[i].split("\\|");
				String path = d[0].trim();
				//String name = d[1].trim(); 
				String pid = d[2].trim(); 
				m.put(path, pid);
				if(file.getPath().equals(path)){
					System.out.println("PID:" + pid);
					return pid;
				}
			}
		}
		return null;
	}
	
	/**
	 * 杀死进程
	 * @param pid
	 * @return
	 */
	public static String kill(String pid){
		String r = "";
		try {
			String killCmd = "cmd.exe /c taskkill /pid " + pid + " -t -f ";
			r = CmdProcessor.shell(killCmd);
			
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
	
	/**
	 * 检查运行状态
	 * @return
	 */
	public boolean checkeRuntimeStatus(){
		boolean flag = false;
		String r = CmdProcessor.shell("cmd.exe /c tasklist|findstr " + conf.getPid()).trim();
		if(!StringUtil.isEmpty(r)){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 启动
	 * @throws Exception 
	 */
	public void start() throws Exception {
		if(!conf.isInstallStatus()){
			String err = conf.getRunName() + "已经卸载,如要启动。请调用install()方法";
			System.out.println(err);
			throw new Exception(err);
		}
		if(!StringUtil.isEmpty(conf.getPid()) && checkeRuntimeStatus()){
			String err = "线程已启动 PID:" + conf.getPid();
			System.out.println(err);
			throw new Exception(err);
		}
		StringBuffer cmd = new StringBuffer();
		String d0 = conf.getRunHome().substring(0,2);
		cmd.append(d0);
		cmd.append("\ncd " + conf.getRunHome());
		cmd.append("\nset JAVA_HOME=").append(conf.getJavaHome());
		cmd.append("\nset classpath=.;" + conf.getJavaHome() + "\\lib\\dt.jar;" + conf.getJavaHome() + "\\lib\\tools.jar;");
		cmd.append("\nset path=" + conf.getPath());
		cmd.append("\nset");
		cmd.append("\njava -version");
		if("bat".equals(conf.getSuffix())){
			cmd.append("\n").append(conf.getRunPath());
		}else if("jar".equals(conf.getSuffix())){
			cmd.append("\n").append("java -jar ").append(conf.getRunPath());
		}
		conf.setStartCmd(cmd.toString());
		try {
			FileUtil.deleteFile(conf.getExecFileName());
			System.out.println("==============cmd==============");
			System.out.println(conf.getStartCmd());
			System.out.println("==============cmd==============");
			FileUtil.writeToFile(conf.getExecFileName(), conf.getStartCmd(), false);
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		loadpidstatus = true;
		t = Executors.newFixedThreadPool(1);
		t.execute(new RunJava());
		new Thread(new Runnable() {
			public void run() {
				delayLoadPid();
			}
		}).start();
	}
	
	/**
	 * 停止
	 */
	public void stop() {
		try {
			String killCmd = "cmd.exe /c taskkill /pid " + conf.getPid() + " -t -f ";
			String r = CmdProcessor.shell(killCmd);
			if(t != null){
				t.shutdownNow();
				t = null;
			}
			if(fixedThreadPool != null){
				fixedThreadPool.shutdownNow();
				fixedThreadPool = null;
			}
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 卸载运行环境生成的文件<br>
	 */
	public void uninstall(){
		FileUtil.deleteFile(conf.getExecFileName());
		FileUtil.deleteDirectory(conf.getJavaHome());
		FileUtil.deleteDirectory(conf.getConsoleLogPath());
		conf.setInstallStatus(false);
		System.out.println("clear success");
	}
	
	public void setJavaHome(String javaHome) {
		conf.setJavaHome(javaHome);
	}

	/**
	 * 获取PID ！注意：此方法会有阻塞,最大阻塞时间10s
	 * @return
	 */
	public String getPid() {
		long time = System.currentTimeMillis();
		while(true){
			if(!StringUtil.isEmpty(conf.getPid())){
				return conf.getPid();
			}
			long time2 = System.currentTimeMillis();
			if((time2 - time) > (1000 * 10)){
				return null;
			}
		}
	}
	
	/**
	 * 是否答应控制台信息
	 * @return
	 */
	public boolean isPrintConsole() {
		return conf.isPrintConsole();
	}
	
	/**
	 * 设置是否答应控制台信息
	 * @param printConsole
	 */
	public void setPrintConsole(boolean printConsole) {
		conf.setPrintConsole(printConsole);
	}

	public static void main(String[] args) throws Exception{
		JavaRunInWindows run = new JavaRunInWindows("Z:\\workspace\\WTDS\\SVN_HOME\\WTDS\\TRUNK\\WTDS\\wlan\\shellnginx\\StartServer.bat");
		run.start();
		System.out.println("++++++++++++++++++++++++++");
		System.out.println(run.getPid());
		System.out.println("++++++++++++++++++++++++++");
		Thread.sleep(3000);
		run.stop();
		run.uninstall();
		Thread.sleep(3000);
		run.install();
		run.start();
		Thread.sleep(5000);
		run.stop();
//		System.exit(0);
	}
	
	/**
	 * 执行java
	 * @author wyj
	 */
	class RunJava implements Runnable{
		public void run() {
			try {
				shell(conf.getExecFileName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 打印日志
	 * @author wyj
	 */
	class Logger {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
		
		FileWriter fw = null;

		/**
		 * 打印日志
		 * 
		 * @param info
		 * @param add
		 */
		private void sendLogger(String info, boolean addflag) {
			String filePath = conf.getConsoleLogPath();
			Date currentTime = new Date();
			String dayStr = formatter2.format(currentTime);
			String dateString = formatter.format(currentTime);
			String fileName = filePath + "/console.log";
			
			try {
				File path = new File(filePath);
				// 如果文件夹不存在则创建
				if (!path.exists() && !path.isDirectory()) {
					path.mkdir();
				}

				File f = new File(fileName);
				String lts = formatter2.format(f.lastModified());
				if(!dayStr.equals(lts)){
					String rfileName = filePath + "/console_" + lts + ".log";
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
//						pw.println(info);
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
		 * @param addflag true:追加打印
		 */
		public void info(String info, boolean addflag) {
			final String msg = info;
			final boolean flag = addflag;
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						new Logger().sendLogger(msg, flag);
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
			System.out.println(info);
			info(info, false);
		}
		
	}

}
