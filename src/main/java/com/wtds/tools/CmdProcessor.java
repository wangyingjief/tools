package com.wtds.tools;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collection;  
import java.util.LinkedList;  
import java.util.List;  
import java.util.Map;  
import java.util.Map.Entry;  
import java.util.concurrent.LinkedBlockingDeque;  
import java.util.concurrent.TimeUnit;  
  
public class CmdProcessor {  
    private Process mProcess; // the cmd process  
    private PrintWriter mWriter; // write cmd streams to process  
    private BufferedReader mReader; // log the cmd response  
    private volatile boolean mStop; // stop the process  
    private LinkedBlockingDeque<String> mCmds; // the cmd container  
    private boolean mLoggable = true; // print log  
    private boolean mWarnTimeout = false;  
    private long mTimeoutMillis = 30 * 1000; // 30s  
  
    private static Charset charset = Charset.forName("GBK");
    
    protected long mCmdStartMillis; // record the cmd start time  
    protected long mProStartMillis; // record the process start time  
  
    public CmdProcessor() {  
        this(null);  
    }  
  
    public CmdProcessor(Collection<String> cmds) {  
        mCmds = new LinkedBlockingDeque<String>();  
        if (cmds != null) {  
            mCmds.addAll(cmds); // here is your cmds  
        }  
    }  
  
