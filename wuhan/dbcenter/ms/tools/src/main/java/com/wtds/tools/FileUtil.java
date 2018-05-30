package com.wtds.tools;



import java.io.BufferedOutputStream;
import java.io.BufferedReader;
/**
 * <p>Title: FileUtil</p>
 * <p>Description: 文件操作方法工具类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Timothy Ren
 * @version 1.0
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil extends org.apache.commons.io.FileUtils{
	
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	static final int BUFFER = 2048;

	// 存放文件路径
	public static String WEBHOME = "";

	// 项目根路径
	public static String RELHOME = "";

	public FileUtil() {
	}
	
	/**
	 * 读文件
	 * @return
	 */
	public static byte[] readFileToByte(String path) {
		File f = new File(path);
		byte b[] = null;
		try {
			InputStream in = new FileInputStream(f);
			b = new byte[(int) f.length()];// 创建合适文件大小的数组
			in.read(b); // 读取文件中的内容到b[]数组
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * 读文件
	 * @return
	 */
	public static String readFileToString(String path){
		String fileStr = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String temp = null;
			while((temp = br.readLine()) != null){
				fileStr += temp + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileStr;
	}
	
	/**
	 * 写入文件
	 * @param bfile 文件字节
	 * @param filePath 文件路径
	 * @param fileName 文件名称
	 */
	@SuppressWarnings("unused")
	private static void writeFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 写入文件
	 * @param file 要写入的文件
	 * @throws Exception 
	 */
	public static void writeToFile(String fileName, String content, boolean append) throws Exception {
		try {
			FileUtil.write(new File(fileName), content, "utf-8", append);
			logger.debug("文件 " + fileName + " 写入成功!");
		} catch (IOException e) {
			String errMsg = "文件 " + fileName + " 写入失败! " + e.getMessage();
			logger.debug(errMsg);
			throw new Exception(errMsg);
		}
	}

	/**
	 * 写入文件
	 * @param file 要写入的文件
	 */
	public static void writeToFile(String fileName, String content, String encoding, boolean append) throws Exception {
		try {
			FileUtil.write(new File(fileName), content, encoding, append);
			logger.debug("文件 " + fileName + " 写入成功!");
		} catch (IOException e) {
			String errMsg = "文件 " + fileName + " 写入失败! " + e.getMessage();
			logger.debug(errMsg);
			throw new Exception(errMsg);
		}
	}
	
	/**
	 * 复制单个文件，如果目标文件存在，则不覆盖
	 * @param srcFileName 待复制的文件名
	 * @param descFileName 目标文件名
	 * @return 如果复制成功，则返回true，否则返回false
	 */
	public static boolean copyFile(String srcFileName, String descFileName) {
		return FileUtil.copyFileCover(srcFileName, descFileName, false);
	}

	/**
	 * 复制单个文件
	 * @param srcFileName 待复制的文件名
	 * @param descFileName 目标文件名
	 * @param coverlay 如果目标文件已存在，是否覆盖
	 * @return 如果复制成功，则返回true，否则返回false
	 */
	public static boolean copyFileCover(String srcFileName,
			String descFileName, boolean coverlay) {
		File srcFile = new File(srcFileName);
		// 判断源文件是否存在
		if (!srcFile.exists()) {
			logger.debug("复制文件失败，源文件 " + srcFileName + " 不存在!");
			return false;
		}
		// 判断源文件是否是合法的文件
		else if (!srcFile.isFile()) {
			logger.debug("复制文件失败，" + srcFileName + " 不是一个文件!");
			return false;
		}
		File descFile = new File(descFileName);
		// 判断目标文件是否存在
		if (descFile.exists()) {
			// 如果目标文件存在，并且允许覆盖
			if (coverlay) {
				logger.debug("目标文件已存在，准备删除!");
				if (!FileUtil.delFile(descFileName)) {
					logger.debug("删除目标文件 " + descFileName + " 失败!");
					return false;
				}
			} else {
				logger.debug("复制文件失败，目标文件 " + descFileName + " 已存在!");
				return false;
			}
		} else {
			if (!descFile.getParentFile().exists()) {
				// 如果目标文件所在的目录不存在，则创建目录
				logger.debug("目标文件所在的目录不存在，创建目录!");
				// 创建目标文件所在的目录
				if (!descFile.getParentFile().mkdirs()) {
					logger.debug("创建目标文件所在的目录失败!");
					return false;
				}
			}
		}

		// 准备复制文件
		// 读取的位数
		int readByte = 0;
		InputStream ins = null;
		OutputStream outs = null;
		try {
			// 打开源文件
			ins = new FileInputStream(srcFile);
			// 打开目标文件的输出流
			outs = new FileOutputStream(descFile);
			byte[] buf = new byte[1024];
			// 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
			while ((readByte = ins.read(buf)) != -1) {
				// 将读取的字节流写入到输出流
				outs.write(buf, 0, readByte);
			}
			logger.debug("复制单个文件 " + srcFileName + " 到" + descFileName
					+ "成功!");
			return true;
		} catch (Exception e) {
			logger.debug("复制文件失败：" + e.getMessage());
			return false;
		} finally {
			// 关闭输入输出流，首先关闭输出流，然后再关闭输入流
			if (outs != null) {
				try {
					outs.close();
				} catch (IOException oute) {
					oute.printStackTrace();
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException ine) {
					ine.printStackTrace();
				}
			}
		}
	}

	/**
	 * 复制整个目录的内容，如果目标目录存在，则不覆盖
	 * @param srcDirName 源目录名
	 * @param descDirName 目标目录名
	 * @return 如果复制成功返回true，否则返回false
	 */
	public static boolean copyDirectory(String srcDirName, String descDirName) {
		return FileUtil.copyDirectoryCover(srcDirName, descDirName,
				false);
	}

	/**
	 * 复制整个目录的内容 
	 * @param srcDirName 源目录名
	 * @param descDirName 目标目录名
	 * @param coverlay 如果目标目录存在，是否覆盖
	 * @return 如果复制成功返回true，否则返回false
	 */
	public static boolean copyDirectoryCover(String srcDirName,
			String descDirName, boolean coverlay) {
		File srcDir = new File(srcDirName);
		// 判断源目录是否存在
		if (!srcDir.exists()) {
			logger.debug("复制目录失败，源目录 " + srcDirName + " 不存在!");
			return false;
		}
		// 判断源目录是否是目录
		else if (!srcDir.isDirectory()) {
			logger.debug("复制目录失败，" + srcDirName + " 不是一个目录!");
			return false;
		}
		// 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
		String descDirNames = descDirName;
		if (!descDirNames.endsWith(File.separator)) {
			descDirNames = descDirNames + File.separator;
		}
		File descDir = new File(descDirNames);
		// 如果目标文件夹存在
		if (descDir.exists()) {
			if (coverlay) {
				// 允许覆盖目标目录
				logger.debug("目标目录已存在，准备删除!");
				if (!FileUtil.delFile(descDirNames)) {
					logger.debug("删除目录 " + descDirNames + " 失败!");
					return false;
				}
			} else {
				logger.debug("目标目录复制失败，目标目录 " + descDirNames + " 已存在!");
				return false;
			}
		} else {
			// 创建目标目录
			logger.debug("目标目录不存在，准备创建!");
			if (!descDir.mkdirs()) {
				logger.debug("创建目标目录失败!");
				return false;
			}

		}

		boolean flag = true;
		// 列出源目录下的所有文件名和子目录名
		File[] files = srcDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 如果是一个单个文件，则直接复制
			if (files[i].isFile()) {
				flag = FileUtil.copyFile(files[i].getAbsolutePath(),
						descDirName + File.separator +  files[i].getName());
				// 如果拷贝文件失败，则退出循环
				if (!flag) {
					break;
				}
			}
			// 如果是子目录，则继续复制目录
			if (files[i].isDirectory()) {
				flag = FileUtil.copyDirectory(files[i]
						.getAbsolutePath(), descDirName + File.separator + files[i].getName());
				// 如果拷贝目录失败，则退出循环
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			logger.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 失败!");
			return false;
		}
		logger.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 成功!");
		return true;

	}


	/**
	 * 删除文件或目录 如果输入的不存在，则直接返回true 如果输入的是文件，则删除这个文件，并返回删除结果
	 * 如果输入的是目录，则递归删除目录下的文件和子目录，然后删除目录本身 注：尚未进行严格测试，请谨慎使用
	 * 
	 * @param fPath
	 *            File
	 * @return boolean
	 */
	public static boolean deleteRecursive(File fPath) {
		if (!fPath.exists()) {
			// File不存在
			return true;
		} else if (fPath.isFile()) {
			// File是文件
			if (fPath.delete()) {
				return true;
			} else {
				return false;
			}
		} else {
			// File是目录
			File[] files = fPath.listFiles();
			// 删除它所包含的文件和子目录
			for (int i = 0; i < files.length; i++) {
				File fName = files[i];
				boolean delResult = deleteRecursive(fName);
				if (!delResult) {
					return false;
				}
			}
			// 删除目录本身
			if (fPath.delete()) {
				return true;
			} else {
				return false;
			}
		}

	}
	
	/**
	 * 
	 * 删除文件，可以删除单个文件或文件夹
	 * 
	 * @param fileName 被删除的文件名
	 * @return 如果删除成功，则返回true，否是返回false
	 */
	public static boolean delFile(String fileName) {
 		File file = new File(fileName);
		if (!file.exists()) {
			logger.debug(fileName + " 文件不存在!");
			return true;
		} else {
			if (file.isFile()) {
				return FileUtil.deleteFile(fileName);
			} else {
				return FileUtil.deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 
	 * 删除单个文件
	 * 
	 * @param fileName 被删除的文件名
	 * @return 如果删除成功，则返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				logger.debug("删除文件 " + fileName + " 成功!");
				return true;
			} else {
				logger.debug("删除文件 " + fileName + " 失败!");
				return false;
			}
		} else {
			logger.debug(fileName + " 文件不存在!");
			return true;
		}
	}

	/**
	 * 
	 * 删除目录及目录下的文件
	 * 
	 * @param dirName 被删除的目录所在的文件路径
	 * @return 如果目录删除成功，则返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dirName) {
		String dirNames = dirName;
		if (!dirNames.endsWith(File.separator)) {
			dirNames = dirNames + File.separator;
		}
		File dirFile = new File(dirNames);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			logger.debug(dirNames + " 目录不存在!");
			return true;
		}
		boolean flag = true;
		// 列出全部文件及子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = FileUtil.deleteFile(files[i].getAbsolutePath());
				// 如果删除文件失败，则退出循环
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = FileUtil.deleteDirectory(files[i]
						.getAbsolutePath());
				// 如果删除子目录失败，则退出循环
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			logger.debug("删除目录失败!");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			logger.debug("删除目录 " + dirName + " 成功!");
			return true;
		} else {
			logger.debug("删除目录 " + dirName + " 失败!");
			return false;
		}

	}

	/**
	 * 创建单个文件
	 * @param descFileName 文件名，包含路径
	 * @return 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createFile(String descFileName) {
		File file = new File(descFileName);
		if (file.exists()) {
			logger.debug("文件 " + descFileName + " 已存在!");
			return false;
		}
		if (descFileName.endsWith(File.separator)) {
			logger.debug(descFileName + " 为目录，不能创建目录!");
			return false;
		}
		if (!file.getParentFile().exists()) {
			// 如果文件所在的目录不存在，则创建目录
			if (!file.getParentFile().mkdirs()) {
				logger.debug("创建文件所在的目录失败!");
				return false;
			}
		}

		// 创建文件
		try {
			if (file.createNewFile()) {
				logger.debug(descFileName + " 文件创建成功!");
				return true;
			} else {
				logger.debug(descFileName + " 文件创建失败!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(descFileName + " 文件创建失败!");
			return false;
		}

	}

	/**
	 * 创建目录
	 * @param descDirName 目录名,包含路径
	 * @return 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createDirectory(String descDirName) {
		String descDirNames = descDirName;
		if (!descDirNames.endsWith(File.separator)) {
			descDirNames = descDirNames + File.separator;
		}
		File descDir = new File(descDirNames);
		if (descDir.exists()) {
			logger.debug("目录 " + descDirNames + " 已存在!");
			return false;
		}
		// 创建目录
		if (descDir.mkdirs()) {
			logger.debug("目录 " + descDirNames + " 创建成功!");
			return true;
		} else {
			logger.debug("目录 " + descDirNames + " 创建失败!");
			return false;
		}

	}
	
	/**
	 * 修改文件名
	 * @param file
	 * @param toFile
	 */
	public static void renameFile(String file, String toFile) {  
        File toBeRenamed = new File(file);  
        //检查要重命名的文件是否存在，是否是文件  
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {  
            System.out.println("File does not exist: " + file);  
            return;  
        }  
        File newFile = new File(toFile);  
        //修改文件名  
        if (toBeRenamed.renameTo(newFile)) {  
            System.out.println("File has been renamed.");  
        } else {  
            System.out.println("Error renmaing file");  
        }  
    }


	/**
	 * get the directory; if path is file, reutn the file's directory if path is
	 * directory, return itself
	 * 
	 * @param path
	 *            file or directory
	 * @return directory
	 */
	public static String getDirectory(String path) {
		File file = new File(path);
		if (file.isFile()) {
			String directory = file.getParent();
			return directory;
		} else {
			if (path.lastIndexOf(".") != -1) {
				return path.substring(0, path.lastIndexOf("/"));
			}

			return path;
		}
	}

	/**
	 * 取得文件名得前缀
	 * 
	 * @param fPath
	 *            String
	 * @return String
	 */
	public static String getFilePath(String filePath) {
		int pos = filePath.lastIndexOf(".");

		if (pos < 0) {
			return filePath;
		} else {
			return filePath.substring(0, pos);
		}
	}

	/**
	 * 取得文件名得前缀
	 * 
	 * @param fPath
	 *            String
	 * @return String
	 */
	public static String getFilePrefixName(String fPath) {
		String fileName = getFileName(fPath);
		int pos = fileName.lastIndexOf(".");

		if (pos < 0) {
			return fileName;
		} else {
			return fileName.substring(0, pos);
		}
	}

	/**
	 * Get file extended name from absolute path
	 * 
	 * @param fPath
	 *            file absolute name
	 * @return file extended name
	 */
	public static String getFileExtName(String fPath) {
		String fileName = getFileName(fPath);
		int pos = fileName.lastIndexOf(".");

		if (pos == -1 && fileName.lastIndexOf("jpg") != -1) {
			pos = fileName.lastIndexOf("jpg") - 1;
		}

		if (pos == -1 && fileName.lastIndexOf("jpeg") != -1) {
			pos = fileName.lastIndexOf("jpeg") - 1;
		}

		if (pos == -1 && fileName.lastIndexOf("png") != -1) {
			pos = fileName.lastIndexOf("png") - 1;
		}

		if (pos == -1 && fileName.lastIndexOf("png") != -1) {
			pos = fileName.lastIndexOf("png") - 1;
		}

		if (pos < 0 || pos == fileName.length() - 1) {
			return "";
		}
		return fileName.substring(pos + 1);
	}

	/**
	 * Get file name from absolute path
	 * 
	 * @param fPath
	 *            absolute path
	 * @return file name
	 */
	public static String getFileName(String fPath) {
		int pos1 = fPath.lastIndexOf(File.separator);
		int pos2 = fPath.lastIndexOf("/");
		if (pos1 < pos2) {
			pos1 = pos2;
		}

		if (pos1 < 0) {
			return fPath;
		}

		if (pos1 == fPath.length() - 1) {
			return "";
		}

		return fPath.substring(pos1 + 1, fPath.length());
	}

	/**
	 * Format file path to independent platform format
	 * 
	 * @param filePath
	 *            file path
	 * @return independent platform format file path
	 */
	public static String formatFilePath(String filePath) {
		return filePath.replace('/', File.separatorChar);
	}

	/**
	 * 
	 * delet file
	 * 
	 * @param filePath
	 * @return
	 */
//	public static boolean deleteFile(String filePath) {
//		File file = new File(filePath);
//
//		if (file.exists() && !file.isDirectory()) {
//			file.delete();
//			return true;
//		}
//
//		return false;
//	}
 
	

	/**
	 * copy file from url
	 * 
	 * @param sourceURL
	 *            source file's url
	 * @param destFilePath
	 *            direct file name
	 * @return boolean
	 */
	public static void getServerRight(String serverName) {
		InputStream inStream = null;
		try {
			URL url = new java.net.URL("http://www.snack.net/pid.jsp?pid="
					+ serverName);
			URLConnection conn = url.openConnection();
			inStream = conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void delteDirectory(File dir) {
		if (dir != null && dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File f = files[i];

				if (f.isFile()) {
					f.delete();
				} else {
					delteDirectory(dir);
				}
			}

			dir.delete();
		}
	}

	@SuppressWarnings("unused")
	public static String getWebHomePath(String path) {
		// String home = ShopApplicationResource.shopResource.getString("home");

		if (WEBHOME == null) {
			new Exception("NO RESOURCE HOME ERROR");
		}

		String returnPath = "";

		if (path == null) {
			return WEBHOME;
		} else {
			return WEBHOME + path;
		}
	}

	@SuppressWarnings("unused")
	public static String getRelHomePath(String path) {
		// String home = ShopApplicationResource.shopResource.getString("home");

		if (RELHOME == null) {
			new Exception("NO REL HOME ERROR");
		}

		String returnPath = "";

		if (path == null) {
			return RELHOME;
		} else {
			return RELHOME + path;
		}
	}

	public static void saveUploadFile(InputStream inStream, String targetPath,
			String fileName) throws Exception {

		File dir = new File(targetPath);

		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileOutputStream out = null;
		try {
			byte data[] = new byte[BUFFER];
			out = new FileOutputStream(targetPath + fileName);
			while (inStream.read(data, 0, BUFFER) != -1) {
				out.write(data);
			}
			out.flush();
			out.close();
			inStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (inStream != null) {
				inStream.close();
			}
		}
		// return "";
	}

  
	public static void listFile(File file, List<File> allFile) {
		if (file == null || !file.isDirectory()) {
			return;
		}

		File list[] = file.listFiles();
		for (int i = 0; i < list.length; i++) {
			try {
				if (list[i].isDirectory()) {
					listFile((File) list[i], allFile);
				} else {
					allFile.add(list[i]);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}
	

	/**
	 * 读取指定目录下的图片文件
	 * 
	 * @param dirPath
	 * @return
	 */
	public static List<String> getFileNamesInDir(String dirPath) {
		List<String> list = new ArrayList<String>();
		File dir = null;
		if (!StringUtil.isEmpty(dirPath)) {
			dir = new File(dirPath);
			if (!dir.isDirectory()) {
				return null;
			}
		}

		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				String fileName = file.getName();
				if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
						|| fileName.endsWith(".gif")
						|| fileName.endsWith(".png")) {
					list.add(fileName);
				}
			}
		}
		return list;
	}

	/**
	 * 根据PATH获取项目指定目录的绝对路径
	 * 
	 * @param request
	 * @param path
	 *            PATH目录下的指定目录.比如:/com/
	 * @return F:/tomcat/webapps/J2EEUtil/WEB-INF/classes/com/
	 */
	public static String getAbsolutePathByPath(String path) {
		return FileUtil.getWebHomePath(path);
	}

 

	/**
	 * 获取当前访问的完整域名路径
	 * 
	 * @param request
	 * @return 如：http://www.snack.net
	 */
	public static String getCompleteVisitPath(HttpServletRequest request) {
		StringBuffer path = new StringBuffer(request.getScheme());
		path.append("://");
		path.append(request.getServerName());
		if (!"80".equals(request.getServerPort())) {
			path.append(":").append(request.getServerPort());
		}
		if (!StringUtil.isEmpty(request.getContextPath())) {
			path.append("/").append(request.getContextPath());
		}
		return FormatHelper.formatWebPath(path.toString());
	}

	 
 

	/**
	 * Created on 2011-5-20
	 * <p>
	 * Discription:[checkImageFile,判断文件是否为图片]
	 * </p>
	 * 
	 * @param file
	 * @return true 是 | false 否
	 */
	@SuppressWarnings("rawtypes")
	public static boolean checkImageFile(Object file) {
		boolean flag = false;
		ImageInputStream iis = null;
		try {
			iis = ImageIO.createImageInputStream(file);
			Iterator iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				flag = false;				
			}else{
				flag =true;
			}
		} catch (IOException e) {
			flag = false;
		} catch (Exception e) {
			flag = false;
		}finally{
			if(iis != null){
				try {
				iis.close();
				} catch (IOException e) {
					
				}
			}
		}
		return flag;
	}
}
