package com.wtds.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Object序列/反序列
 * @author wyj
 *
 */
public abstract class ObjectSerialize implements Serializable{
	
	private static final long serialVersionUID = 10000000031L;

	public abstract Object objectInstance();
	
	String serializeHome = System.getProperty("user.dir") + "/temp/serialize";
	File serializeHomeFile; 
	
	String serializeName;
	
	private void initHome(){
		serializeHomeFile = new File(serializeHome); 
		if(!serializeHomeFile.isDirectory()){
			FileUtil.createDirectory(serializeHome);
		}
	}
	
	/**
	 * 序列化
	 * @return
	 * @throws Exception
	 */
	public String serialize() throws Exception {
		initHome();
		String path = serializeHome + "/" + objectInstance().toString();
		return serialize(path, objectInstance());
	}
	
	/**
	 * 反序列化
	 * @return
	 * @throws Exception
	 */
	public Object reSerialize() throws Exception{
		return reSerialize(serializeName);
	}
	
	/**
	 * 序列化
	 * @return
	 * @throws Exception
	 */
	public static String serialize(String serializePath,Object obj) throws Exception {
		
		String path = serializePath;
		File file = new File(path);
		FileUtil.createFile(path);
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		fos.close();
		return path;
	}
	
	/**
	 * 反序列化
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static Object reSerialize(String path) throws Exception {
		File file = new File(path);
		if(!file.exists()){
			throw new Exception("反序列对象不存在");
		}
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object st1 = ois.readObject();
		ois.close();
		fis.close();
		return st1;
	}
}
