package com.wtds.zk.simplemq.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Random;

import com.wtds.tools.Udp;

public class _Test5 {

	static int i = 1;
	static long s1 = 0;

	public static void main(String[] args) throws IOException {
		for(int i = 0;i<100;i++) {
			Random random = new Random();
			int x = random.nextInt(2);
			System.out.println(x);
		}
		
		
//		Udp udp = new Udp(1258);
//		udp.receiveMessage((String msg, DatagramPacket pack) -> {
//			if (i == 1) {
//				s1 = System.currentTimeMillis();
//			}
//			i++;
//			if (i == 10000) {
//				long s2 = System.currentTimeMillis();
//				System.err.println("time======>" + (s2 - s1));
//				i = 1;
//			}
//			//System.out.println(msg);
//		});
//
//		for (int i = 0; i < 50000; i++) {
//			udp.sendMessage("127.0.0.1", "aaaaaaaaaa", 0);
//		}

	}
}
