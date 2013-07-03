package com.aidufei.protocol.oto;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OtOStatusRequest extends OtORequest {

	private Status mStatus;
	private ResourceInfo mResourceInfo;
	private int mVolume;
	private int mPlayStatus;

	public OtOStatusRequest() {
		this.mCmd = OtORequest.OTO_CMD_STATUS;
		mStatus = null;
		mResourceInfo = null;
		mVolume = -1;
		mPlayStatus = 1;
	}

	public void setPlayStatus(int playStatus) {
		mPlayStatus = playStatus;
	}

	public int getPlayStatus() {
		return mPlayStatus;
	}

	public OtOStatusRequest(Status status, ResourceInfo info, int vol,
			int playStatus) {
		this.mCmd = OtORequest.OTO_CMD_STATUS;
		mStatus = status;
		mResourceInfo = info;
		mVolume = vol;
		mPlayStatus = playStatus;
	}

	public void setVolume(int vol) {
		mVolume = vol;
	}

	public void setStatus(int returnCode, String desc) {
		if (mStatus == null) {
			mStatus = new Status(returnCode, desc);
		} else {
			mStatus.setReturnCode(returnCode);
			mStatus.setDescription(desc);
		}
	}

	public int volume() {
		return mVolume;
	}

	public Status status() {
		return mStatus;
	}

	public ResourceInfo resource() {
		return mResourceInfo;
	}

	@Override
	public String paramString() throws Exception {
		// TODO Auto-generated method stub
		if (mStatus != null) {
			JSONObject json = new JSONObject();
			json.put("Status", mStatus.toJSON());
			if (mResourceInfo != null) {
				json.put("ResourceInfo", mResourceInfo.toJSON());
			}
			json.put("Volume", mVolume);
			json.put("PlayStatus", mPlayStatus);
			return json.toString();
		}
		return null;
	}

	@Override
	public boolean getParam(String paramString) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// LogUtils.trace(Log.VERBOSE,"OtOStatusRequest","getParam: paramString="
		// + paramString);
		if (paramString == null || paramString.length() <= 0) {
			return true;
		}

		JSONObject json = null;
		try {
			json = (JSONObject) new JSONTokener(paramString).nextValue();
			if (json == null)
				return true;
		} catch (JSONException e) {
			return true;
		}
		try {
			JSONObject statjson = json.getJSONObject("Status");
			if (statjson != null) {
				mStatus = new Status();
				mStatus.fromJSON(statjson);
			}
		} catch (JSONException e) {
			return true;
		}

		try {
			JSONObject resjson = json.getJSONObject("ResourceInfo");
			if (resjson != null) {
				mResourceInfo = new ResourceInfo();
				mResourceInfo.fromJSON(resjson);
			}
		} catch (JSONException e) {
		}
		try {
			mVolume = json.getInt("Volume");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}

		try {
			mPlayStatus = json.getInt("PlayStatus");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}

		return true;
	}

	@Override
	protected JSONObject paramJSON() throws Exception {
		// TODO Auto-generated method stub
		if (mStatus != null) {
			JSONObject json = new JSONObject();
			json.put("Status", mStatus.toJSON());
			if (mResourceInfo != null) {
				json.put("ResourceInfo", mResourceInfo.toJSON());
			}
			json.put("Volume", mVolume);
			return json;
		}
		return null;
	}

	@Override
	protected void fromJSON(JSONObject json) throws Exception {
		// TODO Auto-generated method stub
		try {
			JSONObject statjson = json.getJSONObject("Status");
			if (statjson != null) {
				mStatus = new Status();
				mStatus.fromJSON(statjson);
			}
		} catch (JSONException e) {
			return;
		}

		try {
			JSONObject resjson = json.getJSONObject("ResourceInfo");
			if (resjson != null) {
				mResourceInfo = new ResourceInfo();
				mResourceInfo.fromJSON(resjson);
			}
		} catch (JSONException e) {
		}
		try {
			mVolume = json.getInt("Volume");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}

		try {
			mPlayStatus = json.getInt("PlayStatus");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
	}

}
