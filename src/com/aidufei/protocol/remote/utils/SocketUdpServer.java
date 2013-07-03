package com.aidufei.protocol.remote.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SocketUdpServer {
	SocketUtils su = null;

	public static void testUdpReceiver() {
		new Thread(new Runnable() {
			DatagramSocket ds;

			public void run() {
				try {
					this.ds = new DatagramSocket(8822);
					byte[] buf = new byte[2048];
					DatagramPacket dp = new DatagramPacket(buf, 2048);
					while (true) {
						this.ds.receive(dp);

						System.out.println("-----------------"
								+ dp.getAddress().getHostAddress());
						System.out.println(new String(buf, 0, dp.getLength()));
					}
				} catch (SocketException e) {
					System.out.println("socket Exception.....................");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("IO Exception ..................... ");
					e.printStackTrace();
				} finally {
					if (this.ds != null)
						this.ds.close();
				}
			}
		}).start();
	}
}
