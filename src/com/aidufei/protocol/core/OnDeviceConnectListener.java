package com.aidufei.protocol.core;



public interface OnDeviceConnectListener {
	void onConnected(Device remote);
	void onConnectError(Device remote);
	void onConnectDrop(Device remote);
}
