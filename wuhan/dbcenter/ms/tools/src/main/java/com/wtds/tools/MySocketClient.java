package com.wtds.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MySocketClient {

	String address;

	int port;

	Socket socket;

	OutputStream outputStream;

	InputStream inputStream;

	public MySocketClient(String address, int port) {
		this.address = address;
		this.port = port;
	}

	public String SendSingleCommand(String command) {
		String result = "";
		try {
			socket = new Socket(address, port);
			// 建立连接后获得输出流
			outputStream = socket.getOutputStream();
			socket.getOutputStream().write(command.getBytes("UTF-8"));
			// 通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
			socket.shutdownOutput();
			inputStream = socket.getInputStream();
			byte[] bytes = new byte[1024];
			int len;
			StringBuilder sb = new StringBuilder();
			while ((len = inputStream.read(bytes)) != -1) {
				// 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
				sb.append(new String(bytes, 0, len, "UTF-8"));
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				outputStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		MySocketClient client = new MySocketClient("127.0.0.1", 61870);
		System.out.println(client.SendSingleCommand("show"));
	}

}
