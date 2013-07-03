package com.aidufei.remote;

import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.coship.ott.utils.LogUtils;

public class MsgHead {
	public static final int DVBC_MSG_BASE = 256;
	public static final int REQ_EPG_LIST = 257;
	public static final int RES_EPG_LIST = 258;
	public static final int SWITCH_CHANNEL_REQ = 259;
	public static final int SWITCH_CHANNEL_RET = 260;
	public static final int EPG_UPDATE_NOTIFY = 261;
	int msgType;

	public void setMsgType(int type) {
		this.msgType = type;
	}

	public int getMsgType() {
		return this.msgType;
	}

	public void sendOutputMsg(DataOutputStream out) throws Exception {
		if (out == null)
			return;
		out.writeInt(this.msgType);
	}

	public void readInputMsg(DataInputStream input) throws Exception {
		if (input == null)
			return;
		this.msgType = input.readInt();
	}

	public void print() {
		LogUtils.trace(Log.DEBUG,"DTVPlayerEGG", "msgType=" + this.msgType);
	}
}
