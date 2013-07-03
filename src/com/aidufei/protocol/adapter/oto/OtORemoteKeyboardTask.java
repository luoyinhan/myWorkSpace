package com.aidufei.protocol.adapter.oto;

import com.aidufei.remote.KeyInfo;
import com.aidufei.remote.RemoteKeyboard;
import com.aidufei.remote.SocketUtils;

import android.os.AsyncTask;

public class OtORemoteKeyboardTask extends AsyncTask<Integer,Integer,Integer>{

	
	private RemoteKeyboard mKeyboard = null;
	private String mRemoteAddress = null;
	public OtORemoteKeyboardTask(RemoteKeyboard keyboard,String remoteIP){
		mKeyboard = keyboard;
		mRemoteAddress = remoteIP;
	}
	
	@Override
	protected Integer doInBackground(Integer... params) {
		
		if(mKeyboard == null || params == null){
			return 0;
		}
		if(SocketUtils.socketIP == null || !SocketUtils.socketIP.equals(mRemoteAddress)){
			SocketUtils.closeNetwork();
			SocketUtils.socketIP = mRemoteAddress;
			try {
				SocketUtils.startNetwork();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		mKeyboard.setRemote(mRemoteAddress);		
		mKeyboard.remoteSendDownOrUpKeyCode(params[0],KeyInfo.KEY_EVENT_DOWN);
		mKeyboard.remoteSendDownOrUpKeyCode(params[0],KeyInfo.KEY_EVENT_UP);
		
		return 1;
	}
	

}
