package com.aidufei.protocol.adapter.oto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.OnDeviceConnectListener;
import com.coship.ott.utils.LogUtils;

import android.util.Log;


public class OtOHeartBeat  {
	
	private final static String TAG="OtOHeartBeat";	
	private Socket mSocket;
	private DataOutputStream mOutStream = null;
			
	private final static int BEAT_PORT = 9005;
	private Device mRemote = null;
	private Device mConRemote = null;
	private Timer mTimer = null;
	private BeatTask mTask = null;
	private OnDeviceConnectListener mListener = null;
	private int mRetry = 0;
//	private boolean mNeedReconnect = false;
	

	
	public OtOHeartBeat(){
		mRemote = null;
		mTask = null;
	}
	
	public OtOHeartBeat(Device remote){
		mRemote = remote;
		mTask = null;
		start();
	}
	
	
	private boolean connectRemote(){
		InetSocketAddress socketAddress = new InetSocketAddress(mRemote.address(), BEAT_PORT);
		try {
			LogUtils.trace(Log.VERBOSE, TAG, "begin connect to beat port");
			mSocket.connect(socketAddress, 5000);			
		} catch (IOException e) {
			return false;
		}
		
		try {
			mOutStream = new DataOutputStream(mSocket.getOutputStream());
		} catch (IOException e) {				
			return false;
		}	
		mConRemote = mRemote;		
		return true;
	}
	
	private  boolean createSocket(){
		
		boolean retval = true;
		
		
		if(mSocket != null)
			return true;
		
		if(mRemote == null)
			return false;
//		mRemote.setState(Device.STATE_CONNECTING);
		mSocket = new Socket();			
		while( (retval=connectRemote()) == false){
			if(mRetry++ < 3)
				continue;
		}
		
		if(retval == false){
//			mRemote.setState(Device.STATE_IDLE);		
			closeSocket();
		}	
		mRetry = 0;
		return retval;
	}
		

	
	private  void  closeSocket(){
		if(mSocket != null){
			try{
				mSocket.close();
			}catch (IOException e1) {
			}
			mSocket = null;
			mOutStream = null;	
			mConRemote = null;		
		}
	}
	
	
	private void createTimer(){
		if(mTimer == null){
			LogUtils.trace(Log.VERBOSE, TAG,"client beat timer create");
			mTimer = new Timer();
			if(mTask == null)
				mTask = new BeatTask();
			if(mTimer != null && mTask != null){
				mTimer.schedule(mTask, 100, 30000);
			}
		}	
	}
	
	private void closeTimer(){
		if(mTimer != null){
			LogUtils.trace(Log.VERBOSE, TAG,"client beat timer cancel");
			mTimer.cancel();			
			mTimer.purge();			
			mTimer = null;
		}
		if(mTask != null){
			mTask.cancel();
			mTask = null;
		}
	}
	
	
	
	public void addListener(OnDeviceConnectListener l){
		mListener = l;
	}
	
	public synchronized void start(){
		if(mRemote == null || mRemote.address() == null || mRemote.address().length() <= 0)
			return;
		
		createTimer();
	}
	
	
	public synchronized void stop() {
		closeTimer();			
		beatDrop();	
		LogUtils.trace(Log.VERBOSE, "ClientAdapter","client beat stop");
	}
				
	public synchronized void start(Device remote){
		mRemote = remote;
			
		if(mRemote == null){
			stop();		
			return;
		}
		
		if(mConRemote != null){
			if(mConRemote.address().equals(mRemote.address()))
				return;	
			beatDrop();
		}	
		closeTimer();
		createTimer();
	}
	
	
	private synchronized void beat(){
		
		if(createSocket() == false){
			beatError();
			closeTimer();
		}
		
		if(mOutStream == null)
			return;
		
		String beatmsg = "heartbeat";
		byte[] msg = beatmsg.getBytes();
		
		try {
			mOutStream.write(msg);
			beatOK();
		} catch (IOException e) {
			beatDrop();
			closeTimer();
		}	
	}
	
	private void beatDrop(){
	
		if(mListener != null && mConRemote != null){
//			mConRemote.setState(Device.STATE_IDLE);
			mListener.onConnectDrop(mConRemote);
		}
		LogUtils.trace(Log.VERBOSE, TAG, "11111111111beat drop from " + mConRemote.address());
		closeSocket();
	}
	
	private void beatError(){
		if(mListener != null && mConRemote != null){
//			mConRemote.setState(Device.STATE_IDLE);
			mListener.onConnectError(mConRemote);	
		}
		LogUtils.trace(Log.VERBOSE, TAG,"1111111111beat error from " + mConRemote.address());
		closeSocket();
	}
	
	private void beatOK(){
		if(mListener != null && mConRemote != null){
//			mConRemote.setState(Device.STATE_CONNECTED);
			mListener.onConnected(mConRemote);
		}
		LogUtils.trace(Log.VERBOSE, TAG,"beat ok from " + mConRemote.address());
	}
	
	
	
	
	class BeatTask extends TimerTask {
		@Override
		public void run() {		
			beat();		
		}
		
	}
	
}
