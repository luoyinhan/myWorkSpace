package com.aidufei.remote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.util.Log;

import com.coship.ott.utils.LogUtils;

public class RemoteTouch {
	private DatagramSocket socket = null;
	private String mDeviceIP = null;
	public static final int TOUCH_EVENT_DOWN = 0;
	public static final int TOUCH_EVENT_UP = 1;
	public static final int TOUCH_EVENT_MOVE = 2;

	public RemoteTouch(String devIP) {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), e.getMessage());
		}
		mDeviceIP = devIP;
	}

	protected void destory() {
		this.socket.close();
	}

	public void sendTouchEvent(int touchEvent, float sendX, float sendY) {
		LogUtils.trace(Log.DEBUG, "RemoteTouch", "send touch event type:"
				+ touchEvent + " sendX:" + sendX + " sendY:" + sendY);
		TouchRequest requestMove = new TouchRequest();
		requestMove.setTouchAction((short) touchEvent);
		requestMove.setTouchLocation(sendX, sendY);
		UDPCilent.send(this.socket, mDeviceIP, requestMove);
	}

	public void sendMultiTouchEvent(MultiTouchInfo info) {
		int server_port = 8823;
		InetAddress local = null;
		try {
			local = InetAddress.getByName(mDeviceIP);
			byte[] msg = getMultiTouchByteData(info);
			DatagramPacket p = new DatagramPacket(msg, msg.length, local,
					server_port);

			this.socket.send(p);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] getMultiTouchByteData(MultiTouchInfo info) {
		ByteBuffer msgbuf = null;
		byte[] msg = null;
		short clickType = 263;

		msg = new byte[64];
		msgbuf = ByteBuffer.allocate(70);
		msgbuf.putInt(Integer.reverseBytes(info.getFingerNum()));
		for (int i = 0; i < 5; ++i) {
			if (i < info.getFingerNum()) {
				FingerInfo fin = info.getFingerInfo(i);
				msgbuf.putInt(Integer.reverseBytes(fin.getX()));
				msgbuf.putInt(Integer.reverseBytes(fin.getY()));
				msgbuf.putInt(Integer.reverseBytes(fin.getPress()));
				LogUtils.trace(Log.DEBUG, "RemoteTouch",
						"finger " + i + " x:" + fin.getX() + "y: " + fin.getY()
								+ "press: " + fin.getPress());
			} else {
				msgbuf.putInt(0);
				msgbuf.putInt(0);
				msgbuf.putInt(0);
			}

		}

		if ((msgbuf != null) && (msg != null)) {
			LogUtils.trace(Log.DEBUG, "RemoteTouch", "UDP msgLen=" + msg.length
					+ ",msgbuf.capacity()=" + msgbuf.capacity());

			msgbuf.rewind();
			msgbuf.get(msg, 0, msg.length);
			LogUtils.trace(Log.DEBUG, "RemoteTouch", "after get UDP msgLen="
					+ msg.length);
			msgbuf.clear();
		}
		return msg;
	}
}
