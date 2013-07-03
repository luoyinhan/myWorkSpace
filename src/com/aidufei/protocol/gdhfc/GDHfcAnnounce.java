package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;

public class GDHfcAnnounce extends GDHfcRequest{

	private int mData;
	
	public GDHfcAnnounce(){
		super(null,true);
		
		mCmd = GDHfcRequest.CMD_ANNOUNCE;
		mData = 0;	
	}
	
	public GDHfcAnnounce(String serial, int version) {
		super(serial,true);
		// TODO Auto-generated constructor stub
		mCmd = GDHfcRequest.CMD_ANNOUNCE;
		mData = version;
	}
	
	public GDHfcAnnounce(String serial, GDHfcAnnounce req,boolean success) {
		super(serial,false);
		// TODO Auto-generated constructor stub
		mCmd = GDHfcRequest.CMD_ANNOUNCE;
		
		if(req != null){
			mSync = req.mSync;
		}
		
		mData = (success?1:0);
	}

	public int version(){
		return mData;
	}
	
	public boolean success(){
		return (mData == 1? true:false);
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
