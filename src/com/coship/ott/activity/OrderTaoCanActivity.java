package com.coship.ott.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
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

public class OrderTaoCanActivity extends Activity implements OnClickListener {
	private String name, price, code, userName;
	private EditText orderCardpwd;
	private Button btnOk, btnCancel;
	private ImageView helpExitBtn;
	private CancelOrderTaoCanTask mCancelOrderTaoCanTask;
	private ToastUtils mToUtils;
	private TextView taocant, userNameView;
	private WebView webView;
	private ToastUtils mToastUtils;
	private TextView title;
	private String CardNum;
	private String numpssword;
	private boolean isCanClick = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_authority_order);
		AppManager.getAppManager().addActivity(this);
		name = getIntent().getStringExtra("name");
		price = getIntent().getStringExtra("price");
		code = getIntent().getStringExtra("code");
		userName = getIntent().getStringExtra("userName");
		CardNum = getIntent().getStringExtra("CardNum");
		orderCardpwd = (EditText) findViewById(R.id.orderCardpwd);
		// orderCardpwd.setText("");
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		helpExitBtn = (ImageView) findViewById(R.id.helpExitBtn);
		taocant = (TextView) findViewById(R.id.taocan);
		userNameView = (TextView) findViewById(R.id.headLogo);
		userNameView.setText(Session.getInstance().getUserName() + "您好！");
		title = (TextView) findViewById(R.id.title);
		title.setText("您通过智能卡(" + CardNum + ")选择订购套餐");
		taocant.setText(name + "(" + price + ")");
		helpExitBtn.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}

	/**
	 * 退订套餐
	 * 
	 */
	private class CancelOrderTaoCanTask extends
			AsyncTask<Void, Void, BaseJsonBean> {

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			return new UserCenterAction().cancelOrderedProduct(
					InterfaceUrls.CANCEL_ORDERED_PRODUCT, userName, code, "");
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (mToastUtils != null) {
				mToastUtils.cancel();
			}
			if (result != null) {
				Toast.makeText(OrderTaoCanActivity.this, result.getRetInfo(),
						Toast.LENGTH_LONG).show();
				OrderTaoCanActivity.this.finish();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOk:
			numpssword = orderCardpwd.getText().toString().trim();
			if (numpssword != null && !"".equals(numpssword)) {
				VerifyBindDevicenoCardInfoTask mVerifyBindDevicenoCardInfoTask = new VerifyBindDevicenoCardInfoTask();
				mVerifyBindDevicenoCardInfoTask.execute();

			} else {
				Toast.makeText(OrderTaoCanActivity.this, "请输入智能卡密码",
						Toast.LENGTH_SHORT).show();
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
				mToastUtils = new ToastUtils(OrderTaoCanActivity.this,
						"正在退订中。。。");
				mToastUtils.showToastAlong();
				mCancelOrderTaoCanTask = new CancelOrderTaoCanTask();
				mCancelOrderTaoCanTask.execute();
			} else {
				Toast.makeText(OrderTaoCanActivity.this, result.getRetInfo(),
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
