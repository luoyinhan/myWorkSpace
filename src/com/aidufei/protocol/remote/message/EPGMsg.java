package com.aidufei.protocol.remote.message;

import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class EPGMsg extends MsgHead {
	final int MAX_BODY_LEN = 2048;
	boolean isSuccess;
	int bodyLenth;
	byte[] body;

	public EPGMsg() {

		this.body = new byte[2048];
	}

	public void setGetResult(boolean ret) {
		this.isSuccess = ret;
	}

	public boolean isSuccessfull() {
		return this.isSuccess;
	}

	public void setBodyLenth(int len) {
		this.bodyLenth = len;
	}

	public int getBodyLenth() {
		return this.bodyLenth;
	}

	public void setMsgBody(byte[] msgBody) {
		this.body = msgBody;
	}

	public void setMsgBody(String msgBody) {
		this.body = msgBody.getBytes();
	}

	public byte[] getMsgByteBody() {
		return this.body;
	}

	public String getMsgStringBody() {
		String content = new String(this.body);
		content = content.trim();
		return content;
	}

	public void readInputMsg(DataInputStream in) throws Exception {
		int readlen = 0;
		int remainlen = 0;
		int alreayLen = 0;
		this.isSuccess = in.readBoolean();
		this.bodyLenth = in.readInt();
		if (this.bodyLenth <= 0)
			return;
		Log.e("EPGMSG", "bodyLenth:" + this.bodyLenth);
		this.body = new byte[this.bodyLenth + 1];
		remainlen = this.bodyLenth;
		while (remainlen > 0) {
			readlen = in.read(this.body, alreayLen, remainlen);
			remainlen -= readlen;
			alreayLen += readlen;
			Log.e("EPGMSG", "readlen:" + readlen + " already read" + alreayLen);
		}
	}

	public void sendOutputMsg(DataOutputStream out) throws Exception {
		if (out == null) {
			Log.e("DTVPlayerEGG", "out is null");
			return;
		}
		super.sendOutputMsg(out);
		out.writeBoolean(this.isSuccess);
		out.writeInt(this.bodyLenth);
		if (this.bodyLenth > 0)
			out.write(this.body, 0, this.bodyLenth);
		out.flush();
	}

	public void print() {
		super.print();
		Log.e("DTVPlayerEGG", "isSuccess=" + this.isSuccess);
		Log.e("DTVPlayerEGG", "bodyLenth=" + this.bodyLenth);
		Log.e("DTVPlayerEGG", "body=" + new String(this.body).trim());
	}
}
