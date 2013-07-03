package com.aidufei.protocol.remote.handle;

import android.util.Log;
import com.aidufei.protocol.remote.callback.KeepAliveCallBack;
import com.aidufei.protocol.remote.message.AcessDeviceStatusRequest;
import com.aidufei.protocol.remote.message.MulityBroadcast;
import com.aidufei.protocol.remote.utils.SocketUtils;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RemoteControlCenter {
	private boolean lastConnected = false;
	private DeviceInfo device = new DeviceInfo();
	private RemoteKeyboard keyboard = null;
	private RemoteTouch touch = null;
	private RemoteMouse mouse = null;
	private RemoteSensor sensor = null;
	private RemoteAppList appCtrl = null;
	private KeepAliveCallBack keepAlive = null;

	public static final int REQ_APP_LIST = 769;
	public static final int RES_APP_LIST = 770;
	public static final int LAUNCH_APP_REQ = 771;
	public static final int SEND_WEB_URL = 772;
	public static final int GET_MEIDA_INFO = 773;
	public static final int RES_MEIDA_INFO = 774;
	public static final int SEND_KEEP_ALIVE = 775;
	public static final int RES_KEEP_ALIVE = 776;
	private static String TAG = "ControlCenter";
	private static Timer timer = null;
	private static CheckNetworkTask task = null;
	private boolean isFrist = true;

	public static ArrayList<DeviceInfo> remoteDetectIPList() {
		return new MulityBroadcast().sendBroadCast();
	}

	public RemoteControlCenter(DeviceInfo device) {
		this.device = device;
		SocketUtils.socketIP = device.getDeviceIP();

	}

	public RemoteKeyboard getRemoteKeyboard() {
		if (this.keyboard == null) {
			this.keyboard = new RemoteKeyboard(this.device);
		}

		return this.keyboard;
	}

	public RemoteTouch getRemoteTouch() {
		if (this.touch == null) {
			this.touch = new RemoteTouch(this.device);
		}

		return this.touch;
	}

	public RemoteMouse getRemoteMouse() {
		if (this.mouse == null) {
			this.mouse = new RemoteMouse(this.device);
		}

		return this.mouse;
	}

	public RemoteSensor getRemoteSensor() {
		if (this.sensor == null) {
			this.sensor = new RemoteSensor(this.device);
		}

		return this.sensor;
	}

	public RemoteAppList getRemoteAppControl() {
		if (this.appCtrl == null) {
			this.appCtrl = new RemoteAppList(this.device);
		}

		return this.appCtrl;
	}


	public void remoteConnectToHost()
			throws RemoteControlCenter.RemoteConnectException {
		SocketUtils.socketIP = this.device.getDeviceIP();
		try {
			SocketUtils.startNetwork();
			this.lastConnected = true;
			sendAccessDeviceStatus(AcessDeviceStatusRequest.ACESSDEVICE_JOIN_NETWORK);
		} catch (Exception e) {
			e.printStackTrace();
			RemoteConnectException exc = new RemoteConnectException(
					"connect error");
			throw exc;
		}
	}

	public String getLocalHostIPaddress() {
		return SocketUtils.hostip;
	}

	public void remoteKeepAliveToHost(KeepAliveCallBack callback) {
		this.keepAlive = callback;
		if ((timer == null) && (task == null)) {
			task = new CheckNetworkTask();
			timer = new Timer();
			timer.schedule(task, 10000L, 10000L);
		}
	}

	public void destroy() {
		if (task != null) {
			task.cancel();
			task = null;
		}

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		SocketUtils.closeNetwork();
		if (this.touch != null) {
			this.touch.destory();
		}

		if (this.mouse != null) {
			this.mouse.destory();
		}

		if (this.keyboard != null) {
			this.keyboard.destroy();
		}

		if (this.sensor != null) {
			this.sensor.destroy();
		}



		if (this.appCtrl != null) {
			this.appCtrl.destory();
		}


	}

	public void sendAccessDeviceStatus(short message) {
		AcessDeviceStatusRequest asr = new AcessDeviceStatusRequest();
		try {
			byte[] selfIp = null;
			if (selfIp == null) {
				selfIp = InetAddress.getLocalHost().getHostAddress().getBytes();
			}
			asr.setDeviceIP(selfIp);
			asr.setDeviceStatus(message);
			asr.send();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	protected void DestroyConnection() {
		sendAccessDeviceStatus(AcessDeviceStatusRequest.ACESSDEVICE_LEAVE_NETWORK);
		SocketUtils.closeNetwork();
		SocketUtils.socketIP = "";
	}

	class CheckNetworkTask extends TimerTask {
		CheckNetworkTask() {
		}

		public void run() {
			try {
				RemoteControlCenter.this.sendAccessDeviceStatus(AcessDeviceStatusRequest.ACESSDEVICE_LIVE_PACKAGE);
		} catch (Exception e) {
				e.printStackTrace();
				if (RemoteControlCenter.this.keepAlive != null) {
					RemoteControlCenter.this.keepAlive
							.returnConectResult(false);
				}
				RemoteControlCenter.this.timer.cancel();
				RemoteControlCenter.this.timer = null;
			}
		}
	}


	public class RemoteConnectException extends Exception {
		private String description;

		public RemoteConnectException(String description) {
			this.description = description;
		}

		public String getExceptionDescription() {
			return this.description;
		}
	}
}