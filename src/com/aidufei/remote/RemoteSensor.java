package com.aidufei.remote;

import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;

import com.coship.ott.utils.LogUtils;

public class RemoteSensor {
	private DatagramSocket socket;
	private String mDeviceIP = null;
	public static final int SENSORS_ACCECTRTION = 1;
	public static final int SENSORS_ORIENTAION = 3;
	public static final int SENSORS_MAGNETIC_FIELD = 4;
	public static final int SENSORS_TEMPRATURE = 8;

	public RemoteSensor(String devIP) {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		mDeviceIP = devIP;
	}

	public void setRemote(String ip) {
		mDeviceIP = ip;
	}

	protected void destroy() {
		this.socket.close();
	}

	public void sendSensorEvent(int sensorType, float sendX, float sendY,
			float sendZ) {
		UDPCilent.send(this.socket, sensorType, mDeviceIP, sendX, sendY, sendZ);
		LogUtils.trace(Log.DEBUG, "SensorActivity", "send sensor event type:"
				+ sensorType + " sendX:" + sendX + " sendY:" + sendY + " sendZ"
				+ sendZ);
	}
}
