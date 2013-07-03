package com.aidufei.remote;

import com.aidufei.remote.CIOUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MSGHeadObject {
	short s16sndModuleName = 0;
	short s16rcvModuleName = 0;
	short s16msgType = 0;
	short s16usMsglen = 0;
	short s16usRsv = 0;
	short s16usMsgSeq = 0;

	public MSGHeadObject() {
		this.s16sndModuleName = 0;
		this.s16rcvModuleName = 0;
		this.s16msgType = 0;
		this.s16usMsglen = 0;
		this.s16usRsv = 0;
		this.s16usMsgSeq = 0;
	}

	public void setSendModlueName(short name) {
		this.s16sndModuleName = name;
	}

	public void setRcvModlueName(short name) {
		this.s16rcvModuleName = name;
	}

	public void setMsgType(short msgType) {
		this.s16msgType = msgType;
	}

	public void setMsgLen(short msgLen) {
		this.s16usMsglen = msgLen;
	}

	public void setRsv(short rsv) {
		this.s16usRsv = rsv;
	}

	public void setSeq(short seq) {
		this.s16usMsgSeq = seq;
	}

	public short getSendModlueName() {
		return this.s16sndModuleName;
	}

	public short getRcvModlueName() {
		return this.s16rcvModuleName;
	}

	public short getMsgType() {
		return this.s16msgType;
	}

	public short getMsgLen() {
		return this.s16usMsglen;
	}

	public short getRsv() {
		return this.s16usRsv;
	}

	public short getSeq() {
		return this.s16usMsgSeq;
	}

	public void sendHeadMessage(DataOutputStream out) throws IOException {
		CIOUtil.writeShort(out, this.s16sndModuleName);
		CIOUtil.writeShort(out, this.s16rcvModuleName);
		CIOUtil.writeShort(out, this.s16msgType);
		CIOUtil.writeShort(out, this.s16usMsglen);
		CIOUtil.writeShort(out, this.s16usRsv);
		CIOUtil.writeShort(out, this.s16usMsgSeq);
	}

	public void getHeadMessage(DataInputStream in) throws IOException {
		this.s16sndModuleName = CIOUtil.readShort(in);
		this.s16rcvModuleName = CIOUtil.readShort(in);
		this.s16msgType = CIOUtil.readShort(in);
		this.s16usMsglen = CIOUtil.readShort(in);
		this.s16usRsv = CIOUtil.readShort(in);
		this.s16usMsgSeq = CIOUtil.readShort(in);
	}
}