    private void init() throws IOException {  
        mProcess = Runtime.getRuntime().exec("cmd", getEnvs());  
        mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream(),charset));  
        mWriter = new PrintWriter(new OutputStreamWriter(mProcess.getOutputStream(),charset));  
        mCmdStartMillis = System.currentTimeMillis();  
        mProStartMillis = mCmdStartMillis;  
  
        if (mWarnTimeout) {  
            warnTimeout();  
        }  
    }  
  
    private String[] getEnvs() {  
        LinkedList<String> envList = new LinkedList<String>();  
        Map<String, String> envMap = System.getenv();  
        for (Entry<String, String> env : envMap.entrySet()) {  
            if (!env.getKey().contains("=") && !env.getValue().contains("=")) {  
                envList.add(env.getKey() + "=" + env.getValue());  
            }  
        }  
        String[] evns = new String[envList.size()];  
        return envList.toArray(evns);  
    }  
  
    public void addFirst(String cmd) {  
        mCmds.addFirst(cmd);  
    }  
  
    public void addLast(String cmd) {  
        mCmds.addLast(cmd);  
    }  
  
    public void add(String cmd) {  
        addLast(cmd);  
    }  
  
    /** 
     * Print warning if cmd has no response, you can disable it 
     */  
    private void warnTimeout() {  
        new Thread(new Runnable() {  
  
            public void run() {  
                while (!mStop) {  
                    long passedSec = (System.currentTimeMillis() - mCmdStartMillis);  
                    if (passedSec > mTimeoutMillis) {  
                        System.out.println("[" + mCmds.peek() + "] cost seconds:" + passedSec / 1000);  
                    }  
                    try {  
                        Thread.sleep(1050);  
                    } catch (InterruptedException e) {  
                    }  
                }  
            }  
        }).start();  
    }  
  
    public void setLoggable(boolean loggable) {  
        mLoggable = loggable;  
    }  
  
    public void waitFor() throws InterruptedException, IOException {  
        mProcess.waitFor();  
        stop();  
    }  
  
    public void waitFor(final long timeout, TimeUnit timeUnit) throws Exception {  
        String versionStr = System.getProperty("java.version");  
        float version = Float.valueOf(versionStr.substring(0, 3));  
        if (version >= 1.8f) {  
            mProcess.waitFor(timeout, timeUnit);  
        } else {  
            mockupWaitFor(timeout, timeUnit);  
        }  
        stop();  
    }  
  
    /** 
     * If JDK level is lower than 1.8, you can't use method 
     * Process.waitFor(long, TimeUnit). So here mock up a waitFor(long, 
     * TimeUnit) method. 
     *  
     * @param timeout 
     * @throws Exception 
     */  
    private void mockupWaitFor(long timeout, TimeUnit timeUnit) throws Exception {  
        final long timeMillis = timeUnit.toMillis(timeout);  
        final Object tmpLock = new Object();  
  
        new Thread(new Runnable() {  
  
            public void run() {  
                long start = System.currentTimeMillis();  
                while (true) {  
                    if (System.currentTimeMillis() - start > timeMillis) {  
                        synchronized (tmpLock) {  
                            tmpLock.notify();  
                        }  
                        break;  
                    }  
  
                    try {  
                        Thread.sleep(1);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }).start();  
  
        synchronized (tmpLock) {  
            tmpLock.wait();  
        }  
    }  
  
    private void executeCmd(String cmd) {  
        if (!mStop) {  
            mWriter.println(cmd);  
            mWriter.flush();  
            mCmdStartMillis = System.currentTimeMillis();  
        }  
    }  
  
    public void start() throws Exception {  
        init();  
  
        new Thread(new Runnable() {  
  
            public void run() {
                try {  
                    processCmds();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
    
    public String startBySyn()throws Exception {
    	init();
    	return processCmds();
    }
  
    public void stop() throws IOException {  
        mStop = true;  
        mProcess.destroy();  
        mReader.close();  
        mWriter.close();  
    }  
  
    public boolean isStopped() {  
        return mStop;  
    }  
  
    private String processCmds() throws Exception {  
        boolean firstReply = true;  
        StringBuffer buffer = new StringBuffer();  
        StringBuilder result = new StringBuilder();
        char[] chars = new char[1024];  
        while (!mStop) {  
            if (mReader.ready()) {
                int len = mReader.read(chars);  
                buffer.append(chars, 0, len);  
                result.append(chars , 0, len);
                if (mLoggable) {  
                    System.out.print(new String(chars, 0, len));  
                }  
            } else {  
                String log = buffer.toString();
                boolean isReply = isCmdReply(mCmds.peek(), log);  
                if (isReply) {  
                    buffer.delete(0, buffer.length());  
  
                    if (firstReply) {  
                        firstReply = false;  
                    } else {  
                        onCmdReply(mCmds.poll(), log); // on cmd reply  
                    }  
  
                    if (mCmds.peek() == null) { // process next cmd  
                        onFinish();  
                    } else {  
                        String nextCmd = mCmds.peek();  
                        preExecuteCmd(nextCmd);  
                        executeCmd(nextCmd);  
                    }  
                }  
            }  
        }  
        return result.toString();
    }  
  
    // reply???  
    protected boolean isCmdReply(String cmd, String log) throws Exception {  
        String userDir = System.getProperty("user.dir");  
        return log.contains(userDir);  
    }  
  
    // on cmd reply  
    protected void onCmdReply(String cmd, String reply) throws Exception {  
  
    }  
  
    // pre-execute cmd  
    protected void preExecuteCmd(String cmd) throws Exception {  
  
    }  
  
    // all the cmds has been executed  
    protected void onFinish() throws Exception {  
        stop();  
  
        if (mLoggable) {  
            long costSec = (System.currentTimeMillis() - mProStartMillis) / 1000;  
            System.out.println("\nCost time: " + costSec);  
        }  
    }  
    
    /**
	 * 执行cmd命令
	 * @param cmd
	 * @return
	 */
	public static String shell(String cmd){
		String s = null;
		String result = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream(),charset));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream(),charset));
            //从命令行打印出输出结果
            //System.out.println("标准输出命令\n");
            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
            	if(s!=null && !s.equals(""))result += s + "\n";
            }

            //System.out.println("标准错误的输出命令(如果存在):\n");
            while ((s = stdError.readLine()) != null) {
            	if(s!=null && !s.equals(""))result += s + "\n";
            	//System.err.println(s);
            }
        } catch (IOException e) {
            System.err.println("异常发生: ");
            //e.printStackTrace();
            result = "异常发生: " + e.toString();
        }
        return result;
	}
  
    public static void main(String[] args) throws Exception {
//    	String cmd = "cmd.exe /c z: && cd Z:\\workspace\\WTDS\\SVN_HOME\\WTDS\\TRUNK\\WTDS\\wlan\\apidoc\\runing\\ && apidoc -jar apidoc-0.0.1.jar";
//        CmdProcessor pro = new CmdProcessor();  
//        pro.addFirst(cmd);  
//        pro.start();  
//        Thread.sleep(5000);
//        pro.stop();
//        List<String> cmds = new LinkedList<String>();  
//        cmds.add("Z:\\workspace\\WTDS\\SVN_HOME\\WTDS\\TRUNK\\WTDS\\wlan\\apidoc\\runing\\start.bat");  
//        CmdProcessor cd = new CmdProcessor(cmds);  
//        cd.start();  
//        String r = cd.startBySyn();
//        System.out.println("============"+r);
        //cd.stop();
        //shell(cmd);
        
    }  
}  
