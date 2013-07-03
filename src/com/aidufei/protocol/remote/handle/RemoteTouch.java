package com.aidufei.protocol.remote.handle;

import android.util.Log;
import com.aidufei.protocol.remote.message.TouchRequest;
import com.aidufei.protocol.remote.utils.FingerInfo;
import com.aidufei.protocol.remote.utils.MultiTouchInfo;
import com.aidufei.protocol.remote.utils.SocketUtils;
import com.aidufei.protocol.remote.utils.UDPCilent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class RemoteTouch {
	private static final String TAG = "RemoteTouch";
	private DatagramSocket socket = null;
	public static final int TOUCH_EVENT_DOWN = 0;
	public static final int TOUCH_EVENT_UP = 1;
	public static final int TOUCH_EVENT_MOVE = 2;

	public RemoteTouch(DeviceInfo dev) {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	protected void destory() {
		this.socket.close();
	}

	public void sendTouchEvent(int touchEvent, float sendX, float sendY) {
		Log.e("RemoteTouch", "send touch event type:" + touchEvent + " sendX:"
				+ sendX + " sendY:" + sendY);
		TouchRequest requestMove = new TouchRequest();
		requestMove.setTouchAction((short) touchEvent);
		requestMove.setTouchLocation(sendX, sendY);
		UDPCilent.send(this.socket, SocketUtils.socketIP, requestMove);
	}

	public void sendMultiTouchEvent(MultiTouchInfo info) {
		int server_port = 8823;
		InetAddress local = null;
		try {
			local = InetAddress.getByName(SocketUtils.socketIP);
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
				Log.e("RemoteTouch", "finger " + i + " x:" + fin.getX() + "y: "
						+ fin.getY() + "press: " + fin.getPress());
			} else {
				msgbuf.putInt(0);
				msgbuf.putInt(0);
				msgbuf.putInt(0);
			}

		}

		if ((msgbuf != null) && (msg != null)) {
			Log.e("RemoteTouch", "UDP msgLen=" + msg.length
					+ ",msgbuf.capacity()=" + msgbuf.capacity());

			msgbuf.rewind();
			msgbuf.get(msg, 0, msg.length);
			Log.e("RemoteTouch", "after get UDP msgLen=" + msg.length);
			msgbuf.clear();
		}
		return msg;
	}
}
