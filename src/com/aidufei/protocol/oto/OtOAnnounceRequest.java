package com.aidufei.protocol.oto;



import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OtOAnnounceRequest extends OtORequest {
		private String mUserCode;
		private int mTerminalType;
		private String mUserName;
		private String mUUID;
		
		
		public OtOAnnounceRequest(){
			this.mCmd = OtORequest.OTO_CMD_CONNECT;
			
			mUserName = mUserCode = null;
			mTerminalType = 0;
			
			mUUID = null;
		}
		
		public OtOAnnounceRequest(String userCode, int type, String userName,String uuid){
			this.mCmd = OtORequest.OTO_CMD_CONNECT;
			mUserCode = userCode;
			mTerminalType = type;
			mUserName = userName;
			
			mUUID = uuid;
		}
		
		
		public String userCode(){
			return mUserCode;
		}
		
		public int terminalType(){
			return mTerminalType;
		}
		
		public String userName(){
			return mUserName;
		}
		
		public String uuid(){
			return mUUID;
		}
		
		@Override
		public String paramString() throws Exception{
			// TODO Auto-generated method stub
			JSONObject json = new JSONObject();			
			json.put("UserCode", mUserCode == null?"":mUserCode);
			json.put("TerminalType", mTerminalType);
			json.put("UserName", mUserName ==  null? "":mUserName);
			json.put("UUID", mUUID == null?"1234567890":mUUID);
			return json.toString();
		}

		@Override
		public boolean getParam(String paramString) {
			// TODO Auto-generated method stub
			JSONObject json = null;
			try {
				json = (JSONObject) new JSONTokener(paramString).nextValue();
				if(json == null)
					return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			try {
				mUserCode = json.getString("UserCode");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				mUserName = json.getString("UserName");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mTerminalType = json.getInt("TerminalType");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try{
				mUUID = json.getString("UUID");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected JSONObject paramJSON() throws JSONException {
			// TODO Auto-generated method stub
			JSONObject json = new JSONObject();			
			json.put("UserCode", mUserCode == null?"":mUserCode);
			json.put("TerminalType", mTerminalType);
			json.put("UserName", mUserName ==  null? "":mUserName);
			json.put("UUID", mUUID == null?"1234567890":mUUID);
			return json;
		}

		@Override
		protected void fromJSON(JSONObject json) throws Exception {
			// TODO Auto-generated method stub
			try {
				mUserCode = json.getString("UserCode");
			} catch (JSONException e) {
			
			}
			
			try {
				mUserName = json.getString("UserName");
			} catch (JSONException e) {
				
				
			}
			try {
				mTerminalType = json.getInt("TerminalType");
			} catch (JSONException e) {
				
			}
			
			try{
				mUUID = json.getString("UUID");
			} catch (JSONException e) {
				
			}
			return;
		}
		
}
