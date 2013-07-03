package com.aidufei.protocol.common;

import java.nio.ByteBuffer;

public class AcceleratorSensor {
	private float mX;
	private float mY;
	private float mZ;
	
	public AcceleratorSensor(){
		mX = mY = mZ = -1;
	}
	
	public AcceleratorSensor(float x, float y, float z ){
		mX = x;
		mY = y;
		mZ = z;
	}
	
	public float x(){
		return mX;
	}
	
	public float y(){
		return mY;
	}
	
	public float z(){
		return mZ;
	}
	
	public byte[] toByte(){
		
		byte[] msg = new byte[16];
		
		ByteBuffer msgbuf = ByteBuffer.allocate(16);
		if(msgbuf == null)
			return null;
		msgbuf.putInt(1);
		msgbuf.putFloat(mX);
		msgbuf.putFloat(mY);
		msgbuf.putFloat(mZ);
		msgbuf.rewind();
		msgbuf.get(msg,0,16);
		return msg;
	}
	
	public boolean fromByteBuffer(ByteBuffer msg){
		if(msg.capacity() - msg.position() < 12)
			return false;
		mX = msg.getFloat();
		mY = msg.getFloat();
		mZ = msg.getFloat();
		return true;
	}
}
