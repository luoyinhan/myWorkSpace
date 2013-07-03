package com.aidufei.protocol.remote.message;

import android.util.Log;
import com.aidufei.protocol.remote.utils.SocketUtils;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Request {
	protected String TAG = "Request";

	public MSGHeadObject head = new MSGHeadObject();

	public static short msgSequence = 0;
	protected static final short headLen = 12;
	protected static final int OP_NULL = -1;

	public Request() {
		short tmp28_25 = msgSequence;
		msgSequence = (short) (tmp28_25 + 1);
		this.head.setSeq((short) (tmp28_25 % 32767));
	}

	public void send() {
		try {
			this.head.sendHeadMessage(SocketUtils.clientThread.out);
			sendRequest(SocketUtils.clientThread.out);
			Log.e(this.TAG, "send Message :" + this + " success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void sendRequest(DataOutputStream paramDataOutputStream)
			throws IOException;
}
