package com.aidufei.protocol.adapter.oto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.util.Log;

import com.aidufei.protocol.common.GSensor;
import com.aidufei.protocol.common.Mouse;
import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.DeviceAdapter;
import com.aidufei.protocol.oto.Global;
import com.aidufei.protocol.oto.OtOAnnounceRequest;
import com.aidufei.protocol.oto.OtOMediaControlRequest;
import com.aidufei.protocol.oto.OtOPlayRequest;
import com.aidufei.protocol.oto.OtOPullRequest;
import com.aidufei.protocol.oto.OtORequest;
import com.aidufei.protocol.oto.OtOScreenMirrionRequest;
import com.aidufei.protocol.oto.OtOSetVolumeRequest;
import com.aidufei.protocol.oto.OtOStatusRequest;
import com.aidufei.protocol.oto.ResourceInfo;
import com.aidufei.protocol.oto.Status;
import com.aidufei.remote.RemoteKeyboard;
import com.aidufei.remote.RemoteMouse;
import com.aidufei.remote.RemoteSensor;
import com.coship.ott.utils.LogUtils;

public class OtODeviceAdapter extends DeviceAdapter {
	// private OtOHeartBeat mBeat;

	private final static String MULTICAST_IP_ADDRESS = "239.255.255.105";
	private final static int MULTICAST_PORT = 9001;
	private static final int SEARCH_TIME = 3000;
	private Timer mTimer = null;
	private DatagramSocket mSocket = null;
	private InetAddress mMulticastGroup = null;
	private SearchDeviceTask mSearch = null;

	private Device mLocalDevice = null;
	private OtORequestHandler mHandler = null;
	private Thread mRequestHandlerThread = null;

	private static OtODeviceAdapter gAdapter = null;

	private RemoteKeyboard mKeyboard = null;
	private RemoteMouse mMouse = null;
	private RemoteSensor mSensor = null;

	private OtODeviceAdapter() {
		// mBeat = null;
		mHandler = new OtORequestHandler(this);
		mRequestHandlerThread = new Thread(mHandler);
	}

	public void setLocalDevice(Device local) {
		mLocalDevice = local;
	}

	@Override
	public void playVOB(Device remote, String user, String name,
			String resource, String product, long delay) {
		// TODO Auto-generated method stub
		ResourceInfo info = new ResourceInfo(name, resource, product, delay);
		OtOPlayRequest req = new OtOPlayRequest(user, null, info);

		send(remote, req);
	}

	@Override
	public void playVOB(Device remote, String user, String name,
			String resource, String product, long start, long end, int offset) {
		// TODO Auto-generated method stub
		ResourceInfo info = new ResourceInfo(name, resource, product, start,
				end, offset);
		OtOPlayRequest req = new OtOPlayRequest(user, null, info);

		send(remote, req);
	}

	@Override
	public void playVOD(Device remote, String user, String name,
			String resource, String product, String asset, String provider,
			int offset, int duration) {
		// TODO Auto-generated method stub
		ResourceInfo info = new ResourceInfo(name, resource, product, offset,
				duration);
		OtOPlayRequest req = new OtOPlayRequest(user, null, info);

		send(remote, req);
	}

	@Override
	public void playStatusSync(Device remote) {
		// TODO Auto-generated method stub
		OtOStatusRequest req = new OtOStatusRequest();
		send(remote, req);
	}

