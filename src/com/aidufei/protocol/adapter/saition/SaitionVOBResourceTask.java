package com.aidufei.protocol.adapter.saition;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.aidufei.protocol.adapter.gdhfc.GDHfcDevice;
import com.aidufei.protocol.adapter.gdhfc.GDHfcDeviceAdapter;
import com.aidufei.protocol.core.HttpTask;
import com.aidufei.protocol.gdhfc.GDHfcUrlParam;
import com.aidufei.protocol.oto.ResourceInfo;

public class SaitionVOBResourceTask extends HttpTask{
	private static final String  GET_VOB_URL = "/protocolAdapter-webapp/rest/getChannelResourceCode";
	private static final String  URL_TYPE = "tianwei";
	
	private String mResourceCode = null;
	
	private String mTS = null;
	private String mNetwork = null;
	private String mService = null;
	
	private String mRequest = null;
	private SaitionFlyDevice mRemote = null;
	private long mStart = 0;
	private long mEnd = 0;
	private int  mOffset = 0;
	private int  type = ResourceInfo.PLAY_NONE;
	
	
	public SaitionVOBResourceTask(SaitionFlyDevice remote, String ts, String network, String service){
		mRemote = remote;
		mTS = ts;
		mNetwork = network;
		mService = service;
		type = ResourceInfo.PLAY_VOB;
	}
	
	//&tsId=1&serviceId=2&networkId=3
	@Override
	protected String buildRequest() {
		mRequest = null;
		mRequest = GET_VOB_URL + 
	               "?type=" + URL_TYPE  + 
	               "&tsId=" + mTS + 
	               "&serviceId=" + mService +
	               "&networkId=" + mNetwork;
		return mRequest;
	}

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
				return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		try {
			mResourceCode = json.getString("resourceCode");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
    			if(type == ResourceInfo.PLAY_VOB){
    				adapter.onPullVOB(mRemote, mResourceCode,0);
    			}else if(type == ResourceInfo.PLAY_VOB_SHIFT){
    				adapter.onPullVOB(mRemote,mResourceCode, mStart,mEnd,mOffset);
    			}
    		}
    	}	
	}

	@Override
	protected void updateError(int error) {
		// TODO Auto-generated method stub
		
	}

}
