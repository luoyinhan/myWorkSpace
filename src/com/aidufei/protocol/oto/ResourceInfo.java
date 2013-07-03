package com.aidufei.protocol.oto;

import org.json.JSONException;
import org.json.JSONObject;

public class ResourceInfo {
	public static final int PLAY_NONE = 0;
	public static final int PLAY_VOD = 1;
	public static final int PLAY_VOB = 2;
	public static final int PLAY_VOB_SHIFT = 3;
	public static final int PLAY_VOB_DELAY = 4;
	
	
	private String mAssetID = null;
	private String mProviderID = null;
	private String mResourceName = null;
	private String mResourceCode = null;
	private String mProductCode = null;
	private int mPlayType = PLAY_NONE;
	private long mDelay = 0;
	private long mShiftTime = 0;
	private long mShiftEnd = 0;
	private int mTimeCode = 0;
	private int mDuration = 0;	

	public ResourceInfo(){
		
	}
	
	public ResourceInfo(String name, String code, String product, long delay){
		mResourceName = name;
		mResourceCode = code;
		mProductCode = product;
		
		if(delay != 0){
			mPlayType = PLAY_VOB_DELAY;
			mDelay = delay;
		}else{
			mPlayType = PLAY_VOB;
		}
		
	}
	
	
	public ResourceInfo(String assetID, String providerID, int offset, int duration){
		mAssetID = assetID;
		mProviderID = providerID;
		mTimeCode = offset;
		mDuration = duration;
		mPlayType = PLAY_VOD;
	}
	
	
	
	public ResourceInfo(String name, String code, String product, int offset, int duration){
		mResourceName = name;
		mResourceCode = code;
		mProductCode = product;
		
		mPlayType = PLAY_VOD;
		mTimeCode = offset;
		mDuration = duration;
	}
	
	public ResourceInfo(String name, String code, String product, long start, long end,int offset){
		mResourceName = name;
		mResourceCode = code;
		mProductCode = product;
		
		mPlayType = PLAY_VOB_SHIFT;
		mShiftTime = start;
		mShiftEnd = end;		
	}
	
	public String asset(){
		return mAssetID;
	}
	
	public String provider(){
		return mProviderID;
	}
	
	public String name(){
		return mResourceName;
	}
	
	public String code(){
		return mResourceCode;
	}
	
	public String product(){
		return mProductCode;
	}
	
	public int type(){
		return mPlayType;
	}
	
	public int duration(){
		return mDuration;
	}
	
	public int offset(){
		return mTimeCode;
	}
	
	public long delay(){
		return mDelay;
	}
	
	public long start(){
		return mShiftTime;
	}
	
	public long end(){
		return mShiftEnd;
	}
	
	
	public JSONObject toJSON() throws JSONException {
		if(mPlayType == PLAY_NONE)
			return null;
		JSONObject json = new JSONObject();
		
		json.put("ResourceName", mResourceName == null?"":mResourceName);
		json.put("ResourceCode", mResourceCode == null?"":mResourceCode);
		json.put("ProductCode", mProductCode == null?"":mProductCode);
		json.put("PlayType", mPlayType);
		switch(mPlayType){
			case PLAY_VOB:
				break;
			case PLAY_VOD:
				json.put("TimeCode", mTimeCode);
				json.put("Duration", mDuration);
				break;
			case PLAY_VOB_SHIFT:
				json.put("ShiftTime", mShiftTime);
				json.put("ShiftEnd", mShiftEnd);
				break;
			case PLAY_VOB_DELAY:
				json.put("Delay", mDelay);
				break;
			default:
				break;
		}
		
		return json;
	}
	
	public void fromJSON(JSONObject json){
	
		try {
			mPlayType = json.getInt("PlayType");
		} catch (JSONException e) {
			return;
		}
		
		try {
			mResourceName = json.getString("ResourceName");
		} catch (JSONException e) {
		}
		
		try {
			mResourceCode = json.getString("ResourceCode");
		} catch (JSONException e) {
		}
		
		
		try {
			mProductCode = json.getString("ProductCode");
		} catch (JSONException e) {
		}

		switch(mPlayType){
			case PLAY_VOB_DELAY:
				try {
					mDelay = json.getInt("Delay");
				} catch (JSONException e) {
				}
				break;
			case PLAY_VOD:
				try {
					mTimeCode = json.getInt("TimeCode");
					mDuration = json.getInt("Duration");
				} catch (JSONException e) {
				}
				break;
			case PLAY_VOB_SHIFT:
				try {
					mShiftTime = json.getLong("ShiftTime");
					mShiftEnd = json.getLong("ShiftEnd");
				} catch (JSONException e) {
				}
				break;
			default:
				break;
		}
	}
	
}
