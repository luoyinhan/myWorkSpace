package com.coship.ott.transport.util;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.coship.ott.activity.R;
import com.coship.ott.service.UpdateService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.SystemAction;
import com.coship.ott.transport.dto.system.ClientVersionJson;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;

public class UpdateUtils {
	private static final String TAG = "UpdateUtils";
	public static final String UPDATE_APKNAME = "Android_pad.apk";
	public static final String UPDATE_SAVENAME = "UpdateAndroid_pad";
	public static boolean isUpdate = false; // 是否更新
	public static String downLoadUrl;
	public static String updatename;

	/**
	 * 检查是否有新版本并提示下载更新\
	 * 
	 * @param checkType
	 *            0：开机启动自动检查更新 1：用户手动检查更新
	 * */
	public static void checkForUpdate(final Context mContext,
			final int checkType) {
		new AsyncTask<String, Void, ClientVersionJson>() {
			@Override
			protected ClientVersionJson doInBackground(String... params) {
				return new SystemAction().queryClientVersion(
						InterfaceUrls.QUERY_CLIENT_VERSION, 5,
						getVerName(mContext), Session.getInstance()
								.getUserCode());
			}

			@Override
			protected void onPostExecute(ClientVersionJson result) {
				if (null == result || 0 != result.getRet()) {
					LogUtils.trace(Log.DEBUG, TAG, "queryClientVersion failed");
					return;
				}

				String version = result.getClientVersion();
				downLoadUrl = result.getDownloadUrl();
				int forceFlag = result.getForceFlag();
				if (TextUtils.isEmpty(version)
						|| TextUtils.isEmpty(downLoadUrl)) {
					if (1 == checkType) {
						UpdateService.isLoading = false;
						Toast.makeText(mContext, "已经是最新版本了！",
								Toast.LENGTH_SHORT).show();
					}
					return;
				}
				if (forceFlag == 1) {// 强制更新
					// 检测本地是否已经下载更新包
					checkUpdateFile(version, mContext);
				} else {
					String verName = getVerName(mContext);
					StringBuffer sb = new StringBuffer();
					sb.append(mContext.getString(R.string.current_version_is))
							.append(verName)
							.append(mContext.getString(R.string.need_update))
							.append("\n"
									+ mContext
											.getString(R.string.latest_version_name))
							.append(version)
							.append(mContext.getString(R.string.will_update));
					// 显示升级确认对话框
					createUpdateDialog(mContext, sb.toString(), version);
				}
			}
		}.execute();
	}

	private static void checkUpdateFile(String version, Context context) {
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, "没有检测到SDCard,无法更新!", Toast.LENGTH_LONG)
					.show();
			return;
		}
		String fileName = UpdateUtils.UPDATE_SAVENAME + version + ".apk";
		updatename = fileName;
		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);
		if (file.exists() && getUninatllApkInfo(context, updatename)) {
			updateConfirm(context, "已检测到SDCard中未安装“多屏看”升级包，请确认安装。", fileName);
		} else {
			if (file.exists()) {
				file.delete();
			}
			Intent service = new Intent(context, UpdateService.class);
			context.startService(service);
			isUpdate = true;
		}
	}

	private static void updateConfirm(final Context mContext,
			String messageText, final String fileName) {
		new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.update))
				.setCancelable(false).setMessage(messageText)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(), fileName)),
								"application/vnd.android.package-archive");
						mContext.startActivity(intent);
					}
				}).show();
	}

	public static boolean getUninatllApkInfo(Context context, String updatename) {
		boolean result = false;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(
					Environment.getExternalStorageDirectory() + "/"
							+ updatename, PackageManager.GET_ACTIVITIES);
			if (info != null) {
				LogUtils.trace(Log.DEBUG, TAG, "apk 未出现异常");
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		LogUtils.trace(Log.ERROR, TAG, " apk 出现异常");
		return result;
	}

	private static void createUpdateDialog(final Context mContext,
			String messageText, final String version) {
		new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.update))
				.setMessage(messageText)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						checkUpdateFile(version, mContext);
					}

				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();

	}

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.coship.ott.activity", 0).versionCode;
		} catch (NameNotFoundException e) {
			LogUtils.trace(Log.ERROR, TAG, e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.coship.ott.activity", 0).versionName;
		} catch (NameNotFoundException e) {
			LogUtils.trace(Log.ERROR, TAG, e.getMessage());
		}
		return verName;

	}

	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}

	/**
	 * 把两个数字的商转化为百分比
	 * */
	public static String numToBFB(int numInt1, int numInt2) {
		String bfb = "";
		float numFloat1 = (float) numInt1;
		float numFloat2 = (float) numInt2;
		float result = numFloat1 / numFloat2 * 100;
		bfb = String.valueOf(result);
		if (bfb.length() > 2) {
			bfb = bfb.substring(0, 2);
		}
		return "(" + bfb + "%)";
	}
}
