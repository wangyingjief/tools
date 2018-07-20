package com.wtds.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Sftp {

	private Session session;// 会话
	private Channel channel;// 连接通道
	private ChannelSftp sftp;// sftp操作类

	/**
	 * 连接ftp/sftp服务器
	 * 
	 * @param address
	 * @param port
	 * @param username
	 * @param password
	 */
	public void connection(String address, int port, String username, String password) {

		/** 密钥的密码 */
		// String privateKey ="key";
		// /** 密钥文件路径 */
		// String passphrase ="path";

		Session session = null;
		Channel channel = null;
		ChannelSftp sftp = null;// sftp操作类

		JSch jsch = new JSch();

		// 设置密钥和密码
		// 支持密钥的方式登陆，只需在jsch.getSession之前设置一下密钥的相关信息就可以了
		// if (privateKey != null && !"".equals(privateKey)) {
		// if (passphrase != null && "".equals(passphrase)) {
		// //设置带口令的密钥
		// jsch.addIdentity(privateKey, passphrase);
		// } else {
		// //设置不带口令的密钥
		// jsch.addIdentity(privateKey);
		// }
		// }
		try {

			session = jsch.getSession(username, address, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");// 不验证 HostKey
			try {
				session.connect();
			} catch (Exception e) {
				if (session.isConnected())
					session.disconnect();
			}
			channel = session.openChannel("sftp");
			try {
				channel.connect();
			} catch (Exception e) {
				if (channel.isConnected())
					channel.disconnect();
			}
			sftp = (ChannelSftp) channel;

			this.channel = channel;
			this.session = session;
			this.sftp = sftp;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 断开连接
	 * 
	 */
	public void close() {
		if (null != sftp) {
			sftp.disconnect();
			sftp.exit();
			sftp = null;
		}
		if (null != channel) {
			channel.disconnect();
			channel = null;
		}
		if (null != session) {
			session.disconnect();
			session = null;
		}
	}

	/**
	 * 上传文件夹至指定的远程文件夹中(如果远程存在，会覆盖)
	 * 
	 * @param localDir
	 * @param remoteDir
	 * @throws Exception
	 */
	public void uploadDir(String localDir, String remoteDir) throws Exception {
		File local = new File(localDir);
		if (!local.isDirectory()) {
			throw new Exception("Error : Non Folder!");
		}
		// List<String> remoteFile = this.listFiles(uploadDir);
		// if(remoteFile != null && remoteFile.size() > 0) {
		// throw new Exception("Error : Remote Exist Folder!");
		// }
		// 获取指定本地目录所有文件
		List<File> allFile = new ArrayList<File>();
		FileUtil.listFile(local, allFile);
		if (allFile != null && allFile.size() > 0) {
			for (File f : allFile) {
				String rDir = "";
				if (local.getParent().equals("/") || local.getParent().equals("\\")) {
					rDir = remoteDir + f.getParent();
				} else {
					rDir = remoteDir + f.getParent().replace(local.getParent(), "");
				}
				this.upload(rDir, f.getPath());
				System.out.println(f.getName() + " > " + rDir + "/" + f.getName() + "   ok!");
			}
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param directory
	 *            上传的目录-相对于SFPT设置的用户访问目录， 为空则在SFTP设置的根目录进行创建文件（除设置了服务器全磁盘访问）
	 * @param uploadFile
	 *            要上传的文件全路径
	 */
	public void upload(String directory, String uploadFile) throws Exception {

		try {
			try {
				sftp.cd(directory); // 进入目录
			} catch (SftpException sException) {
				if (sftp.SSH_FX_NO_SUCH_FILE == sException.id) { // 指定上传路径不存在
					// sftp.mkdir(directory);// 创建目录
					// sftp.cd(directory); // 进入目录

					String[] folders = directory.split("/");
					// 返回的结果数组比预计的数组多了一个元素，第一个元素为空
					// System.out.println(folders.length);
					String path = "";
					// 第一个元素是空字符串，所以从第二个元素开始遍历，即i=1
					for (int i = 1; i < folders.length; i++) {
						String folder = folders[i];
						path = path + "/" + folder;
						try {
							// 用是否能进入该目录来判断该目录是否存在
							sftp.cd(path);
						} catch (SftpException e) {
							// 上面不能进入则报错，执行以下的创建命令
							sftp.mkdir(path);
						}
					}
					sftp.cd(directory); // 进入目录
				}
			}

			File file = new File(uploadFile);
			InputStream in = new FileInputStream(file);
			sftp.put(in, file.getName());
			in.close();
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param directory
	 *            下载目录 根据SFTP设置的根目录来进行传入
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 */
	public void download(String directory, String downloadFile, String saveFile) throws Exception {

		try {

			sftp.cd(directory); // 进入目录
			File file = new File(saveFile);
			boolean bFile;
			bFile = false;
			bFile = file.exists();
			if (!bFile) {
				bFile = file.mkdirs();// 创建目录
			}
			OutputStream out = new FileOutputStream(new File(saveFile, downloadFile));

			sftp.get(downloadFile, out);

			out.flush();
			out.close();

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 */
	public void delete(String directory, String deleteFile) throws Exception {
		try {
			sftp.cd(directory); // 进入的目录应该是要删除的目录的上一级
			sftp.rm(deleteFile);// 删除目录
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            要列出的目录
	 * @return list 文件名列表
	 * @throws Exception
	 */
	public List<String> listFiles(String directory) throws Exception {
		Vector fileList = null;
		List<String> fileNameList = new ArrayList<String>();
		fileList = sftp.ls(directory); // 返回目录下所有文件名称
		Iterator it = fileList.iterator();
		while (it.hasNext()) {
			String fileName = ((LsEntry) it.next()).getFilename();
			if (".".equals(fileName) || "..".equals(fileName)) {
				continue;
			}
			fileNameList.add(fileName);
		}
		return fileNameList;
	}

	/**
	 * 删除目录下所有文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 */
	public void deleteAllFile(String directory) throws Exception {

		try {
			List<String> files = listFiles(directory);// 返回目录下所有文件名称
			sftp.cd(directory); // 进入目录

			for (String deleteFile : files) {
				sftp.rm(deleteFile);// 循环一次删除目录下的文件
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

	}

	/**
	 * 删除目录 (删除的目录必须为空)
	 * 
	 * @param deleteDir
	 *            要删除的目录
	 */
	public void deleteDir(String deleteDir) throws Exception {
		try {
			sftp.rmdir(deleteDir);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param directory
	 *            要创建的目录 位置
	 * @param dir
	 *            要创建的目录
	 */
	public void creatDir(String directory, String dir) throws Exception {
		try {
			sftp.cd(directory);
			sftp.mkdir(dir);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * 更改文件名
	 * 
	 * @param directory
	 *            文件所在目录
	 * @param oldFileNm
	 *            原文件名
	 * @param newFileNm
	 *            新文件名
	 * @throws Exception
	 */
	public void rename(String directory, String oldFileNm, String newFileNm) throws Exception {
		try {
			sftp.cd(directory);
			sftp.rename(oldFileNm, newFileNm);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * 进入目录
	 * 
	 * @param directory
	 * @throws Exception
	 */
	public void cd(String directory) throws Exception {
		try {
			sftp.cd(directory); // 目录要一级一级进
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	public static void main(String[] args) throws Exception {
		// Sftp sftp = new Sftp();
		// sftp.connection("114.55.136.158", 22, "root", "pass12#$");
		// sftp.upload("/opt/test/a/b/c", System.getProperty("user.dir") +
		// "/config/db.properties");
		// sftp.close();
		String local = "/Users/joymting/workspace/dbcenter/ms/data-clean/P0-NovaDataClean/P01-NovaDataClean-Manager/target/web";
		List<File> allFile = new ArrayList<File>();
		FileUtil.listFile(new File(local), allFile);
		if (allFile != null && allFile.size() > 0) {
			for (File f : allFile) {
				String remoteDir = f.getParent().replace(local, "");
				System.out.println(remoteDir);
				// this.upload(uploadDir, f.getPath());
			}
		}
	}
}
