package com.coship.ott.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.ToastUtils;

public class UnOrderActivity extends Activity implements OnClickListener {
	private String name, price, code, userName;
	private EditText orderCardpwd;
	private Button btnOk, btnCancel;
	private ImageView helpExitBtn;
	private OrderNewTaoCanTask mOrderNewTaoCan;
	private ToastUtils mToUtils;
	private TextView taocant, userNameView;
	private CheckBox checkBox;
	private WebView webView;
	private String CardNum;
	private TextView title;
	private String numpssword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_authority_unsubscribe);
		AppManager.getAppManager().addActivity(this);
		userName = getIntent().getStringExtra("userName");
		name = getIntent().getStringExtra("name");
		CardNum = getIntent().getStringExtra("CardNum");
		price = getIntent().getStringExtra("price");
		code = getIntent().getStringExtra("code");
		orderCardpwd = (EditText) findViewById(R.id.orderCardpwd);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		helpExitBtn = (ImageView) findViewById(R.id.helpExitBtn);
		taocant = (TextView) findViewById(R.id.taocan);
		userNameView = (TextView) findViewById(R.id.headLogo);
		userNameView.setText(Session.getInstance().getUserName() + "您好！");
		title = (TextView) findViewById(R.id.title);
		title.setText("您通过智能卡(" + CardNum + ")选择订购套餐");
		taocant.setText(name + "(" + price + ")");

		checkBox = (CheckBox) findViewById(R.id.checkbox);
		webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("file:///android_asset/html/auuounce.html");

		helpExitBtn.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					checkBox.isChecked();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOk:
			numpssword = orderCardpwd.getText().toString().trim();
			if (TextUtils.isEmpty(numpssword)) {
				Toast.makeText(UnOrderActivity.this, "请输入智能卡密码",
						Toast.LENGTH_SHORT).show();
			}
			if (checkBox.isChecked()) {
				VerifyBindDevicenoCardInfoTask mVerifyBindDevicenoCardInfoTask = new VerifyBindDevicenoCardInfoTask();
				mVerifyBindDevicenoCardInfoTask.execute();
			}
			break;
		case R.id.btnCancel:
			onBackPressed();
			break;
		case R.id.helpExitBtn:
			onBackPressed();
			break;

		default:
			break;
		}

	}

	/**
	 * 订购套餐
	 * 
	 */
	private class OrderNewTaoCanTask extends
			AsyncTask<String, Void, BaseJsonBean> {

		@Override
		protected BaseJsonBean doInBackground(String... params) {
			return new UserCenterAction().createOrderProduct(
					InterfaceUrls.CREATE_ORDER_PRODUCT, userName, code, "", "");
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (mToUtils != null) {
				mToUtils.cancel();
			}
			if (result != null) {
				Toast.makeText(UnOrderActivity.this, result.getRetInfo(),
						Toast.LENGTH_LONG).show();

				UnOrderActivity.this.finish();
			}
		}

	}

	/**
	 * 校验智能卡及密码
	 * 
	 */
	private class VerifyBindDevicenoCardInfoTask extends
			AsyncTask<Void, Void, BaseJsonBean> {

		@Override
		protected BaseJsonBean doInBackground(Void... params) {

			return new UserCenterAction().validateCombineDevice(
					InterfaceUrls.VALIDATE_COMBINE_DEVICE, CardNum, numpssword);

		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (result != null && result.getRet() == 0) {
				mToUtils = new ToastUtils(UnOrderActivity.this, "正在订购业务。。。");
				mToUtils.showToastAlong();
				mOrderNewTaoCan = new OrderNewTaoCanTask();
				mOrderNewTaoCan.execute();
			} else {
				Toast.makeText(UnOrderActivity.this, result.getRetInfo(),
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