	@Override
	public void getVolume(Device remote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVolume(Device remote, int volume) {
		// TODO Auto-generated method stub
		OtOSetVolumeRequest req = new OtOSetVolumeRequest(volume);
		send(remote, req);
	}

	@Override
	public void playSync(Device remote) {
		// TODO Auto-generated method stub
		OtOPullRequest req = new OtOPullRequest();
		send(remote, req);
	}

	public void playProgress(Device remote, int progress) {
		OtOMediaControlRequest req = new OtOMediaControlRequest(
				Global.MEDIA_PROGRESS);
		req.setProgress(progress);
		send(remote, req);
	}

	@Override
	public void playControl(Device remote, int control, Object data) {
		// TODO Auto-generated method stub
		OtOMediaControlRequest req = new OtOMediaControlRequest(control);

		if (control == Global.MEDIA_PROGRESS || control == Global.MEDIA_SEEK) {
			int progress = (Integer) data;
			req.setProgress(progress);
		}
		send(remote, req);
	}

	@Override
	public void key(Device remote, int key) {
		// TODO Auto-generated method stub
		if (mKeyboard == null) {
			mKeyboard = new RemoteKeyboard(remote.address());
		}

		OtORemoteKeyboardTask task = new OtORemoteKeyboardTask(mKeyboard,
				remote.address());

		task.execute(key);
		return;
	}

	@Override
	public void mouse(Device remote, Mouse mouse) {
		// TODO Auto-generated method stub
		if (mMouse == null) {
			mMouse = new RemoteMouse(remote.address());
		} else {
			mMouse.setRemote(remote.address());
		}

		if (mMouse != null && mouse != null) {
			switch (mouse.action()) {
			case Mouse.ACTION_DOUBLE_CLICK:
				if (mouse.type() == Mouse.MOUSE_LEFT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_DOUBLE_CLICK);
				} else if (mouse.type() == Mouse.MOUSE_RIGHT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_DOUBLE_CLICK);
				} else if (mouse.type() == Mouse.MOUSE_MID) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_DOWN);
				}
				break;
			case Mouse.ACTION_SINGLE_CLICK:
				if (mouse.type() == Mouse.MOUSE_LEFT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_SINGLE_CLICK);
				} else if (mouse.type() == Mouse.MOUSE_RIGHT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_SINGLE_CLICK);
				} else if (mouse.type() == Mouse.MOUSE_MID) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_DOWN);
				}
				break;
			case Mouse.ACTION_DOWN:
				if (mouse.type() == Mouse.MOUSE_LEFT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_DOWN);
				} else if (mouse.type() == Mouse.MOUSE_RIGHT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_DOWN);
				} else if (mouse.type() == Mouse.MOUSE_MID) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_DOWN);
				}
				break;
			case Mouse.ACTION_UP:
				if (mouse.type() == Mouse.MOUSE_LEFT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_UP);
				} else if (mouse.type() == Mouse.MOUSE_RIGHT) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_UP);
				} else if (mouse.type() == Mouse.MOUSE_MID) {
					mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_UP);
				}
				break;
			case Mouse.ACTION_MOVE:
				mMouse.sendMouseMoveEvent(RemoteMouse.MOUSE_ACTION_MOVE,
						mouse.x(), mouse.y());
				break;
			case Mouse.ACTION_ROLL:
				if (mouse.type() == Mouse.MOUSE_ROLL_UP) {
					mMouse.sendMouseWheelEvent(0);
				} else if (mouse.type() == Mouse.MOUSE_ROLL_DOWN) {
					mMouse.sendMouseWheelEvent(2);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void sensor(Device remote, GSensor sensor) {
		// TODO Auto-generated method stub
		if (mSensor == null) {
			mSensor = new RemoteSensor(remote.address());
		} else {
			mSensor.setRemote(remote.address());
		}

		mSensor.sendSensorEvent(sensor.type(), sensor.x(), sensor.y(),
				sensor.z());

	}

	@Override
	public void text(Device remote, byte[] text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendURL(Device remote, String url, int command, JSONObject param) {
		// TODO Auto-generated method stub

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
		if (remote == null)
			return false;
		if (mOnDeviceConnectListener != null) {
			mOnDeviceConnectListener.onConnected(remote);
		}
		//
		// if(mBeat == null){
		// mBeat = new OtOHeartBeat();
		// if(mBeat == null)
		// return false;
		// mBeat.addListener(mOnDeviceConnectListener);
		// }
		//
		// mBeat.start(remote);
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
		if (req instanceof OtOPlayRequest) {
			onPlay(remote, req);
		} else if (req instanceof OtOStatusRequest) {
			onPlayStatusSync(remote, req);
		} else if (req instanceof OtOMediaControlRequest) {
			onPlayControl(remote, req);
		} else if (req instanceof OtOPullRequest) {
			onPlaySync(remote, req);
		} else if (req instanceof OtOSetVolumeRequest) {
			onSetVolume(remote, req);
		}
	}

	@Override
	public void onPlay(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
			return;
		}
		OtOPlayRequest request = (OtOPlayRequest) req;
		Status stat = request.status();
		ResourceInfo info = request.resource();
		if (stat == null) {
			mClientRequestListener.onPlay(remote, Status.STATUS_UNKNOWN, null,
					info);
		} else
			mClientRequestListener.onPlay(remote, stat.returnCode(),
					stat.description(), info);
		return;
	}

	@Override
	public void onPlayStatusSync(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
			return;
		}
		OtOStatusRequest request = (OtOStatusRequest) req;
		Status stat = request.status();
		ResourceInfo info = request.resource();
		if (stat == null) {
			mClientRequestListener.onGetStatus(remote, Status.STATUS_UNKNOWN,
					null, info);
			mClientRequestListener.onGetVolume(remote, Status.STATUS_UNKNOWN,
					null, request.volume());
			mClientRequestListener.onGetPlayStatus(remote,
					Status.STATUS_UNKNOWN, null, request.getPlayStatus());
		} else {
			mClientRequestListener.onGetStatus(remote, stat.returnCode(),
					stat.description(), info);
			mClientRequestListener.onGetVolume(remote, stat.returnCode(),
					stat.description(), request.volume());
			mClientRequestListener.onGetPlayStatus(remote, stat.returnCode(),
					null, request.getPlayStatus());
		}
		return;
	}

	@Override
	public void onGetVolume(Device remote, Object req) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void onSetVolume(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null || req == null) {
			return;
		}
		OtOSetVolumeRequest request = (OtOSetVolumeRequest) req;
		Status stat = request.status();
		if (stat == null) {
			mClientRequestListener.onSetVolume(remote, Status.STATUS_UNKNOWN,
					null, request.volume());
		} else {
			mClientRequestListener.onSetVolume(remote, stat.returnCode(),
					stat.description(), request.volume());
		}
		return;
	}

	@Override
	public void onPlaySync(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
			return;
		}
		OtOPullRequest request = (OtOPullRequest) req;
		Status stat = request.status();
		ResourceInfo info = request.resource();
		if (stat == null) {
			mClientRequestListener.onPull(remote, Status.STATUS_UNKNOWN, null,
					info);
		} else {
			mClientRequestListener.onPull(remote, stat.returnCode(),
					stat.description(), info);
		}
		return;
	}

	@Override
	public void onPlayControl(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
			return;
		}
		OtOMediaControlRequest request = (OtOMediaControlRequest) req;
		Status stat = request.status();
		if (stat == null) {
			mClientRequestListener.onPlayControl(remote, Status.STATUS_UNKNOWN,
					null);
		} else {
			mClientRequestListener.onPlayControl(remote, stat.returnCode(),
					stat.description());
		}
		return;
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

	public static DeviceAdapter create() {
		if (gAdapter == null)
			gAdapter = new OtODeviceAdapter();
		return gAdapter;
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
				mMulticastGroup = InetAddress.getByName(MULTICAST_IP_ADDRESS);
				mSocket = new DatagramSocket(null);
				mSocket.setReuseAddress(true);
				InetSocketAddress socketAddress = new InetSocketAddress(
						MULTICAST_PORT + 10);
				mSocket.bind(socketAddress);
			} catch (UnknownHostException e) {
				mSocket = null;
				return false;
			} catch (IOException e) {
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
				" >>>>>>>>>>>>>>begin search devices. ");
		OtOAnnounceRequest req = new OtOAnnounceRequest(mLocalDevice.serial(),
				mLocalDevice.type(), mLocalDevice.name(), mLocalDevice.uuid());
		// NotifyRequest request = new NotifyRequest(req,false);
		byte[] msg = req.toByte();
		if (msg == null)
			return false;
		try {
			DatagramPacket data = new DatagramPacket(msg, msg.length,
					mMulticastGroup, MULTICAST_PORT);
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

		byte[] buffer = new byte[500];
		try {
			while (endTime - startTime < SEARCH_TIME) {
				mSocket.setSoTimeout(SEARCH_TIME);
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "startTime:"
						+ startTime + " endTime" + endTime);
				datagram = new DatagramPacket(buffer, buffer.length);
				mSocket.receive(datagram);

				OtORequest req = OtORequest.parseCommand(datagram.getData(),
						datagram.getLength());
				if (req == null)
					continue;

				if (req instanceof OtOAnnounceRequest) {
					OtOAnnounceRequest announce = (OtOAnnounceRequest) req;
					Device remote = new OtODevice(announce.userName(),
							announce.userCode(), announce.terminalType(),
							announce.uuid(), datagram.getAddress()
									.getHostAddress());
					LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
							"found remote device-------------ip:"
									+ datagram.getAddress().getHostAddress()
									+ ",port:" + datagram.getPort() + ",name:"
									+ announce.userName() + ",usercode:"
									+ announce.userCode() + ",terminal type:"
									+ announce.terminalType());
					// add remote device

					LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
							"mOnDeviceSearchListener:"
									+ mOnDeviceSearchListener);
					if (mOnDeviceSearchListener != null) {
						mOnDeviceSearchListener.onDeviceOnline(remote);
					}
				}

				endTime = System.currentTimeMillis();
			}
		} catch (Exception e) {
			LogUtils.trace(Log.INFO, LogUtils.getTAG(),
					" found device time out");
		}
		if (mOnDeviceSearchListener != null) {
			mOnDeviceSearchListener.onSearchEnd();
		}
		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
				"<<<<<<<<<<<<<<,search Device end.");
		return;
	}

	class SearchDeviceTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (OtODeviceAdapter.this.searchRemoteDevices() == true)
				OtODeviceAdapter.this.getRemoteDevices();
		}

	}

	@Override
	public void disconnect(Device remote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mirrion(Device remote, boolean start) {
		// TODO Auto-generated method stub
		OtOScreenMirrionRequest req = new OtOScreenMirrionRequest(start);
		send(remote, req);
	}

	@Override
	public void onMirrion(Device remote, Object req) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
			return;
		}
		OtOScreenMirrionRequest request = (OtOScreenMirrionRequest) req;
		Status stat = request.status();
		if (stat == null) {
			mClientRequestListener.onMirrion(remote, request.mirrioned(),
					Status.STATUS_UNKNOWN, null);
		} else
			mClientRequestListener.onMirrion(remote, request.mirrioned(),
					stat.returnCode(), stat.description());
		return;
	}

	@Override
	public void onGetPlayStatus(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

}
