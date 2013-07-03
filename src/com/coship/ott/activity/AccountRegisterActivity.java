package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.transport.util.TestingUtil;

public class AccountRegisterActivity extends Activity implements
		OnClickListener {
	private EditText mUsername;
	private EditText mCreatPwd;
	private EditText mInP_Pwd;
	private EditText mPwdAnswer;
	private EditText mCardNum;
	private EditText mCarPwd;
	private Button mNextBtn;
	private ImageView mCancle;
	private Spinner spinner;// 密保问题
	private String strQuestion;
	private String userName, creatPwd, inp_Pwd, pwdAnswer, mcardNum, mcarPwd;
	private int verifyAccountName = -1;
	private int verifyBindDevicenoCard = -1;
	private int mPosition = 0;
	public static int errortimes = 0;
	private RelativeLayout userRegister;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register);
		mContext = this;
		userRegister = (RelativeLayout) findViewById(R.id.user_register);
		mUsername = (EditText) findViewById(R.id.username_edit);
		mCreatPwd = (EditText) findViewById(R.id.createpwd_edit);
		mInP_Pwd = (EditText) findViewById(R.id.againpwd_edit);
		mPwdAnswer = (EditText) findViewById(R.id.pwdanswer_edit);
		mCardNum = (EditText) findViewById(R.id.cardnum_edit);
		mCarPwd = (EditText) findViewById(R.id.cardpwd_edit);
		mNextBtn = (Button) findViewById(R.id.nextSubmitButton);
		mCancle = (ImageView) findViewById(R.id.helpExitBtn);
		spinner = (Spinner) this.findViewById(R.id.questionEdit);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View arg1,
					int position, long arg3) {
				mPosition = position;
				strQuestion = (String) adapterView.getItemAtPosition(position);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				strQuestion = (String) arg0.getItemAtPosition(mPosition);
			}
		});
		mNextBtn.setOnClickListener(this);
		mCancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.helpExitBtn:
			onBackPressed();
			break;
		case R.id.nextSubmitButton:
			getAllEditTextValuce();

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		userRegister.setVisibility(View.VISIBLE);
	}

	private void getAllEditTextValuce() {
		userName = mUsername.getText().toString().trim();
		creatPwd = mCreatPwd.getText().toString().trim();
		inp_Pwd = mInP_Pwd.getText().toString().trim();
		pwdAnswer = mPwdAnswer.getText().toString().trim();
		mcardNum = mCardNum.getText().toString().trim();
		mcarPwd = mCarPwd.getText().toString().trim();
		if (userName == null || "".equals(userName)) {
			Toast.makeText(AccountRegisterActivity.this, "请输入用户名称",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (creatPwd == null || "".equals(creatPwd)) {
			Toast.makeText(AccountRegisterActivity.this, "请输入创建密码",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (!inp_Pwd.equals(creatPwd)) {
			Toast.makeText(AccountRegisterActivity.this, "重复密码与创建密码不一致",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(pwdAnswer)) {
			Toast.makeText(AccountRegisterActivity.this, "请填写密保答案",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(mcardNum)) {
			Toast.makeText(AccountRegisterActivity.this, "请输入智能卡号",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(mcarPwd)) {
			Toast.makeText(AccountRegisterActivity.this, "请输入智能卡密码",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (userName.length() < 6 || userName.length() > 32) {
			Toast.makeText(AccountRegisterActivity.this, "请保证用户名在6至32位数之间",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (creatPwd.length() < 6 || creatPwd.length() > 32) {
			Toast.makeText(AccountRegisterActivity.this, "请保证用户密码在6至32位数之间",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// else if (!TestingUtil.isFitMode(userName)) {
		// Toast.makeText(AccountRegisterActivity.this, "用户名格式错误",
		// Toast.LENGTH_SHORT).show();
		// return;
		// } else if (!TestingUtil.isFitMode(creatPwd)) {
		// Toast.makeText(AccountRegisterActivity.this, "用户密码格式错误",
		// Toast.LENGTH_SHORT).show();
		// return;
		// } else if (!TestingUtil.isFitMode(mcardNum)) {
		// Toast.makeText(AccountRegisterActivity.this, "智能卡号格式错误",
		// Toast.LENGTH_SHORT).show();
		// return;
		// } else if (!TestingUtil.isFitMode(mcarPwd)) {
		// Toast.makeText(AccountRegisterActivity.this, "智能卡密码格式错误",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		VerifyAccountNameTask mVerifyAccountNameTask = new VerifyAccountNameTask();
		mVerifyAccountNameTask.execute();
		long errorTime = (Long) MulScreenSharePerfance.getInstance(mContext)
				.getValue("errorTime", "Long");
		long currTime = System.currentTimeMillis() / 1000;
		// 当前时间没有超过15分钟则提示
		if (errorTime > 0 && errorTime + 15 * 60 > currTime) {
			Toast.makeText(mContext, "您已连续5次智能卡密码输入错误，请15分钟后再尝试",
					Toast.LENGTH_SHORT).show();
			return;
		}
		VerifyBindDevicenoCardInfoTask mVerifyBindDevicenoCardInfoTask = new VerifyBindDevicenoCardInfoTask();
		mVerifyBindDevicenoCardInfoTask.execute();
	}

	private void enterNextRegster() {
		if (verifyAccountName == 0 && verifyBindDevicenoCard == 0) {
			Intent intent = new Intent(mContext,
					NextAccountRegsterActivity.class);
			intent.putExtra("userName", userName);
			intent.putExtra("creatPwd", creatPwd);
			intent.putExtra("inp_Pwd", inp_Pwd);
			intent.putExtra("pwdQuestion", strQuestion);
			intent.putExtra("pwdAnswer", pwdAnswer);
			intent.putExtra("cardNum", mcardNum);
			intent.putExtra("carPwd", mcarPwd);
			intent.putExtra("mPosition", mPosition);
			AccountRegisterActivity.this.startActivity(intent);
			userRegister.setVisibility(View.INVISIBLE);// 隐藏当前Activity
			verifyAccountName = -1;
			verifyBindDevicenoCard = -1;
		}
	}

	/**
	 * 校验用户名
	 * 
	 */
	private class VerifyAccountNameTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private String accountNmae = "";

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			try {
				accountNmae = URLEncoder.encode(userName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return new UserCenterAction().validateUserName(
					InterfaceUrls.VERIFY_ACCOUNTNAME, accountNmae);

		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (result == null || result.getRet() != 0) {
				if (result != null) {
					Toast.makeText(AccountRegisterActivity.this,
							result.getRetInfo(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(AccountRegisterActivity.this, "请求异常！",
							Toast.LENGTH_LONG).show();
				}
			} else {
				verifyAccountName = result.getRet();
				enterNextRegster();
			}
		}

	}

	/**
	 * 校验智能卡及密码
	 * 
	 */
	private class VerifyBindDevicenoCardInfoTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private String cardNum = "";
		private String carPwd = "";

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			return new UserCenterAction().validateCombineDevice(
					InterfaceUrls.VALIDATE_COMBINE_DEVICE, mcardNum, mcarPwd);
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (result == null) {
				return;
			}
			if (result.getRet() != 0) {
				errortimes++;// 记住错误的次数
				String tips = "您的智能卡号和密码验证失败.";
				if (result != null) {
					tips = result.getRetInfo();
				}
				if (errortimes >= 5) {
					tips = "您已连续5次智能卡密码输入错误，请15分钟后再尝试.";
					// 记住当前时间点在SharePerference中
					long errorTime = System.currentTimeMillis() / 1000;// 获取当前秒
					MulScreenSharePerfance.getInstance(mContext).putValue(
							"errorTime", errorTime);
				}
				Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
			} else {
				verifyBindDevicenoCard = result.getRet();
				enterNextRegster();
			}
		}
	}
}
