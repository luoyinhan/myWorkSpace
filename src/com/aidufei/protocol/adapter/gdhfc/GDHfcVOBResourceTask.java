package com.aidufei.protocol.adapter.gdhfc;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.aidufei.protocol.core.HttpTask;
import com.aidufei.protocol.gdhfc.GDHfcUrlParam;

public class GDHfcVOBResourceTask extends HttpTask {
	private static final String GET_VOB_URL = "/protocolAdapter-webapp/rest/getChannelResourceCode";
	private static final String URL_TYPE = "gd";

	private String mResourceCode = null;

	GDHfcUrlParam mParam = null;
	private String mRequest = null;
	private GDHfcDevice mRemote = null;
	private long mStart = 0;
	private long mEnd = 0;
	private int mOffset = 0;
	private int type = GDHfcUrlParam.NONE;

	public GDHfcVOBResourceTask(GDHfcDevice remote, GDHfcUrlParam param) {
		mParam = param;
		type = GDHfcUrlParam.NONE;
		mRemote = remote;

	}

	@Override
	protected String buildRequest() {
		// TODO Auto-generated method stub
		mRequest = null;
		if (mParam == null)
			return null;
		switch (mParam.type()) {
		case GDHfcUrlParam.VOB:
		case GDHfcUrlParam.VOB_SHIFT:
			mRequest = GET_VOB_URL + "?type=" + URL_TYPE + "&tv_VideoPID="
					+ mParam.videoPID() + "&tv_AudioPID=" + mParam.audioPID()
					+ "&tv_Modulation=" + mParam.modulation() + "&tv_freq="
					+ (int) mParam.frequency() + "&tv_ProgramNumber="
					+ mParam.programNumber() + "&tv_SymbolRate="
					+ mParam.symbolRate();
			break;
		default:
			return null;
		}
		mResourceCode = null;
		return mRequest;
	}

	@Override
	protected boolean parseResponse(String strResponse) {
		// TODO Auto-generated method stub
		JSONObject json = null;

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
				return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}

		try {
			mResourceCode = json.getString("resourceCode");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}

	@Override
	protected void updateProgress(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleResponse() {
		// TODO Auto-generated method stub
		if (mRemote != null && mRemote.adapter() != null) {
			if (mRemote.adapter() instanceof GDHfcDeviceAdapter) {
				GDHfcDeviceAdapter adapter = (GDHfcDeviceAdapter) mRemote
						.adapter();
				if (mParam.type() == GDHfcUrlParam.VOB) {
					adapter.onPlayVOB(mRemote, mResourceCode, 0);
				} else if (mParam.type() == GDHfcUrlParam.VOB_SHIFT) {
					adapter.onPlayVOB(mRemote, mResourceCode, mParam.start(),
							mParam.end(), mParam.offset());
				}
			}
		}
	}

	@Override
	protected void updateError(int error) {
		// TODO Auto-generated method stub

	}
}
