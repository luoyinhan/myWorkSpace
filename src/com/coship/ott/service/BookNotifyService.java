package com.coship.ott.service;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.coship.ott.activity.ProgramParticularActivity;
import com.coship.ott.activity.R;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.BookAction;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.book.Book;
import com.coship.ott.transport.dto.book.BooksJson;
import com.coship.ott.transport.dto.user.CheckLoginJson;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.MyApplication;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.Utility;

public class BookNotifyService extends Service {
	private static final String TAG = "BookNotifyService";

	private Context mContext;
	private static final int MESSAGE_CHECK_WILL_START = 1;

	protected static final int MESSAGE_CHECK_USER_START = 2;

	private int NOTIFICATION_START_ID = 0;
	private String userCode;
	public static ArrayList<Book> books = new ArrayList<Book>();
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_CHECK_WILL_START:
				getWillPlay(books);
				break;
			case MESSAGE_CHECK_USER_START:
				checkUser();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		mContext = this;
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.trace(Log.DEBUG, TAG, "system boot complete!");
		mContext = this;
		// 设置用户智能卡号
		userCode = (String) MulScreenSharePerfance.getInstance(mContext)
				.getValue("UserCode", "String");
		Session session = Session.getInstance();
		if (session.isLogined()) {
			session.setUserCode(userCode);
			checkUser();
		}
		queryBook();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 获取用户预约内容
	 * */
	private void queryBook() {
		// 取媒资信息
		new AsyncTask<String, Void, BooksJson>() {
			@Override
			protected BooksJson doInBackground(String... params) {
				if (TextUtils.isEmpty(userCode)) {
					return null;
				}
				return new BookAction().queryBook(InterfaceUrls.QUERY_BOOK,
						userCode);
			};

			@Override
			protected void onPostExecute(BooksJson result) {
				if (null != result && 0 == result.getRet()) {
					books = result.getBooks();
					getWillPlay(books);
				}
			}
		}.execute();
	}

	private void getWillPlay(ArrayList<Book> books) {
		long nowTimeSeconds = System.currentTimeMillis() / 1000;
		long programStartSeconds = 0l;
		ArrayList<Book> delList = new ArrayList<Book>();
		Log.v(TAG, "BookNotice num :" + books.size());
		for (Book book : books) {
			Log.v(TAG,
					"BookName: " + book.getChannelName() + "-"
							+ book.getBeginTime());
			programStartSeconds = Utility
					.dealTimeToSeconds(book.getBeginTime());
			if (0 < programStartSeconds - nowTimeSeconds
					&& programStartSeconds - nowTimeSeconds < 60) {
				String channelName = book.getChannelName();
				String programName = book.getEventName();
				String startTime = book.getBeginTime();
				String programId = book.getProgramId();
				sendNotification(programId, channelName, programName, startTime);
				delList.add(book);
			}
		}
		books.removeAll(delList);
		mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_WILL_START, 60 * 1000);
	}

	private void sendNotification(String programId, String channelName,
			String programName, String startTime) {
		// Notification管理器
		NotificationManager mNmanager = (NotificationManager) mContext
				.getSystemService(NOTIFICATION_SERVICE);
		// 后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间（不明白这个有什么用）
		Notification mNotification = new Notification(R.drawable.book_notify,
				getString(R.string.notify_title), System.currentTimeMillis());
		mNotification.defaults = Notification.DEFAULT_SOUND;
		mNotification.contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.book_notification_content);

		try {
			startTime = startTime.substring(11);
		} catch (Exception e) {
		}
		if (channelName == null) {
			channelName = "";
		}
		if (programName == null) {
			programName = "";
		}
		mNotification.contentView.setTextViewText(R.id.bookNotify, "您预定"
				+ channelName + "的节目" + programName + "将于" + startTime
				+ "开始，请留意收看！");
		// 点击通知后的动作，到播放界面
		mNotification.contentIntent = PendingIntent.getActivity(mContext,
				NOTIFICATION_START_ID, getPlayIntent(programId), 0);

		mNmanager.notify(NOTIFICATION_START_ID++, mNotification);
	}

	/**
	 * 跳转到播放页面并开始播放
	 * 
	 * @param assetId
	 *            当前播放媒资的Id
	 * @param playType
	 *            当前播放内容是直播还是点播
	 * */
	public Intent getPlayIntent(String programId) {
		Boolean isFromNotifyService = true;
		Intent intent = new Intent(this, ProgramParticularActivity.class);
		intent.putExtra("programId", programId);
		intent.putExtra("isFromNotifyService", isFromNotifyService);
		return intent;
	}

	/**
	 * 检测是否有同账号登录
	 * */
	private void checkUser() {
		// 取媒资信息
		new AsyncTask<String, Void, CheckLoginJson>() {
			@Override
			protected CheckLoginJson doInBackground(String... params) {
				if (TextUtils.isEmpty(Session.getInstance().getUserName())) {
					return null;
				}
				return new UserCenterAction().checkUer(
						InterfaceUrls.GET_HEARTBEAT, Session.getInstance()
								.getUserName(), Session.getInstance()
								.getToken());
			};

			@Override
			protected void onPostExecute(CheckLoginJson result) {
				if (null != result && 0 == result.getRet()) {
					String optType = result.getOptType();
					if (optType.equals("1")) {
						// 提示用户退出应用
						MyApplication app = (MyApplication) getApplication();
						app.showExitDialog(result.getOptContent());
					}
				}
				// 5分钟查询一次
				mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_USER_START,
						1 * 60 * 1000);
			}
		}.execute();
	}
}
