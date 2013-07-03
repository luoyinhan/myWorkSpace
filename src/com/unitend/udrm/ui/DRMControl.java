package com.unitend.udrm.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.coship.ott.activity.MainTabHostActivity;
import com.coship.ott.activity.UserCenterTabActivity;
import com.coship.ott.constant.Constant;
import com.coship.ott.constant.DeviceBingDingError;
import com.coship.ott.constant.UdrmDefine;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.DeviceListEntity;
import com.coship.ott.transport.dto.PlayItem;
import com.coship.ott.transport.dto.vod.PlayURLJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.MyApplication;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.ToastUtils;
import com.unitend.udrm.util.LibUDRM;
import com.unitend.udrm.util.LibUDRMException;

public class DRMControl {
	private static String TAG = "DRMControl";
	private Context mContext;
	private Session session;
	public static LibUDRM mLibUDRM;
	private String mUserNameStr, PWord, passWord;
	private GetDRMDeviceBingDing mGetDRMDeviceBingDing;
	private SetDRMEnvironment mSetDRMEnvironment;
	private Intent intent;
	private static int DEVICE_TAG = 2;
	public static ToastUtils mToastUtils;
	private String mPath;
	private String resourceCode;
	private int playType;
	private long delay;
	private long shifttime;
	private long shiftend;
	private long timecode;
	private int videoType;
	private int fmt;
	private int playTime;
	private String assetName;
	private String assetID;
	private String providerID;
	private String posterUrl;
	private String programID;
	private long tempShiftTime;
	private String subID;
	private String productCode;
	private static int bingDingState;

	public DRMControl(Context mContext, Session session, Intent intent) {
		super();
		this.mContext = mContext;
		this.session = session;
		this.intent = intent;
		mToastUtils = new ToastUtils(mContext, "正在加载播放数据...");
		mToastUtils.showToastAlong();
		initDate();
		initDRM();
	}

	private void initDate() {
		mPath = intent.getStringExtra("playUrl");
		resourceCode = intent.getStringExtra("resourceCode");
		playType = intent.getIntExtra("playType", 0);
		delay = intent.getLongExtra("delay", 0l);
		shifttime = intent.getLongExtra("shifttime", 0l);
		tempShiftTime = shifttime;
		shiftend = intent.getLongExtra("shiftend", 0l);
		timecode = intent.getLongExtra("timecode", 0l);
		videoType = intent.getIntExtra("videoType", 0);
		fmt = intent.getIntExtra("fmt", 2);
		playTime = intent.getIntExtra("playTime", 0);
		assetName = intent.getStringExtra("assetName");
		assetID = intent.getStringExtra("assetID");
		providerID = intent.getStringExtra("providerID");
		posterUrl = intent.getStringExtra("posterUrl");
		programID = intent.getStringExtra("programID");
		if (MyApplication.playItem != null) {
			PlayItem item = MyApplication.playItem.get(resourceCode);
			if (item != null) {
				subID = item.getSubID();
				productCode = item.getProductCode();
			}
		}
		getPlayUrl();
	}

