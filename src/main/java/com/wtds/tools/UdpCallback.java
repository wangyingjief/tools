package com.wtds.tools;

import java.net.DatagramPacket;

public interface UdpCallback {
	public void getDatagramPacket(String msg,DatagramPacket pack);
}
