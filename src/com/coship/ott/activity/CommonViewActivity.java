package com.coship.ott.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.fragment.UserCenterFragment;
import com.coship.ott.service.BookNotifyService;
import com.coship.ott.service.UpdateService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.NoticeAction;
import com.coship.ott.transport.action.SystemAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.notice.NoticeCountJson;
import com.coship.ott.transport.dto.system.SystemTimeJson;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.DialogUtils;
import com.coship.ott.utils.MyApplication;
import com.coship.ott.utils.Session;

/**
 * 头部，topBar按钮事件处理。
 * */
public class CommonViewActivity extends Activity {
	public Context mContext;
	private boolean isCancel = false;
	protected ImageView notice;
	private TextView noticeCount;
	protected RelativeLayout noticeFull;
	private String lastTime;
	public static int mCount = 0;// 通知的条数

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("STARTED_SUCCESS")) {
				// initNotice();
			}
			// 初始化登录图标
			initLoginSwitch();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topbar);
		// 添加未捕获异常监听
		mContext = this;
		// 注册消息接收处理
		IntentFilter filter = new IntentFilter();
		filter.addAction("LOGIN_SUCCESS");
		filter.addAction("LOGOUT_SUCCESS");
		filter.addAction("STARTED_SUCCESS");// 登录成功
		registerReceiver(receiver, filter);
		lastTime = (String) MulScreenSharePerfance.getInstance(mContext)
				.getValue("lastLoginTime", "String");
	}

	// 获得公告的数量
	private void initNotice() {
		new AsyncTask<Void, Void, NoticeCountJson>() {
			@Override
			protected NoticeCountJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new NoticeAction().queryNewNotice(
						InterfaceUrls.QUERY_NEWNOTICE, lastTime);
			}

			@Override
			protected void onPostExecute(NoticeCountJson result) {
				boolean bol = BaseJsonBean.checkResult(mContext, result);
				if (bol) {
					// 初始化公告数量图标
					mCount = result.getCount();
					if (mCount > 0) {
						noticeFull.setVisibility(View.VISIBLE);
						notice.setVisibility(View.INVISIBLE);
						noticeCount.setText("" + mCount);
					}
				}
				// // 存入本次打开应用時服務器的时间
				getSystemTime();
			}
		}.execute();
	}

	/**
	 * 从服务器获取时间,并存入本地
	 */
	private void getSystemTime() { // 获取系统时间
		new AsyncTask<Void, Void, SystemTimeJson>() {
			@Override
			protected SystemTimeJson doInBackground(Void... params) {
				return new SystemAction()
						.getSystemTimeInfo(InterfaceUrls.GET_SYSTEM_TIME);
			}

			@Override
			protected void onPostExecute(SystemTimeJson result) {
				if (null != result && 0 == result.getRet()) {
					MulScreenSharePerfance.getInstance(mContext).putValue(
							"lastLoginTime", result.getDateTime());
				} else {
				}
			}
		}.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 初始化登录图标
		initLoginSwitch();
		// 只能放在这里才会改变UI
		noticeCount = (TextView) findViewById(R.id.notice_count);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		notice = (ImageView) findViewById(R.id.notice);
		// if (mCount > 0) {
		// noticeFull.setVisibility(View.VISIBLE);
		// notice.setVisibility(View.INVISIBLE);
		// noticeCount.setText("" + mCount);
		// } else {
		// notice.setVisibility(View.VISIBLE);
		// noticeFull.setVisibility(View.INVISIBLE);
		// }
	}

	// 响应
	public void noticeSwitch(View v) {
		// mCount = 0;
		// notice.setVisibility(View.VISIBLE);
		// noticeFull.setVisibility(View.INVISIBLE);
		// noticeCount.setText("");
		// 跳转到公告页面
		Intent intent = new Intent(mContext, NoticeActivity.class);
		startActivity(intent);
	}

	public void initLoginSwitch() {
		Session session = Session.getInstance();
		if (session.isLogined()) {
			findViewById(R.id.logout).setVisibility(View.VISIBLE);
			findViewById(R.id.login).setVisibility(View.GONE);
		} else {
			findViewById(R.id.login).setVisibility(View.VISIBLE);
			findViewById(R.id.logout).setVisibility(View.GONE);
		}
	}

	/**
	 * 登录与注销
	 * */
	public void switchLogin(View v) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			// 登录
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
		} else {
			// 注销
			DialogUtils.alertDialog("注销", "退出后不会删除任何历史数据，下次登录依然可以使用本账号！",
					mContext, DialogClickListener());
		}
	}

	private DialogInterface.OnClickListener DialogClickListener() {
		final Session session = Session.getInstance();
		return new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				isCancel = true;
				session.setLogined(false);
				session.setUserCode("");
				session.setUserName("");
				session.setPassWord("");
				session.setToken("");
				MyApplication app = (MyApplication) getApplicationContext();
				app.setPlayItem(null);// 清空上次用户存储的playData
				MulScreenSharePerfance.getInstance(mContext).putValue(
						"UserCode", "");
				Intent service = new Intent(mContext, BookNotifyService.class);
				mContext.stopService(service);
				UserCenterFragment.mBindDeviceNo = "";
				UserCenterFragment.mName = "";
				Intent in = new Intent(mContext, MainTabHostActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				in.putExtra("isCancel", isCancel);
				mContext.startActivity(in);
				Toast.makeText(mContext, getString(R.string.logout_success),
						Toast.LENGTH_LONG).show();
				findViewById(R.id.login).setVisibility(View.VISIBLE);
				findViewById(R.id.logout).setVisibility(View.GONE);
			}
		};

	}

	public CommonViewActivity() {
		super();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("确认退出吗？")
				.setIcon(null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (UpdateService.isLoading) {// 正在下载更新则在后台继续下载
							finish();
						} else {
							MulScreenSharePerfance.getInstance(mContext)
									.putValue("UserCode", "");
							MyApplication app = (MyApplication) getApplicationContext();
							app.setPlayItem(null);// 清空上次用户存储的playData
							// 杀掉本进程
							// int pid = android.os.Process.myPid();
							// android.os.Process.killProcess(pid);
							// 完全关闭应用
							AppManager.getAppManager().AppExit(mContext);
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}
}