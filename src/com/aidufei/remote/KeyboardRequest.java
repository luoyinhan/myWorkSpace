package com.aidufei.remote;

import com.aidufei.remote.CIOUtil;
import com.aidufei.remote.MessageDef;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeyboardRequest extends Request {
	private short keyValue = 0;
	private boolean flagShift = false;

	public KeyboardRequest() {
		this.head.setMsgType(MessageDef.MUAP_MSGTYPE_KEYBOARD);
		this.head.setSendModlueName((short) 1);
		this.head.setRcvModlueName((short) 2);
		short msgLen = 14;
		this.head.setMsgLen(msgLen);
		this.head.setRsv((short) 0);
		this.head.setSeq((short) 0);
	}

	public void setCurKeyValue(short key) {
		this.keyValue = key;
	}

	public void setFlagShift(boolean flag) {
		this.flagShift = flag;
	}

	protected void sendRequest(DataOutputStream out) throws IOException {
		CIOUtil.writeShort(out, this.keyValue);
		CIOUtil.writeBoolean(out, this.flagShift);
		out.flush();
	}
}
