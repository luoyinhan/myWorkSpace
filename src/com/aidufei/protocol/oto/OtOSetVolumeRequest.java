package com.aidufei.protocol.oto;



import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OtOSetVolumeRequest extends OtORequest{
	private int mVolume;
	private Status mStatus;
	
	public OtOSetVolumeRequest(){
		this.mCmd = OtORequest.OTO_CMD_SVOLUME;
		mVolume = -1;
	}
	public OtOSetVolumeRequest(int vol){
		this.mCmd = OtORequest.OTO_CMD_SVOLUME;
		mVolume = vol;
	}
	
	public void setVolume(int vol){
		mVolume = vol;
	}
	
	public void setStatus(int returnCode, String desc){
		if(mStatus == null){
			mStatus = new Status(returnCode, desc);
		}else{
			mStatus.setReturnCode(returnCode);
			mStatus.setDescription(desc);
		}
	}
	
	public Status status(){
		return mStatus;
	}
	
	public int volume(){
		return mVolume;
	}
	
	@Override
	public String paramString() throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("Volume", mVolume);
		if(mStatus != null){
			json.put("Status",mStatus.toJSON());
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
			mVolume = json.getInt("Volume");
		} catch (JSONException e) {
		}
		
		try {
			JSONObject statjson = json.getJSONObject("Status");
			if(statjson != null){
				mStatus = new Status();
				mStatus.fromJSON(statjson);
			}
		} catch (JSONException e) {
		}
		
		return true;
	}
	@Override
	protected JSONObject paramJSON() throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("Volume", mVolume);
		if(mStatus != null){
			json.put("Status",mStatus.toJSON());
		}
		return json;
	}
	
	@Override
	protected void fromJSON(JSONObject json) throws Exception {
		// TODO Auto-generated method stub
		try {
			mVolume = json.getInt("Volume");
		} catch (JSONException e) {
		}
		
		try {
			JSONObject statjson = json.getJSONObject("Status");
			if(statjson != null){
				mStatus = new Status();
				mStatus.fromJSON(statjson);
			}
		} catch (JSONException e) {
		}
	}

}
