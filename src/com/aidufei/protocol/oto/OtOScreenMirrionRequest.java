package com.aidufei.protocol.oto;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OtOScreenMirrionRequest extends OtORequest {
	
	private boolean mMirrion = false;
	private Status mStatus = null;
	public OtOScreenMirrionRequest(boolean mirrion){
		this.mCmd = OtORequest.OTO_CMD_MIRRION;
		mMirrion = mirrion;
		mStatus = null;
	}
	
	public OtOScreenMirrionRequest(boolean mirrion, int retCode, String desc){
		this.mCmd = OtORequest.OTO_CMD_MIRRION;
		mMirrion = mirrion;
		mStatus = new Status(retCode,desc);
	}
	
	public boolean mirrioned(){
		return mMirrion;
	}
	
	public Status status(){
		return mStatus;
	}
	
	@Override
	public String paramString() throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("Mirrion", mMirrion == true? 1: 0);
		if(mStatus != null){
			json.put("Status", mStatus.toJSON());
		
		}
		return json.toString();
	}

	@Override
	public boolean getParam(String paramString) {
		// TODO Auto-generated method stub
		if(paramString == null || paramString.length() <= 0){
			return true;
		}	
		JSONObject json = null;
		try {
			json = (JSONObject) new JSONTokener(paramString).nextValue();
			if(json == null)
				return true;
		} catch (JSONException e) {
			return true;
		}
		try {
			 JSONObject statjson = json.getJSONObject("Status");
			 if(statjson != null){
				 mStatus = new Status();
				 if(mStatus == null){
					 return true;
				 }
				 mStatus.fromJSON(statjson);
			 }
		} catch (JSONException e) {
		}
				
		try {
			int mirrion = json.getInt("Mirrion");
			mMirrion = mirrion==1?true:false;
		} catch (JSONException e) {			
		}		
		return true;
	}

	@Override
	protected JSONObject paramJSON() throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("Mirrion", mMirrion == true?1:0);
		if(mStatus != null){
			json.put("Status", mStatus.toJSON());
			
		}
		return json;
	}

	@Override
	protected void fromJSON(JSONObject json) throws Exception {
		// TODO Auto-generated method stub
		try {
			 JSONObject statjson = json.getJSONObject("Status");
			 if(statjson != null){
				 mStatus = new Status();
				 if(mStatus == null){
					 return;
				 }
				 mStatus.fromJSON(statjson);
			 }
		} catch (JSONException e) {
		}
				
		try {
			int mirrion = json.getInt("Mirrion");
			mMirrion = mirrion == 0?false:true;
		} catch (JSONException e) {			
		}	
	}

}
