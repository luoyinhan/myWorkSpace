package com.aidufei.remote;

import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;

import com.coship.ott.utils.LogUtils;

public class RemoteMouse {
	private DatagramSocket socket = null;
	private String mDeviceIP = null;
	public static final int MOUSE_WHEEL_DOWN = 0;
	public static final int MOUSE_WHEEL_UP = 2;
	public static final short MOUSE_ACTION_MOVE = 256;
	public static final short MOUSE_RIGHT_SINGLE_CLICK = 513;
	public static final short MOUSE_RIGHT_DOUBLE_CLICK = 514;
	public static final short MOUSE_RIGHT_DOWN = 515;
	public static final short MOUSE_RIGHT_UP = 516;
	public static final short MOUSE_RGIHT_DOWN_MOVE = 517;
	public static final short MOUSE_LEFT_SINGLE_CLICK = 769;
	public static final short MOUSE_LEFT_DOUBLE_CLICK = 770;
	public static final short MOUSE_LEFT_DOWN = 771;
	public static final short MOUSE_LEFT_UP = 772;
	public static final short MOUSE_LEFT_DOWN_MOVE = 773;
	public static final short MOUSE_WHEEL = 774;

	public RemoteMouse(String devIP) {
		this.mDeviceIP = devIP;
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void setRemote(String ip) {
		mDeviceIP = ip;
	}

	protected void destory() {
		this.socket.close();
	}

	public void sendMouseWheelEvent(int WheelEvent) {
		MouseRequest request = new MouseRequest();
		request.setMouseClickType(MOUSE_WHEEL);
		if (WheelEvent == 0) {
			request.dx = -10.0F;
		} else if (WheelEvent == 2) {
			request.dx = 10.0F;
		}

		UDPCilent.send(this.socket, mDeviceIP, request);
	}

	public void sendMouseMoveEvent(int event_type, float sendX, float sendY) {
		LogUtils.trace(Log.DEBUG, "RemoteMouse", "send mouse move: sendX:"
				+ sendX + " sendY:" + sendY);
		MouseRequest mr = new MouseRequest();
		mr.setMouseClickType((short) event_type);
		mr.setDxDy(sendX, sendY);
		UDPCilent.send(this.socket, mDeviceIP, mr);
	}

	public void sendMouseClickEvent(int event_type) {
		if (event_type == MOUSE_RIGHT_SINGLE_CLICK) {
			RemoteKeyboard key = new RemoteKeyboard(this.mDeviceIP);
			key.remoteSendDownAndUpKeyCode(4);
		} else {
			MouseRequest mr = new MouseRequest();
			mr.setMouseClickType((short) event_type);
			UDPCilent.send(this.socket, mDeviceIP, mr);
		}
	}
}
