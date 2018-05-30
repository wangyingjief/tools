package com.wtds.tools.javaruninwindows;

import com.wtds.tools.ObjectSerialize;

public class Config extends ObjectSerialize{
	private static final long serialVersionUID = 120013123003214L;
	
	// 自定义javahome
	private String javaHome;
	// 运行环境路径
	private String runHome;
	// 需要运行的bat或者jar路径
	private String runPath;
	// 运行名称
	private String runName;
	// 后缀标记
	private String suffixTag = ".bat.jar";
	// 后缀
	private String suffix;
	// 环境变量
	private String path;
	// 启动命令
	private String startCmd = "";
	// 包装后需要执行的bat(由系统生成)
	private String execFileName;
	// windows进程中的java.exe运行目录
	private String runJavaPath;
	// 线程ID
	private String pid;
	
	// 是否打印应控制台日志(默认开)
	private boolean printConsole = true;
	
	// 控制台日志路径
	private String consoleLogPath;

	// 装载情况
	private boolean installStatus = false;

	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	public String getRunHome() {
		return runHome;
	}

	public void setRunHome(String runHome) {
		this.runHome = runHome;
	}

	public String getRunPath() {
		return runPath;
	}

	public void setRunPath(String runPath) {
		this.runPath = runPath;
	}

	public String getRunName() {
		return runName;
	}

	public void setRunName(String runName) {
		this.runName = runName;
	}

	public String getSuffixTag() {
		return suffixTag;
	}

	public void setSuffixTag(String suffixTag) {
		this.suffixTag = suffixTag;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStartCmd() {
		return startCmd;
	}

	public void setStartCmd(String startCmd) {
		this.startCmd = startCmd;
	}

	public String getExecFileName() {
		return execFileName;
	}

	public void setExecFileName(String execFileName) {
		this.execFileName = execFileName;
	}

	public String getRunJavaPath() {
		return runJavaPath;
	}

	public void setRunJavaPath(String runJavaPath) {
		this.runJavaPath = runJavaPath;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public boolean isPrintConsole() {
		return printConsole;
	}

	public void setPrintConsole(boolean printConsole) {
		this.printConsole = printConsole;
	}

	public String getConsoleLogPath() {
		return consoleLogPath;
	}

	public void setConsoleLogPath(String consoleLogPath) {
		this.consoleLogPath = consoleLogPath;
	}

	public boolean isInstallStatus() {
		return installStatus;
	}

	public void setInstallStatus(boolean installStatus) {
		this.installStatus = installStatus;
	}

	@Override
	public Object objectInstance() {
		return this;
	}
	
	
}
