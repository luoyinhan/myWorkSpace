package com.aidufei.protocol.adapter.saition;

import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.DeviceAdapter;
import com.aidufei.protocol.oto.Global;
import com.coship.saition.facade.device.SaitionDevice;

public class SaitionFlyDevice extends Device{

	public SaitionFlyDevice(){
		super(null,null,Global.TERMINAL_SAITION_BOX,null,null);
	}
	public SaitionFlyDevice(SaitionDevice device){
		super(device.getIp(),device.getPort() + "",Global.TERMINAL_SAITION_BOX,device.getMac(),device.getIp());		
	}
	
	public SaitionFlyDevice(String ip, int port, String serial){
		super(ip,port+"",Global.TERMINAL_SAITION_BOX,serial,ip);
	}
	
	@Override
	protected DeviceAdapter createAdapter() {
		// TODO Auto-generated method stub
		return SaitionFlyDeviceAdapter.create();
	}

}
