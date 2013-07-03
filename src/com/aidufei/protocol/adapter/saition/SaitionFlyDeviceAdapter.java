package com.aidufei.protocol.adapter.saition;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

import com.aidufei.protocol.common.GSensor;
import com.aidufei.protocol.common.Mouse;
import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.DeviceAdapter;
import com.aidufei.protocol.oto.Global;
import com.aidufei.protocol.oto.OtOMediaControlRequest;
import com.aidufei.protocol.oto.OtOPlayRequest;
import com.aidufei.protocol.oto.OtOPullRequest;
import com.aidufei.protocol.oto.OtORequest;
import com.aidufei.protocol.oto.OtOSetVolumeRequest;
import com.aidufei.protocol.oto.OtOStatusRequest;
import com.aidufei.protocol.oto.ResourceInfo;
import com.aidufei.protocol.oto.Status;
import com.coship.ott.utils.LogUtils;
import com.coship.saition.facade.device.ISaitionListener;
import com.coship.saition.facade.device.SaitionDevice;
import com.coship.saition.facade.device.SaitionDeviceFacade;
import com.coship.saition.facade.device.SaitionDeviceType;
import com.coship.saition.facade.device.event.AddSaitionDeviceEvent;
import com.coship.saition.facade.device.event.CustomFeifeikanEvent;
import com.coship.saition.facade.device.event.GetPlayMMSContentRespEvent;
import com.coship.saition.facade.device.event.GetPlayURLByAsyncRespEvent;
import com.coship.saition.facade.device.event.GetProcessRespEvent;
import com.coship.saition.facade.device.event.GetVolumeRespEvent;
import com.coship.saition.facade.device.event.SaitionEvent;

