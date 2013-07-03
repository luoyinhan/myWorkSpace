package com.aidufei.protocol.adapter.saition;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.aidufei.protocol.core.HttpTask;
import com.aidufei.protocol.oto.ResourceInfo;

public class SaitionVOBParamTask extends HttpTask{
	
	private static final String  GET_VOB_URL = "/protocolAdapter-webapp/rest/getChannelElements";
	private static final String  URL_TYPE = "tianwei";
	
	private String mResouceCode;
	private SaitionFlyDevice mRemote;
	private long mStart = 0;
	private long mEnd = 0;
	private int mOffset = 0;
	
	private String mService = null;
	private String mTS = null;
	private String mNetwork = null;
	
	private int mType = ResourceInfo.PLAY_NONE;

	
	private String mRequest = null;
	public SaitionVOBParamTask(SaitionFlyDevice remote,String resource,  long start, long end,int offset){
		mResouceCode = resource;
		mRemote = remote;
		mStart = start;
		mEnd = end;
		mOffset = offset;
		mType = ResourceInfo.PLAY_VOB_SHIFT;	
	}
	
	public SaitionVOBParamTask(SaitionFlyDevice remote,String resource,int delay){
		mResouceCode = resource;
		mRemote = remote;
		mType = ResourceInfo.PLAY_VOB;
		mOffset = delay;
	}
	
	@Override
	protected String buildRequest() {
		mRequest = GET_VOB_URL +
				   "?type=" + URL_TYPE +
				   "&resourceCode=" + mResouceCode;
		
		return mRequest;
	}

	
	
	//{"serviceId":"2","tsId":"1","networkId":"3"}
		private boolean parseChannel(JSONObject json){
			try {
				mService = json.getString("serviceId");
				mTS = json.getString("tsId");
				mNetwork = json.getString("networkId");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return false;
			}	
			return true;
		}
		
	//{"ret":"0","channelElements":{"serviceId":"2","tsId":"1","networkId":"3"},"retInfo":""}
	@Override
	protected boolean parseResponse(String strResponse) {
		// TODO Auto-generated method stub
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
		JSONObject channelJson = null;
		try {
			channelJson = json.getJSONObject("channelElements");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		if(channelJson == null)
			return false;
		return parseChannel(channelJson);
		
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
    			switch(mType){
    			case ResourceInfo.PLAY_VOB:
    			case ResourceInfo.PLAY_VOB_DELAY:
    				adapter.playVOB(mRemote, mService,mTS,mNetwork,mOffset);
    				break;
    			case ResourceInfo.PLAY_VOB_SHIFT:
    				adapter.playVOB(mRemote, mService, mTS, mNetwork, mStart, mEnd, mOffset);
    				break;
    			default:
    				return;
    			}
    		}
    	}	
	}

	@Override
	protected void updateError(int error) {
		// TODO Auto-generated method stub
		
	}

}
