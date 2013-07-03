package com.aidufei.remote;

import com.aidufei.remote.CIOUtil;
import com.aidufei.remote.MessageDef;
import java.io.DataOutputStream;
import java.io.IOException;

public class TouchRequest extends Request {
	public static final int ACTION_UP = 1;
	public static final int ACTION_MOVE = 2;
	public static final int ACTION_DOWN = 0;
	public short action;
	public float xLoc;
	public float yLoc;

	public TouchRequest() {
		this.head.setMsgType(MessageDef.MUAP_MSGTYPE_TOUCH);
		this.head.setSendModlueName((short) 1);
		this.head.setRcvModlueName((short) 2);
		short msgLen = 22;
		this.head.setMsgLen(msgLen);
		this.head.setRsv((short) 0);
	}

	public void setTouchAction(short type) {
		this.action = type;
	}

	public void setTouchLocation(float x, float y) {
		this.xLoc = x;
		this.yLoc = y;
	}

	protected void sendRequest(DataOutputStream out) throws IOException {
		CIOUtil.writeShort(out, this.action);
		CIOUtil.writeFloat(out, this.xLoc);
		CIOUtil.writeFloat(out, this.yLoc);
		out.flush();
	}
}
