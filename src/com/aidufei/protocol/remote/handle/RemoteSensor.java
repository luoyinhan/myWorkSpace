package com.aidufei.protocol.remote.handle;

import android.util.Log;
import com.aidufei.protocol.remote.utils.SocketUtils;
import com.aidufei.protocol.remote.utils.UDPCilent;
import java.net.DatagramSocket;
import java.net.SocketException;

public class RemoteSensor {
	private static final String TAG = "SensorActivity";
	private DatagramSocket socket;
	public static final int SENSORS_ACCECTRTION = 1;
	public static final int SENSORS_ORIENTAION = 3;
	public static final int SENSORS_MAGNETIC_FIELD = 4;
	public static final int SENSORS_TEMPRATURE = 8;

	public RemoteSensor(DeviceInfo dev) {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	protected void destroy() {
		this.socket.close();
	}

	public void sendSensorEvent(int sensorType, float sendX, float sendY,
			float sendZ) {
		UDPCilent.send(this.socket, sensorType, SocketUtils.socketIP, sendX,
				sendY, sendZ);
		Log.e("SensorActivity", "send sensor event type:" + sensorType
				+ " sendX:" + sendX + " sendY:" + sendY + " sendZ" + sendZ);
	}
}
