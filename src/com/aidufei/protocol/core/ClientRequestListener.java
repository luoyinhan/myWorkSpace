package com.aidufei.protocol.core;

import com.aidufei.protocol.oto.ResourceInfo;
import com.aidufei.protocol.oto.Status;

public interface ClientRequestListener {
	
	//public void onAnnounce(String userName, String userCode, int type, String ip);
	
	public void onPlay(Device remote,int retCode,String desc, ResourceInfo info);
	public void onGetStatus(Device remote,int retCode,String desc, ResourceInfo info);
	public void onPull(Device remote,int retCode,String desc, ResourceInfo info);	
	public void onGetVolume(Device remote,int retCode,String desc,int volume);
	public void onGetPlayStatus(Device remote,int retCode,String desc,int playStatus);
	public void onSetVolume(Device remote,int retCode,String desc,int volume);
	public void onPlayControl(Device remote,int retCode,String desc);
	public void onMirrion(Device remote, boolean started,int retCode, String desc);
	
	public void onKey(Device remote, int retCode, String desc);
	public void onTextInput(Device remote, int retCode, String desc);
	public void onSensor(Device remote, int retCode, String desc);
	
}
