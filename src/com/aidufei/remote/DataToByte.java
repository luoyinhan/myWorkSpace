package com.aidufei.remote;

import android.util.Log;
import com.aidufei.remote.MSGHeadObject;
import com.aidufei.remote.MouseRequest;
import com.aidufei.remote.Request;
import com.aidufei.remote.TouchRequest;
import com.coship.ott.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DataToByte {
	public byte[] getMsgByteArray(int eventType, float pointX, float pointY,
			float pointZ) {
		int index = 0;
		byte[] msgByte = new byte[20];
		byte[] temp = new byte[4];

		for (int i = 0; i < 2; ++i) {
			msgByte[index] = 0;
			++index;
		}

		temp = intToByteL(eventType);
		for (int i = 0; i < 2; ++i) {
			msgByte[index] = temp[i];
			++index;
		}
		for (int i = 0; i < 2; ++i) {
			msgByte[index] = 0;
			++index;
		}

		temp = floatToByteL(pointX);
		for (int i = 0; i < 4; ++i) {
			msgByte[index] = temp[i];
			++index;
		}
		temp = floatToByteL(pointY);
		for (int i = 0; i < 4; ++i) {
			msgByte[index] = temp[i];
			++index;
		}
		temp = floatToByteL(pointZ);
		for (int i = 0; i < 4; ++i) {
			msgByte[index] = temp[i];
			++index;
		}
		return msgByte;
	}

	public static byte[] getRequestByteData(Request req) {
		ByteBuffer msgbuf = null;
		byte[] msg = null;
		if (req instanceof MouseRequest) {
			MouseRequest mouseMsg = (MouseRequest) req;
			msg = new byte[22];
			msgbuf = ByteBuffer.allocate(40);
			MSGHeadObject mouseHead = mouseMsg.head;
			msgbuf.putShort(Short.reverseBytes(mouseHead.getSendModlueName()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getRcvModlueName()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getMsgType()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getMsgLen()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getRsv()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getSeq()));
			msgbuf.putShort(Short.reverseBytes(mouseMsg.getMouseClickType()));
			msgbuf.putInt(Integer.reverseBytes((int) mouseMsg.dx));
			msgbuf.putInt(Integer.reverseBytes((int) mouseMsg.dy));
		} else if (req instanceof TouchRequest) {
			TouchRequest touchMsg = (TouchRequest) req;
			msg = new byte[22];
			msgbuf = ByteBuffer.allocate(40);
			MSGHeadObject mouseHead = touchMsg.head;
			msgbuf.putShort(Short.reverseBytes(mouseHead.getSendModlueName()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getRcvModlueName()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getMsgType()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getMsgLen()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getRsv()));
			msgbuf.putShort(Short.reverseBytes(mouseHead.getSeq()));
			msgbuf.putShort(Short.reverseBytes(touchMsg.action));
			int sendDownX = (int) (touchMsg.xLoc * 1280.0F);
			int sendDownY = (int) (touchMsg.yLoc * 720.0F);
			LogUtils.trace(Log.DEBUG, "-", "x = " + sendDownX + " ,y = " + sendDownY);
			msgbuf.putInt(Integer.reverseBytes(sendDownX));
			msgbuf.putInt(Integer.reverseBytes(sendDownY));
		}

		if ((msgbuf != null) && (msg != null)) {
			LogUtils.trace(Log.DEBUG,"lipingning", "UDP msgLen=" + msg.length
					+ ",msgbuf.capacity()=" + msgbuf.capacity());

			msgbuf.rewind();
			msgbuf.get(msg, 0, msg.length);
			LogUtils.trace(Log.DEBUG,"lipingning", "after get UDP msgLen=" + msg.length);
			msgbuf.clear();
		}

		return msg;
	}

	public int AddDataToArray(byte[] bytebuffer, byte byteValue, int index) {
		bytebuffer[index] = byteValue;
		return 1;
	}

	public int AddDataToArray(byte[] bytebuffer, int intValue, int index) {
		return intToByte(bytebuffer, intValue);
	}

	public int AddDataToArray(byte[] bytebuffer, float floatValue, int index) {
		return floatToByte(bytebuffer, floatValue);
	}

	public int intToByte(byte[] bytebuffer, int intValue) {
		bytebuffer[0] = (byte) ((intValue & 0xFF000000) >> 24);
		bytebuffer[1] = (byte) ((intValue & 0xFF0000) >> 16);
		bytebuffer[2] = (byte) ((intValue & 0xFF00) >> 8);
		bytebuffer[3] = (byte) (intValue & 0xFF);
		return 4;
	}

	public byte[] intToByte(int intValue) {
		byte[] result = new byte[4];
		result[0] = (byte) ((intValue & 0xFF000000) >> 24);
		result[1] = (byte) ((intValue & 0xFF0000) >> 16);
		result[2] = (byte) ((intValue & 0xFF00) >> 8);
		result[3] = (byte) (intValue & 0xFF);
		return result;
	}

	public byte[] intToByteL(int intValue) {
		byte[] result = new byte[4];
		result[0] = (byte) (intValue & 0xFF);
		result[1] = (byte) ((intValue & 0xFF00) >> 8);
		result[2] = (byte) ((intValue & 0xFF0000) >> 16);
		result[3] = (byte) ((intValue & 0xFF000000) >> 24);
		return result;
	}

	public int floatToByte(byte[] bytebuffer, float floatValue) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(floatValue);
		bb.get(bytebuffer);
		return 4;
	}

	public byte[] floatToByteL(float v) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		byte[] ret = new byte[4];
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(v);
		bb.get(ret);
		int tmp30_29 = 0;
		byte[] tmp30_28 = ret;
		tmp30_28[tmp30_29] = (byte) (tmp30_28[tmp30_29] ^ ret[3]);
		int tmp40_39 = 3;
		byte[] tmp40_38 = ret;
		tmp40_38[tmp40_39] = (byte) (tmp40_38[tmp40_39] ^ ret[0]);
		int tmp50_49 = 0;
		byte[] tmp50_48 = ret;
		tmp50_48[tmp50_49] = (byte) (tmp50_48[tmp50_49] ^ ret[3]);
		int tmp60_59 = 1;
		byte[] tmp60_58 = ret;
		tmp60_58[tmp60_59] = (byte) (tmp60_58[tmp60_59] ^ ret[2]);
		int tmp70_69 = 2;
		byte[] tmp70_68 = ret;
		tmp70_68[tmp70_69] = (byte) (tmp70_68[tmp70_69] ^ ret[1]);
		int tmp80_79 = 1;
		byte[] tmp80_78 = ret;
		tmp80_78[tmp80_79] = (byte) (tmp80_78[tmp80_79] ^ ret[2]);

		return ret;
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
