package com.aidufei.protocol.adapter.saition;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.aidufei.protocol.core.HttpTask;

public class SaitionVODParamTask extends HttpTask {

	private String mResourceCode = null;
	private String mContentID = "11790";

	private String mRequest = null;
	private int mOffset = 0;
	private int mDuration = 0;

	private SaitionFlyDevice mRemote = null;

	public SaitionVODParamTask(SaitionFlyDevice remote, String resource,
			int offset, int duration) {
		mResourceCode = resource;
		mRemote = remote;
		mDuration = duration;
		mOffset = offset;
	}

	@Override
	protected String buildRequest() {
		// TODO Auto-generated method stub
		mRequest = "/msis/getContentId?version=V001&resourceCode="
				+ mResourceCode;
		return mRequest;
	}

	// {"data":"1002","ret":"0","retInfo":""}
	@Override
	protected boolean parseResponse(String strResponse) {
		// TODO Auto-generated method stub
		JSONObject json = null;
		if (strResponse == null || strResponse.length() <= 0)
			return false;

		try {
			json = (JSONObject) new JSONTokener(strResponse).nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}

		int ret;
		try {
			ret = json.getInt("ret");
			if (ret != 0)
				return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}

		try {
			mContentID = json.getString("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mContentID = null;
			return false;
		}

		return true;

	}

	@Override
	protected void updateProgress(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleResponse() {
		// TODO Auto-generated method stub
		if (mRemote != null && mRemote.adapter() != null) {
			if (mRemote.adapter() instanceof SaitionFlyDeviceAdapter) {
				SaitionFlyDeviceAdapter adapter = (SaitionFlyDeviceAdapter) mRemote
						.adapter();
				adapter.playVOD(mRemote, mContentID, mOffset, mDuration);
			}
		}
	}

	@Override
	protected void updateError(int error) {
		// TODO Auto-generated method stub

	}

}
