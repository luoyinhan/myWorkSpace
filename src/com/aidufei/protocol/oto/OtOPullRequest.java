package com.aidufei.protocol.oto;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OtOPullRequest extends OtORequest{
	private ResourceInfo mResourceInfo;
	private Status  mStatus;
	
	public OtOPullRequest(){
		this.mCmd = OtORequest.OTO_CMD_PULL;
		mResourceInfo = null;
		mStatus = null;
	}
	
	public OtOPullRequest(Status status, ResourceInfo info){
		this.mCmd = OtORequest.OTO_CMD_PULL;
		mResourceInfo = info;
		mStatus = status;
	}
	
	public void setStatus(int returnCode, String desc){
		if(mStatus == null){
			mStatus = new Status(returnCode,desc);
		}else{
			mStatus.setReturnCode(returnCode);
			mStatus.setDescription(desc);
		}
	}
	
	public void setResourceInfo(ResourceInfo info){
		mResourceInfo = info;
	}
	
	public Status status(){
		return mStatus;
	}
	
	public ResourceInfo resource(){
		return mResourceInfo;
	}
	@Override
	public String paramString() throws Exception {
		// TODO Auto-generated method stub
		
		if(mStatus != null){
			JSONObject json = new JSONObject();
			json.put("Status", mStatus.toJSON());
			if(mResourceInfo != null){
				json.put("ResourceInfo", mResourceInfo.toJSON());
			}
			return json.toString();
		}
		return null;
		
		
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
			JSONObject resjson = json.getJSONObject("ResourceInfo");
			if(resjson != null){
				mResourceInfo = new ResourceInfo();
				if(mResourceInfo == null)
					return true;
				mResourceInfo.fromJSON(resjson);
			}
		} catch (JSONException e) {			
		}		
		return true;
	}

	@Override
	protected JSONObject paramJSON() throws Exception {
		// TODO Auto-generated method stub
		if(mStatus != null){
			JSONObject json = new JSONObject();
			json.put("Status", mStatus.toJSON());
			if(mResourceInfo != null){
				json.put("ResourceInfo", mResourceInfo.toJSON());
			}
			return json;
		}
		return null;
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
			JSONObject resjson = json.getJSONObject("ResourceInfo");
			if(resjson != null){
				mResourceInfo = new ResourceInfo();
				if(mResourceInfo == null)
					return;
				mResourceInfo.fromJSON(resjson);
			}
		} catch (JSONException e) {			
		}		
	}

}
