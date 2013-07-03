package com.coship.ott.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.coship.ott.activity.LoginActivity;
import com.coship.ott.activity.MainTabHostActivity;
import com.coship.ott.activity.R;
import com.coship.ott.activity.UserCenterTabActivity;

public class UIUtility {
	/**
	 * 提示未登录
	 * */
	public static void showDialog(final Context context) {
		final Context mContext = context;
		new AlertDialog.Builder(mContext)
				.setTitle("提示")
				.setMessage("您还没有登录！")
				.setIcon(null)
				.setPositiveButton("登录", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(mContext,
								LoginActivity.class);
						mContext.startActivity(intent);
						UserCenterTabActivity.isComing =0; 
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (UserCenterTabActivity.isComing == 1) {
							boolean isCancel = true;
							Intent intent = new Intent(context,
									MainTabHostActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra("isCancel", isCancel);
							context.startActivity(intent);
							UserCenterTabActivity.isComing =0;
						}
					}
				}).show();

	}

	/**
	 * 检测网络是否存在
	 */
	public static void showNTSettingDialog(final Context context) {
		AlertDialog.Builder builders = new AlertDialog.Builder(context);
		builders.setTitle(R.string.notify_title);
		builders.setMessage(R.string.network_unconnect);
		builders.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 进入无线网络配置界面
				context.startActivity(new Intent(
						Settings.ACTION_WIRELESS_SETTINGS));
				// 关闭当前activity
				android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程
			}
		});
		builders.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 关闭当前activity
				android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程
			}
		});
		builders.show();
	}
}