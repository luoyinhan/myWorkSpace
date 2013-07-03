package com.coship.ott.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coship.ott.utils.LogUtils;

public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompleteReceiver";
	public static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			LogUtils.trace(Log.DEBUG, TAG, "system boot complete!");
			Intent service = new Intent(context, BookNotifyService.class);
			context.startService(service);
		}
	}
}