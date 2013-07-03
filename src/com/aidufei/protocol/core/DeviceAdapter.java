package com.aidufei.protocol.core;


public abstract class DeviceAdapter implements RemoteAction{
	
	protected ClientRequestListener mClientRequestListener = null;
	protected OnDeviceSearchListener mOnDeviceSearchListener = null;
	protected OnDeviceConnectListener mOnDeviceConnectListener = null;

	public DeviceAdapter(){
		
	}
	
	public void setOnDeviceSearchListener(OnDeviceSearchListener l){
		mOnDeviceSearchListener = l;
	}
	
	public void setRequestListener(ClientRequestListener l){
		mClientRequestListener = l;
	}
	
	public void setOnDeviceConnectListener(OnDeviceConnectListener l){
		mOnDeviceConnectListener = l;
	}
	
	public abstract void start();
	public abstract void stop();
	
	public abstract void search();
	public abstract boolean connect(Device remote);
	public abstract void disconnect(Device remote);
	protected abstract boolean send(Device remote, Object req);
	protected abstract void handle(Device remote, Object req);
	
	public abstract void onPlay(Device remote, Object req);
	public abstract void onPlayStatusSync(Device remote,Object req);
	public abstract void onGetVolume(Device remote,Object req);
	public abstract void onSetVolume(Device remote,Object req);
	public abstract void onPlaySync(Device remote,Object req);
	public abstract void onPlayControl(Device remote, Object req);
	public abstract void onGetPlayStatus(Device remote, Object req);
	
	public abstract void onKey(Device remote, Object req);
	public abstract void onMouse(Device remote, Object req);
	public abstract void onSenor(Device remote, Object req);
	public abstract void onTextInput(Device remote, Object req);
	
	public abstract void onMirrion(Device remote,Object req);
	
}
