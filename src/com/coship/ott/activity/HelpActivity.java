package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.transport.util.UpdateUtils;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;

public class HelpActivity extends Activity implements OnClickListener {
	private static final String Tag = "HelpActivity";
	private Context mContext;
	private TextView aboutView;
	// 意见反馈
	private TextView suggestview;
	// 反馈类型 0：问题反馈;1：改善建议;2：内容需求;3：新手咨询;4：其他
	private int feedbackType;
	private TextView messageView;
	private TextView systemSet;// 系统设置
	private List<TextView> headLists = new ArrayList<TextView>();
	private List<ViewGroup> layoutLists = new ArrayList<ViewGroup>();

	private RadioGroup mRadioGroup;
	// 问题反馈按钮、改善建议按钮、内容需求按钮、新手咨询按钮、其它按钮
	private RadioButton bugsRadio, betterRadio, contentRadio, newuserRadio,
			otherRadio;
	private ToggleButton onoff;
	private ProgressBar mProgressBar;
	private MulScreenSharePerfance slidingDrawerOnOff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		setupView();
	}

	private void setupView() {
		// 取对应的布局存储在layoutLists里
		RelativeLayout suggestLayout = (RelativeLayout) this
				.findViewById(R.id.suggest);
		layoutLists.add(suggestLayout);
		LinearLayout aboutLayout = (LinearLayout) this.findViewById(R.id.about);
		layoutLists.add(aboutLayout);
		RelativeLayout messageLayout = (RelativeLayout) this
				.findViewById(R.id.help);
		layoutLists.add(messageLayout);
		RelativeLayout systemsetLayout = (RelativeLayout) this
				.findViewById(R.id.setsystem);
		layoutLists.add(systemsetLayout);

		// 取标题并存储在headLists里
		suggestview = (TextView) this.findViewById(R.id.helpSuggest);
		headLists.add(suggestview);
		aboutView = (TextView) this.findViewById(R.id.update);
		headLists.add(aboutView);
		messageView = (TextView) this.findViewById(R.id.helpMessage);
		headLists.add(messageView);
		systemSet = (TextView) this.findViewById(R.id.systemSet);
		headLists.add(systemSet);

		// 设置监听
		for (int index = 0, len = headLists.size(); index < len; index++) {
			headLists.get(index)
					.setOnClickListener(new HeadViewListener(index));
		}

		// 退出按钮
		this.findViewById(R.id.helpExitBtn).setOnClickListener(this);
		// 意见反馈提交按钮
		this.findViewById(R.id.helpSubmitButton).setOnClickListener(this);
		// 意见反馈提交按钮
		this.findViewById(R.id.queryClientVersion).setOnClickListener(this);
		/* 取得 RadioGroup、RadioButton对象 */
		mRadioGroup = (RadioGroup) findViewById(R.id.suggestRG);

		bugsRadio = (RadioButton) findViewById(R.id.suggestBugs);
		betterRadio = (RadioButton) findViewById(R.id.suggestBetter);
		contentRadio = (RadioButton) findViewById(R.id.suggestContent);
		newuserRadio = (RadioButton) findViewById(R.id.suggestNewuser);
		otherRadio = (RadioButton) findViewById(R.id.suggestOther);
		onoff = (ToggleButton) findViewById(R.id.onoff);
		slidingDrawerOnOff = MulScreenSharePerfance.getInstance(mContext);
		if ((Boolean) slidingDrawerOnOff.getValue("onoff", "Boolean")) {
			onoff.setChecked(true);
		} else {
			onoff.setChecked(false);
		}
		onoff.setOnCheckedChangeListener(checkedListenner());
		TextView nowVersion = (TextView) this.findViewById(R.id.nowVersion);
		nowVersion.setText("多屏看AndroidPad客户端" + getVerName(mContext));

		/* RadioGroup用OnCheckedChangeListener来运行 */
		mRadioGroup.setOnCheckedChangeListener(mChangeRadio);
		// 加载中
		mProgressBar = (ProgressBar) findViewById(R.id.helpLoadingBar);
	}

	private OnCheckedChangeListener checkedListenner() {
		return new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					slidingDrawerOnOff.putValue("onoff", true);
				} else {
					slidingDrawerOnOff.putValue("onoff", false);
				}
			}
		};
	}

	class HeadViewListener implements OnClickListener {
		// 当前获得焦点的位置
		private int index;

		public HeadViewListener(int tabIndex) {
			this.index = tabIndex;
		}

		@Override
		public void onClick(View v) {
			for (int i = 0, len = headLists.size(); i < len; i++) {
				if (i == this.index) {
					headLists.get(i).setBackgroundResource(
							R.drawable.help_focus);
					layoutLists.get(i).setVisibility(View.VISIBLE);
				} else {
					headLists.get(i).setBackgroundResource(0);
					layoutLists.get(i).setVisibility(View.GONE);
				}
			}
			findViewById(R.id.helpHead).requestFocus();
		}
	}

	private RadioGroup.OnCheckedChangeListener mChangeRadio = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == bugsRadio.getId()) {
				feedbackType = 0;
			} else if (checkedId == betterRadio.getId()) {
				feedbackType = 1;
			} else if (checkedId == contentRadio.getId()) {
				feedbackType = 2;
			} else if (checkedId == newuserRadio.getId()) {
				feedbackType = 3;
			} else if (checkedId == otherRadio.getId()) {
				feedbackType = 4;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.helpExitBtn:
			onBackPressed();
			break;
		case R.id.queryClientVersion:
			UpdateUtils.checkForUpdate(mContext, 1);
			break;
		case R.id.helpSubmitButton:
			addUserFeedBack();
			break;
		default:
			break;
		}
	}

	/**
	 * 提交反馈信息
	 * */
	private void addUserFeedBack() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}

		EditText sugDescEdit = (EditText) this.findViewById(R.id.sugDescEdit);
		final String feedback = sugDescEdit.getText().toString();
		if (TextUtils.isEmpty(feedback)) {
			Toast.makeText(mContext, "请输入您要反馈的信息！", Toast.LENGTH_LONG).show();
			return;
		}
		EditText sugPhoneEdit = (EditText) this.findViewById(R.id.sugPhoneEdit);
		final String phone = sugPhoneEdit.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(mContext, "请输入您的手机号码！", Toast.LENGTH_LONG).show();
			return;
		}
		EditText sugEmailEdit = (EditText) this.findViewById(R.id.sugEmailEdit);
		final String email = sugEmailEdit.getText().toString();
		if (TextUtils.isEmpty(email)) {
			Toast.makeText(mContext, "请输入您的邮箱地址！", Toast.LENGTH_LONG).show();
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			@Override
			protected void onPreExecute() {
				mProgressBar.setVisibility(View.VISIBLE);
			};

			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				String encodeFeedBack = "";
				try {
					encodeFeedBack = URLEncoder.encode(feedback, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LogUtils.trace(Log.DEBUG, Tag, feedback
							+ " 转换为UTF-8编码时发生错误！");
				}
				return new UserAction().addUserFeedBack(
						InterfaceUrls.ADD_USER_FEEDBACK, session.getUserCode(),
						feedbackType, encodeFeedBack, phone, email, "");
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				mProgressBar.setVisibility(View.GONE);
				if (null != result && 0 == result.getRet()) {
					Toast.makeText(mContext,
							getString(R.string.feed_back_success),
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mContext,
							getString(R.string.feed_back_failed),
							Toast.LENGTH_LONG).show();
				}
				finish();
			};
		}.execute();
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.coship.ott.activity", 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return verName;

	}
}