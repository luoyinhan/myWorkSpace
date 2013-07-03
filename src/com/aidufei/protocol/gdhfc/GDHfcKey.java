package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;

public class GDHfcKey extends GDHfcRequest {

	private int mData;
	
	public GDHfcKey(){
		super(null,false);
		
		mCmd = GDHfcRequest.CMD_KEY;
		mData = 0;
	}
	
	public GDHfcKey(String serial, int key) {
		super(serial,true);
		// TODO Auto-generated constructor stub
		mCmd = GDHfcRequest.CMD_KEY;
		mData = key;
	}
	
	public GDHfcKey(String serial, GDHfcKey req, boolean success) {
		super(serial,false);
		// TODO Auto-generated constructor stub
		mCmd = GDHfcRequest.CMD_KEY;
		
		if(req != null){
			mSync = req.mSync;
		}
		
		mData = success? 1: 0;
	}
	
	public int key(){
		return mData;
	}
	
	public boolean success(){
		return mData == 1? true:false;
	}

	@Override
	protected byte[] paramToBytes() {
		// TODO Auto-generated method stub
		byte[] msg = new byte[4];
				
		ByteBuffer msgbuf = ByteBuffer.allocate(4);
		if(msgbuf == null)
			return null;
		msgbuf.putInt(mData);
				
		msgbuf.rewind();
		msgbuf.get(msg,0,4);
		return msg;
	}

	@Override
	protected boolean paramFromBytes(ByteBuffer buf) {
		// TODO Auto-generated method stub
		if(buf.capacity() - buf.position() < 4)
			return false;
		mData = buf.getInt();
		return true;
	}

}
