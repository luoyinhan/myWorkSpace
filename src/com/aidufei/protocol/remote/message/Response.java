package com.aidufei.protocol.remote.message;

import android.util.Log;
import com.aidufei.protocol.remote.utils.CIOUtil;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class Response implements Runnable {
	protected static String TAG = "Response";
	protected MSGHeadObject headObject;

	public Response() {
		this.headObject = new MSGHeadObject();
	}

	public static void readHead(DataInputStream data, MSGHeadObject head)
			throws Exception {
		head.s16sndModuleName = CIOUtil.readShort(data);
		head.s16rcvModuleName = CIOUtil.readShort(data);
		head.s16msgType = CIOUtil.readShort(data);
		head.s16usMsglen = CIOUtil.readShort(data);
		head.s16usRsv = CIOUtil.readShort(data);
		head.s16usMsgSeq = CIOUtil.readShort(data);
	}

	public void run() {
		handleResponse();
	}

	public static Response createResponse(MSGHeadObject head) {
		Response response = null;

		if (response == null) {
			Log.e(TAG, " unknow response ,ingnored it !" + head.getMsgType());
			return null;
		}
		Log.e(TAG, " create new response ... ... " + head.s16msgType + ":"
				+ response);
		response.headObject = head;
		return response;
	}

	public abstract void handleResponse();

	public abstract void parseResponse(DataInputStream paramDataInputStream)
			throws IOException;
}
