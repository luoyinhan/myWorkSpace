package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class GDHfcURL extends GDHfcRequest {
    
	private String mAppUrl = null;
	private int mResult = -1;
	private String mError = null;
	private String mCACard = null;
	private int mCommand = -1;
	private GDHfcUrlParam mParam = null;
	private JSONObject mJSONParam = null;
	
	public GDHfcURL(){
		super(null,true);
	}
	
	public GDHfcURL(String serial, String appUrl,GDHfcUrlParam param) {
		super(serial, true);
		
		// TODO Auto-generated constructor stub
		mCmd = GDHfcRequest.CMD_URL;
		mCommand = 1;
		mAppUrl = appUrl;
		mParam = param;
	}
	
	public GDHfcURL(String serial, String appUrl,int cmd,JSONObject param){
		super(serial, true);
		
		mCmd = GDHfcRequest.CMD_URL;
		mCommand = cmd;
		mAppUrl = appUrl;
		mJSONParam = param;
		
	}

	
	
	public GDHfcURL(String serial, GDHfcURL req, int result, String error, String caCard,GDHfcUrlParam param){
		super(serial, false);
		
		mCmd = GDHfcRequest.CMD_URL;
		if(req != null && req.isRequest()){
			mSync = req.mSync;
		}
		mCommand = 2;
		mResult = result;
		mError = error;
		mCACard = caCard;
		mParam = param;
	}
	
	public GDHfcURL(String serial, GDHfcURL req, int result, String error ){
		super(serial, false);
		
		mCmd = GDHfcRequest.CMD_URL;
		if(req != null && req.isRequest()){
			mSync = req.mSync;
		}
		mCommand = 1;
		mResult = result;
		mError = error;
	}
	
	public boolean isPull(){
		return mCommand == 2?true:false;
	}
	
	public boolean isPush(){
		return mCommand == 1?true: false;
		
	}
	
	public GDHfcUrlParam param(){
		return mParam;
	}
	
	public int result(){
		return mResult;
	}
	
	public String message(){
		return mError;
	}
	
	public String CACard(){
		return mCACard;
	}
	
	private JSONObject pushCommandJSON() throws JSONException{
		
		JSONObject json = new JSONObject();
		
		json.put("appUrl", mAppUrl);
		json.put("command", mCommand);
		
		if(mParam == null){
			json.put("parameters",new JSONObject());
		}else{
			JSONObject paramJSON = mParam.toJSON();
			json.put("parameters", paramJSON == null? (new JSONObject()):paramJSON);
		}
		return json;
	}
	
	private JSONObject pushResponseJSON() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject param = new JSONObject();
		
		param.put("result", mResult);
		param.put("message",mError == null?"":mError);
		json.put("command", mCommand);
		json.put("parameters",param);
		return json;
		
		
	}
	
	
private JSONObject extendCommandJSON() throws JSONException{
		
		JSONObject json = new JSONObject();
		
		json.put("appUrl", mAppUrl);
		json.put("command", mCommand);
		
		json.put("parameters", mJSONParam == null?new JSONObject():mJSONParam);
		return json;
		
	}
	
	private JSONObject pullCommandJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("command", mCommand);
		json.put("appUrl", mAppUrl);
		
		json.put("parameters",new JSONObject());
		
		return json;
	}
	
	private JSONObject pullResponseJSON() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject param = null;
		
		if(mParam != null){
			param = mParam.toJSON();
		}
		
		if(param == null){
			param = new JSONObject();
		}
		
		param.put("result", mResult);
		param.put("message", mError);
		param.put("caCardNo",mCACard);
		
		
		json.put("command",mCommand);		
		json.put("parameters",param);
		
		return json;
	}
	
	@Override
	protected byte[] paramToBytes() {
		// TODO Auto-generated method stub
		JSONObject json = null;
		if(isRequest()){
			switch(mCommand){
				case 1:					
					try {
						json = pushCommandJSON();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						return null;
					}
					break;
					
				case 2:
					try {
						json = pullCommandJSON();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						return null;
					}
					break;
				default:
					try {
						json = extendCommandJSON();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						return null;
					}
					break;
				
				    
			}
		}else{
			switch(mCommand){
				case 1:					
					try {
						json = pushResponseJSON();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						return null;
					}
					break;
				case 2:
					try {
						json = pullResponseJSON();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						return null;
					}
					break;
				default:
					return null;
			}
		}
		
		if(json == null)
			return null;
		
		String msg = json.toString();
		if(msg == null)
			return null;
		
		return msg.getBytes();
	}

	
	
    
	
	@Override
	protected boolean paramFromBytes(ByteBuffer buf) {
		// TODO Auto-generated method stub
		JSONObject json = null;
		
		
		byte[] param = new byte[buf.capacity() - buf.position()];
		buf.get(param,0, param.length - 1);
		String paramStr = new String(param);
		
		
		try {
			json = (JSONObject) new JSONTokener(paramStr).nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		try {
			mCommand = json.getInt("command");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		JSONObject paramJson  = null;
		try {
			paramJson = json.getJSONObject("parameters");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		if(isRequest()){
			try {
				mAppUrl = json.getString("appUrl");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return false;
			}
			if(mCommand == 1){
					mParam = new GDHfcUrlParam();
					return mParam.fromJSON(paramJson);				
			}
		}else{
			if(mCommand == 1){
				try {
					mResult = paramJson.getInt("result");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					return false;
				}
				
				try {
					mError = paramJson.getString("message");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}
				
				return true;	
			}else if(mCommand == 2){ //pull status
				mParam = new GDHfcUrlParam();
				
				try {
					mResult = paramJson.getInt("result");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					return false;
				}
				
				try {
					mError = paramJson.getString("message");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}
				
				try {
					mCACard = paramJson.getString("caCardNo");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					mCACard = null;
				}
				return mParam.fromJSON(paramJson);
			}
		}
		return true;
	}

}