public class SaitionFlyDeviceAdapter extends DeviceAdapter implements
		ISaitionListener {

	private static final String SAITION_VOD_ADDRESS = "172.16.100.80:554";

	private static SaitionFlyDeviceAdapter gAdapter = null;

	private String mConvertServiceAddress = null;
	private SaitionDeviceFacade mSaition = null;
	private Timer mSearchTimer = null;
	private SearchDeviceTask mSearchTask = null;

	// private Handler mHandler = new Handler(){
	//
	// };

	public static SaitionFlyDeviceAdapter create() {
		if (gAdapter == null)
			gAdapter = new SaitionFlyDeviceAdapter();

		return gAdapter;
	}

	private SaitionFlyDeviceAdapter() {
		mSaition = SaitionDeviceFacade.getInstance();
		if (mSaition != null)
			mSaition.addSaitonListener(this);
	}

	public void setEPGServerAddress(String address) {
		mConvertServiceAddress = address;
	}

	public void playVOB(Device remote, String service, String ts,
			String network, int delay) {

		String url = null;
		if (delay == 0) {
			url = "dvb://" + network + "." + ts + "." + service;
			if (mSaition != null)
				mSaition.playURL(url);
		} else {
			url = "dvb2://" + network + "." + ts + "." + service + "." + delay;
			if (mSaition != null)
				mSaition.playURL(url);
		}
	}

	public void playVOB(Device remote, String service, String ts,
			String network, long start, long end, int offset) {
		String url = null;
	}

	public void playVOD(Device remote, String contentID, int offset,
			int duration) {

		String url = "rtsp://" + SAITION_VOD_ADDRESS + "/" + contentID
				+ "^^^?startTime=" + offset + "&endTime=" + duration;

		if (mSaition != null)
			mSaition.playURL(url);

	}

	@Override
	public void playVOB(Device remote, String user, String name,
			String resource, String product, long delay) {
		// TODO Auto-generated method stub
		// if(remote instanceof SaitionFlyDevice){
		// SaitionVOBParamTask task = new SaitionVOBParamTask((SaitionFlyDevice)
		// remote,resource,delay);
		// task.execute(mConvertServiceAddress);
		// }
	}

	@Override
	public void playVOB(Device remote, String user, String name,
			String resource, String product, long start, long end, int offset) {
		// TODO Auto-generated method stub
		if (remote instanceof SaitionFlyDevice) {
			SaitionVOBParamTask task = new SaitionVOBParamTask(
					(SaitionFlyDevice) remote, resource, start, end, offset);
			task.execute("mConvertServiceAddress");
		}
	}

	@Override
	public void playVOD(Device remote, String user, String name,
			String resource, String product, String asset, String provider,
			int offset, int duration) {
		// TODO Auto-generated method stub
		// convert resource to contentID;

		// playVOD(remote,"11790", 0, 0);
		if (remote instanceof SaitionFlyDevice) {
			SaitionVODParamTask task = new SaitionVODParamTask(
					(SaitionFlyDevice) remote, resource, offset, duration);
			task.execute(mConvertServiceAddress);
		}
	}

	@Override
	public void playStatusSync(Device remote) {
		// TODO Auto-generated method stub
		// OtOStatusRequest req = new OtOStatusRequest();
		//
		// send(remote, req);
		if (mSaition != null) {
			mSaition.getProcess();
			mSaition.getVolume();

		}
	}

	@Override
	public void getVolume(Device remote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVolume(Device remote, int volume) {
		// TODO Auto-generated method stub
		// OtOSetVolumeRequest req = new OtOSetVolumeRequest(volume);
		// send(remote, req);
		if (mSaition != null) {
			mSaition.setVolume(volume);
		}

	}

	@Override
	public void playSync(Device remote) {
		// TODO Auto-generated method stub
		// OtOPullRequest req = new OtOPullRequest();
		// send(remote, req);

		// mSaition.getPlayURLByAsync();
		mSaition.getPlayMMSContent();
	}

	@Override
	public void playControl(Device remote, int control, Object data) {
		// TODO Auto-generated method stub

		// OtOMediaControlRequest req = new OtOMediaControlRequest(control);
		// if(control == Global.MEDIA_PROGRESS)
		// req.setProgress((Integer)data);
		// send(remote, req);
		if (control == Global.MEDIA_PROGRESS) {
			if (mSaition != null) {
				int percent = (Integer) data / 10;
				mSaition.setProcess(percent);
			}
		} else if (control == Global.MEDIA_STOP) {
			if (mSaition != null) {
				mSaition.exitPlayWithoutTip();
			}
		}

	}

	@Override
	public void key(Device remote, int key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouse(Device remote, Mouse mouse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sensor(Device remote, GSensor sensor) {
		// TODO Auto-generated method stub

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
		// if(mSearchTimer == null){
		// mSearchTimer = new Timer();
		// if(mSearchTask == null)
		// mSearchTask = new SearchDeviceTask();
		// if(mSearchTimer != null && mSearchTask != null)
		// mSearchTimer.schedule(mSearchTask, 100L, 60 * 1000);
		// }
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		// if(mSearchTimer != null){
		// mSearchTimer.cancel();
		// mSearchTimer.purge();
		// mSearchTimer = null;
		// return;
		// }
		//
		// if(mSearchTask != null){
		// mSearchTask.cancel();
		// mSearchTask = null;
		// }
	}

	@Override
	public void search() {
		// TODO Auto-generated method stub
		if (mSaition == null)
			return;
		mSaition.stopSearchWiFi();
		mSaition.searchWiFi();
	}

	@Override
	public boolean connect(Device remote) {
		// TODO Auto-generated method stub
		// if(remote instanceof SaitionFlyDevice){
		// SaitionDevice device = new SaitionDevice(remote.uuid(),
		// remote.address(),
		// Integer.parseInt(remote.serial()));
		// if(mSaition.quickConnectDevice(device) < 0){
		// if(mOnDeviceConnectListener != null)
		// mOnDeviceConnectListener.onConnectDrop(remote);
		// return false;
		// }else{
		// if(mOnDeviceConnectListener != null)
		// mOnDeviceConnectListener.onConnected(remote);
		// return true;
		// }
		// }
		// return false;
		if (remote instanceof SaitionFlyDevice) {
			ConnectDeviceTask task = new ConnectDeviceTask();
			task.execute(remote);
			return true;
		}
		return false;
	}

	@Override
	public void disconnect(Device remote) {
		// TODO Auto-generated method stub
		if (remote instanceof SaitionFlyDevice) {
			SaitionDevice device = new SaitionDevice(remote.uuid(),
					remote.address(), Integer.parseInt(remote.serial()));
			mSaition.deviceManager.disConnectDevice(device);
		}
	}

	@Override
	protected boolean send(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (req instanceof OtORequest && remote instanceof SaitionFlyDevice) {
			JSONObject jsonObject;
			OtORequest request = (OtORequest) req;
			try {
				jsonObject = request.toJSON();
			} catch (Exception e) {
				return false;
			}
			if (jsonObject == null)
				return false;
			mSaition.deviceManager.sendJson(jsonObject);
			return true;
		}
		return false;
	}

	@Override
	protected void handle(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (req instanceof OtOMediaControlRequest) {
			onPlayControl(remote, req);
		} else if (req instanceof OtOPlayRequest) {
			onPlay(remote, req);
		} else if (req instanceof OtOPullRequest) {
			onPlaySync(remote, req);
		} else if (req instanceof OtOSetVolumeRequest) {
			onSetVolume(remote, req);
		} else if (req instanceof OtOStatusRequest) {
			onPlayStatusSync(remote, req);
		}
	}

	@Override
	public void onPlay(Device remote, Object req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayStatusSync(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
			return;
		}

		GetProcessRespEvent evt = (GetProcessRespEvent) req;

		// evt.getProcess()

		// if(mClientRequestListener == null ||remote == null ||
		// remote.address() == null){
		// return;
		// }
		// OtOStatusRequest request = (OtOStatusRequest)req;
		// Status stat = request.status();
		// ResourceInfo info = request.resource();
		// if(stat == null){
		// mClientRequestListener.onGetStatus(remote,Status.STATUS_UNKNOWN,
		// null,info);
		// mClientRequestListener.onGetVolume(remote,Status.STATUS_UNKNOWN,
		// null, request.volume());
		// }else{
		// mClientRequestListener.onGetStatus(remote,stat.returnCode(),
		// stat.description(), info);
		// mClientRequestListener.onGetVolume(remote,stat.returnCode(),
		// stat.description(), request.volume());
		// }
		return;
	}

	@Override
	public void onGetVolume(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
			return;
		}
		GetVolumeRespEvent event = (GetVolumeRespEvent) req;
		mClientRequestListener
				.onGetVolume(remote, 100, null, event.getVolume());
	}

	@Override
	public void onSetVolume(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null || remote == null
				|| remote.address() == null) {
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

	public void onPullVOD(Device remote, String resourceCode, int offset,
			int duration) {
		ResourceInfo info = new ResourceInfo(null, resourceCode, null, offset,
				duration);
		mClientRequestListener.onPull(remote, 100, null, info);
	}

	public void onPullVOB(Device remote, String resourceCode, int delay) {
		ResourceInfo info = new ResourceInfo(null, resourceCode, null, delay);
		mClientRequestListener.onPull(remote, 100, null, info);
	}

	public void onPullVOB(Device remote, String resourceCode, long start,
			long end, int offset) {
		ResourceInfo info = new ResourceInfo(null, resourceCode, null, start,
				end, offset);
		mClientRequestListener.onPull(remote, 100, null, info);
	}

	@Override
	public void onPlaySync(Device remote, Object req) {
		// TODO Auto-generated method stub
		if (mClientRequestListener == null) {
			return;
		}
		// OtOPullRequest request = (OtOPullRequest)req;
		// Status stat = request.status();
		// ResourceInfo info = request.resource();
		// if(stat == null){
		// mClientRequestListener.onPull(remote,Status.STATUS_UNKNOWN, null,
		// info);
		// }else{
		// mClientRequestListener.onPull(remote,stat.returnCode(),
		// stat.description(), info);
		// }
		// return;

		// GetPlayURLByAsyncRespEvent event = (GetPlayURLByAsyncRespEvent)req;
		// LogUtils.trace(Log.DEBUG,TAG,event.getJsonObject().toString());
		// parsePlaySync(event.getJsonObject());
		GetPlayMMSContentRespEvent event = (GetPlayMMSContentRespEvent) req;
		String MMS = event.getMmsContent();
		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "---------------get MMS:"
				+ MMS);
		MMSParser parser = new MMSParser(MMS);
		if (parser != null) {
			if (parser.type() == MMSParser.VOB
					|| parser.type() == MMSParser.VOB_DELAY) {
				SaitionVOBResourceTask task = new SaitionVOBResourceTask(
						(SaitionFlyDevice) remote, parser.ts(),
						parser.network(), parser.service());
				task.execute(mConvertServiceAddress);
			} else if (parser.type() == MMSParser.VOD) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
						"Saition Pull ContentID=" + parser.content());
				SaitionVODResourceTask task = new SaitionVODResourceTask(
						(SaitionFlyDevice) remote, parser.content(), 0, 0);
				task.execute(mConvertServiceAddress);
			}
		}
		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), MMS);
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

	// rtsp://172.16.100.80:554/11790^^^?startTime=0&areaCode=2011
	private String parseURL(String param) {
		JSONObject paramJSON = null;
		if (param == null)
			return null;
		try {
			paramJSON = (JSONObject) new JSONTokener(param).nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
		String url = null;
		try {
			url = paramJSON.getString("URL");
			if (url == null) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
						"get PlayURL URL error, URL=null");
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}

		if (url.contains("rtsp:")) {
			int end = url.indexOf("^^^");
			String sub = url.substring(0, end);
			int start = sub.lastIndexOf('/');
			String strContentID = sub.substring(start + 1);
			// here will convert contentID to resourceCode;
			return strContentID;
		} else if (url.contains("dvb:")) {
			return null;
		} else if (url.contains("dvb2:")) {
			return null;
		}
		return null;
	}

	private void parsePlaySync(JSONObject json) {
		int headCommand = 0;
		String str = json.toString();
		try {
			headCommand = json.getInt("head_cmd");
			if (headCommand != 16) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
						"get PlayURL head_cmd error, head_cmd=" + headCommand);
				return;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return;
		}

		int command = 0;
		try {
			command = json.getInt("Command");

			if (command != 2) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
						"get PlayURL command error, command=" + command);
				return;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return;
		}

		String param = null;
		try {
			param = json.getString("Param");
			if (param == null) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
						"get PlayURL param error, param=null");
				return;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return;
		}

		/*
		 * String url = null; try { url = param.getString("URL"); if(url ==
		 * null){ LogUtils.trace(Log.DEBUG,
		 * LogUtils.getTAG(),"get PlayURL URL error, URL=null"); return; } }
		 * catch (JSONException e) { // TODO Auto-generated catch block return;
		 * }
		 */

		String content = parseURL(param);

	}

	@Override
	public void processSaitionEvent(SaitionEvent evt) {
		// TODO Auto-generated method stub
		if (evt instanceof AddSaitionDeviceEvent) {
			SaitionDevice device = evt.getSaitionDevice();
			if (device == null || device.getType() == null
					|| device.getType() != SaitionDeviceType.WIFI)
				return;
			mSaition.stopSearchWiFi();
			LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
					"found a saition device ip:" + device.getIp() + ",port:"
							+ device.getPort() + ",stbid=" + device.getStbid());
			Device remote = new SaitionFlyDevice(device.getIp(),
					device.getPort(), device.getStbid());
			if (mOnDeviceSearchListener != null) {
				mOnDeviceSearchListener.onDeviceOnline(remote);
			}

		} else if (evt instanceof CustomFeifeikanEvent) {
			// here will handle received response
			OtORequest req = OtORequest
					.parseCommand(((CustomFeifeikanEvent) evt).getJsonStr());
			if (req == null)
				return;
			SaitionDevice dev = evt.getSaitionDevice();
			Device remote = null;
			if (dev != null)
				remote = new SaitionFlyDevice(dev.getIp(), dev.getPort(),
						dev.getStbid());
			else
				remote = new SaitionFlyDevice();
			handle(remote, req);
		} else if (evt instanceof GetPlayURLByAsyncRespEvent) {
			onPlaySync(null, evt);
		} else if (evt instanceof GetVolumeRespEvent) {
			SaitionDevice dev = evt.getSaitionDevice();
			Device remote = null;
			if (dev != null)
				remote = new SaitionFlyDevice(dev.getIp(), dev.getPort(),
						dev.getStbid());
			else
				remote = new SaitionFlyDevice();
			onGetVolume(remote, evt);
		} else if (evt instanceof GetProcessRespEvent) {
			SaitionDevice dev = evt.getSaitionDevice();
			Device remote = null;
			if (dev != null)
				remote = new SaitionFlyDevice(dev.getIp(), dev.getPort(),
						dev.getStbid());
			else
				remote = new SaitionFlyDevice();
			onPlayStatusSync(remote, evt);
		} else if (evt instanceof GetPlayMMSContentRespEvent) {
			SaitionDevice dev = evt.getSaitionDevice();
			Device remote = null;
			if (dev != null)
				remote = new SaitionFlyDevice(dev.getIp(), dev.getPort(),
						dev.getStbid());
			else
				remote = new SaitionFlyDevice();
			onPlaySync(remote, evt);

		}
	}

	class SearchDeviceTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mSaition != null) {
				LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
						"begin search SaitionDevice");
				mSaition.stopSearchWiFi();
				mSaition.searchWiFi();
			}
		}

	}

	class ConnectDeviceTask extends AsyncTask<Device, Integer, Integer> {

		@Override
		protected Integer doInBackground(Device... params) {
			// TODO Auto-generated method stub
			if (params == null || params[0] == null) {
				return -1;
			}
			Device remote = params[0];
			if (remote instanceof SaitionFlyDevice) {
				SaitionDevice device = new SaitionDevice(remote.uuid(),
						remote.address(), Integer.parseInt(remote.serial()));
				if (mSaition.quickConnectDevice(device) < 0) {
					if (mOnDeviceConnectListener != null)
						mOnDeviceConnectListener.onConnectDrop(remote);
					return -1;
				} else {
					if (mOnDeviceConnectListener != null)
						mOnDeviceConnectListener.onConnected(remote);
					return 0;
				}
			}
			return -1;
		}

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
