package com.aidufei.protocol.remote.message;

import android.util.Log;
import com.aidufei.protocol.remote.handle.DeviceInfo;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class MulityBroadcast {
	private String TAG;
	private String multicastIp;
	private int multicastPort;
	public static ArrayList<DeviceInfo> deviceList = null;
	public static ArrayList<String> ipList;
	public static final int SEARCH_TIME = 3000;//5000;
	MulticastSocket socket;
	InetAddress multicastGroup;

	public MulityBroadcast() {
		this.TAG = "MulityBroadcast";

		this.multicastIp = "239.255.255.100";
		this.multicastPort = 2012;
	}

	public ArrayList<DeviceInfo> receiveDeviceList() {
		long startTime = System.currentTimeMillis();
		long endTime = startTime;
		DatagramPacket datagram = null;

		byte[] buffer = new byte[500];
		try {
			if (deviceList == null) {
				deviceList = new ArrayList();
			}
			if (ipList == null) {
				ipList = new ArrayList();
			}
			deviceList.clear();
			ipList.clear();
			this.socket.setSoTimeout(SEARCH_TIME);
			while (endTime - startTime < SEARCH_TIME) {
				Log
						.e(this.TAG, "startTime:" + startTime + " endTime"
								+ endTime);
				datagram = new DatagramPacket(buffer, buffer.length);
				this.socket.receive(datagram);

				int port = datagram.getPort();
				DeviceInfo device = new DeviceInfo();
				String ip = datagram.getAddress().getHostAddress();
				device.setDeviceIP(ip);

				Log.e(this.TAG, "Receive MulityBroadcast ip:" + ip + ",port:"
						+ port);
				if ((ipList.indexOf(ip) == -1)
						&& (!ip.equals(getLocalIpAddress()))
					) {
					deviceList.add(device);
					ipList.add(ip);
					Log.e(this.TAG, "broadcastIps add " + ip);
				}
				endTime = System.currentTimeMillis();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e(this.TAG, "out the loop");
		return deviceList;
	}

	public String getLocalIpAddress() {
		try {
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				if(intf.getName().equals("ppp0"))
					continue;
				Enumeration enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();
					if (!inetAddress.isLoopbackAddress())
						return inetAddress.getHostAddress().toString();
				}
			}
		} catch (SocketException ex) {
			Log.e(this.TAG, ex.toString());
		}
		return null;
	}

	public ArrayList<DeviceInfo> sendBroadCast() {
		try {
			this.multicastGroup = InetAddress.getByName(this.multicastIp);
			this.socket = new MulticastSocket(this.multicastPort);
			this.socket.joinGroup(this.multicastGroup);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String msg = "MUAP_SEARCH!";
		try {
			DatagramPacket data = new DatagramPacket(msg.getBytes(), msg
					.length(), this.multicastGroup, this.multicastPort);

			for (int i = 0; i < 1; ++i) {
				this.socket.send(data);
				Log.e(this.TAG, i + " send broadcast to " + this.multicastIp
						+ ":" + this.multicastPort);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return receiveDeviceList();
	}
}
