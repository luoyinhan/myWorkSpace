package com.aidufei.protocol.remote.message;

import com.aidufei.protocol.remote.utils.CIOUtil;
import com.aidufei.protocol.remote.callback.MessageDef;
import java.io.DataOutputStream;
import java.io.IOException;

public class MouseRequest extends Request {
	short clickType;
	public float dx;
	public float dy;

	public MouseRequest() {
		this.head.setMsgType(MessageDef.MUAP_MSGTYPE_MOUSE);
		this.head.setSendModlueName((short) 1);
		this.head.setRcvModlueName((short) 2);
		short msgLen = 22;
		this.head.setMsgLen(msgLen);
		this.head.setRsv((short) 0);
		this.head.setSeq((short) 0);
	}

	public void setMouseClickType(short type) {
		this.clickType = type;
	}

	public short getMouseClickType() {
		return this.clickType;
	}

	public void setDxDy(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}

	protected void sendRequest(DataOutputStream out) throws IOException {
		CIOUtil.writeShort(out, this.clickType);
		CIOUtil.writeInt(out, (int) this.dx);
		CIOUtil.writeInt(out, (int) this.dy);
		out.flush();
	}
}
