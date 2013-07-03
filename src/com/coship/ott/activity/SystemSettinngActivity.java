package com.coship.ott.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;

import com.aidufei.protocol.adapter.oto.OtODevice;
import com.aidufei.protocol.adapter.oto.TerminalSerial;
import com.aidufei.protocol.core.Client;
import com.aidufei.protocol.oto.Global;
import com.coship.ott.constant.Constant;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.transport.util.NetTransportUtil;

public class SystemSettinngActivity extends Activity implements OnClickListener {
	public static Context context;
	public static final String share_file = "config";
	public static final String server_url = "url";
	private Button btnOk, btnTopWay, btnIDC;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.system_setting);
		super.onCreate(savedInstanceState);
		context = this;
		initViews();
	}

	private void initViews() {
		btnOk = (Button) findViewById(R.id.button1);
		btnTopWay = (Button) findViewById(R.id.button2);
		btnTopWay.setEnabled(false);
		btnIDC = (Button) findViewById(R.id.button3);
		editText = (EditText) findViewById(R.id.editText1);
		SharedPreferences settings = getSharedPreferences(share_file, 0);
		// String url = settings.getString(server_url, Constant.SERVER_ADDR);
		String url = (String) MulScreenSharePerfance.getInstance(context)
				.getValue(server_url, "String");
		editText.setText(url);
		btnOk.setOnClickListener(this);
		btnTopWay.setOnClickListener(this);
		btnIDC.setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button2:
			editText.setText("116.77.70.115:8080");

			break;
		case R.id.button3:
			editText.setText("portal.feifeikan.com:80");
			break;

		case R.id.button4:
			editText.setText("wasu.feifeikan.com:8080");
			break;

		case R.id.button1: {
			String input = editText.getText().toString();

			if (TextUtils.isEmpty(input)) {
				Toast.makeText(SystemSettinngActivity.this, "没有输入内容.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			MulScreenSharePerfance.getInstance(context).putValue(server_url,
					null);
			MulScreenSharePerfance.getInstance(context).putValue(server_url,
					input);
			if (input.startsWith("http://")) {
				if (input.endsWith("/")) {
				} else {
					input = input + "/";
				}
			} else {
				if (input.endsWith("/")) {
					input = "http://" + input;
				} else {
					input = "http://" + input + "/";
				}
			}
			NetTransportUtil.setRequestUrl(input);
			Constant.SERVER_ADDR = input;
			// Client client = Client.create();
			// // client.setGDHfcUrlService(editText.getText().toString());
			// // client.setSaitionEPGService(editText.getText().toString());
			// OtODevice local = new OtODevice("飞飞看", "serial",
			// Global.TERMINAL_APHONE, TerminalSerial.getTerminalSerial(
			// SystemSettinngActivity.this).uuid(), null);
			// client.setLocalDevice(local);
			Toast.makeText(SystemSettinngActivity.this, "设置成功.",
					Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
		default:
			break;
		}
	}
}
