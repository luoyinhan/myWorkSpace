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

import android.os.AsyncTask;
import android.util.Log;

import com.aidufei.protocol.gdhfc.GDHfcUrlParam;
import com.coship.ott.utils.LogUtils;

public class GDHfcVOBParamTask2 extends AsyncTask<String, Integer, Integer> {

	private static final String GET_VOB_URL = "http://183.62.141.34/protocolAdapter-webapp/rest/getChannelElements";
	private static final String URL_TYPE = "gd";

	private String mResouceCode;

	GDHfcUrlParam mParam = null;
	private String mRequest = null;
	private GDHfcDevice mRemote = null;
	private long mStart = 0;
	private long mEnd = 0;
	private int mOffset = 0;
	private int type = GDHfcUrlParam.NONE;

	public GDHfcVOBParamTask2(String resource, GDHfcDevice remote, long start,
			long end, int offset) {
		mResouceCode = resource;
		mParam = null;
		mRemote = remote;
		mStart = start;
		mEnd = end;
		mOffset = offset;

		type = GDHfcUrlParam.VOB_SHIFT;

	}

	public GDHfcVOBParamTask2(String resource, GDHfcDevice remote) {
		mResouceCode = resource;
		mParam = null;
		mRemote = remote;
		type = GDHfcUrlParam.VOB;
	}

	@Override
	protected void onPreExecute() {
		mRequest = GET_VOB_URL + "?" + "type=" + URL_TYPE + "&"
				+ "resourceCode=" + mResouceCode;
		mParam = null;
	}

	// {"tv_VideoPID":"0","tv_AudioPID":"0","tv_Modulation":"0","tv_freq":"29100","tv_ProgramNumber":"0","tv_SymbolRate":"0"}
	private boolean parseChannel(JSONObject json) {
		float freq = 0;
		try {
			freq = (float) json.getDouble("tv_freq");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}

		int symbolRate = 0;
		try {
			symbolRate = json.getInt("tv_SymbolRate");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			symbolRate = 6875;
		}

		int video = 0;
		try {
			video = json.getInt("tv_VideoPID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}

		int audio = 0;
		try {
			audio = json.getInt("tv_AudioPID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}

		int program = 0;
		try {
			program = json.getInt("tv_ProgramNumber");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}

		String modulation = null;
		try {
			modulation = json.getString("tv_Modulation");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			modulation = "64QAM";
		}
		if (type == GDHfcUrlParam.VOB) {
			mParam = new GDHfcUrlParam(freq, symbolRate, modulation, program,
					video, audio);
		} else if (type == GDHfcUrlParam.VOB_SHIFT) {
			mParam = new GDHfcUrlParam(freq, symbolRate, modulation, program,
					video, audio, mStart, mEnd, mOffset);
		}
		return true;
	}

	// {"ret":"0","channelElements":{"tv_VideoPID":"0","tv_AudioPID":"0","tv_Modulation":"0","tv_freq":"29100","tv_ProgramNumber":"0","tv_SymbolRate":"0"},"retInfo":""}
	private boolean parseResponse(byte[] response) {
		JSONObject json = null;
		String strResponse = new String(response);
		if (strResponse == null || strResponse.length() <= 0)
			return false;

		try {
			json = (JSONObject) new JSONTokener(strResponse).nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		int ret;
		try {
			ret = json.getInt("ret");
			if (ret != 0)
				return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		JSONObject channelJson = null;
		try {
			channelJson = json.getJSONObject("channelElements");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		if (channelJson == null)
			return false;
		return parseChannel(channelJson);

	}

	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(mRequest);
			HttpResponse response = client.execute(get);
			if (response == null)
				return -1;
			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				long total = entity.getContentLength();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int count = 0;
				int length = -1;
				while ((length = is.read(buf)) != -1) {
					baos.write(buf, 0, length);
					count += length;
					// 调用publishProgress公布进度,最后onProgressUpdate方法将被执行
					publishProgress((int) ((count / (float) total) * 100));
					// 为了演示进度,休眠500毫秒
					// Thread.sleep(500);
				}
				// return new String(baos.toByteArray(), "gb2312");
				if (parseResponse(baos.toByteArray()) == true) {
					return 0;
				}
			}
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, LogUtils.getTAG(), e.getMessage());
			return -1;
		}
		return -1;

	}

	@Override
	protected void onProgressUpdate(Integer... progresses) {

	}

	// onPostExecute方法用于在执行完后台任务后更新UI,显示结果
	@Override
	protected void onPostExecute(Integer result) {
		if (result == null)
			return;
		if (result < 0) {
			return;
		}
		if (mRemote != null && mRemote.adapter() != null) {
			if (mRemote.adapter() instanceof GDHfcDeviceAdapter) {
				GDHfcDeviceAdapter adapter = (GDHfcDeviceAdapter) mRemote
						.adapter();
				adapter.playVOB(mRemote, mParam);
			}
		}
	}

	// onCancelled方法用于在取消执行中的任务时更改UI
	@Override
	protected void onCancelled() {

	}

}
