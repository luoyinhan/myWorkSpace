package com.aidufei.protocol.core;

import java.util.ArrayList;

public class DeviceList implements OnDeviceSearchListener,OnDeviceConnectListener{
	
	private static DeviceList  gDeviceList = null;
	private ArrayList<Device> mDevices = new ArrayList<Device>();
	private OnDeviceChangedListener mListener = null;
	private int mCurrent = -1;
	private Device mCurrentDevice = null;
	
	private DeviceList(){
		
	}
	
	public static DeviceList create(){
		if(gDeviceList == null)
			gDeviceList = new DeviceList();
		return gDeviceList;
	}
	
	
	//add by zhangrp
	public void clear(){
		if(mDevices != null){
			mDevices.clear();
		}
	}
	
	public void setOnDeviceChangedListener(OnDeviceChangedListener l){
		mListener = l;
	}
	
	public int count(){
		if(mDevices == null)
			return -1;
		
		return mDevices.size();
	}
	
	public Device current(){
		return mCurrentDevice;
	}
	
	
	public Device get(int position){
		if(position < 0 || position >= mDevices.size())
			return null;
		return mDevices.get(position);
	}
	
	public  int location(Device remote){
		for(int i = 0; i < mDevices.size();i++){
			Device dev = mDevices.get(i);
			if(dev.address() != null && dev.address().equals(remote.address())){
				return i;
			}
		}
		return -1;
	}
	
	private void changeCurrentState(int state){
		if(mCurrentDevice != null && mCurrentDevice.state() != state){
			mCurrentDevice.setState(state);
			if(mListener != null){
				mListener.onDeviceChanged(this);
			}
		}
		if(mCurrentDevice != null && mCurrentDevice.state() == Device.STATE_IDLE){
			mCurrentDevice = null;
		}		
	}
	
	private int changeDeviceState(Device remote, int state){
		for(int i = 0; i < mDevices.size(); i++){
			Device dev = mDevices.get(i);
			if(dev != null && dev.address() != null && dev.address().equals(remote.address())){
				if(dev.state() != state){
					if(mListener != null){
						mListener.onDeviceChanged(this);
					}
				}
				dev.setState(state);
				return i;
			}
		}
		
		mDevices.add(remote);
		remote.setState(state);
		if(mListener != null){
			mListener.onDeviceChanged(this);
		}
		return mDevices.size() - 1;
	}
	
	public synchronized boolean connectDevice(Device remote){
		
		if(remote == null || remote.address() == null)
			return false;
		if(mCurrentDevice != null && remote.address().equals(mCurrentDevice.address()) &&
		   mCurrentDevice.state() >= Device.STATE_CONNECTING){
			return true;
		}
		if(mCurrentDevice!= null && mCurrentDevice.adapter() != null){
			mCurrentDevice.adapter().disconnect(mCurrentDevice);
			changeCurrentState(Device.STATE_IDLE);
		}
		
		int index = changeDeviceState(remote,Device.STATE_CONNECTING);
		if(index >= 0){
			mCurrentDevice = mDevices.get(index);
			if(mCurrentDevice!= null && mCurrentDevice.adapter() != null){
				mCurrentDevice.adapter().connect(mCurrentDevice);
			}	
		}
		return true;		
	}


	@Override
	public synchronized void onDeviceOnline(Device remote) {

		if(remote == null){
			return;
		}
		
		for(Device dev: mDevices){
			if(dev.address() != null && dev.address().equals(remote.address())){
//				if(mListener != null)
//					mListener.onDeviceChanged(this);
				return;
			}
		}
		mDevices.add(remote);
//		if(mListener != null)
//			mListener.onDeviceChanged(this);
	}

	@Override
	public synchronized void onSearchStart() {
		// TODO Auto-generated method stub
		if(mListener != null)
			mListener.onDeviceChanged(this);
		
	}

	@Override
	public synchronized void onSearchEnd() {
		// TODO Auto-generated method stub
		if(mListener != null)
			mListener.onDeviceChanged(this);
		
	}

	@Override
	public synchronized void onConnected(Device remote) {
		// TODO Auto-generated method stub
		if(remote == null || remote.address() == null)
			return;
		
		if(mCurrentDevice != null && mCurrentDevice.address() != null && 
		   mCurrentDevice.address().equals(remote.address())){
			changeCurrentState(Device.STATE_CONNECTED);
			return;
		}
		if(mCurrentDevice != null && mCurrentDevice.state() != Device.STATE_IDLE){
			if(mCurrentDevice.adapter() != null)
				mCurrentDevice.adapter().disconnect(mCurrentDevice);
			changeCurrentState(Device.STATE_IDLE);
		}
		
		int index = changeDeviceState(remote,Device.STATE_CONNECTED);
		if(index >= 0){
			mCurrentDevice = mDevices.get(index);
		}
		
	}

	@Override
	public synchronized void onConnectError(Device remote) {
		// TODO Auto-generated method stub
		if(remote == null || remote.address() == null)
			return;
		
		if(mCurrentDevice != null && mCurrentDevice.address() != null && 
		   mCurrentDevice.address().equals(remote.address())){
			changeCurrentState(Device.STATE_IDLE);
			return;
		}
				
		changeDeviceState(remote,Device.STATE_IDLE);

	}

	@Override
	public synchronized void onConnectDrop(Device remote) {
		// TODO Auto-generated method stub
		
		if(remote == null || remote.address() == null)
			return;
		if(mCurrentDevice != null && mCurrentDevice.address() != null && 
		   mCurrentDevice.address().equals(remote.address())){
		   changeCurrentState(Device.STATE_IDLE);
			return;
		}
						
		changeDeviceState(remote,Device.STATE_IDLE);
		
	}
	
	
	
}
