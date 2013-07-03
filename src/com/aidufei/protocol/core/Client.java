package com.aidufei.protocol.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.aidufei.protocol.adapter.gdhfc.GDHfcDeviceAdapter;
import com.aidufei.protocol.adapter.oto.OtODevice;
import com.aidufei.protocol.adapter.oto.OtODeviceAdapter;
import com.aidufei.protocol.adapter.saition.SaitionFlyDeviceAdapter;
import com.coship.ott.utils.LogUtils;

public class Client {
	private static Client gClient = null;
	
	private Stack mStack = null;
	
	private Client(){
		init();
	}
	
	private void init(){
		mStack = Stack.createStack();
		
		DeviceAdapter otOAdapter = OtODeviceAdapter.create();
		mStack.addAdapter(otOAdapter);
		
//		DeviceAdapter gdHfcAdapter = GDHfcDeviceAdapter.create();
//		GDHfcDevice device = new GDHfcDevice(TerminalSerial.localAddress(),TerminalSerial.localAddress(),Global.TERMINAL_APHONE);
//		((GDHfcDeviceAdapter)gdHfcAdapter).setLocalDevice(device);
//		mStack.addAdapter(gdHfcAdapter);
//		
//		
//		DeviceAdapter SaitionAdapter = SaitionFlyDeviceAdapter.create();
//		mStack.addAdapter(SaitionAdapter);
		
		mStack.start();
	}
	
	public void setLocalDevice(Device local){
	   if(local instanceof OtODevice){
		   OtODeviceAdapter otOAdapter = (OtODeviceAdapter)OtODeviceAdapter.create();
		   if(otOAdapter != null){
			   otOAdapter.setLocalDevice(local);
		   }
		   return;
	   }
	}
	
	public void setGDHfcUrlService(String address){
		GDHfcDeviceAdapter adapter = (GDHfcDeviceAdapter) GDHfcDeviceAdapter.create();
		if(adapter != null){
			adapter.setUrlSerivce(address);
		}
	}
	
	public void setSaitionEPGService(String address){
		SaitionFlyDeviceAdapter adapter = (SaitionFlyDeviceAdapter)SaitionFlyDeviceAdapter.create();
		if(adapter != null){
			adapter.setEPGServerAddress(address);
		}
	}
	
	public void setClientRequestListener(ClientRequestListener l){
		mStack.setClientRequestListener(l);
	}
	
	public DeviceList devices(){
		if(mStack == null)
			return null;
		return mStack.devices();
	}
	
	public Device current(){
		if(mStack == null)
			return null;
		return mStack.devices().current();
	}
	
	public void searchDevice(){
		if(mStack != null && mStack.devices()!= null){
			mStack.devices().clear();
		}
		mStack.search();
	}
	
	public boolean connect(Device remote){
		if(mStack != null)
			return mStack.connect(remote);
		return false;
	}
	
	public static Client create(){
		if(gClient == null)
			gClient = new Client();
		return gClient;
	}
	
	
	
	public boolean autoConnect(Context mContext){
		
		SharedPreferences settings = mContext.getSharedPreferences("device_uuid", 0);
		String device_uuid = settings.getString("uuid",
				null);
		searchDevice();
		DeviceList deviceList = devices();
		
		if(deviceList == null || deviceList.count() <= 0){
			return false;
		}
		LogUtils.trace(Log.DEBUG, "Test", "device Count:"+deviceList.count());
		if(!TextUtils.isEmpty(device_uuid)){
			for (int i = 0; i < deviceList.count(); i++) {
				Device remote = deviceList.get(i);
				if(remote != null && !TextUtils.isEmpty(remote.serial())){
					if(device_uuid.equals(remote.serial())){
						if(remote == null || remote.address() == null)
							return false;
						if(remote.state() != Device.STATE_IDLE)
							return false;
						return connect(remote);
					}
				}
			}	
		} 
		
		final Device remote = deviceList.get(0);
		if(remote == null || remote.address() == null)
			return false;
		if(remote.state() != Device.STATE_IDLE)
			return false;
		return connect(remote);
	}
}
