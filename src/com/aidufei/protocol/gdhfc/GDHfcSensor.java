package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;

import com.aidufei.protocol.common.AcceleratorSensor;
import com.aidufei.protocol.common.GSensor;

public class GDHfcSensor extends GDHfcRequest {

	int mStatus;
	GSensor mSensor = null;
	
	public GDHfcSensor(){
		super(null,true);
		
		mStatus = -1;
		mSensor = null;
	}
	
	public GDHfcSensor(String serial, GSensor sensor) {
		super(serial, true);
		// TODO Auto-generated constructor stub
		mSensor = sensor;
	}

	public GDHfcSensor(String serial, GDHfcSensor req, boolean success){
		super(serial, false);
		
		if(req != null){
			mSync = req.mSync;
		}
		
		mStatus = success?1:0;
	}
	
	
	@Override
	protected byte[] paramToBytes() {
		byte[] msg = null;
		if(isRequest()){
			if(mSensor != null){
				return mSensor.toByte();
			}
		}else{
			msg  = new byte[4];
			ByteBuffer msgbuf = ByteBuffer.allocate(4);
			if(msgbuf == null)
				return null;
			msgbuf.putInt(mStatus);		
			msgbuf.rewind();
			msgbuf.get(msg,0,4);
			return msg;
		}
		
		return null;
	}

	@Override
	protected boolean paramFromBytes(ByteBuffer buf) {
		// TODO Auto-generated method stub
		if(isRequest()){
			
			if(buf.capacity() - buf.position() < 4)
				return false;
			
			int type = buf.getInt();
			if(type == 1){
				mSensor = new GSensor();
			}
			if(mSensor.fromByteBuffer(buf) == false)
				return false;
			mStatus = -1;
			return true;
		}else{
			
			if(buf.capacity() - buf.position() < 4)
				return false;
			
			mStatus = buf.getInt();
			mSensor = null;
			return true;
		}
		
		
	}

}
