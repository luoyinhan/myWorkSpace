package com.aidufei.protocol.remote.message;

import android.util.Log;

public class ChannelInfor {
	private String channleName = null;
	private int index = 0;
	private String playUrl = null;

	private final String TAG = "FileInfo";

	public void setChannelName(String name) {
		this.channleName = name;
	}

	public String getChannelName() {
		return this.channleName;
	}

	public void setPlayUrl(String url) {
		this.playUrl = url;
	}

	public String getPlayUrl() {
		return this.playUrl;
	}

	public void setChannelIndex(int index) {
		this.index = index;
	}

	public int getChannelID() {
		return this.index;
	}

	public void print() {
		Log.v("FileInfo", "ChannelName=" + this.channleName);
		Log.v("FileInfo", "index=" + this.index);
		Log.v("FileInfo", "playUrl=" + this.playUrl);
	}
}
