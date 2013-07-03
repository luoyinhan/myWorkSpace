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

public class RemoteKeyboard {
	int win_width = 0;
	int win_height = 0;
	private long startTime = 0L;
	private DatagramSocket socket = null;
	String mIP = null;
	private int KEYCODE_MUTE_DRIVER = 113;

	public RemoteKeyboard(String ip) {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		mIP = ip;
	}

	protected void destroy() {
		this.socket.close();
	}

	public void setRemote(String IP) {
		mIP = IP;
	}

	public void remoteSendDownAndUpKeyCode(int keycode) {
		KeyboardRequest request = new KeyboardRequest();
		int key_value = -1;
		boolean shift_press = false;
		switch (keycode) {
		case 133:
			key_value = 29;
			shift_press = true;
			break;
		case 134:
			key_value = 30;
			shift_press = true;
			break;
		case 135:
			key_value = 31;
			shift_press = true;
			break;
		case 136:
			key_value = 32;
			shift_press = true;
			break;
		case 137:
			key_value = 33;
			shift_press = true;
			break;
		case 138:
			key_value = 34;
			shift_press = true;
			break;
		case 139:
			key_value = 35;
			shift_press = true;
			break;
		case 140:
			key_value = 36;
			shift_press = true;
			break;
		case 141:
			key_value = 37;
			shift_press = true;
			break;
		case 142:
			key_value = 38;
			shift_press = true;
			break;
		case 143:
			key_value = 39;
			shift_press = true;
			break;
		case 144:
			key_value = 40;
			shift_press = true;
			break;
		case 145:
			key_value = 41;
			shift_press = true;
			break;
		case 146:
			key_value = 42;
			shift_press = true;
			break;
		case 147:
			key_value = 43;
			shift_press = true;
			break;
		case 148:
			key_value = 44;
			shift_press = true;
			break;
		case 149:
			key_value = 45;
			shift_press = true;
			break;
		case 150:
			key_value = 46;
			shift_press = true;
			break;
		case 151:
			key_value = 47;
			shift_press = true;
			break;
		case 152:
			key_value = 48;
			shift_press = true;
			break;
		case 153:
			key_value = 49;
			shift_press = true;
			break;
		case 154:
			key_value = 50;
			shift_press = true;
			break;
		case 155:
			key_value = 51;
			shift_press = true;
			break;
		case 156:
			key_value = 52;
			shift_press = true;
			break;
		case 157:
			key_value = 53;
			shift_press = true;
			break;
		case 158:
			key_value = 54;
			shift_press = true;
			break;
		case 111:
			key_value = 10;
			shift_press = true;
			break;
		case 112:
			key_value = 12;
			shift_press = true;
			break;
		case 114:
			key_value = 14;
			shift_press = true;
			break;
		case 113:
			key_value = 13;
			shift_press = true;
			break;
		case 115:
			key_value = 15;
			shift_press = true;
			break;
		case 116:
			key_value = 16;
			shift_press = true;
			break;
		case 117:
			key_value = 7;
			shift_press = true;
			break;
		case 118:
			key_value = 70;
			shift_press = true;
			break;
		case 119:
			key_value = 68;
			shift_press = true;
			break;
		case 120:
			key_value = 76;
			shift_press = true;
			break;
		case 124:
			key_value = 71;
			shift_press = true;
			break;
		case 126:
			key_value = 74;
			shift_press = true;
			break;
		case 127:
			key_value = 75;
			shift_press = true;
			break;
		case 128:
			key_value = 73;
			shift_press = true;
			break;
		case 129:
			key_value = 11;
			shift_press = true;
			break;
		case 125:
			key_value = 72;
			shift_press = true;
			break;
		case 121:
		case 122:
		case 123:
		case 130:
		case 131:
		case 132:
		default:
			key_value = keycode;
		}

		if (keycode == 91) {
			remoteSendToBoardVirtualDriver(this.KEYCODE_MUTE_DRIVER, (short) 1);
			remoteSendToBoardVirtualDriver(this.KEYCODE_MUTE_DRIVER, (short) 0);
		} else {
			request.setCurKeyValue((short) key_value);
			request.setFlagShift(shift_press);
			request.send();
		}

		LogUtils.trace(Log.DEBUG, "RemoteKeyboard",
				"send keyboard click key_value:" + key_value + " shift:"
						+ shift_press);
	}

