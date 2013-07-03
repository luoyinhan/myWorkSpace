package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.coship.ott.fragment.UserCenterFragment;
import com.coship.ott.service.BookNotifyService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserAction;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.user.LoginJson;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.transport.util.TestingUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.ToastUtils;

public class NextAccountRegsterActivity extends Activity implements
		OnClickListener {
	private EditText userMail, userPhone, userNickname, userIntroduce;
	private Button mSubmit;
	private static final String COMBINE_DEVICE_NAME = "COMBINE_DEVICE_NAME";
	private ImageView mHelpExitBtn;
	private String muserName, creatPwd, inp_Pwd, pwdQuestion, pwdAnswer,
			cardNum, carPwd;
	private String userMailst, userPhonest, userNicknamest, userIntroducest;
	private RegisterAccountTask mRegisterAccount;
	private String mRemark = "";
	private ToastUtils mToUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_opened);
		initData();
		userMail = (EditText) findViewById(R.id.userMail);
		userPhone = (EditText) findViewById(R.id.userPhone);
		userNickname = (EditText) findViewById(R.id.userNickname);
		userIntroduce = (EditText) findViewById(R.id.Introduce);
		mHelpExitBtn = (ImageView) findViewById(R.id.helpExitBtn);
		mSubmit = (Button) findViewById(R.id.helpSubmitButton);
		mHelpExitBtn.setOnClickListener(this);
		mSubmit.setOnClickListener(this);
	}

	private void initData() {
		muserName = getIntent().getStringExtra("userName");
		creatPwd = getIntent().getStringExtra("creatPwd");
		inp_Pwd = getIntent().getStringExtra("inp_Pwd");
		pwdQuestion = getIntent().getStringExtra("pwdQuestion");
		pwdAnswer = getIntent().getStringExtra("pwdAnswer");
		cardNum = getIntent().getStringExtra("cardNum");
		carPwd = getIntent().getStringExtra("carPwd");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.helpExitBtn:
			onBackPressed();
			break;
		case R.id.helpSubmitButton:
			initeNextData();
			break;
		default:
			break;
		}
	}

	private void initeNextData() {
		userMailst = userMail.getText().toString().trim();
		userPhonest = userPhone.getText().toString().trim();
		userNicknamest = userNickname.getText().toString().trim();
		userIntroducest = userIntroduce.getText().toString().trim();
		if ((!TestingUtil.isMobileNO(userPhonest) && !"".equals(userPhonest))
				&& userPhonest != null) {
			Toast.makeText(NextAccountRegsterActivity.this, "请输入正确的手机号码",
					Toast.LENGTH_LONG).show();
			return;
		}
		if ((!TestingUtil.isEmail(userMailst)) && !"".equals(userMailst)
				&& userMailst != null) {
			Toast.makeText(NextAccountRegsterActivity.this, "请输入正确的邮箱地址",
					Toast.LENGTH_LONG).show();
			return;
		}
		mRemark = pwdQuestion + "|" + pwdAnswer;
		mToUtils = new ToastUtils(NextAccountRegsterActivity.this, "正在注册中...");
		mToUtils.showToastAlong();
		mRegisterAccount = new RegisterAccountTask();
		mRegisterAccount.execute();
	}

	/**
	 * 密码修改中，提交智能卡密码修改
	 * 
	 */
	private class RegisterAccountTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private String userName, mPasswd, mNickName, mLogo, mSign, mEmail,
				mBindDeviceno, Remark;

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			try {
				userName = URLEncoder.encode(muserName, "UTF-8");
				mPasswd = URLEncoder.encode(creatPwd, "UTF-8");
				mNickName = URLEncoder.encode(userNicknamest, "UTF-8");
				mLogo = URLEncoder.encode("", "UTF-8");
				mSign = URLEncoder.encode(userIntroducest, "UTF-8");
				mEmail = URLEncoder.encode(userMailst, "UTF-8");
				mBindDeviceno = URLEncoder.encode(cardNum, "UTF-8");
				Remark = URLEncoder.encode(mRemark, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return new UserCenterAction().userRegister(
					InterfaceUrls.GET_REGISTER, userName, mPasswd, mNickName,
					mLogo, mSign, mEmail, userPhonest, mBindDeviceno, Remark);
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (result != null) {

				if (result.getRet() == 0) {
					Toast.makeText(NextAccountRegsterActivity.this, "注册成功",
							Toast.LENGTH_LONG).show();
					Session session = Session.getInstance();
					session.setUserName(userName);
					session.setPassWord(mPasswd);
					session.setLogined(true);
					MulScreenSharePerfance.getInstance(
							NextAccountRegsterActivity.this).putValue(
							COMBINE_DEVICE_NAME, mBindDeviceno.trim());
					login(userName, mPasswd);

				} else {
					if (mToUtils != null) {
						mToUtils.cancel();
					}
					Toast.makeText(NextAccountRegsterActivity.this,
							result.getRetInfo(), Toast.LENGTH_LONG).show();
					NextAccountRegsterActivity.this.finish();
				}
			}
		}

		private void login(final String loginName, final String loginPwd) {
			// 登录
			new AsyncTask<Void, Void, LoginJson>() {
				@Override
				protected LoginJson doInBackground(Void... params) {
					return new UserAction().login(InterfaceUrls.LOGIN,
							loginName, loginPwd);
				}

				@Override
				protected void onPostExecute(LoginJson result) {
					if (mToUtils != null) {
						mToUtils.cancel();
					}

					if (null != result && 0 == result.getRet()) {
						Session session = Session.getInstance();
						session.setLogined(true);
						session.setUserCode(result.getUserCode());
						session.setToken(result.getToken());// 用户令牌
						session.setUserName(loginName);
						session.setPassWord(loginPwd);
						session.setMacPath(getLocalMacAddress());
						MulScreenSharePerfance.getInstance(
								NextAccountRegsterActivity.this).putValue(
								"UserCode", result.getUserCode());
						Intent service = new Intent(
								NextAccountRegsterActivity.this,
								BookNotifyService.class);
						NextAccountRegsterActivity.this.startService(service);
						UserCenterFragment.mBindDeviceNo = cardNum;// 智能卡号赋给用户中心智能卡号
						Intent in = new Intent(NextAccountRegsterActivity.this,
								MainTabHostActivity.class);
						in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.putExtra("isCancel", true);
						in.putExtra("index", 5);
						UserCenterTabActivity.mSelect = 2;// 指定跳转到用户中心权限与套餐
						NextAccountRegsterActivity.this.startActivity(in);
						Intent intent = new Intent();
						intent.setAction("LOGIN_SUCCESS");
						sendBroadcast(intent);
					} else {
						Toast.makeText(NextAccountRegsterActivity.this,
								result.getRetInfo(), Toast.LENGTH_SHORT).show();
					}
					NextAccountRegsterActivity.this.finish();
				};
			}.execute();
		};
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
}
