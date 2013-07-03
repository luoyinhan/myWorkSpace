package com.coship.ott.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.coship.ott.activity.R;
import com.coship.ott.transport.util.UpdateUtils;

/**
 * @ClassName: UpdateService (升级界面)
 * @author 905183
 * @date 2012-5-29 下午18:33:50
 */
public class UpdateService extends Service {
	private static final int NOTIFICATION_START_ID = 100;
	private static final int NOTIFICATION_FINISH_ID = 101;

	private Context mContext;
	// 是否正在下载
	public static boolean isLoading = false;
	private boolean mFlag = true;
	// 升级包文件大小
	private long fileTotalSize = -1;
	// 已下载文件大小
	private long count = 0;
	private NotificationManager mNmanager;
	private Notification mNotification;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mNotification.defaults = 0;
			mNotification.contentView.setProgressBar(R.id.pb, 100, msg.arg1,
					false);
			mNotification.contentView.setTextViewText(R.id.dowloadStatue, "进度"
					+ msg.arg1 + "%");
			mNmanager.notify(NOTIFICATION_START_ID, mNotification);

			if (count == fileTotalSize) {
				mNmanager.cancel(NOTIFICATION_START_ID);
				isLoading = false;
				Toast.makeText(mContext, R.string.download_finish,
						Toast.LENGTH_LONG).show();
				// 点击通知后的动作，到升级页面
				mNotification.contentView.setTextViewText(R.id.dowloadStatue,
						"下载完成，点击开始更新！");
				// 启动升级窗口
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(
						Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(),
								UpdateUtils.updatename)),
						"application/vnd.android.package-archive");
				mNotification.contentIntent = PendingIntent.getActivity(
						mContext, 0, intent, 0);
				mNmanager.notify(NOTIFICATION_FINISH_ID, mNotification);
				startActivity(intent);
				stopSelf();
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mContext = this;
	}

	@Override
	public void onStart(Intent intent, int startid) {
		if (!isLoading) {
			initNotification();
			new Thread() {
				public void run() {
					downLoadfile();
				};
			}.start();
			new CheckPrograssThread().start();
		}
	}

	private void initNotification() {
		// Notification管理器
		mNmanager = (NotificationManager) mContext
				.getSystemService(NOTIFICATION_SERVICE);
		// 后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间（不明白这个有什么用）
		mNotification = new Notification(R.drawable.top_logo, "多屏看应用更新",
				System.currentTimeMillis());
		mNotification.defaults = Notification.DEFAULT_ALL;
		mNotification.contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.saition_status_bar_content);
		mNmanager.notify(NOTIFICATION_START_ID, mNotification);
	}

	private void downLoadfile() {
		isLoading = true;
		count = 0;

		if (TextUtils.isEmpty(UpdateUtils.downLoadUrl)) {
			return;
		}

		mNmanager.cancel(NOTIFICATION_FINISH_ID);
		HttpClient client = new DefaultHttpClient();
		try {
			HttpGet get = new HttpGet(UpdateUtils.downLoadUrl);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			fileTotalSize = entity.getContentLength();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				File file = new File(Environment.getExternalStorageDirectory(),
						UpdateUtils.updatename);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				fileOutputStream = new FileOutputStream(file);

				byte[] buf = new byte[1024 * 3];
				int ch = -1;

				synchronized (this) {
					mFlag = true;
				}
				while (mFlag) {
					ch = is.read(buf);
					if (ch < 0) {
						break;
					}
					fileOutputStream.write(buf, 0, ch);
					count += ch;
				}
			}
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			is.close();
		} catch (ClientProtocolException e) {
		} catch (IllegalArgumentException e) {
		} catch (IOException e) {
		} finally {
			isLoading = false;
		}
	}

	class CheckPrograssThread extends Thread {
		@Override
		public void run() {
			try {
				while (isLoading) {
					Thread.sleep(1000);
					Message msg = handler.obtainMessage();
					msg.arg1 = (int) (count * 100 / fileTotalSize);
					msg.sendToTarget();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isLoading = false;
		mNmanager.cancel(NOTIFICATION_START_ID);
	}
}