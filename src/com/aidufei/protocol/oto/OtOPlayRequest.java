package com.aidufei.protocol.oto;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.coship.ott.utils.LogUtils;

public class OtOPlayRequest extends OtORequest {
	private String mUserCode;
	private String mSubID;
	private Status mStatus;
	private ResourceInfo mResourceInfo;

	public OtOPlayRequest() {
		this.mCmd = OtORequest.OTO_CMD_PLAY;
		mResourceInfo = null;
		mUserCode = mSubID = null;
		mStatus = null;
	}

	public OtOPlayRequest(Status status, ResourceInfo info) {
		this.mCmd = OtORequest.OTO_CMD_PLAY;
		mUserCode = mSubID = null;
		mStatus = status;
		mResourceInfo = info;
	}

	public OtOPlayRequest(String userCode, String subID, ResourceInfo info) {
		this.mCmd = OtORequest.OTO_CMD_PLAY;
		mUserCode = userCode;
		mSubID = subID;
		mResourceInfo = info;
		mStatus = null;
	}

	/*
	 * public OtOPlayRequest(String userCode, String resourceName,String
	 * resourceCode, String productCode, String subID, int delay){ this.mCmd =
	 * OtORequest.OTO_CMD_PLAY; mUserCode = userCode; mSubID = subID;
	 * mResourceInfo = new ResourceInfo(resourceName,resourceCode, productCode,
	 * delay); }
	 * 
	 * public OtOPlayRequest(String userCode, String resourceName, String
	 * resourceCode, String productCode,String subID,int offset, int duration){
	 * this.mCmd = OtORequest.OTO_CMD_PLAY; mUserCode = userCode; mSubID =
	 * subID; mResourceInfo = new ResourceInfo(resourceName,resourceCode,
	 * productCode, offset,duration); }
	 * 
	 * public OtOPlayRequest(String userCode, String resourceName,String
	 * resourceCode, String productCode, String subID, long start, long end){
	 * this.mCmd = OtORequest.OTO_CMD_PLAY; mUserCode = userCode; mSubID =
	 * subID; mResourceInfo = new ResourceInfo(resourceName,resourceCode,
	 * productCode, start, end); }
	 */
	public void setPlay(String userCode, String subID, ResourceInfo info) {
		mUserCode = userCode;
		mSubID = subID;
		mResourceInfo = info;
		mStatus = null;
	}

	/*
	 * public void setVOBPlay(String userCode, String resourceName,String
	 * resourceCode,String productCode, String subID, int offset){ mUserCode =
	 * userCode; mSubID = subID; mResourceInfo = new
	 * ResourceInfo(resourceName,resourceCode,productCode,offset); }
	 * 
	 * public void setVOBPlay(String userCode, String resourceName,String
	 * resourceCode,String productCode, String subID, long start, long end){
	 * mUserCode = userCode; mSubID = subID; mResourceInfo = new
	 * ResourceInfo(resourceName,resourceCode,productCode,start,end); }
	 * 
	 * public void setVODPlay(String userCode, String resourceName,String
	 * resourceCode,String productCode, String subID, int offset,int duration){
	 * mUserCode = userCode; mSubID = subID; mResourceInfo = new
	 * ResourceInfo(resourceName,resourceCode,productCode,offset,duration); }
	 */
	public void setResourceInfo(ResourceInfo info) {
		mResourceInfo = info;
	}

	public void setStatus(int returnCode, String desc) {
		mUserCode = mSubID = null;
		mStatus = new Status(returnCode, desc);
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
		JSONObject json = new JSONObject();

		if (mResourceInfo != null) {
			json.put("ResourceInfo", mResourceInfo.toJSON());
		}

		if (mStatus == null) {
			json.put("UserCode", mUserCode == null ? "" : mUserCode);
			json.put("SubID", mSubID == null ? "" : mSubID);
		} else {
			json.put("Status", mStatus.toJSON());
		}

		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
				"Play Request: " + json.toString());
		return json.toString();

	}

	@Override
	public boolean getParam(String paramString) {
		// TODO Auto-generated method stub
		if (paramString == null || paramString.length() <= 0)
			return false;

		JSONObject json = null;
		try {
			json = (JSONObject) new JSONTokener(paramString).nextValue();
			if (json == null)
				return false;
		} catch (JSONException e) {

			return false;
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
			JSONObject statjson = json.getJSONObject("Status");
			if (statjson != null) {
				mStatus = new Status();
				mStatus.fromJSON(statjson);
				return true;
			}
		} catch (JSONException e) {

		}

		try {
			mUserCode = json.getString("UserCode");
		} catch (JSONException e) {
		}

		try {
			mSubID = json.getString("SubID");
		} catch (JSONException e) {

		}

		return true;
	}

	@Override
	protected JSONObject paramJSON() throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();

		if (mResourceInfo != null) {
			json.put("ResourceInfo", mResourceInfo.toJSON());
		}

		if (mStatus == null) {
			json.put("UserCode", mUserCode == null ? "" : mUserCode);
			json.put("SubID", mSubID == null ? "" : mSubID);
		} else {
			json.put("Status", mStatus.toJSON());
		}

		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
				"Play Request: " + json.toString());
		return json;
	}

	@Override
	protected void fromJSON(JSONObject json) throws Exception {
		// TODO Auto-generated method stub
		try {
			JSONObject resjson = json.getJSONObject("ResourceInfo");
			if (resjson != null) {
				mResourceInfo = new ResourceInfo();
				mResourceInfo.fromJSON(resjson);
			}
		} catch (JSONException e) {

		}

		try {
			JSONObject statjson = json.getJSONObject("Status");
			if (statjson != null) {
				mStatus = new Status();
				mStatus.fromJSON(statjson);
				return;
			}
		} catch (JSONException e) {

		}

		try {
			mUserCode = json.getString("UserCode");
		} catch (JSONException e) {
		}

		try {
			mSubID = json.getString("SubID");
		} catch (JSONException e) {

		}

	}
}
