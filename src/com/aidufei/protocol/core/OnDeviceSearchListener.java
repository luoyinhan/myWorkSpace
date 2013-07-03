package com.aidufei.protocol.core;


public interface OnDeviceSearchListener {
	
	public void onSearchStart();
	public void onDeviceOnline(Device remote);
	public void onSearchEnd();
}
