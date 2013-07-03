package com.aidufei.protocol.adapter.gdhfc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.util.Log;

import com.aidufei.protocol.common.GSensor;
import com.aidufei.protocol.common.Mouse;
import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.DeviceAdapter;
import com.aidufei.protocol.gdhfc.GDHfcAnnounce;
import com.aidufei.protocol.gdhfc.GDHfcApp;
import com.aidufei.protocol.gdhfc.GDHfcKey;
import com.aidufei.protocol.gdhfc.GDHfcMouse;
import com.aidufei.protocol.gdhfc.GDHfcRequest;
import com.aidufei.protocol.gdhfc.GDHfcSensor;
import com.aidufei.protocol.gdhfc.GDHfcTextInput;
import com.aidufei.protocol.gdhfc.GDHfcURL;
import com.aidufei.protocol.gdhfc.GDHfcUrlParam;
import com.aidufei.protocol.oto.Global;
import com.aidufei.protocol.oto.ResourceInfo;
import com.coship.ott.utils.LogUtils;

public class GDHfcDeviceAdapter extends DeviceAdapter {

	private final static String BROADCAST_IP_ADDRESS = "255.255.255.255";
	private final static int BROADCAST_PORT = 6666;

	private static GDHfcDeviceAdapter gAdapter = null;

	private String mConvertServAddress = null; // "192.168.88.200:8088";
												// //"183.62.141.34";

	private String mVobAppUrl = null; // "http://192.168.88.106/play/index.html";
	private String mVodAppUrl = null; // "http://192.168.88.106/play/index.html";
	private String mImageAppUrl = null; // "http://192.168.88.106/play/index.html";

	private static final int SEARCH_TIME = 3000;
	private Timer mTimer = null;
	private DatagramSocket mSocket = null;
	private InetAddress mMulticastGroup = null;
	private SearchDeviceTask mSearch = null;
	private GDHfcDevice mLocalDevice = null;

	private GDHfcRequestHandler mHandler = null;
	private Thread mRequestHandlerThread = null;

	private boolean mDeviceSearched = false;

	// private boolean mPlayState = false;
	// private boolean mPullState = false;
	//
	// private static final int MSG_PUSH_PLAY = 1;
	// private static final int MSG_PULL_PLAY = 2;
	//
	// private Handler mConvertHandler = new Handler(){
	// public void handleMessage(Message msg) {
	// switch(msg.what){
	// case MSG_PUSH_PLAY:
	// if(mPlayState == false){
	// return;
	// }
	//
	// }
	// }
	// };

	private GDHfcDeviceAdapter() {
		mHandler = new GDHfcRequestHandler(this);
		mRequestHandlerThread = new Thread(mHandler);

	}

	public void setLocalDevice(GDHfcDevice local) {
		mLocalDevice = local;
	}

	public void setUrlSerivce(String address) {
		mConvertServAddress = address;
		getPlayUrl();
	}

	public void setPlayUrl(String vob, String vod) {
		mVobAppUrl = vob;
		mVodAppUrl = vod;
	}

	private void getPlayUrl() {
		if (mConvertServAddress == null)
			return;
		GDHfcPlayUrlTask task = new GDHfcPlayUrlTask(this);
		task.execute(mConvertServAddress);
	}

	public void playVOB(Device remote, GDHfcUrlParam param) {

		GDHfcURL req = new GDHfcURL(remote.serial(), mVobAppUrl, param);
		send(remote, req);
	}

	@Override
	public void playVOB(Device remote, String user, String name,
			String resource, String product, long delay) {
		// TODO Auto-generated method stub
		// if(mPlayState == true)
		// return;
		// mPlayState = true;
		if (mConvertServAddress == null)
			return;
		if (mVobAppUrl == null) {
			GDHfcPlayUrlTask task = new GDHfcPlayUrlTask(this,
					(GDHfcDevice) remote, resource);
			task.execute(mConvertServAddress);
			return;
		}
		if (remote instanceof GDHfcDevice) {
			GDHfcVOBParamTask task = new GDHfcVOBParamTask(resource,
					(GDHfcDevice) remote);
			task.execute(mConvertServAddress);
		}

	}

	@Override
	public void playVOB(Device remote, String user, String name,
			String resource, String product, long start, long end, int offset) {
		// TODO Auto-generated method stub
		if (mConvertServAddress == null)
			return;
		if (mVobAppUrl == null) {
			GDHfcPlayUrlTask task = new GDHfcPlayUrlTask(this,
					(GDHfcDevice) remote, resource, start, end, offset);
			task.execute(mConvertServAddress);
			return;
		}
		if (remote instanceof GDHfcDevice) {
			GDHfcVOBParamTask task = new GDHfcVOBParamTask(resource,
					(GDHfcDevice) remote, start, end, offset);
			task.execute(mConvertServAddress);
		}
	}

