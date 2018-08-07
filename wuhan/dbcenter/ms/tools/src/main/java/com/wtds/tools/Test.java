package com.wtds.tools;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Test {
	static int i = 0;
	public static void main(String[] args) throws Exception{
		
		Udp udp = new Udp(22002);
		udp.receiveMessage(new UdpCallback() {
			@Override
			public void getDatagramPacket(String msg,DatagramPacket pack) {
				System.out.println(i+"|"+msg);
				i++;
			}
		});
		
//		while(true){
//			DatagramPacket dp = udp.receiveBroadCast(22002, 512);
//			System.out.println("接收1:"+new String(dp.getData()));
//		}
		
		//remsg();
	}
	
	/** 
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节) 
     *  
     * @param bytes 
     * @return 
     */  
    public static String bytes2kb(long bytes) {  
        BigDecimal filesize = new BigDecimal(bytes);  
        BigDecimal megabyte = new BigDecimal(1024 * 1024);  
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        if (returnValue > 1)  
            return (returnValue + "MB");  
        BigDecimal kilobyte = new BigDecimal(1024);  
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        return (returnValue + "KB");  
    }  
	
	static DatagramSocket socket = null;
	public static void remsg() throws SocketException {
		// 1、创建DatagramSocket;
		socket = new DatagramSocket(22002);
		new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						new Test().receiveMessage(22002, new UdpCallback() {
							@Override
							public void getDatagramPacket(String msg, DatagramPacket pack) {
								// TODO Auto-generated method stub
								
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}
	
	public void receiveMessage(int port, final UdpCallback callback) throws IOException {

		// 2、创建数据包，用于接收内容。
		byte[] buf = new byte[512];
		final DatagramPacket packet = new DatagramPacket(buf, buf.length);

		// 3、接收数据
		socket.receive(packet);
		new Thread(new Runnable() {
			@Override
			public void run() {
				callback.getDatagramPacket(new String(packet.getData()),packet);
			}
		}).start();

	}
}
