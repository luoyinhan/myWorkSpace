package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;

import com.aidufei.protocol.common.Mouse;

public class GDHfcMouse extends GDHfcRequest {

	private int mStatus;
	private Mouse mMouse;
	
	public GDHfcMouse(){
		super(null,true);
		
		mMouse = null;
		mStatus = 0;
	}
	
	
	public GDHfcMouse(String serial, Mouse mouse) {
		super(serial, true);
		// TODO Auto-generated constructor stub
		mMouse = mouse;
	}

	public GDHfcMouse(String serial, GDHfcMouse req, boolean success){
		super(serial,false);
		
		if(req != null && req.isRequest()){
			mSync = req.mSync;
		}
		
		mStatus = success?1:0;
	}
	
	public Mouse mouse(){
		return mMouse;
	}
	
	public boolean success(){
		return mStatus == 1? true: false;
	}
	
	@Override
	protected byte[] paramToBytes() {
		byte[] msg = null;
		if(isRequest()){
			if(mMouse != null){
				return mMouse.toByte();
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
			mMouse = new Mouse();
			if(mMouse.fromByteBuffer(buf) == false)
				return false;
		}else{
			mMouse = null;
			if(buf.capacity() - buf.position() < 4)
				return false;
			mStatus = buf.getInt();			
		}
		return true;
	}

}
