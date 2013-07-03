package com.aidufei.protocol.adapter.saition;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.HttpTask;

public class SaitionVODResourceTask extends HttpTask{

	private String mContentID = null;
	private String mResourceCode = null;
	private Device mRemote = null;
	
	private int mOffset = 0;
	private int mDuration = 0;
	
	public SaitionVODResourceTask(Device remote, String content,int offset,int duration){
		mRemote =  remote;
		mContentID = content;
		
		mOffset = offset;
		mDuration = duration;
	}
	@Override
	protected String buildRequest() {
		// TODO Auto-generated method stub
		return "/msis/getContentId?version=V001&contentId=" + mContentID;
	}

	@Override
	protected boolean parseResponse(String strResponse) {
		JSONObject json = null;
		if(strResponse == null || strResponse.length() <= 0)
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
			if(ret != 0)
				return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		try {
			mResourceCode = json.getString("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mResourceCode = null;
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
		if(mRemote != null && mRemote.adapter() != null){
    		if(mRemote.adapter() instanceof SaitionFlyDeviceAdapter){
    			SaitionFlyDeviceAdapter adapter = (SaitionFlyDeviceAdapter)mRemote.adapter();
    				adapter.onPullVOD(mRemote, mResourceCode,mOffset,mDuration);
    		}
    	}
	}

	@Override
	protected void updateError(int error) {
		// TODO Auto-generated method stub
		
	}

}
