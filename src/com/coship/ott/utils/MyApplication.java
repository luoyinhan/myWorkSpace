package com.coship.ott.utils;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.coship.ott.activity.MainTabHostActivity;
import com.coship.ott.constant.Constant;
import com.coship.ott.fragment.UserCenterFragment;
import com.coship.ott.service.BookNotifyService;
import com.coship.ott.transport.dto.PlayItem;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	// 保存播放串的健全id及对应的productCode
	public static HashMap<String, PlayItem> playItem;
	private AlertDialog aDialog;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.trace(Log.DEBUG, TAG, " == 启动捕获未知异常监听，并记录日志 == ");
		// 捕获未知异常监听，并记录日志
		LogUtils.processGlobalException(this);
		playItem = new HashMap<String, PlayItem>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtils.delLogs();
					LogUtils.trace(Log.DEBUG, TAG, "清除过期的日志文件");
				} catch (Exception e) {
					LogUtils.trace(Log.ERROR, TAG,
							"清除过期的日志文件出错:" + e.getMessage());
				}
			}
		}).start();
		initImageLoader(getApplicationContext());

	}

	/**
	 * 初始化图片加载器
	 */
	private void initImageLoader(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnFail(getRandomResource())
				.showImageForEmptyUri(getRandomResource()).cacheInMemory()
				.cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(options).build();
		ImageLoader.getInstance().init(config);
	}

	public HashMap<String, PlayItem> getPlayItem() {
		return playItem;
	}

	public void setPlayItem(HashMap<String, PlayItem> playItem) {
		if (playItem == null) {// 当清空时则重新实例化map
			this.playItem = new HashMap<String, PlayItem>();
		}
	}

	public void showExitDialog(String info) {
		if (aDialog != null && aDialog.isShowing()) {
			return;
		}
		String infotips = "有相同账号在其他终端登录";
		if (!TextUtils.isEmpty(info)) {
			infotips = info;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("提示")
				.setCancelable(false)
				.setMessage(infotips)
				.setPositiveButton("退出登录",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									// 用户注销
									Session session = Session.getInstance();
									boolean isCancel = true;
									session.setLogined(false);
									session.setUserCode("");
									session.setToken("");
									session.setUserName("");
									session.setPassWord("");
									MulScreenSharePerfance.getInstance(
											getApplicationContext()).putValue(
											"UserCode", "");
									Intent service = new Intent(
											getApplicationContext(),
											BookNotifyService.class);
									getApplicationContext()
											.stopService(service);
									UserCenterFragment.mBindDeviceNo = "";
									UserCenterFragment.mName = "";
									// 清空Activity堆栈
									AppManager.getAppManager()
											.finishAllActivity();
									Intent in = new Intent(
											getApplicationContext(),
											MainTabHostActivity.class);
									in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									in.putExtra("isCancel", isCancel);
									getApplicationContext().startActivity(in);
								} catch (Exception e) {
									Log.e(TAG,
											"Close App wrong:" + e.toString());
								}
							}
						});
		aDialog = builder.create();
		aDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		aDialog.show();
	}

	// 获得随机默认图片
	public int getRandomResource() {
		int randomIndex = 0;
		randomIndex = (int) (Math.random() * 10);
		// if (this.getWidth() < this.getHeight()) {
		return Constant.vPics[randomIndex];
		// } else {
		// return Constant.hPics[randomIndex];
		// }
	}
}