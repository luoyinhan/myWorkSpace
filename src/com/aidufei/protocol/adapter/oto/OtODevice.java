package com.aidufei.protocol.adapter.oto;

import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.DeviceAdapter;

public class OtODevice extends Device{

	
	public OtODevice(String name, String serial, int type, String uuid, String address){
		super(name,serial,type,uuid,address);
		
	}
	@Override
	protected DeviceAdapter createAdapter() {
		// TODO Auto-generated method stub
		return OtODeviceAdapter.create();
	}

}
