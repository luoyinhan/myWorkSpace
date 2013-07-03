package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;
import java.util.Random;

import android.util.Log;

import com.coship.ott.utils.LogUtils;

public abstract class GDHfcRequest {

	public static final int REQUEST = 0;
	public static final int RESPONSE = 1;

	protected static final int CMD_ANNOUNCE = 0x01;
	protected static final int CMD_KEY = 0x02;
	protected static final int CMD_TEXT_INPUT = 0x03;
	protected static final int CMD_SENSOR = 0x04;
	protected static final int CMD_MOUSE = 0x05;
	protected static final int CMD_URL = 0x06;
	protected static final int CMD_APP = 0x07;

	private static final String MAGIC = "~#";

	private String mSerial;
	private byte mCheck;
	private int mParamLength;
	private boolean mIsRequest = true;

	protected byte mCmd;
	protected int mSync;

	public GDHfcRequest(String serial, boolean isRequest) {
		mCmd = 0;

		Random ran = new Random();
		mSync = ran.nextInt();

		mSerial = serial;
		mParamLength = 0;
		mCheck = 0;
		mIsRequest = isRequest;
	}

	public String serial() {
		return mSerial;
	}

	public boolean isRequest() {
		return mIsRequest;

	}

	protected void setIsRequest(boolean isRequest) {
		mIsRequest = isRequest;
	}

	public byte[] toByte() {

		byte[] param = paramToBytes();

		if (param != null) {
			mParamLength = param.length;
		} else {
			mParamLength = 0;
		}

		byte[] msg = new byte[mParamLength + 44];

		ByteBuffer msgbuf = ByteBuffer.allocate(mParamLength + 44);
		if (msgbuf == null)
			return null;
		msgbuf.put(MAGIC.getBytes());
		msgbuf.putInt(mSync);
		if (mSerial == null) {
			mSerial = "guangdongshengwang feifeikan kan";
		}

		if (mSerial.length() < 32) {
			String sufix = "";
			for (int i = 0; i < (32 - mSerial.length()); i++) {
				sufix += " ";
			}
			mSerial += sufix;
		}

		msgbuf.put(mSerial.getBytes());

		byte command = mCmd;
		if (mIsRequest == false) {
			command += 0x80;
		}
		msgbuf.put(command);
		msgbuf.putInt(mParamLength);
		if (param != null) {
			msgbuf.put(param);
		}

		for (int i = 0; i < mParamLength + 43; i++) {
			mCheck += msgbuf.get(i);
		}
		msgbuf.put(mCheck);

		msgbuf.rewind();
		msgbuf.get(msg, 0, mParamLength + 44);

		return msg;

	}

	protected abstract byte[] paramToBytes();

	protected abstract boolean paramFromBytes(ByteBuffer buf);

	public static GDHfcRequest parse(byte[] msg, int length) {
		GDHfcRequest req = null;
		if (msg == null || length < 44)
			return null;
		ByteBuffer msgbuf = ByteBuffer.allocate(length + 1);
		if (msgbuf == null)
			return null;

		msgbuf.clear();
		msgbuf.put(msg, 0, length);
		msgbuf.rewind();
		byte[] magic = new byte[2];
		magic[0] = msgbuf.get();
		magic[1] = msgbuf.get();
		String strmagic = new String(magic);
		if (!strmagic.equals(MAGIC)) {
			return null;
		}
		int sync = msgbuf.getInt();
		byte[] serial = new byte[32];
		msgbuf.get(serial, 0, 32);

		byte command = msgbuf.get();
		boolean isRequest = false;
		if ((command & 0x80) == 0) {
			isRequest = true;
		} else {
			command &= 0x7f;
		}
		int paramlen = msgbuf.getInt();

		LogUtils.trace(Log.DEBUG, "GDHfcRequest", "get command=" + command);
		switch (command) {
		case CMD_ANNOUNCE:
			req = new GDHfcAnnounce();
			break;
		case CMD_KEY:
			req = new GDHfcKey();
			break;
		case CMD_MOUSE:
			req = new GDHfcMouse();
			break;
		case CMD_TEXT_INPUT:
			req = new GDHfcTextInput();
			break;
		case CMD_SENSOR:
			req = new GDHfcSensor();
			break;
		case CMD_URL:
			req = new GDHfcURL();
			break;
		case CMD_APP:
			req = new GDHfcApp();
			break;
		default:
			return null;
		}

		req.mSync = sync;
		req.mSerial = new String(serial);
		req.mIsRequest = isRequest;
		req.mParamLength = paramlen;
		if (req.paramFromBytes(msgbuf) == false)
			return null;
		return req;
	}

}
