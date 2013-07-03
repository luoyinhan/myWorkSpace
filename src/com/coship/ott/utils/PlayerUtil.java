package com.coship.ott.utils;

import android.content.Context;
import android.content.Intent;

import com.unitend.udrm.ui.DRMControl;

public class PlayerUtil {

	public static void playVod(Context mContext, String resourceCode,
			int videoType, String assetName, String posterUrl, String assetID,
			String providerID) {
		Intent intent = new Intent();
		intent.putExtra("resourceCode", resourceCode);
		intent.putExtra("playType", 1);
		intent.putExtra("videoType", videoType);
		intent.putExtra("assetName", assetName);
		intent.putExtra("posterUrl", posterUrl);
		intent.putExtra("assetID", assetID);
		intent.putExtra("providerID", providerID);
		play(mContext, intent);
	}

	public static void playLive(Context mContext, String resourceCode,
			int videoType, String assetName, String posterUrl) {
		Intent intent = new Intent();
		intent.putExtra("resourceCode", resourceCode);
		intent.putExtra("playType", 2);
		intent.putExtra("videoType", videoType);
		intent.putExtra("assetName", assetName);
		intent.putExtra("posterUrl", posterUrl);
		play(mContext, intent);
	}

	public static void playShift(Context mContext, String resourceCode,
			long shifttime, long shiftend, int videoType, String assetName,
			String posterUrl, String programID) {
		Intent intent = new Intent();
		intent.putExtra("resourceCode", resourceCode);
		intent.putExtra("playType", 3);
		intent.putExtra("shifttime", shifttime);
		intent.putExtra("shiftend", shiftend);
		intent.putExtra("videoType", videoType);
		intent.putExtra("assetName", assetName);
		intent.putExtra("posterUrl", posterUrl);
		intent.putExtra("programID", programID);// 添加一个节目Id，便于分享是查询
		play(mContext, intent);
	}

	public static void playDelay(Context mContext, String resourceCode,
			long delay, int videoType, String assetName, String posterUrl) {
		Intent intent = new Intent();
		intent.putExtra("resourceCode", resourceCode);
		intent.putExtra("playType", 4);
		intent.putExtra("delay", delay);
		intent.putExtra("videoType", videoType);
		intent.putExtra("assetName", assetName);
		intent.putExtra("posterUrl", posterUrl);
		play(mContext, intent);
	}

	private static void play(Context mContext, Intent intent) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		// DRM绑定
		new DRMControl(mContext, session, intent);
	}

}