	@Override
	public void playVOD(Device remote, String user, String name,
			String resource, String product, String asset, String provider,
			int offset, int duration) {
		// TODO Auto-generated method stub

		if (mConvertServAddress == null)
			return;
		if (mVodAppUrl == null) {
			GDHfcPlayUrlTask task = new GDHfcPlayUrlTask(this,
					(GDHfcDevice) remote, asset, provider, offset, duration);
			task.execute(mConvertServAddress);
			return;
		}
		GDHfcUrlParam param = new GDHfcUrlParam(asset, provider, offset);
		GDHfcURL req = new GDHfcURL(remote.serial(), mVodAppUrl, param);
		send(remote, req);
	}

	@Override
	public void playStatusSync(Device remote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getVolume(Device remote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVolume(Device remote, int volume) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playSync(Device remote) {
		// TODO Auto-generated method stub
		GDHfcApp req = new GDHfcApp(remote.serial());
		send(remote, req);
	}

	@Override
	public void playControl(Device remote, int control, Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void key(Device remote, int key) {
		// TODO Auto-generated method stub
		GDHfcKey req = new GDHfcKey(remote.serial(), key);
		send(remote, req);
	}

	@Override
	public void mouse(Device remote, Mouse mouse) {
		// TODO Auto-generated method stub
		GDHfcMouse req = new GDHfcMouse(remote.serial(), mouse);
		send(remote, req);
	}

	@Override
	public void sensor(Device remote, GSensor sensor) {
		// TODO Auto-generated method stub
		GDHfcSensor req = new GDHfcSensor(remote.serial(), sensor);
		send(remote, sensor);
	}

	@Override
	public void text(Device remote, byte[] text) {
		// TODO Auto-generated method stub
		GDHfcTextInput req = new GDHfcTextInput(remote.serial(), text);
		send(remote, req);
	}

	@Override
	public void sendURL(Device remote, String url, int command, JSONObject param) {
		// TODO Auto-generated method stub
		GDHfcURL req = new GDHfcURL(remote.serial(), url, command, param);
		send(remote, req);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (mRequestHandlerThread != null) {
			mRequestHandlerThread.start();
		}
		// createTimer();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (mRequestHandlerThread != null) {
			mHandler.finish();
		}
		// if(mTimer != null){
		// mTimer.cancel();
		// mTimer.purge();
		// mTimer = null;
		// }
		//
		// if(mSearch != null){
		// mSearch.cancel();
		// mSearch = null;
		// }
	}

	@Override
	public void search() {
		// TODO Auto-generated method stub

		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}

		if (mSearch != null) {
			mSearch.cancel();
			mSearch = null;
		}

		createTimer();
	}

	@Override
	public boolean connect(Device remote) {
		// TODO Auto-generated method stub
		if (mOnDeviceConnectListener != null)
			mOnDeviceConnectListener.onConnected(remote);
		return true;
	}

	@Override
	protected boolean send(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mHandler != null)
			return mHandler.sendRequest(remote, req);
		return false;
	}

	@Override
	protected void handle(Device remote, Object req) {
		// TODO Auto-generated method stub

		if (req == null || remote == null)
			return;
		if (req instanceof GDHfcURL) {
			GDHfcURL url = (GDHfcURL) req;
			if (url.isPush()) {
				onPlay(remote, req);
			} else if (url.isPull()) {
				onPlaySync(remote, req);
			}
		} else if (req instanceof GDHfcApp) {
			GDHfcApp url = (GDHfcApp) req;
			if (url.isPush()) {
				onPlay(remote, req);
			} else if (url.isPull()) {
				onPlaySync(remote, req);
			}
		} else if (req instanceof GDHfcKey) {
			onKey(remote, req);
		} else if (req instanceof GDHfcMouse) {
			onMouse(remote, req);
		} else if (req instanceof GDHfcSensor) {
			onSenor(remote, req);
		} else if (req instanceof GDHfcTextInput) {
			onTextInput(remote, req);
		}
	}