	public void remoteSendDownOrUpKeyCode(int keycode, int event_type) {
		LogUtils.trace(Log.DEBUG, "remoteActivity", "keyRepeater send  key "
				+ keycode + " event type: " + event_type);
		if (keycode == 91) {
			if (event_type == 0) {
				remoteSendToBoardVirtualDriver(this.KEYCODE_MUTE_DRIVER,
						(short) 1);
			} else {
				if (event_type != 2)
					return;
				remoteSendToBoardVirtualDriver(this.KEYCODE_MUTE_DRIVER,
						(short) 0);
			}

		} else
			sendLongPressKeyRequest(keycode, (short) event_type);
	}

	private void remoteSendToBoardVirtualDriver(int keycode, short event_type) {
		int server_port = 8822;
		InetAddress local = null;
		try {
			local = InetAddress.getByName(mIP);
			byte[] msg = getKeyCodeByteData(keycode, event_type);
			DatagramPacket p = new DatagramPacket(msg, msg.length, local,
					server_port);

			this.socket.send(p);
			LogUtils.trace(Log.DEBUG, "RemoteKeyboard",
					"send Keyboard request to driver keycode:" + keycode
							+ "eventype" + event_type);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] getKeyCodeByteData(int keycode, short event_type) {
		ByteBuffer msgbuf = null;
		byte[] msg = null;
		short clickType = 263;

		msg = new byte[22];
		KeyboardRequest keyMsg = new KeyboardRequest();
		msgbuf = ByteBuffer.allocate(40);
		MSGHeadObject keyHead = keyMsg.head;
		msgbuf.putShort(Short.reverseBytes(keyHead.getSendModlueName()));
		msgbuf.putShort(Short.reverseBytes(keyHead.getRcvModlueName()));
		msgbuf.putShort(Short.reverseBytes(keyHead.getMsgType()));
		msgbuf.putShort(Short.reverseBytes(keyHead.getMsgLen()));
		msgbuf.putShort(Short.reverseBytes(keyHead.getRsv()));
		msgbuf.putShort(Short.reverseBytes(keyHead.getSeq()));
		msgbuf.putShort(Short.reverseBytes(clickType));
		int sendDownX = keycode;
		int sendDownY = event_type;
		msgbuf.putInt(Integer.reverseBytes(sendDownX));
		msgbuf.putInt(Integer.reverseBytes(sendDownY));

		if ((msgbuf != null) && (msg != null)) {
			LogUtils.trace(Log.DEBUG, "lipingning", "UDP msgLen=" + msg.length
					+ ",msgbuf.capacity()=" + msgbuf.capacity());

			msgbuf.rewind();
			msgbuf.get(msg, 0, msg.length);
			LogUtils.trace(Log.DEBUG, "lipingning", "after get UDP msgLen="
					+ msg.length);
			msgbuf.clear();
		}
		return msg;
	}

	private void sendLongPressKeyRequest(int key_value, short event_type) {
		KeyboardRequest request = new KeyboardRequest();
		request.head.setSendModlueName((short) 7);
		request.head.setMsgLen((short) 18);
		try {
			request.head.sendHeadMessage(SocketUtils.clientThread.out);
			SocketUtils.clientThread.out.writeInt(Integer
					.reverseBytes(key_value));
			SocketUtils.clientThread.out.writeShort(Short
					.reverseBytes(event_type));
			SocketUtils.clientThread.out.writeBoolean(false);
			SocketUtils.clientThread.out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
