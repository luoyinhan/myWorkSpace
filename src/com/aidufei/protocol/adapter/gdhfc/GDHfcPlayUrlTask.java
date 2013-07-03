package com.aidufei.protocol.adapter.gdhfc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.aidufei.protocol.core.HttpTask;
import com.aidufei.protocol.gdhfc.GDHfcUrlParam;

import android.os.AsyncTask;
import android.util.Log;

public class GDHfcPlayUrlTask extends HttpTask {
	
	private static final String URL_LOCATION =  "/protocolAdapter-webapp/rest/getUrlConfig";
	private static final String URL_TYPE = "gd";
	
	private String mVOBUrl = null;
	private String mVODUrl = null;
	
	
	private String mResource = null;
	private int mType = GDHfcUrlParam.NONE;
	private GDHfcDeviceAdapter mAdapter = null;
	
	private GDHfcDevice mRemote  = null;
	private long mStart = 0;
	private long mEnd = 0;
	private int mOffset = 0;
	private int mDuration = 0;
	
	private String mAsset = null;
	private String mProvider = null;
	
	public GDHfcPlayUrlTask(GDHfcDeviceAdapter adapter){
		mAdapter = adapter;
	}
	
	public GDHfcPlayUrlTask(GDHfcDeviceAdapter adapter, GDHfcDevice remote,String resource){
		mAdapter = adapter;
		mResource = resource;
		mRemote = remote;
		mType = GDHfcUrlParam.VOB;
	}
	
	public GDHfcPlayUrlTask(GDHfcDeviceAdapter adapter, GDHfcDevice remote,String resource, long start, long end, int offset){
		mAdapter = adapter;
		mResource = resource;
		mType = GDHfcUrlParam.VOB_SHIFT;
		
		mRemote = remote;
		mStart = start;
		mEnd = end;
		mOffset = offset;
		
	}
	
	public GDHfcPlayUrlTask(GDHfcDeviceAdapter adapter, GDHfcDevice remote,String asset, String provider, int offset,int duration){
		mAdapter = adapter;		
		mType = GDHfcUrlParam.VOD;
		mAsset = asset;
		mProvider = provider;
		mRemote = remote;
		mOffset = offset;
		mDuration = duration;
	}
	
	@Override
	protected String buildRequest() {
		// TODO Auto-generated method stub
		return  URL_LOCATION + "?type=" + URL_TYPE;
	}
	
//	{"vodUrl":"192.168.88.100:106/play/index.html","vobUrl":"main://html/changePage.html"}
	private boolean parseUrl(JSONObject json){
		
		try {
			mVODUrl = json.getString("vodUrl");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		try {
			mVOBUrl = json.getString("vobUrl");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
//	{"ret":"0","urlConfig":{"vodUrl":"192.168.88.100:106/play/index.html","vobUrl":"main://html/changePage.html"},"retInfo":""} 
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
		
		JSONObject urlJson = null;
		try {
			urlJson = json.getJSONObject("urlConfig");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		
		if(urlJson == null)
			return false;
		return parseUrl(urlJson);
		
	}

	@Override
	protected void updateProgress(int progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleResponse() {
		// TODO Auto-generated method stub
		
		switch(mType){
			case GDHfcUrlParam.NONE:
				if(mAdapter != null)
					mAdapter.setPlayUrl(mVOBUrl, mVODUrl);
				break;
			case GDHfcUrlParam.VOB:
				
				if(mAdapter != null){
					mAdapter.setPlayUrl(mVOBUrl, mVODUrl);
					if(mVOBUrl == null)
						return;
					mAdapter.playVOB(mRemote, null, null, mResource, null, 0);
				}
				break;
			case GDHfcUrlParam.VOB_SHIFT:
				if(mAdapter != null){
					mAdapter.setPlayUrl(mVOBUrl, mVODUrl);
					if(mVOBUrl == null)
						return;
					mAdapter.playVOB(mRemote, null, null, mResource, null, mStart, mEnd, mOffset);
				}
				break;
			case GDHfcUrlParam.VOD:
				if(mAdapter != null){
					mAdapter.setPlayUrl(mVOBUrl, mVODUrl);
					if(mVODUrl == null)
						return;
					mAdapter.playVOD(mRemote, null, null, null, null, mAsset, mProvider, mOffset, mDuration);
				}
				break;
		}
	}

	@Override
	protected void updateError(int error) {
		// TODO Auto-generated method stub
		
	}
}
