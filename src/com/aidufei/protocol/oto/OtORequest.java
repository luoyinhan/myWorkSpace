package com.aidufei.protocol.oto;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.coship.ott.utils.LogUtils;

public abstract class OtORequest extends Object {
	public final static int OTO_CMD_UNKNOWN = -1;
	public final static int OTO_CMD_CONNECT = 100;
	public final static int OTO_CMD_PLAY = 101;
	public final static int OTO_CMD_PULL = 102;
	public final static int OTO_CMD_STATUS = 103;
	public final static int OTO_CMD_SVOLUME = 104;
	public final static int OTO_CMD_CONTROL = 105;
	public final static int OTO_CMD_MIRRION = 106;

	protected int mCmd;
	private int mParamLength;
	private String mParam;

	public OtORequest() {
		mCmd = OTO_CMD_UNKNOWN;
		mParamLength = 0;
		mParam = null;
	}

	public int command() {
		return mCmd;
	}

	public byte[] toByte() {

		try {
			mParam = paramString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		if (mParam == null)
			mParamLength = 0;
		else
			mParamLength = mParam.getBytes().length;
		byte[] msg = new byte[mParamLength + 8];
		ByteBuffer msgbuf = ByteBuffer.allocate(mParamLength + 8);
		if (msgbuf == null)
			return null;

		msgbuf.putInt(mCmd);
		msgbuf.putInt(mParamLength);
		if (mParam != null)
			msgbuf.put(mParam.getBytes());

		if (msg != null) {
			msgbuf.rewind();
			msgbuf.get(msg, 0, mParamLength + 8);
		}
		return msg;
	}

	public boolean fromBytes(byte[] msg, int length) {

		if (msg == null || length < 8)
			return false;
		ByteBuffer msgbuf = ByteBuffer.allocate(length + 1);
		if (msgbuf == null)
			return false;

		msgbuf.clear();
		msgbuf.put(msg, 0, length);
		msgbuf.rewind();
		mCmd = msgbuf.getInt();
		mParamLength = msgbuf.getInt();
		LogUtils.trace(Log.VERBOSE, "OtORequest", "parse OtORequest: cmd="
				+ mCmd + " paramLength=" + mParamLength + " bufferlen="
				+ length);
		if (mParamLength > 0 && length > 8) {
			msgbuf.clear();
			msgbuf.put(msg, 8, length - 8);
			msgbuf.rewind();
			mParam = new String(msgbuf.array());
			LogUtils.trace(Log.VERBOSE, "OtORequest", "parse param: param="
					+ mParam);
			getParam(mParam);
		}
		return true;
	}

	public static OtORequest parseCommand(String msg) {
		OtORequest req = null;
		if (msg == null || msg.length() < 0)
			return null;

		JSONObject json = null;

		try {
			json = (JSONObject) new JSONTokener(msg).nextValue();
			if (json == null)
				return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}

		int command = 0;
		int length = 0;
		JSONObject param = null;
		try {
			command = json.getInt("Command");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}

		try {
			length = json.getInt("ParamLength");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}

		try {
			param = json.getJSONObject("Param");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}

		switch (command) {
		case OTO_CMD_CONNECT:
			req = new OtOAnnounceRequest();
			break;
		case OTO_CMD_PLAY:
			req = new OtOPlayRequest();
			break;
		case OTO_CMD_PULL:
			req = new OtOPullRequest();
			break;
		case OTO_CMD_STATUS:
			req = new OtOStatusRequest();
			break;
		case OTO_CMD_SVOLUME:
			req = new OtOSetVolumeRequest();
			break;
		case OTO_CMD_CONTROL:
			req = new OtOMediaControlRequest();
			break;
		default:
			return null;
		}

		try {
			req.fromJSON(param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		req.mParamLength = length;
		return req;
	}

	public static OtORequest parseCommand(byte[] msg, int length) {
		OtORequest req = null;
		if (msg == null || length < 8)
			return null;
		ByteBuffer msgbuf = ByteBuffer.allocate(length + 1);
		if (msgbuf == null)
			return null;

		msgbuf.clear();
		msgbuf.put(msg, 0, length);
		msgbuf.rewind();
		int command = msgbuf.getInt();
		LogUtils.trace(Log.VERBOSE, "OtORequest", "get command=" + command);
		switch (command) {
		case OTO_CMD_CONNECT:
			req = new OtOAnnounceRequest();
			break;
		case OTO_CMD_PLAY:
			req = new OtOPlayRequest();
			break;
		case OTO_CMD_PULL:
			req = new OtOPullRequest();
			break;
		case OTO_CMD_STATUS:
			req = new OtOStatusRequest();
			break;
		case OTO_CMD_SVOLUME:
			req = new OtOSetVolumeRequest();
			break;
		case OTO_CMD_CONTROL:
			req = new OtOMediaControlRequest();
			break;
		default:
			return null;
		}
		req.mParamLength = length;
		req.fromBytes(msg, length);
		return req;
	}

	public JSONObject toJSON() throws Exception {
		JSONObject json = new JSONObject();

		json.put("Command", mCmd);
		json.put("ParamLength", mParamLength);
		json.put("Param", paramJSON());
		return json;
	}

	public abstract String paramString() throws Exception;

	public abstract boolean getParam(String paramString);

	protected abstract JSONObject paramJSON() throws Exception;

	protected abstract void fromJSON(JSONObject param) throws Exception;

}
