package com.coship.ott.activity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.coship.ott.service.BookNotifyService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.FavoriteAction;
import com.coship.ott.transport.action.UserAction;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.Favourite;
import com.coship.ott.transport.dto.UserInfoJson;
import com.coship.ott.transport.dto.favourite.FavouriteAssetListJson;
import com.coship.ott.transport.dto.user.LoginJson;
import com.coship.ott.transport.dto.vod.AssetListInfo;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.utils.DbHelper;
import com.coship.ott.utils.Session;

public class LoginActivity extends Activity implements OnClickListener,
		OnEditorActionListener, OnCheckedChangeListener {
	private static final String LOGIN_NAME = "USER_LOGIN_NAME";
	private static final String LOGIN_PWD = "USER_LOGIN_PWD";
	private static final String IS_AUTO_LOGIN = "USER_AUTO_LOGIN";
	private static final String IS_REMEBER_LOGINMSG = "USER_REMBER_LOGIN_MSG";
	public static int errortimes = 0;

	private Context mContext;
	// CA卡号输入框
	private EditText loginNameEdit;
	// 用户密码输入框
	private EditText loginPWDEdit;
	// 下次自动登录选择框
	private CheckBox autoLogin;
	private MulScreenSharePerfance spInstance;
	// 记住登录信息选择框
	private CheckBox remLoginMsg;
	private TextView mLoginPWDRetake;
	private Button account, email, phone;
	private TextView loginName;
	private ImageView exist_bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mContext = this;
		setupView();
	}

	private void setupView() {
		exist_bt = (ImageView) this.findViewById(R.id.exist_bt);
		account = (Button) this.findViewById(R.id.account);
		email = (Button) this.findViewById(R.id.email);
		phone = (Button) this.findViewById(R.id.phone);
		account.setOnClickListener(this);
		exist_bt.setOnClickListener(this);
		email.setOnClickListener(this);
		phone.setOnClickListener(this);
		loginName = (TextView) findViewById(R.id.loginName);
		email.setBackgroundResource(R.drawable.bottom_line);
		phone.setBackgroundResource(R.drawable.bottom_line);
		account.setBackgroundResource(R.drawable.btn_focus_line);
		// 退出按钮
		this.findViewById(R.id.helpExitBtn).setOnClickListener(this);
		// 注册
		this.findViewById(R.id.account_register).setOnClickListener(this);
		// CA卡号输入
		loginNameEdit = (EditText) this.findViewById(R.id.loginNameEdit);
		loginNameEdit.setOnEditorActionListener(this);
		// 密码输入
		loginPWDEdit = (EditText) this.findViewById(R.id.loginPWDEdit);
		loginPWDEdit.setOnEditorActionListener(this);
		// 下次自动登录
		autoLogin = (CheckBox) this.findViewById(R.id.checkAutoLogin);
		autoLogin.setOnCheckedChangeListener(this);
		// 记住登录信息
		remLoginMsg = (CheckBox) this.findViewById(R.id.checkRemLoginMsg);
		remLoginMsg.setOnCheckedChangeListener(this);
		mLoginPWDRetake = (TextView) this.findViewById(R.id.loginPWDRetake);
		mLoginPWDRetake.setOnClickListener(this);
		// 登录提交按钮
		this.findViewById(R.id.helpSubmitButton).setOnClickListener(this);
		// 初始化自动登录选择框状态
		boolean isAutoLogin = (Boolean) MulScreenSharePerfance.getInstance(
				mContext).getValue(IS_AUTO_LOGIN, "Boolean");
		// 初始化用户名
		String loginName = (String) MulScreenSharePerfance
				.getInstance(mContext).getValue(LOGIN_NAME, "String");
		loginNameEdit.setText(loginName + "");
		if (isAutoLogin) { // 自动登录，填充输入框内容
			initEditText();
			autoLogin.setChecked(true);
			remLoginMsg.setChecked(true);
			remLoginMsg.setEnabled(false);
		} else {
			autoLogin.setChecked(false);
			remLoginMsg.setEnabled(true);
			// 初始化记住登录信息框状态
			boolean isRemeberLoginMsg = (Boolean) MulScreenSharePerfance
					.getInstance(mContext).getValue(IS_REMEBER_LOGINMSG,
							"Boolean");
			if (isRemeberLoginMsg) {
				initEditText();
				remLoginMsg.setChecked(true);
			} else {
				remLoginMsg.setChecked(false);
			}
		}
	}

	private void initEditText() {
		// 初始化输入框内容
		try {
			String loginPWD = (String) MulScreenSharePerfance.getInstance(
					mContext).getValue(LOGIN_PWD, "String");
			loginPWDEdit.setText(loginPWD + "");
		} catch (Exception e) {
		}
	}

	private void login() {
		// 获取登录用户名
		final String loginName = loginNameEdit.getText().toString().trim();
		// 获取登录密码
		final String loginPwd = loginPWDEdit.getText().toString().trim();
		if (loginName.equals("")) {
			Toast.makeText(mContext, getString(R.string.login_namenull),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (loginPwd.equals("")) {
			Toast.makeText(mContext, getString(R.string.login_pwdnull),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 获得是否记住用户信息
		boolean isRemeberLoginMsg = (Boolean) MulScreenSharePerfance
				.getInstance(mContext).getValue(IS_REMEBER_LOGINMSG, "Boolean");
		if (isRemeberLoginMsg) {
			MulScreenSharePerfance.getInstance(mContext).putValue(LOGIN_PWD,
					loginPWDEdit.getText().toString().trim());
		}

		// 登录
		new AsyncTask<Void, Void, LoginJson>() {
			@Override
			protected LoginJson doInBackground(Void... params) {
				return new UserAction().login(InterfaceUrls.LOGIN, loginName,
						loginPwd);
			}

			@Override
			protected void onPostExecute(LoginJson result) {
				if (null != result) {
					if (0 == result.getRet()) {
						Session session = Session.getInstance();
						session.setLogined(true);
						session.setUserCode(result.getUserCode());
						session.setUserName(loginName);
						session.setToken(result.getToken());// 用户令牌
						MulScreenSharePerfance.getInstance(mContext).putValue(
								"UserCode", result.getUserCode());
						session.setMacPath(getLocalMacAddress());
						session.setPassWord(loginPwd);
						getUserFavourite();// 获得用户所有的收藏记录并写入本地数据库
						getUserInfoTask();// 写入用户信息到Session
						Intent intent = new Intent();
						intent.setAction("LOGIN_SUCCESS");
						sendBroadcast(intent);// 发送广播更新用户状态
						Intent service = new Intent(mContext,
								BookNotifyService.class);
						mContext.startService(service);
						Toast.makeText(mContext,
								getString(R.string.login_success),
								Toast.LENGTH_SHORT).show();
						// 总是记住用户名
						MulScreenSharePerfance.getInstance(mContext).putValue(
								LOGIN_NAME,
								loginNameEdit.getText().toString().trim());
						finish();
					} else if (3103 == result.getRet()) {// 密码错误
						errortimes++;// 记住错误的次数
						String tips = "您的用戶密码验证失败.";
						if (result != null) {
							tips = result.getRetInfo();
						}
						if (errortimes >= 5) {
							tips = "您已连续5次登录密码输入错误，请15分钟后再尝试";
							// 记住当前时间点在SharePerference中
							long errorTime = System.currentTimeMillis() / 1000;// 获取当前秒
							MulScreenSharePerfance.getInstance(mContext)
									.putValue("LoginErrorTime", errorTime);
						}
						Toast.makeText(mContext, tips, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(mContext, result.getRetInfo(),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(mContext, getString(R.string.login_failed),
							Toast.LENGTH_SHORT).show();
				}
			};
		}.execute();
	};

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
		if (TextUtils.isEmpty(macaddress)) {
			macaddress = getMac();
		}

		return macaddress;
	}

	String getMac() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.helpExitBtn:
			onBackPressed();
			break;
		case R.id.exist_bt:
			onBackPressed();
			break;
		case R.id.helpSubmitButton:
			long errorTime = (Long) MulScreenSharePerfance
					.getInstance(mContext).getValue("LoginErrorTime", "Long");
			long currTime = System.currentTimeMillis() / 1000;
			// 当前时间没有超过15分钟则提示
			if (errorTime > 0 && errorTime + 15 * 60 > currTime) {
				Toast.makeText(mContext, "您已连续5次登录密码输入错误，请15分钟后再尝试",
						Toast.LENGTH_SHORT).show();
				return;
			}
			login();
			break;
		case R.id.account_register:
			Intent intent = new Intent(LoginActivity.this,
					AccountRegisterActivity.class);
			LoginActivity.this.startActivity(intent);
			LoginActivity.this.finish();
			break;
		case R.id.loginPWDRetake:
			Intent in = new Intent(LoginActivity.this,
					FoundPassWordActivity.class);
			LoginActivity.this.startActivity(in);
			LoginActivity.this.finish();
			break;
		case R.id.account:
			email.setBackgroundResource(R.drawable.bottom_line);
			phone.setBackgroundResource(R.drawable.bottom_line);
			account.setBackgroundResource(R.drawable.btn_focus_line);
			loginName.setText(R.string.login_name);
			break;
		case R.id.email:
			account.setBackgroundResource(R.drawable.bottom_line);
			phone.setBackgroundResource(R.drawable.bottom_line);
			email.setBackgroundResource(R.drawable.btn_focus_line);
			loginName.setText(R.string.emil_num);
			break;
		case R.id.phone:
			account.setBackgroundResource(R.drawable.bottom_line);
			email.setBackgroundResource(R.drawable.bottom_line);
			phone.setBackgroundResource(R.drawable.btn_focus_line);
			loginName.setText(R.string.phone_num);
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		switch (id) {
		case R.id.checkAutoLogin:
			if (isChecked) {
				// 自动登录
				MulScreenSharePerfance.getInstance(mContext).putValue(
						IS_AUTO_LOGIN, true);
				// 记住登录信息
				remLoginMsg.setChecked(true);
				remLoginMsg.setEnabled(false);
				MulScreenSharePerfance.getInstance(mContext).putValue(
						IS_REMEBER_LOGINMSG, true);
			} else {
				MulScreenSharePerfance.getInstance(mContext).putValue(
						IS_AUTO_LOGIN, false);
				remLoginMsg.setEnabled(true);
			}
			break;
		case R.id.checkRemLoginMsg:
			if (isChecked) {
				// 记住登录信息
				MulScreenSharePerfance.getInstance(mContext).putValue(
						IS_REMEBER_LOGINMSG, true);
			} else {
				MulScreenSharePerfance.getInstance(mContext).putValue(
						IS_REMEBER_LOGINMSG, false);
				MulScreenSharePerfance.getInstance(mContext).putValue(
						LOGIN_NAME, "");
				MulScreenSharePerfance.getInstance(mContext).putValue(
						LOGIN_PWD, "");
			}
			break;
		}
	}

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

	// 获取用户的个人信息
	private void getUserInfoTask() {
		new AsyncTask<Void, Void, UserInfoJson>() {

			@Override
			protected UserInfoJson doInBackground(Void... params) {
				String usernameContent = "";
				try {
					usernameContent = URLEncoder.encode(Session.getInstance()
							.getUserName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return new UserCenterAction().getUserInformation(
						InterfaceUrls.GET_USERINFO, usernameContent);
			}

			@Override
			protected void onPostExecute(UserInfoJson result) {
				if (result != null && result.getRet() == 0) {
					String nickName = result.getUserInfo().getNickName();
					String bindDeviceNo = result.getUserInfo()
							.getBindDeviceNo();
					Session.getInstance().setBindNo(bindDeviceNo);
					Session.getInstance().setNickName(nickName);
				}
			}
		}.execute();
	}
}