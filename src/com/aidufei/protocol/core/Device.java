package com.aidufei.protocol.core;

import android.util.Log;

import com.coship.ott.utils.LogUtils;

public abstract class Device {
	public static final int STATE_IDLE = 0;
	public static final int STATE_DROP = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	private String mName;
	private String mSerial;
	private int mType;
	private String mUUID;
	private DeviceAdapter mAdapter;
	private int mState;
	private String mAddress;

	public Device() {
		mName = mSerial = null;
		mType = 0;
		mUUID = null;
		mAdapter = createAdapter();
		mState = STATE_IDLE;
	}

	public Device(String name, String serial, int type, String uuid,
			String address) {
		mName = name;
		mSerial = serial;
		mType = type;
		mUUID = uuid;
		mAddress = address;
		mAdapter = createAdapter();
		mState = STATE_IDLE;
	}

	public DeviceAdapter adapter() {
		return mAdapter;
	}

	public String name() {
		return mName;
	}

	public String serial() {
		return mSerial;
	}

	public String uuid() {
		return mUUID;
	}

	public int type() {
		return mType;
	}

	public String address() {
		return mAddress;
	}

	public int state() {
		return mState;
	}

	public void setState(int state) {
		LogUtils.trace(Log.DEBUG, "Device", mAddress + ": state=" + mState
				+ ",will change to =" + state);
		mState = state;
	}

	protected abstract DeviceAdapter createAdapter();
}
