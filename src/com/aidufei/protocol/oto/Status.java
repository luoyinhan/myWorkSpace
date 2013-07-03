package com.aidufei.protocol.oto;

import org.json.JSONException;
import org.json.JSONObject;

public class Status {
	public static final int STATUS_UNKNOWN = 0;
	public static final int STATUS_OK = 100;
	public static final int STATUS_PROCESSING = 99;
	public static final int STATUS_FAILED = 101;
	private int mReturnCode;
	private String mDescription;
	
	
	public Status(int retCode, String desc){
		mReturnCode = retCode;
		mDescription = desc;
	}
	
	public Status(){
		mReturnCode = STATUS_UNKNOWN;
		mDescription = null;
	}
	
	public void setReturnCode(int code){
		mReturnCode = code;
	}
	
	public void setDescription(String desc){
		mDescription = desc;
	}
	
	public int returnCode(){
		return mReturnCode;
	}
	
	public String description(){
		return mDescription;
	}
	
	public JSONObject toJSON() throws JSONException{
		
		JSONObject json = new JSONObject();
		json.put("ReturnCode", mReturnCode);
		json.put("Description", mDescription == null? "" : mDescription);
		return json;		
	}
	
	public void fromJSON(JSONObject json){
		if(json == null)
			return;
		try{
			mReturnCode = json.getInt("ReturnCode");
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		try{
			mDescription = json.getString("Description");
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
	}
	
}
