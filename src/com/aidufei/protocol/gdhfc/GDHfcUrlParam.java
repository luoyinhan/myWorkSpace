package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

public class GDHfcUrlParam {
	public static final int NONE = 0;
	public static final int VOB = 1;
	public static final int VOD = 2;
	public static final int VOB_SHIFT = 3;
	public static final int IMAGE = 4;
	
	private int mType = NONE;
	private float mFrequency = 0;
	private int mSymbolRate = 0;
	private String mModulation = null;
	private int  mProgramNumber = 0;
	private int mVideoPID = 0;
	private int mAudioPID = 0;
	
	
	private int mOffsetTime = 0;
	private String mAssetID = null;
	private String mProviderID = null;
	
	
	private long mStartTime = 0;
	private long mEndTime = 0;
	
	private String mImageAddress = null;

	
	public GDHfcUrlParam(){
		mType = NONE;
		mFrequency = 0;
		mSymbolRate = 6875;
		mModulation = "64QAM";
		mProgramNumber = 0;
		mVideoPID = 0;
		mAudioPID = 0;
		
		mOffsetTime = 0;
		mAssetID = null;
		mProviderID = null;
		
		mStartTime = 0;
		mEndTime = 0;
		

	}
	
	public GDHfcUrlParam(float freq, int rate, String modulation, int program, int video, int audio){
		mType = VOB;
		mFrequency = freq;
		mSymbolRate = rate;
		mModulation = modulation;
		mProgramNumber = program;
		mVideoPID = video;
		mAudioPID = audio;
		
		mOffsetTime = 0;
		mAssetID = null;
		mProviderID = null;
		
		mStartTime = 0;
		mEndTime = 0;
		
	}
	
	public GDHfcUrlParam(String asset, String provider, int offset){
		mType = VOD;
		mFrequency = 0;
		mSymbolRate = 0;
		mModulation = null;
		mProgramNumber = 0;
		mVideoPID = 0;
		mAudioPID = 0;
		
		mOffsetTime = offset;
		mAssetID = asset;
		mProviderID = provider;
		
		mStartTime = 0;
		mEndTime = 0;
		
	}
	
	public GDHfcUrlParam(String imageAddress){
		mType = IMAGE;
		mImageAddress = imageAddress;
	}
	
	public GDHfcUrlParam(float freq, int rate, String modulation, int program, int video, int audio,long start, long end, int offset){
		mType = VOB_SHIFT;
		mFrequency = freq;
		mSymbolRate = rate;
		mModulation = modulation;
		mProgramNumber = program;
		mVideoPID = video;
		mAudioPID = audio;
		mStartTime = start;
		mEndTime = end;
		mOffsetTime = offset;
		
		mAssetID = null;
		mProviderID = null;	
	}
	

	public int type(){
		return mType;
	}
	
	public float frequency(){
		return mFrequency;
	}
	
	public int symbolRate(){
		return mSymbolRate;
	}
	
	public String modulation(){
		return mModulation;
	}
	
	public int programNumber(){
		return mProgramNumber;
	}
	
	public int videoPID(){
		return mVideoPID;
	}
	
	public int audioPID(){
		return mAudioPID;
	}
	
	public String assetID(){
		return mAssetID;
	}
	
	public String providerID(){
		return mProviderID;
	}
	
	public long start(){
		return mStartTime;
	}
	
	public long end(){
		return mEndTime;
	}
	
	public int offset(){
		return mOffsetTime;
	}
	
	public String image(){
		return mImageAddress;
	}
	
	public JSONObject toJSON() throws JSONException{
		
		JSONObject json = new JSONObject();
		json.put("type", mType);
		switch(mType){
		case VOB:
			
			json.put("tv_freq", mFrequency);
			json.put("tv_SymbolRate", mSymbolRate);
			json.put("tv_Modulation", mModulation);
			json.put("tv_ProgramNumber", mProgramNumber);
			json.put("tv_VideoPID", mVideoPID);
			json.put("tv_AudioPID", mAudioPID);
			break;
		case VOD:
			json.put("offset_time",mOffsetTime);
			json.put("assetID", mAssetID);
			json.put("providerID", mProviderID);
			break;
		case VOB_SHIFT:
			json.put("tv_freq", mFrequency);
			json.put("tv_SymbolRate", mSymbolRate);
			json.put("tv_Modulation", mModulation);
			json.put("tv_ProgramNumber", mProgramNumber);
			json.put("tv_VideoPID", mVideoPID);
			json.put("tv_AudioPID", mAudioPID);
			json.put("startTime", mStartTime);
			json.put("endTime", mEndTime);
			json.put("offset_time", mOffsetTime);
			break;
		case IMAGE:
			json.put("image", mImageAddress == null?"":mImageAddress);
			break;
		default:
			return null;
		}
		
		return json;
	}
	
	private boolean VOBShiftFromJSON(JSONObject json){
		
		VOBFromJSON(json);
		
		try {
			mOffsetTime = json.getInt("offset_time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mOffsetTime = 0;
		}
		
		try {
			mStartTime = json.getLong("startTime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mStartTime = 0;
		}
		
		try {
			mEndTime = json.getLong("endTime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mEndTime = 0;
		}
		return true;
		
	}
	private boolean VODFromJSON(JSONObject json){

		try {
			mAssetID = json.getString("assetID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mAssetID = null;
			return false;
		}
		
		try {
			mProviderID = json.getString("providerID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mProviderID = null;
			return false;
		}
		
		try {
			mOffsetTime = json.getInt("offset_time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mOffsetTime = 0;
		}
		
		return true;
	}
	
	private boolean VOBFromJSON(JSONObject json){
		try {
			mFrequency = (float) json.getDouble("tv_freq");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mFrequency = 0;
		}
		
		try {
			mSymbolRate = json.getInt("tv_SymbolRate");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mSymbolRate = 0;
		}
		
		try {
			mModulation = json.getString("tv_Modulation");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mModulation = "64QAM";
		}
		
		try {
			mProgramNumber = json.getInt("tv_ProgramNumber");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mProgramNumber = 0;
		}
		
		try {
			mVideoPID = json.getInt("tv_VideoPID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mVideoPID = 0;
		}
		
		try {
			mAudioPID = json.getInt("tv_AudioPID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mAudioPID = 0;
		}
		return true;
	}
	
	
	private boolean ImageFromJSON(JSONObject json){
		try {
			mImageAddress = json.getString("image");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			mImageAddress = null;
		}
		return true;
	}
	
	public boolean fromJSON(JSONObject json){
		try {
			mType = json.getInt("type");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		switch(mType){
			case VOB:
				return VOBFromJSON(json);
			case VOD:
				return VODFromJSON(json);
			case VOB_SHIFT:
				return VOBShiftFromJSON(json);
			case IMAGE:
				return ImageFromJSON(json);
			default:
				return false;
		}
	}
}