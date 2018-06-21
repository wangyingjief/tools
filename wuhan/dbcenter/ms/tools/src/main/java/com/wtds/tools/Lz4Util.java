package com.wtds.tools;

import net.jpountz.lz4.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by beini on 2017/10/30. lz4:https://github.com/lz4/lz4-java
 */
public class Lz4Util {

	/**
	 * @param srcByte
	 *            原始数据
	 * @return 压缩后的数据
	 */
	public static byte[] compressedByte(byte[] srcByte) {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		LZ4Compressor compressor = factory.fastCompressor();
		return compressor.compress(srcByte);
	}

	/**
	 * @param compressorByte
	 *            压缩后的数据
	 * @param srcLength
	 *            压缩前的数据长度
	 * @return
	 */
	public static byte[] decompressorByte(byte[] compressorByte, int srcLength) {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		LZ4FastDecompressor decompressor = factory.fastDecompressor();
		return decompressor.decompress(compressorByte, srcLength);
	}

	/**
	 * @param srcByte
	 * @param blockSize
	 *            一次压缩的大小 取值范围 64 字节-32M之间
	 * @return
	 * @throws IOException
	 */
	public static byte[] lz4Compress(byte[] srcByte, int blockSize) throws IOException {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		LZ4Compressor compressor = factory.fastCompressor();
		LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(byteOutput, blockSize, compressor);
		compressedOutput.write(srcByte);
		compressedOutput.close();
		return byteOutput.toByteArray();
	}

	/**
	 * @param compressorByte
	 * @param blockSize
	 *            一次压缩的大小 取值范围 64 字节-32M之间
	 * @return
	 * @throws IOException
	 */
	public static byte[] lz4Decompress(byte[] compressorByte, int blockSize) throws IOException {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(blockSize);
		LZ4FastDecompressor decompresser = factory.fastDecompressor();
		LZ4BlockInputStream lzis = new LZ4BlockInputStream(new ByteArrayInputStream(compressorByte), decompresser);
		int count;
		byte[] buffer = new byte[blockSize];
		while ((count = lzis.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}
		lzis.close();
		return baos.toByteArray();
	}

	/**
	 * File to byte[]
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] returnFileByte(String filePath) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		FileChannel channel = fileInputStream.getChannel();
		ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
		channel.read(byteBuffer);
		return byteBuffer.array();
	}

	/**
	 * createFile
	 *
	 * @param fileByte
	 * @param filePath
	 */
	public static void createFile(byte[] fileByte, String filePath) {
		BufferedOutputStream bufferedOutputStream;
		FileOutputStream fileOutputStream;
		File file = new File(filePath);
		try {
			fileOutputStream = new FileOutputStream(file);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			bufferedOutputStream.write(fileByte);
			fileOutputStream.close();
			bufferedOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String aString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n"
				+ "<mapping sourceDb=\"sourcdDb\" sourceTable=\"sourceTable\" sourceType=\"sourceType\" targetType=\"targetType\">\r\n"
				+ "    <dic>\r\n" + "        <map name=\"mapName1\">\r\n"
				+ "            <content key=\"key1\" value=\"value1\"/>\r\n"
				+ "            <content key=\"key2\" value=\"value2\"/>\r\n" + "        </map>\r\n" + "    </dic>\r\n"
				+ "    <row>\r\n"
				+ "        <column delete=\"false\" insert=\"true\" name=\"tName\" pk=\"pk\" update=\"false\">\r\n"
				+ "            <target dataType=\"ty\" db=\"db1\" field=\"f1\" table=\"table1\">\r\n"
				+ "                <formatter value=\"yyyy-MM-dd hh:mm:ss\"/>\r\n"
				+ "                <relevant s=\"s1\" t=\"t1\"/>\r\n" + "            </target>\r\n"
				+ "        </column>\r\n" + "    </row>\r\n" + "</mapping>";
		int len = aString.getBytes().length;
		System.out.println(len);
		byte[] y1 = compressedByte(aString.getBytes());
		System.out.println(y1.length);
		
		byte[] y2 = decompressorByte(y1,len);
		System.out.println(new String(y2));
	}
}
