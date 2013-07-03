package com.aidufei.protocol.oto;



import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OtOMediaControlRequest extends OtORequest{

	private int mControl;
	private int mProgress;
	private Status mStatus;
	public OtOMediaControlRequest(){
		this.mCmd = OtORequest.OTO_CMD_CONTROL;
		mControl = Global.MEDIA_NONE;
		mStatus = null;
		mProgress = -1;
	}
	
	public OtOMediaControlRequest(int control){
		this.mCmd = OtORequest.OTO_CMD_CONTROL;
		mControl = control;
		mStatus = null;
		mProgress = -1;
	}
	
	public int control(){
		return mControl;
	}
	
	
	public void setControl(int control){
		mControl = control;
		mStatus = null;
	}
	
	public void setProgress(int progress){
		mProgress = progress;
		mStatus = null;
	}
	
	public int progress(){
		return mProgress;
	}
	
	public void setStatus(int returnCode, String desc){
		mStatus = new Status(returnCode, desc);
	}
	
	public Status status(){
		return mStatus;
	}
	
	@Override
	public String paramString() throws Exception {
		// TODO Auto-generated method stub
		
		JSONObject json = new JSONObject();
		if(mStatus != null){
			json.put("Status", mStatus.toJSON());
		}else{
			json.put("Control", mControl);
			if(mControl == Global.MEDIA_PROGRESS){
				json.put("Progress", mProgress);
			}
		}
		return json.toString();
	}

	@Override
	public boolean getParam(String paramString) {
		// TODO Auto-generated method stub
		if(paramString == null || paramString.length() <= 0)
			return true;
		
		JSONObject json = null;
		try {
			json = (JSONObject) new JSONTokener(paramString).nextValue();
			if(json == null)
				return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return true;
		}
		
		try {
			JSONObject statjson = json.getJSONObject("Status");
			if(statjson != null){
				mStatus = new Status();
				mStatus.fromJSON(statjson);
			}
		} catch (JSONException e) {
		}
		
		try {
			mControl = json.getInt("Control");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		
		if(mControl == Global.MEDIA_PROGRESS){
			try {
				mProgress = json.getInt("Progress");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}
		}
		return true;
	}

	@Override
	protected JSONObject paramJSON() throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		if(mStatus != null){
			json.put("Status", mStatus.toJSON());
		}else{
			json.put("Control", mControl);
			if(mControl == Global.MEDIA_PROGRESS){
				json.put("Progress", mProgress);
			}
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
				mStatus.fromJSON(statjson);
			}
		} catch (JSONException e) {
		}
		
		try {
			mControl = json.getInt("Control");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		
		if(mControl == Global.MEDIA_PROGRESS){
			try {
				mProgress = json.getInt("Progress");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}
		}
	}


	
}
