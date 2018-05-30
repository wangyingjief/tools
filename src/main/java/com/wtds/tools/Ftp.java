package com.wtds.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class Ftp {
	/**
	 * 上传文件总时间 s
	 */
	private int uploadTimeCount = 0;
	/**
	 * 上传文件总大小
	 */
	private int uploadFileLenthCount = 0;
	
	public int getUploadTimeCount() {
		return uploadTimeCount;
	}

	public int getUploadFileLenthCount() {
		return uploadFileLenthCount;
	}

	private String host = "";
	private int port = 21;
	private String user = "";
	private String password = "";
	private String ftpPath = "/";
	
	@SuppressWarnings("unused")
	private Ftp(){}
	
	public Ftp(String host,int port,String user,String pwd,String ftpPath){
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = pwd;
		this.ftpPath = ftpPath;
	}
	
	 /**
     * 连接FTP Server
     * @throws IOException
     */
    public static final String ANONYMOUS_USER="anonymous";
    private FTPClient connect(){        
        FTPClient client = new FTPClient();
        try{
            //连接FTP Server
            client.connect(this.host, this.port);
            //登陆
            if(this.user==null||"".equals(this.user)){
                //使用匿名登陆
                client.login(ANONYMOUS_USER, ANONYMOUS_USER);
            }else{
                client.login(this.user, this.password);
            }
            //设置文件格式
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            //获取FTP Server 应答
            int reply = client.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                client.disconnect();
                return null;
            }
            //切换工作目录
            changeWorkingDirectory(client);
           
            System.out.println("===连接到FTP："+host+":"+port);
        }catch(IOException e){
            return null;
        }
        return client;
    }
    /**
     * 切换工作目录，远程目录不存在时，创建目录
     * @param client
     * @throws IOException
     */
    public void changeWorkingDirectory(FTPClient client) throws IOException{
        if(this.ftpPath!=null&&!"".equals(this.ftpPath)){
            boolean ok = client.changeWorkingDirectory(this.ftpPath);
            if(!ok){
                //ftpPath 不存在，手动创建目录
                StringTokenizer token = new StringTokenizer(this.ftpPath,"\\//");
                while(token.hasMoreTokens()){
                    String path = token.nextToken();
                    client.makeDirectory(path);
                    client.changeWorkingDirectory(path);
                }
            }
        }
    }
    /**
     * 断开FTP连接
     * @param ftpClient
     * @throws IOException
     */
    public void close(FTPClient ftpClient) throws IOException{
        if(ftpClient!=null && ftpClient.isConnected()){
            ftpClient.logout();
            ftpClient.disconnect();
        }
        System.out.println("!!!断开FTP连接："+host+":"+port);
    }
	
	 /**
     * 上传文件
     * @param targetName 上传到ftp文件名
     * @param localFile 本地文件路径
     * @return
     */
    public boolean upload(String targetName,String localFile){
        //连接ftp server
        FTPClient ftpClient = connect();
        
        if(ftpClient==null){
            System.out.println("连接FTP服务器["+host+":"+port+"]失败！");
            return false;
        }
        File file = new File(localFile);
        
        //设置上传后文件名
        if(targetName==null||"".equals(targetName))
            targetName = file.getName();
        FileInputStream fis = null;
        try{
            long now = System.currentTimeMillis();
            //开始上传文件
            fis = new FileInputStream(file);
            
        	ftpClient.setControlEncoding("ISO-8859-1");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            
            System.out.println(">>>开始上传文件："+file.getName());
            boolean ok = ftpClient.storeFile(new String(targetName.getBytes("GBK"), "ISO-8859-1"),fis);
            //boolean ok = ftpClient.storeFile(targetName, fis);
            if(ok){//上传成功
                long times = System.currentTimeMillis() - now;
                uploadTimeCount += times;
                uploadFileLenthCount += file.length();
                System.out.println(String.format(">>>上传成功：大小：%s,上传时间：%d秒", formatSize(file.length()),times/1000));
            }else//上传失败
                System.err.println(String.format(">>>上传失败：大小：%s", formatSize(file.length())));
        }catch(IOException e){
            System.err.println(String.format(">>>上传失败：大小：%s", formatSize(file.length())));
            e.printStackTrace();
            return false;
        }finally{
            try{
                if(fis!=null)
                    fis.close();
                close(ftpClient);
            }catch(Exception e){}
        }
        return true;
    }
    
    /**
     * 下载文件
     * @param localPath 本地存放路径
     * @return
     */
    public int download(String localPath){
        //  连接ftp server
        FTPClient ftpClient = connect();
        if(ftpClient==null){
            System.out.println("连接FTP服务器["+host+":"+port+"]失败！");
            return 0;
        }

        File dir = new File(localPath);
        if(!dir.exists())
            dir.mkdirs();
        FTPFile[] ftpFiles = null;
        try{
            ftpFiles = ftpClient.listFiles();
            if(ftpFiles==null||ftpFiles.length==0)
                return 0;
        }catch(IOException e){
            return 0;
        }
        int c = 0;
        for(int i=0;i<ftpFiles.length;i++){
            FileOutputStream fos = null;
            try{
                String name = ftpFiles[i].getName();
                fos = new FileOutputStream(new File(dir.getAbsolutePath()+File.separator+name));
                System.out.println("<<<开始下载文件："+name);

                long now = System.currentTimeMillis();
                boolean ok = ftpClient.retrieveFile(new String(name.getBytes("UTF-8"),"ISO-8859-1"), fos);
                if(ok){//下载成功
                    long times = System.currentTimeMillis() - now;
                    System.out.println(String.format("<<<下载成功：大小：%s,上传时间：%d秒", formatSize(ftpFiles[i].getSize()),times/1000));
                    c++;
                }else{
                    System.err.println("<<<下载失败");
                }
            }catch(IOException e){
                System.err.println("<<<下载失败");
                e.printStackTrace();
            }finally{
                try{
                    if(fos!=null)
                        fos.close();
                    close(ftpClient);
                }catch(Exception e){}
            }
        }
        return c;
    }
    
    @SuppressWarnings("unused")
	private static final DecimalFormat DF = new DecimalFormat("#.##");
    /**
     * 格式化文件大小（B，KB，MB，GB）
     * @param size
     * @return
     */
	public static String formatSize(long size) {
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "bytes";
		} else if (size < 1024 * 1024) {
			float kbsize = size / 1024f;
			return formater.format(kbsize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mbsize = size / 1024f / 1024f;
			return formater.format(mbsize) + "MB";
		} else if (size < 1024 * 1024 * 1024 * 1024) {
			float gbsize = size / 1024f / 1024f / 1024f;
			return formater.format(gbsize) + "GB";
		} else {
			return "size: error";
		}
	}
    
    public static void main(String args[]){
        Ftp ftp = new Ftp("172.21.4.133",21,"crhinfo","crhinfo","/CCU/CR400AF/207/");
        
        ftp.upload("1车TCU历史故障_07062016_000.txt", "D:\\Work\\JAVA\\01CTU\\uploadFile\\2017\\9\\27\\1车TCU历史故障_07062016_000.txt");
//        System.out.println("");
//        ftp.download("E:/ftp/log/");
    }
	
}