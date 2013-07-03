package com.aidufei.protocol.adapter.gdhfc;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.aidufei.protocol.core.HttpTask;
import com.aidufei.protocol.gdhfc.GDHfcUrlParam;

public class GDHfcVOBParamTask extends HttpTask{
	
	private static final String URL_LOCATION = "/protocolAdapter-webapp/rest/getChannelElements";
	private static final String URL_TYPE = "gd";
	
	private String mResourceCode = null;
	private GDHfcUrlParam mParam = null;
	private int mType = GDHfcUrlParam.NONE;
	
	
	private GDHfcDevice mRemote = null;
	private long mStart = 0;
	private long mEnd = 0;
	private int mOffset = 0;
	
	public GDHfcVOBParamTask(String resource, GDHfcDevice remote, long start, long end,int offset){
		mResourceCode = resource;
		mParam = null;
		mRemote = remote;
		mStart = start;
		mEnd = end;
		mOffset = offset;	
		mType = GDHfcUrlParam.VOB_SHIFT;
		
	}
	
	
	public GDHfcVOBParamTask(String resource,GDHfcDevice remote){
		mResourceCode = resource;
		mParam = null;
		mRemote = remote;
		mType = GDHfcUrlParam.VOB;
	}
	
	@Override
	protected String buildRequest() {
		// TODO Auto-generated method stub
		if(mResourceCode == null)
			return null;
		return  URL_LOCATION + "?type=" + URL_TYPE + "&resourceCode=" + mResourceCode;
	}
	
	
//	{"tv_VideoPID":"0","tv_AudioPID":"0","tv_Modulation":"0","tv_freq":"29100","tv_ProgramNumber":"0","tv_SymbolRate":"0"}
	private boolean parseChannel(JSONObject json){
		
		float freq = 0;
		try {
			freq = (float) json.getDouble("tv_freq");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		int symbolRate = 0;
		try {
			symbolRate = json.getInt("tv_SymbolRate");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			symbolRate = 6875;
		}
		
		int video = 0;
		try {
			video = json.getInt("tv_VideoPID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		
		int audio = 0;
		try {
			audio = json.getInt("tv_AudioPID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		
		int program = 0;
		try {
			program = json.getInt("tv_ProgramNumber");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		
		String modulation = null;
		try {
			modulation = json.getString("tv_Modulation");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			modulation = "64QAM";
		}
		if(mType == GDHfcUrlParam.VOB){
			mParam = new GDHfcUrlParam(freq,symbolRate,modulation,program,video,audio);
		}else if(mType == GDHfcUrlParam.VOB_SHIFT){
			mParam = new GDHfcUrlParam(freq,symbolRate,modulation,program,video,audio,mStart,mEnd,mOffset);
		}
		return true;
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
    		if(mRemote.adapter() instanceof GDHfcDeviceAdapter){
    			GDHfcDeviceAdapter adapter = (GDHfcDeviceAdapter)mRemote.adapter();
    			adapter.playVOB(mRemote, mParam);
    		}
    	}	
	}

	@Override
	protected void updateError(int error) {
		// TODO Auto-generated method stub
		
	}

}
