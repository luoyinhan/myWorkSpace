package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.util.TestingUtil;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.UIUtility;

public class ChangePassWordActivity extends Activity implements OnClickListener {
	private Button btSecretly, btCard, mSubmitBtn, mEmptyBtn;
	private EditText mNewPassWord;
	private EditText mInputPassWord;
	private ImageView helpExitBtn;
	private int index = 0;
	private String userName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepasswordactivity);
		AppManager.getAppManager().addActivity(this);
		initView();
	}

	private void initView() {
		userName = getIntent().getStringExtra("userName");
		index = getIntent().getIntExtra("index", 1);
		btSecretly = (Button) findViewById(R.id.btSecretly);
		btCard = (Button) findViewById(R.id.btCard);
		mNewPassWord = (EditText) findViewById(R.id.loginNameEdit);
		mInputPassWord = (EditText) findViewById(R.id.protaction_loginPWDEdit);
		if (index == 1) {
			btSecretly.setBackgroundResource(R.drawable.bottom_line);
			btCard.setBackgroundResource(R.drawable.btn_focus_line);
		} else {
			btCard.setBackgroundResource(R.drawable.bottom_line);
			btSecretly.setBackgroundResource(R.drawable.btn_focus_line);
		}
		helpExitBtn = (ImageView) findViewById(R.id.helpExitBtn);
		btSecretly.setOnClickListener(this);
		mSubmitBtn = (Button) findViewById(R.id.helpSubmitButton);
		mEmptyBtn = (Button) findViewById(R.id.EmptyBtn);
		btCard.setOnClickListener(this);
		helpExitBtn.setOnClickListener(this);
		mSubmitBtn.setOnClickListener(this);
		mEmptyBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btSecretly:
			// btCard.setBackgroundResource(R.drawable.bottom_line);
			// btSecretly.setBackgroundResource(R.drawable.btn_focus_line);
			// index = 2;
			break;
		case R.id.btCard:
			// btSecretly.setBackgroundResource(R.drawable.bottom_line);
			// btCard.setBackgroundResource(R.drawable.btn_focus_line);
			// index = 1;
			break;
		case R.id.helpExitBtn:
			onBackPressed();
			break;
		case R.id.helpSubmitButton:
			String newPassWord = mNewPassWord.getText().toString().trim();
			String inputPassWord = mInputPassWord.getText().toString().trim();
			if (TextUtils.isEmpty(newPassWord)) {
				Toast.makeText(ChangePassWordActivity.this, "请输入新的多屏看密码",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (TextUtils.isEmpty(inputPassWord)) {
				Toast.makeText(ChangePassWordActivity.this, "请输入重复密码",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (!newPassWord.equals(inputPassWord)) {
				Toast.makeText(ChangePassWordActivity.this, "重复密码与创建密码不一致",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (!TestingUtil.isFitMode(newPassWord)) {
				Toast.makeText(ChangePassWordActivity.this, "密码格式错误",
						Toast.LENGTH_SHORT).show();
				return;
			}
			ChangePassWordTask mChangePassWordTask = new ChangePassWordTask(
					newPassWord);
			mChangePassWordTask.execute();
			break;
		case R.id.EmptyBtn:
			mNewPassWord.setText("");
			mInputPassWord.setText("");
			break;
		default:
			break;
		}
	}

	/**
	 * 修改密码
	 * 
	 */
	private class ChangePassWordTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private String newPassword;

		public ChangePassWordTask(String newPassword) {
			super();
			this.newPassword = newPassword;
		}

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			String userNameContent = "", passwordAgainStr = "";
			try {
				userNameContent = URLEncoder.encode(userName, "UTF-8");
				passwordAgainStr = URLEncoder.encode(newPassword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return new UserCenterAction().restPassWord(
					InterfaceUrls.MODACCOUNT_PASSWORD, userNameContent,
					passwordAgainStr);
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (null != result && 0 == result.getRet()) {
				Toast.makeText(ChangePassWordActivity.this, "密码修改成功！",
						Toast.LENGTH_LONG).show();
				finish();
				UIUtility.showDialog(ChangePassWordActivity.this);
			} else {
				Toast.makeText(ChangePassWordActivity.this,
						"密码修改失败！" + result.getRetInfo(), Toast.LENGTH_LONG)
						.show();
			}
		}

	}
}
