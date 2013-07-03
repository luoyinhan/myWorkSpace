package com.aidufei.protocol.core;

import java.util.ArrayList;

public class Stack {

	private static Stack mStack = null;
	private ArrayList<DeviceAdapter> mDeviceAdapters = new ArrayList<DeviceAdapter>();
	private DeviceList mDevices = DeviceList.create();

	private Stack() {
	}

	public void addAdapter(DeviceAdapter adapter) {
		if (mDeviceAdapters == null)
			return;
		adapter.setOnDeviceSearchListener(mDevices);
		adapter.setOnDeviceConnectListener(mDevices);
		mDeviceAdapters.add(adapter);
	}

	public static Stack createStack() {
		if (mStack == null)
			mStack = new Stack();
		return mStack;
	}

	public DeviceList devices() {
		return mDevices;
	}

	public void search() {
		for (DeviceAdapter adapter : mDeviceAdapters) {
			adapter.search();
		}
	}

	public void setClientRequestListener(ClientRequestListener l) {
		for (DeviceAdapter adapter : mDeviceAdapters) {
			adapter.setRequestListener(l);
		}
	}

	public void start() {
		for (DeviceAdapter adapter : mDeviceAdapters) {
			adapter.start();
		}
	}

	public void stop() {
		for (DeviceAdapter adapter : mDeviceAdapters) {
			adapter.stop();
		}
	}

	public boolean connect(Device remote) {
		if (mDevices != null)
			return mDevices.connectDevice(remote);
		return false;
	}

}
