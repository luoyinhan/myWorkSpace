package com.aidufei.protocol.gdhfc;

import java.nio.ByteBuffer;

public class GDHfcTextInput extends GDHfcRequest {

	private int mStatus = 0;
	private byte[] mText = null;
	
	public GDHfcTextInput(){
		super(null,true);
		mText = null;
		mStatus = -1;
	}
	
	public GDHfcTextInput(String serial, byte[] text) {
		super(serial, true);
		// TODO Auto-generated constructor stub
		mCmd = GDHfcRequest.CMD_TEXT_INPUT;
		mText = text;
	}
	
	public GDHfcTextInput(String serial, GDHfcTextInput req, boolean success){
		super(serial,false);
		
		if(req != null){
			mSync = req.mSync;
		}
		
		mStatus = success? 1: 0;
		mText = null;
	}

	public byte[] text(){
		return mText;
	}
	
	public boolean success(){
		return mStatus == 1? true: false;
	}
	
	@Override
	protected byte[] paramToBytes() {
		// TODO Auto-generated method stub
		byte[] msg = null;
		if(isRequest()){
			return mText;
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
		
	
		
		
		
		
	}

	@Override
	protected boolean paramFromBytes(ByteBuffer buf) {
		// TODO Auto-generated method stub
		if(isRequest()){
			mText = null;
			if(buf.capacity() - buf.position() <= 1)
				return true;
			byte[] text = new byte[buf.capacity() - buf.position()];
			if(text == null)
				return false;
			buf.get(text,0,text.length - 1);
			mText = text;
			return true;
		}else{
			if(buf.capacity() - buf.position() < 4){
				return false;
			}
			
			mStatus = buf.getInt();
			return true;
		}
	}
	
	

}
