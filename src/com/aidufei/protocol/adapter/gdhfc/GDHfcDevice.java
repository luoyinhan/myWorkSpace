package com.aidufei.protocol.adapter.gdhfc;

import com.aidufei.protocol.adapter.oto.OtODeviceAdapter;
import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.DeviceAdapter;

public class GDHfcDevice extends Device{

	public GDHfcDevice(String serail,String address,int type){
		super(address,serail,type,serail,address);
		
	}
	@Override
	protected DeviceAdapter createAdapter() {
		// TODO Auto-generated method stub
		return GDHfcDeviceAdapter.create();
	}

}