	@Override
	public void onPlay(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null || req == null) {
			return;
		}
		mClientRequestListener.onPlay(remote, 100, null, null);
	}

	@Override
	public void onPlayStatusSync(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null || req == null) {
			return;
		}
		mClientRequestListener.onGetStatus(remote, 100, null, null);
	}

	@Override
	public void onGetVolume(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null || req == null) {
			return;
		}
	}

	@Override
	public void onSetVolume(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null || req == null) {
			return;
		}
		mClientRequestListener.onSetVolume(remote, 100, null, 20);
	}

	public void onPlayVOB(Device remote, String resource, long delay) {
		ResourceInfo info = new ResourceInfo(null, resource, null, delay);
		mClientRequestListener.onPull(remote, 100, null, info);
	}

	public void onPlayVOB(Device remote, String resource, long start, long end,
			int offset) {
		ResourceInfo info = new ResourceInfo(null, resource, null, start, end,
				offset);
		mClientRequestListener.onPull(remote, 100, null, info);
	}

	@Override
	public void onPlaySync(Device remote, Object req) {
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null || req == null) {
			return;
		}

		// TODO Auto-generated method stub
		if (req instanceof GDHfcApp) {
			GDHfcApp request = (GDHfcApp) req;
			if (request.isPull() == false)
				return;
			if (request.result() == 0) {
				mClientRequestListener.onPull(remote, request.result(),
						request.message(), null);
				return;
			}

			GDHfcUrlParam param = request.param();
			if (param.type() == GDHfcUrlParam.VOD) {
				ResourceInfo info = new ResourceInfo(param.assetID(),
						param.providerID(), param.offset(), 0);
				mClientRequestListener.onPull(remote, 100, null, info);
				return;
			} else {
				GDHfcVOBResourceTask task = new GDHfcVOBResourceTask(
						(GDHfcDevice) remote, param);
				task.execute(mConvertServAddress);
			}
		}
	}

	@Override
	public void onPlayControl(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouse(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSenor(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextInput(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect(Device remote) {
		// TODO Auto-generated method stub
		if (mOnDeviceConnectListener != null) {
			mOnDeviceConnectListener.onConnectDrop(remote);
		}
	}

	private void createTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
			if (mSearch == null)
				mSearch = new SearchDeviceTask();
			if (mTimer != null && mSearch != null)
				mTimer.schedule(mSearch, 100L);
		}
	}

	private boolean createSocket() {
		if (mSocket == null) {
			try {
				mMulticastGroup = InetAddress.getByName(BROADCAST_IP_ADDRESS);

				mSocket = new DatagramSocket();

				if (mSocket != null)
					mSocket.setBroadcast(true);
			} catch (UnknownHostException e) {
				mSocket = null;
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				mSocket = null;
				return false;
			}
		}

		if (mSocket == null)
			return false;

		return true;
	}

	private synchronized boolean searchRemoteDevices() {

		if (createSocket() == false) {
			return false;
		}
		if (mOnDeviceSearchListener != null) {
			mOnDeviceSearchListener.onSearchStart();
		}
		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
				" ---------------------begin search GDHfc devices. ");

		GDHfcAnnounce req = new GDHfcAnnounce(mLocalDevice.serial(), 1);
		// NotifyRequest request = new NotifyRequest(req,false);
		byte[] msg = req.toByte();
		if (msg == null)
			return false;
		try {
			DatagramPacket data = new DatagramPacket(msg, msg.length,
					mMulticastGroup, BROADCAST_PORT);
			mSocket.send(data);
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private synchronized void getRemoteDevices() {
		long startTime = System.currentTimeMillis();
		long endTime = startTime;
		DatagramPacket datagram = null;
		mDeviceSearched = false;
		byte[] buffer = new byte[500];
		try {
			mSocket.setSoTimeout(SEARCH_TIME);
			while (endTime - startTime < SEARCH_TIME) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "startTime:"
						+ startTime + " endTime" + endTime);
				datagram = new DatagramPacket(buffer, buffer.length);
				mSocket.receive(datagram);

				GDHfcRequest req = GDHfcRequest.parse(datagram.getData(),
						datagram.getLength());
				if (req == null)
					continue;

				if (req instanceof GDHfcAnnounce) {
					GDHfcAnnounce announce = (GDHfcAnnounce) req;
					Device remote = new GDHfcDevice(announce.serial(), datagram
							.getAddress().getHostAddress(),
							Global.TERMINAL_GDHFC_BOX);
					LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
							"found remote device-------------ip:"
									+ datagram.getAddress().getHostAddress()
									+ ",port:" + datagram.getPort() + ",name:"
									+ announce.serial() + ",usercode:"
									+ announce.serial() + ",terminal type:"
									+ Global.TERMINAL_GDHFC_BOX);
					// add remote device
					if (mOnDeviceSearchListener != null) {
						mOnDeviceSearchListener.onDeviceOnline(remote);
						mDeviceSearched = true;
					}

				}

				endTime = System.currentTimeMillis();
			}
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, LogUtils.getTAG(),
					" found device time out");
		}
		if (mDeviceSearched == false) {
			Device remote = new GDHfcDevice("12345678", "192.168.88.2",
					Global.TERMINAL_GDHFC_BOX);
			if (mOnDeviceSearchListener != null) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
						"NOT found GDHfcDevice,add default Device 192.168.88.2");
				mOnDeviceSearchListener.onDeviceOnline(remote);
			}
		}
		if (mOnDeviceSearchListener != null) {
			mOnDeviceSearchListener.onSearchEnd();
		}
		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
				"+++++++++++++++++++++++++GDHfcDevice search Device end.");
		return;
	}

	class SearchDeviceTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (GDHfcDeviceAdapter.this.searchRemoteDevices() == true)
				GDHfcDeviceAdapter.this.getRemoteDevices();
		}

	}

	public static DeviceAdapter create() {
		if (gAdapter == null) {
			gAdapter = new GDHfcDeviceAdapter();
		}
		return gAdapter;
	}

	@Override
	public void mirrion(Device remote, boolean start) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMirrion(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPlayStatus(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

}
