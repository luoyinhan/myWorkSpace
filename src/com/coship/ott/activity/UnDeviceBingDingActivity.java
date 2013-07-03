package com.coship.ott.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.constant.Constant;
import com.coship.ott.constant.DeviceBingDingError;
import com.coship.ott.constant.UdrmDefine;
import com.coship.ott.fragment.DeviceBingDingFragment;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.ToastUtils;

public class UnDeviceBingDingActivity extends Activity implements
		OnClickListener {
	private final static String TAG = "UnDeviceBingDingActivity";
	private TextView you_sure_tx;
	private Button device_ok, device_cancel, bingding_ok;
	private ImageView helpExitBtn;
	private String ID, MACAddr, DeviceName;
	private String mUserNameStr, passWord;
	private int type = 8;
	private UnDeviceBingDing mUnDeviceBingDing;
	private ToastUtils mToastUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.un_device_bingding);
		AppManager.getAppManager().addActivity(this);
		initGetIntent();
		initView();
		mUserNameStr = (String) MulScreenSharePerfance.getInstance(
				UnDeviceBingDingActivity.this).getValue(
				MainTabHostActivity.LOGIN_NAME, "String");
		passWord = (String) MulScreenSharePerfance.getInstance(
				UnDeviceBingDingActivity.this).getValue(
				MainTabHostActivity.LOGIN_PWD, "String");
	}

	private void initView() {
		you_sure_tx = (TextView) findViewById(R.id.you_sure);
		device_ok = (Button) findViewById(R.id.device_ok);
		device_cancel = (Button) findViewById(R.id.device_cancel);
		helpExitBtn = (ImageView) findViewById(R.id.helpExitBtn);
		if (type == Constant.BINGDING_DEVICE) {

			bingding_ok = (Button) findViewById(R.id.bingding_ok);
			bingding_ok.setVisibility(View.VISIBLE);
			device_ok.setVisibility(View.GONE);
			device_cancel.setVisibility(View.GONE);
			bingding_ok.setOnClickListener(this);
		}
		device_ok.setOnClickListener(this);
		device_cancel.setOnClickListener(this);
		helpExitBtn.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		switch (type) {
		case Constant.UNDEVICE_BINGDING:
			you_sure_tx.setText("您确定需要解除：" + DeviceName + "的绑定?");

			break;
		case Constant.NEET_UNBINGDING_ONE_DEVICE:
			you_sure_tx.setText("您当前绑定的设备数量已达3个，如需绑定本机；请先解除其他设备绑定!");
			break;
		case Constant.BINGDING_DEVICE:
			you_sure_tx.setText("您已完成当前设备" + DeviceName + "的绑定");
			break;

		default:
			break;
		}
	}

	private void initGetIntent() {
		// TODO Auto-generated method stub
		ID = getIntent().getStringExtra("ID");
		MACAddr = getIntent().getStringExtra("MACAddr");
		DeviceName = getIntent().getStringExtra("DeviceName");
		type = getIntent().getIntExtra("type", 8);
	}

	/**
	 * 查询设备绑定列表
	 * 
	 */
	private class UnDeviceBingDing extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			return UserCenterTabActivity.mlibUDRM.UDRMAgentUnbindDevice(
					mUserNameStr, passWord, ID, MACAddr, DeviceName);
		}

		@Override
		protected void onPostExecute(Integer unbindState) {
			if (mToastUtils != null) {
				mToastUtils.cancel();
			}
			if (unbindState == UdrmDefine.UDRM_ERROR_OK) {
				Toast.makeText(UnDeviceBingDingActivity.this, "解绑成功",
						Toast.LENGTH_LONG).show();
				finish();
			} else {
				new DeviceBingDingError().getDebugError(
						UserCenterTabActivity.mlibUDRM,
						UnDeviceBingDingActivity.this);
				LogUtils.trace(Log.DEBUG, TAG, "解绑出错了");
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.device_ok:
			if (type == Constant.NEET_UNBINGDING_ONE_DEVICE) {
				onBackPressed();
			} else if (type == Constant.UNDEVICE_BINGDING) {
				mToastUtils = new ToastUtils(UnDeviceBingDingActivity.this);
				mToastUtils.showLongToast(UnDeviceBingDingActivity.this,
						"正在解除绑定中...");
				mUnDeviceBingDing = new UnDeviceBingDing();
				mUnDeviceBingDing.execute();
			}
			break;
		case R.id.device_cancel:
			DeviceBingDingFragment.fromUnbd = true;
			onBackPressed();
			break;
		case R.id.helpExitBtn:
			DeviceBingDingFragment.fromUnbd = true;
			onBackPressed();
			break;
		case R.id.bingding_ok:
			finish();
			break;

		default:
			break;
		}
	}
}
