package com.aidufei.protocol.remote.handle;

import android.util.Log;
import com.aidufei.protocol.remote.callback.GetEpgListCallBack;
import com.aidufei.protocol.remote.message.ChannelSwitchMsg;
import com.aidufei.protocol.remote.message.EPGMsg;
import com.aidufei.protocol.remote.message.EpgSaxXml;
import com.aidufei.protocol.remote.message.MsgHead;
import com.aidufei.protocol.remote.utils.SocketUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MediaTransPlayer {
	private static final String TAG = "RemoteTransPlayer";
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private Socket socket = null;
	private dtvClientThread client = null;
	private int PORT = 8824;
	private MsgHead msgHead = new MsgHead();
	private EPGMsg Epgmsg = new EPGMsg();
	private ChannelSwitchMsg SwitchChMsg = new ChannelSwitchMsg();

	GetEpgListCallBack getEpgCallback = null;

	public MediaTransPlayer() {
		if (this.client != null)
			return;
		try {
			this.socket = new Socket(SocketUtils.socketIP, this.PORT);
			this.in = new DataInputStream(this.socket.getInputStream());
			this.out = new DataOutputStream(this.socket.getOutputStream());
			this.client = new dtvClientThread();
			this.client.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			destory();
		} catch (IOException e) {
			e.printStackTrace();
			destory();
		}
	}

	public void sendEpgReq(GetEpgListCallBack callback) {
		this.getEpgCallback = callback;
		EPGMsg msg = new EPGMsg();
		msg.setMsgType(257);
		msg.setBodyLenth(0);
		try {
			msg.sendOutputMsg(this.out);
			msg.print();
		} catch (Exception e) {
			e.printStackTrace();
			destory();
		}
	}

	public String getMediaDtvUrl() {
		String url = "";
		url = "rtsp://" + SocketUtils.socketIP + ":4554/iptv&chn18" + ".sdp";

		return url;
	}

	public void sendSwitchProgram(int ID, String name) {
		try {
			ChannelSwitchMsg SwitchMsg = new ChannelSwitchMsg(ID, name);
			SwitchMsg.setMsgType(259);
			SwitchMsg.sendOutputMsg(this.out);
			Log.e("SocketClient", "sendSwitchComman");
			SwitchMsg.print();
		} catch (Exception e) {
			e.printStackTrace();
			destory();
		}
	}

	public void destory() {
		try {
			if (this.in != null) {
				this.in.close();
				this.in = null;
			}
			if (this.out != null) {
				this.out.close();
				this.out = null;
			}
			if (this.socket != null) {
				this.socket.close();
				this.socket = null;
			}
			if (this.client != null) {
				this.client.interrupt();
				this.client = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class dtvClientThread extends Thread {
		byte[] msgBody;

		dtvClientThread() {
			this.msgBody = new byte[1024];
		}

		public void run() {
			/*
			 * try { label0: if (MediaTransPlayer.this.in != null);
			 * MediaTransPlayer
			 * .this.msgHead.setMsgType(MediaTransPlayer.this.in.readInt());
			 * switch (MediaTransPlayer.this.msgHead.getMsgType()) { case 258:
			 * Log.e("SocketClient", "msgtype: " +
			 * MediaTransPlayer.this.msgHead.getMsgType());
			 * MediaTransPlayer.this
			 * .Epgmsg.setMsgType(MediaTransPlayer.this.msgHead.getMsgType());
			 * MediaTransPlayer
			 * .this.Epgmsg.readInputMsg(MediaTransPlayer.this.in);
			 * MediaTransPlayer.this.Epgmsg.print(); if
			 * (MediaTransPlayer.this.Epgmsg.getBodyLenth() > 0); ArrayList
			 * channelInfoList = new ArrayList(); channelInfoList =
			 * EpgSaxXml.parse(MediaTransPlayer.this.Epgmsg.getMsgStringBody());
			 * if (MediaTransPlayer.this.getEpgCallback != null) {
			 * MediaTransPlayer
			 * .this.getEpgCallback.returnEpgList(channelInfoList); }
			 * 
			 * break; case 260: Log.e("SocketClient", "SWITCH_CHANNEL_RET");
			 * MediaTransPlayer
			 * .this.SwitchChMsg.setMsgType(MediaTransPlayer.this
			 * .msgHead.getMsgType());
			 * MediaTransPlayer.this.SwitchChMsg.readInputMsg
			 * (MediaTransPlayer.this.in);
			 * MediaTransPlayer.this.SwitchChMsg.print();
			 * 
			 * break label0: }
			 * 
			 * } catch (Exception e) { e.printStackTrace();
			 * MediaTransPlayer.this.destory(); }
			 */
		}
	}
}
