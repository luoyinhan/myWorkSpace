package com.coship.ott.activity;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

import com.coship.ott.constant.Constant;
import com.coship.ott.service.BookNotifyService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.FavoriteAction;
import com.coship.ott.transport.action.SystemAction;
import com.coship.ott.transport.action.UserAction;
import com.coship.ott.transport.dto.Favourite;
import com.coship.ott.transport.dto.favourite.FavouriteAssetListJson;
import com.coship.ott.transport.dto.user.LoginJson;
import com.coship.ott.transport.dto.vod.AssetListInfo;
import com.coship.ott.transport.dto.vod.HomePageContent;
import com.coship.ott.transport.dto.vod.HomePageContentJson;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.transport.util.UpdateUtils;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.DbHelper;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.view.CustormImageView;

/**
 * 底部切换页面控件
 * 
 * @author Administrator
 */
public class MainTabHostActivity extends TabActivity {
	public static final String LOGIN_NAME = "USER_LOGIN_NAME";
	public static final String LOGIN_PWD = "USER_LOGIN_PWD";
	private static final String IS_AUTO_LOGIN = "USER_AUTO_LOGIN";
	private static final String LOGIN_FRAMES_PATH = "LOGIN_FRAMES_PATH";
	public Context mContext;
	private Handler handler = new Handler();
	private TabHost tabHost;
	private Boolean isloading = false;
	private boolean isCancel = false;
	protected ImageView down_bar;
	protected ImageView helpView;
	public static boolean mIsStarted = false;
	private int index = 0;
	private int witch = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加未捕获异常监听
		mContext = this;
		// AppManager.getAppManager().addActivity(this);
		LogUtils.trace(Log.INFO, "MainTabHostActivity", "app is Started!");
		// 判断网络
		if (!NetTransportUtil.testIfNetWorkReachable(mContext)) {
			UIUtility.showNTSettingDialog(mContext);
			return;
		}
		initData();
		isCancel = getIntent().getBooleanExtra("isCancel", false);
		index = getIntent().getIntExtra("index", 0);
		witch = getIntent().getIntExtra("witch", 0);
		if (isCancel) {
			setContentView(R.layout.main);
			down_bar = (ImageView) findViewById(R.id.down_bar);
			helpView = (ImageView) findViewById(R.id.helpView);
			setupView();
		} else {
			getHomePage();
		}
		mIsStarted = true;
	}

	/**
	 * 设置服务器地址
	 */
	private void initData() {
		MulScreenSharePerfance settings = MulScreenSharePerfance
				.getInstance(mContext);
		String url = (String) settings.getValue(
				SystemSettinngActivity.server_url, "String");
		if (TextUtils.isEmpty(url)) {
			url = Constant.SERVER_ADDR;
		}
		if (url.startsWith("http://")) {
			if (url.endsWith("/")) {
			} else {
				url = url + "/";
			}
		} else {
			if (url.endsWith("/")) {
				url = "http://" + url;
			} else {
				url = "http://" + url + "/";
			}
		}
		NetTransportUtil.setRequestUrl(url);
		Constant.SERVER_ADDR = url;
	}

	private void setupView() {
		if (down_bar == null || helpView == null) {
			down_bar = (ImageView) findViewById(R.id.down_bar);
			helpView = (ImageView) findViewById(R.id.helpView);
		}
		down_bar.setVisibility(View.VISIBLE);
		helpView.setVisibility(View.VISIBLE);
		tabHost = this.getTabHost();
		// 首页
		tabHost.addTab(tabHost.newTabSpec("Recommend")
				.setIndicator(composeLayout())
				.setContent(new Intent(this, RecommendActivity.class)));
		// 直播
		tabHost.addTab(tabHost.newTabSpec("Live").setIndicator(composeLayout())
				.setContent(new Intent(this, LiveActivity.class)));
		// 点播
		tabHost.addTab(tabHost.newTabSpec("Vod").setIndicator(composeLayout())
				.setContent(new Intent(this, VodActivity.class)));
		// 专题
		tabHost.addTab(tabHost.newTabSpec("RankActivity")
				.setIndicator(composeLayout())
				.setContent(new Intent(this, RankActivity.class)));
		// 搜索
		tabHost.addTab(tabHost.newTabSpec("Search")
				.setIndicator(composeLayout())
				.setContent(new Intent(this, SearchActivity.class)));

		// 用户中心
		tabHost.addTab(tabHost.newTabSpec("UserZone")
				.setIndicator(composeLayout())
				.setContent(new Intent(this, UserCenterTabActivity.class)));

		int firstNowBgId = R.drawable.nav0;
		final TabWidget tabWidget = tabHost.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			// 设置Tab的背景
			tabWidget.getChildAt(i).setBackgroundDrawable(
					getResources().getDrawable(firstNowBgId + i));
		}

		tabHost.setCurrentTab(index);
		tabWidget.getChildAt(index).setBackgroundDrawable(
				getResources().getDrawable(R.drawable.navcurr0 + index));
		// 设置Tab变换时的监听事件
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			int firstFocusBgId = R.drawable.navcurr0;
			int firstNowBgId = R.drawable.nav0;

			@Override
			public void onTabChanged(String tabId) {
				// 当点击tab选项卡的时候，更改当前的背景
				for (int i = 0; i < tabWidget.getChildCount(); i++) {
					View v = tabWidget.getChildAt(i);
					if (tabHost.getCurrentTab() == i) {
						v.setBackgroundDrawable(getResources().getDrawable(
								firstFocusBgId + i));
					} else {
						// 这里最后需要和上面的设置保持一致,也可以用图片作为背景最好
						v.setBackgroundDrawable(getResources().getDrawable(
								firstNowBgId + i));
					}
				}
			}
		});

		// 帮助键响应事件
		ImageView helpView = (ImageView) this.findViewById(R.id.helpView);
		helpView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainTabHostActivity.this,
						HelpActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * 这个设置Tab标签本身的布局
	 */
	public View composeLayout() {
		ImageView tabView = new ImageView(this);
		tabView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		return tabView;
	}

	private void autoLogin() {
		boolean isAutoLogin = (Boolean) MulScreenSharePerfance.getInstance(
				mContext).getValue(IS_AUTO_LOGIN, "Boolean");
		if (isAutoLogin) {
			// 获取登录信息
			try {
				String loginName = (String) MulScreenSharePerfance.getInstance(
						mContext).getValue(LOGIN_NAME, "String");
				String loginPWD = (String) MulScreenSharePerfance.getInstance(
						mContext).getValue(LOGIN_PWD, "String");
				login(loginName, loginPWD);
			} catch (Exception e) {
			}
		} else {
		}
	}

	private void login(final String loginName, final String loginPwd) {
		// 登录
		new AsyncTask<Void, Void, LoginJson>() {
			@Override
			protected LoginJson doInBackground(Void... params) {
				return new UserAction().login(InterfaceUrls.LOGIN, loginName,
						loginPwd);
			}

			@Override
			protected void onPostExecute(LoginJson result) {
				if (null != result && 0 == result.getRet()) {
					Session session = Session.getInstance();
					session.setLogined(true);
					session.setUserCode(result.getUserCode());
					session.setToken(result.getToken());// 用户令牌
					MulScreenSharePerfance.getInstance(mContext).putValue(
							"UserCode", result.getUserCode());
					session.setUserName(loginName);
					session.setPassWord(loginPwd);
					session.setMacPath(getLocalMacAddress());
					getUserFavourite();
					Intent intent = new Intent();
					intent.setAction("LOGIN_SUCCESS");
					sendBroadcast(intent);
					Intent service = new Intent(mContext,
							BookNotifyService.class);
					mContext.startService(service);
				}
			};
		}.execute();
	};

	/**
	 * 查询当前用户的所有的收藏记录
	 * */
	private void getUserFavourite() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			return;
		}
		new AsyncTask<Void, Void, FavouriteAssetListJson>() {
			@Override
			protected FavouriteAssetListJson doInBackground(Void... params) {
				return new FavoriteAction().getFavorite(
						InterfaceUrls.GET_FAVOURITE, session.getUserCode(),
						session.getUserName());
			};

			@Override
			protected void onPostExecute(FavouriteAssetListJson result) {
				if (null != result && 0 == result.getRet()) {
					// 当前用户的所有收藏记录
					ArrayList<AssetListInfo> userFavourite = result
							.getFavorite();
					if (null == userFavourite) {
						return;
					}
					ArrayList<Favourite> favourites = new ArrayList<Favourite>();
					Favourite favourite;
					for (AssetListInfo info : userFavourite) {
						favourite = new Favourite();
						favourite.setResourceCode(info.getResourceCode());
						favourite.setUserCode(session.getUserCode());
						favourites.add(favourite);
					}
					DbHelper dbhelper = new DbHelper(mContext);
					dbhelper.deleteAllData();
					dbhelper.insertAllData(favourites);
					dbhelper.closeConn();
				}
			};
		}.execute();
	}

	/**
	 * 获取设备mac地址
	 * 
	 * @return mac地址
	 */
	public String getLocalMacAddress() {
		String macaddress = "";
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		WifiInfo info = wifi.getConnectionInfo();
		String Mac = info.getMacAddress();
		if (Mac != null && !"".equals(Mac)) {
			macaddress = Mac.replace(":", "");
		}
		return macaddress;
	}

	private void getHomePage() {
		final MulScreenSharePerfance sp = MulScreenSharePerfance
				.getInstance(mContext);
		final int homePageVersion = (Integer) sp.getValue("HOMEPAGE_VERSION",
				"Integer");
		final Session session = Session.getInstance();
		final CustormImageView imageview = new CustormImageView(mContext);
		new AsyncTask<Void, Void, HomePageContentJson>() {
			@Override
			protected void onPreExecute() {
				isloading = true;
			}

			@Override
			protected HomePageContentJson doInBackground(Void... params) {
				return new SystemAction().getHomePageConten(
						InterfaceUrls.GET_HOMEPAGE_CONTENT, homePageVersion,
						session.getUserCode());
			}

			@Override
			protected void onPostExecute(HomePageContentJson result) {
				setContentView(R.layout.main);
				down_bar = (ImageView) findViewById(R.id.down_bar);
				helpView = (ImageView) findViewById(R.id.helpView);
				down_bar.setVisibility(View.INVISIBLE);
				helpView.setVisibility(View.INVISIBLE);
				RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				imageview.setLayoutParams(layoutParams);
				mainLayout.addView(imageview);

				if (null != result && 0 == result.getRet()) {
					HomePageContent mydata = result.getData();
					if (null != mydata
							&& !TextUtils.isEmpty(mydata.getPicURL())) {
						sp.putValue("HOMEPAGE_VERSION", result.getData()
								.getHomePageVersion());
						sp.putValue("HOMEPAGE_URL", result.getData()
								.getPicURL());
						// 设置启动页面
						String login_frames_path = result.getData().getPicURL();
						if (!TextUtils.isEmpty(login_frames_path)) {
							imageview.setImageHttpUrl(login_frames_path);
							MulScreenSharePerfance.getInstance(mContext)
									.putValue(LOGIN_FRAMES_PATH,
											result.getData().getPicURL());
						}
					} else {
						String login_frames_path = (String) MulScreenSharePerfance
								.getInstance(mContext).getValue(
										LOGIN_FRAMES_PATH, "String");

						if (!TextUtils.isEmpty(login_frames_path)) {

							imageview.setImageHttpUrl(login_frames_path);
						} else {
							imageview
									.setBackgroundResource(R.drawable.home_page);

						}
					}
				} else {
					// 设置启动页面
					imageview.setBackgroundResource(R.drawable.home_page);
				}
				isloading = false;
			}
		}.execute();
		// 监听是否在加载
		timerLoading(imageview);
	}

	private void timerLoading(final CustormImageView imageview) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!isloading) {
					// 设置3秒后消失
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							imageview.setVisibility(View.GONE);
							Intent intent = null;
							String newVersion = UpdateUtils
									.getVerName(mContext).substring(1);
							String oldVersion = getFirstUseState(mContext);
							if (oldVersion.equals("")
									|| (Integer.parseInt(newVersion.replace(
											".", "")) > Integer
											.parseInt(oldVersion.substring(1)
													.replace(".", "")))) { // 检测版本是否显示多屏看引导页面
								intent = new Intent(mContext,
										FirstUseActivity.class);
								mContext.startActivity(intent);
								MainTabHostActivity.this.finish();
							}
							// 初始化
							setupView();
							// 自动登录
							autoLogin();
							// 发送进入应用成功广播
							Intent startedSuc = new Intent();
							startedSuc.setAction("STARTED_SUCCESS");
							sendBroadcast(startedSuc);
							// 检测是否有更新
							UpdateUtils.checkForUpdate(mContext, 0);
						}
					}, 2000);
				} else {
					timerLoading(imageview);
				}
			}
		}, 100);
	}

	/***************************
	 * Function : getFirstUseState Description : 获取 ”是否为第一次使用多屏看“
	 ****************************/
	public static String getFirstUseState(Context context) {
		String version = (String) MulScreenSharePerfance.getInstance(context)
				.getValue("newVersion", "String");
		return version;
	}

	/***************************
	 * Function : setFirstUseState Description : 设置 ”是否为第一次使用多屏看“
	 ****************************/
	public static void setFirstUseState(Context context, String newVersion) {
		MulScreenSharePerfance sp = MulScreenSharePerfance.getInstance(context);
		sp.putValue("newVersion", newVersion);
	}
}