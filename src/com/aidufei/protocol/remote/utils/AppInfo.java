package com.aidufei.protocol.remote.utils;

import android.util.Log;

public class AppInfo {
	private String packageName = "";
	private String appName = "";
	private int index = 0;
	private byte[] icon = null;

	private final String TAG = "Push";

	public void setPackageName(String name) {
		this.packageName = name;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setAppName(String name) {
		this.appName = name;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setPackageIcon(byte[] icon) {
		this.icon = new byte[icon.length];
		this.icon = icon;
	}

	public byte[] getPackageIcon() {
		return this.icon;
	}

	public void setPackageIndex(int index) {
		this.index = index;
	}

	public int getPackageIndex() {
		return this.index;
	}

	public void print() {
		Log.e("Push", "ChannelName=" + this.packageName);
	}
}
