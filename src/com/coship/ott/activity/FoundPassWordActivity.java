package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.utils.LogUtils;

public class FoundPassWordActivity extends Activity implements OnClickListener {
	private Button btSecretly;
	private Button btCard;
	private EditText mNewPassWord;
	private EditText mRePaWod;
	private Button mSubmitBtn;
	private Button mEmptyBtn;
	private ImageView helpExitBtn;
	private TextView username;
	private TextView psd_qustion;
	private Spinner spinner;// 密保问题
	private TextView protaction_ass;
	private EditText protaction_loginPWDEdit;
	private String strQuestion;
	private int index = 2;
	private String userName;
	private String cardNum;
	private String userAnswer;
	private FindPassWordTask mFindPassWordTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_reset_password);
		helpExitBtn = (ImageView) findViewById(R.id.helpExitBtn);
		btSecretly = (Button) findViewById(R.id.btSecretly);
		btCard = (Button) findViewById(R.id.btCard);
		username = (TextView) findViewById(R.id.new_user_pwd);
		mNewPassWord = (EditText) findViewById(R.id.loginNameEdit);
		psd_qustion = (TextView) findViewById(R.id.loginPWD);
		mRePaWod = (EditText) findViewById(R.id.loginPWDEdit);
		protaction_ass = (TextView) findViewById(R.id.protaction_loginPWD);
		protaction_loginPWDEdit = (EditText) findViewById(R.id.protaction_loginPWDEdit);
		spinner = (Spinner) this.findViewById(R.id.questionEdit);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View arg1,
					int position, long arg3) {
				strQuestion = (String) adapterView.getItemAtPosition(position);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				strQuestion = (String) arg0.getItemAtPosition(0);
			}
		});
		mSubmitBtn = (Button) findViewById(R.id.helpSubmitButton);
		mEmptyBtn = (Button) findViewById(R.id.EmptyBtn);
		helpExitBtn.setOnClickListener(this);
		mSubmitBtn.setOnClickListener(this);
		mEmptyBtn.setOnClickListener(this);
		btSecretly.setOnClickListener(this);
		btCard.setOnClickListener(this);
		btCard.setBackgroundResource(R.drawable.bottom_line);
		btSecretly.setBackgroundResource(R.drawable.btn_focus_line);
		username.setText("用户名：");
		psd_qustion.setText("密保问题：");
		protaction_ass.setText("密保答案：");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.helpExitBtn:
			onBackPressed();
			break;
		case R.id.helpSubmitButton:
			// 提交
			getEditTextdata(index);
			break;
		case R.id.EmptyBtn:
			mNewPassWord.setText("");
			mRePaWod.setText("");
			protaction_loginPWDEdit.setText("");
			break;
		case R.id.btSecretly:
			btCard.setBackgroundResource(R.drawable.bottom_line);
			btSecretly.setBackgroundResource(R.drawable.btn_focus_line);
			mRePaWod.setVisibility(View.GONE);
			spinner.setVisibility(View.VISIBLE);
			username.setText("用户名：");
			psd_qustion.setText("密保问题：");
			protaction_ass.setText("密保答案：");
			mNewPassWord.setText("");
			mRePaWod.setText("");
			protaction_loginPWDEdit.setText("");
			index = 2;
			break;
		case R.id.btCard:
			btSecretly.setBackgroundResource(R.drawable.bottom_line);
			btCard.setBackgroundResource(R.drawable.btn_focus_line);
			mRePaWod.setVisibility(View.VISIBLE);
			spinner.setVisibility(View.GONE);
			username.setText("用户名：");
			psd_qustion.setText("智能卡卡号：");
			protaction_ass.setText("智能卡密码：");
			mNewPassWord.setText("");
			mRePaWod.setText("");
			protaction_loginPWDEdit.setText("");

			index = 1;
			break;
		default:
			break;
		}
	}

	private void getEditTextdata(int index) {
		userName = mNewPassWord.getText().toString().trim();
		userAnswer = protaction_loginPWDEdit.getText().toString().trim();
		if (index == 2) {
			// protaction_loginPWDEdit.setVisibility(View.GONE);
			if (userName == null || "".equals(userName)) {
				Toast.makeText(FoundPassWordActivity.this, "请填写用户名",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (userAnswer == null || "".equals(userAnswer)) {
				Toast.makeText(FoundPassWordActivity.this, "请填写密保答案",
						Toast.LENGTH_SHORT).show();
				return;
			}

		} else {
			protaction_loginPWDEdit.setVisibility(View.VISIBLE);
			userAnswer = protaction_loginPWDEdit.getText().toString().trim();
			cardNum = mRePaWod.getText().toString().trim();
			if (userName == null || "".equals(userName)) {
				Toast.makeText(FoundPassWordActivity.this, "请填写用户名",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (userAnswer == null || "".equals(userAnswer)) {
				Toast.makeText(FoundPassWordActivity.this, "请填写智能卡密码",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (cardNum == null || "".equals(cardNum)) {
				Toast.makeText(FoundPassWordActivity.this, "请填写智能卡卡号",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
		mFindPassWordTask = new FindPassWordTask();
		mFindPassWordTask.execute();
	}

	/**
	 * 找回密码
	 * 
	 */
	private class FindPassWordTask extends AsyncTask<Void, Void, BaseJsonBean> {

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			String usernameContent = "", remark = "";
			try {
				usernameContent = URLEncoder.encode(userName, "UTF-8");
				remark = strQuestion + "|" + userAnswer;
				remark = URLEncoder.encode(remark, "UTF-8");
				userAnswer = URLEncoder.encode(userAnswer, "UTF-8");
				// strQuestion = URLEncoder.encode(strQuestion, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			BaseJsonBean baseJson = null;
			if (index == 1) {// 智能卡
				baseJson = new UserCenterAction().validateUserInfo(
						InterfaceUrls.VALIDATE_USER_INFO, index,
						usernameContent, cardNum, userAnswer, remark);
			} else { // 密保问题
				baseJson = new UserCenterAction().validateUserInfo(
						InterfaceUrls.VALIDATE_USER_INFO, index,
						usernameContent, "", "", remark);
			}
			return baseJson;
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (null != result && 0 == result.getRet()) {
				Toast.makeText(FoundPassWordActivity.this, result.getRetInfo(),
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(FoundPassWordActivity.this,
						ChangePassWordActivity.class);
				intent.putExtra("userName", userName);
				intent.putExtra("index", index);
				FoundPassWordActivity.this.startActivity(intent);
				FoundPassWordActivity.this.finish();
			} else {
				Toast.makeText(FoundPassWordActivity.this, result.getRetInfo(),
						Toast.LENGTH_LONG).show();
			}
		}

	}
}
