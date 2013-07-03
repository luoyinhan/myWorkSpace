package com.aidufei.protocol.remote.utils;

import android.util.Log;
import com.aidufei.protocol.remote.message.Request;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class UDPCilent {
	public static final String TAG = "UDPClient";

	public static void send(DatagramSocket s, String remoteHostIp, Request req) {
		int server_port = 8822;
		InetAddress local = null;
		try {
			local = InetAddress.getByName(remoteHostIp);
			byte[] msg = DataToByte.getRequestByteData(req);
			DatagramPacket p = new DatagramPacket(msg, msg.length, local,
					server_port);

			s.send(p);
			Log.e("UDPClient", "send request :" + req + " success!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void send(DatagramSocket s, int eventType,
			String remoteHostIp, float pointX, float pointY, float pointZ) {
		int server_port = 11021;
		InetAddress local = null;
		try {
			local = InetAddress.getByName(remoteHostIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		byte[] msg = new DataToByte().getMsgByteArray(eventType, pointX,
				pointY, pointZ);

		DatagramPacket p = new DatagramPacket(msg, msg.length, local,
				server_port);
		try {
			s.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] floatToByte(float v) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		byte[] ret = new byte[4];
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(v);
		bb.get(ret);
		return ret;
	}
}