	/**
	 * 获取播放串
	 * */
	private void getPlayUrl() {
		new AsyncTask<Void, Void, PlayURLJson>() {

			@Override
			protected PlayURLJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getPlayURL(InterfaceUrls.GET_PLAYURL,
						resourceCode, productCode, subID,
						session.getUserCode(), playType, delay, shifttime,
						shiftend, timecode, fmt);
			}

			@Override
			protected void onPostExecute(PlayURLJson result) {

				if (null == result || 0 != result.getRet()) {
					mToastUtils.cancel();
					if (result != null) {
						Toast.makeText(mContext, result.getRetInfo(),
								Toast.LENGTH_SHORT).show();
					}
					return;
				}
				if (0 == result.getRet()) {
					mPath = result.getPalyURL();
					if (TextUtils.isEmpty(mPath)) {
						mToastUtils.cancel();
						Toast.makeText(mContext, "无法获取播放地址！",
								Toast.LENGTH_SHORT).show();
						return;
					}
					intent.putExtra("playUrl", mPath);
					intent.putExtra("subID", result.getSubID());
					intent.putExtra("productCode", result.getProductCode());
					if (TextUtils.isEmpty(subID)) {
						PlayItem item = new PlayItem();
						item.setProductCode(result.getProductCode());
						item.setSubID(result.getSubID());
						MyApplication.playItem.put(resourceCode, item);
					}
					enterPlayer(mContext, intent);
				}
			}
		}.execute();
	}

	/**
	 * 进入播放页面
	 * 
	 * @param context
	 * @param intent
	 */
	private void enterPlayer(Context context, Intent intent) {
		String playUrl = intent.getStringExtra("playUrl");
		if (!TextUtils.isEmpty(playUrl)
				&& bingDingState == UdrmDefine.UDRM_ERROR_OK) {
			intent.setClass(mContext, VideoPlayerActivity.class);
			mContext.startActivity(intent);
			bingDingState = -1;
			if (DRMControl.mToastUtils != null) {
				DRMControl.mToastUtils.cancel();
			}
		}
	}

	private void initDRM() {
		mUserNameStr = session.getUserName();
		PWord = session.getPassWord();
		passWord = NetTransportUtil.getMD5(PWord);
		if (mToastUtils.isEnter) {
			mToastUtils.cancel();
			return;
		}
		mSetDRMEnvironment = new SetDRMEnvironment();
		mSetDRMEnvironment.execute();
	}

	/**
	 * 设置环境变量
	 * 
	 */
	private class SetDRMEnvironment extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				LibUDRM.useIOMX(mContext);
				mLibUDRM = LibUDRM.getInstance();
				mLibUDRM.UDRMAgentDeviceLocalPath(VideoPlayerActivity.sdPath
						+ "/.UDRM");
				LibUDRM.useIOMX(false);
			} catch (LibUDRMException e) {
				e.printStackTrace();
			}
			// 设置环境变量
			int enveiomentState = mLibUDRM.UDRMAgentSetEnv(mUserNameStr,
					passWord, session.getMacPath(), Constant.DEVICENAME,
					Constant.DRMURL);
			return enveiomentState;
		}

		@Override
		protected void onPostExecute(Integer enveiomentState) {
			bingDingState = mLibUDRM.UDRMAgentCheckBindStatus();
			if (bingDingState == UdrmDefine.UDRM_ERROR_OK) {
				LogUtils.trace(Log.INFO, TAG, "BindState:" + ",设置环境变量成功");
				String resourceCode = intent.getStringExtra("resourceCode");
				if (TextUtils.isEmpty(resourceCode)) {
					mToastUtils.cancel();
					return;
				}
				// 跳转到播放器页面
				enterPlayer(mContext, intent);
			} else if (bingDingState == UdrmDefine.UDRM_ERROR_NEED_BIND) {
				mGetDRMDeviceBingDing = new GetDRMDeviceBingDing();
				mGetDRMDeviceBingDing.execute();
			} else {
				if (mToastUtils != null) {
					mToastUtils.cancel();
				}
				Toast.makeText(mContext, "绑定播放器异常！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 获取DRM设备绑定
	 * 
	 */
	private class GetDRMDeviceBingDing extends AsyncTask<Void, Void, Integer> {

		byte devicelist[] = new byte[2048];

		@Override
		protected Integer doInBackground(Void... params) {
			return mLibUDRM.UDRMAgentGetBindedDeviceList(mUserNameStr,
					devicelist);
		}

		@Override
		protected void onPostExecute(Integer DeviceListState) {

			List<DeviceListEntity> mList = new ArrayList<DeviceListEntity>();
			List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
			if (DeviceListState == UdrmDefine.UDRM_ERROR_OK) {
				String devlist = new String(devicelist, 0, 2048);
				String[] devItem = devlist.split("\\^&&&");
				for (int i = 0; i < devItem.length; i++) {
					String[] item = devItem[i].split("&\\^\\^\\^");
					if (item.length != 6) {
						continue;
					}
					DeviceListEntity listEntity = new DeviceListEntity();
					listEntity.setPchDeviceName(item[4]);// device name;
					listEntity.setPchDRMID(item[2]);
					listEntity.setPchMACAddr(item[3]);
					mList.add(listEntity);
				}
				for (int i = 0; i < mList.size(); i++) {
					HashMap<String, Object> mItem = new HashMap<String, Object>();
					DeviceListEntity listEntity = mList.get(i);
					mItem.put("id", listEntity.getPchDRMID());
					mItem.put("macaddr", listEntity.getPchMACAddr());
					mItem.put("devicename", listEntity.getPchDeviceName());
					mData.add(mItem);
				}
			} else {
				if (mToastUtils != null) {
					mToastUtils.cancel();
				}
				Toast.makeText(mContext, "获取绑定列表出错", Toast.LENGTH_SHORT).show();
				int errr = new DeviceBingDingError().getDebugError(mLibUDRM,
						mContext);
				LogUtils.trace(Log.ERROR, TAG, "获取列表出错了" + errr);
			}
			LogUtils.trace(Log.INFO, TAG, "-mToastUtils.isEnter--"
					+ mToastUtils.isEnter);
			if (mToastUtils.isEnter) {
				mToastUtils.cancel();
				return;
			}
			if (mData.size() >= 3) {
				if (mToastUtils != null) {
					mToastUtils.cancel();
				}
				dialog();
			} else {
				DRMDeviceBingDing mDRMDeviceBingDing = new DRMDeviceBingDing();
				mDRMDeviceBingDing.execute();
			}
		}
	}

	/**
	 * DRM自动绑定
	 * 
	 */
	private class DRMDeviceBingDing extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			int deviceBingDingState = mLibUDRM.UDRMAgentBindDevice(
					mUserNameStr, passWord, session.getMacPath(),
					Constant.DEVICENAME);
			return deviceBingDingState;
		}

		@Override
		protected void onPostExecute(Integer deviceBingDingState) {
			bingDingState = deviceBingDingState;
			if (deviceBingDingState == UdrmDefine.UDRM_ERROR_OK) {
				LogUtils.trace(Log.INFO, TAG, "自动绑定成功！");
				String resourceCode = intent.getStringExtra("resourceCode");
				if (TextUtils.isEmpty(resourceCode)) {
					return;
				}
				LogUtils.trace(Log.INFO, TAG, "-mToastUtils.isEnter--"
						+ mToastUtils.isEnter);
				if (mToastUtils.isEnter) {// 防止用户点击播放视频的按钮又点击了返回键仍然跳入到播放界面
					mToastUtils.cancel();
					return;
				}
				// 跳转到播放器页面
				enterPlayer(mContext, intent);
			} else {
				if (mToastUtils != null) {
					mToastUtils.cancel();
				}
				new DeviceBingDingError().getDebugError(mLibUDRM, mContext);
			}

		}

	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage("当前账号已经绑定超过3个设备，是否先解除其它绑定？");
		builder.setTitle("温馨提示");
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent in = new Intent(mContext, MainTabHostActivity.class);
				in.putExtra("isCancel", true);
				in.putExtra("index", 5);
				// 跳转到设备绑定页面
				UserCenterTabActivity.witch = 1;
				UserCenterTabActivity.mSelect = 1;
				mContext.startActivity(in);
			}
		});

		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
		}
		return false;
	}
}
