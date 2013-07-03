package com.aidufei.protocol.devicelist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aidufei.protocol.core.Client;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class DeviceDialog extends Dialog implements OnShowListener {

	Display mDisplay = null;
	Context mContext = null;
	DeviceListAdapter mAdapter = null;
	ListView mListView = null;
	LinearLayout tips;
	ProgressBar progressBar;
	TextView tipsTextView;

	private OnItemClickListener mDeviceChoiceListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			mAdapter.setChoice(position);
			parent.setSelection(position);
			LogUtils.trace(Log.DEBUG, "DeviceDialog",
					"onClickItem position=" + position + "selected postion="
							+ parent.getSelectedItemPosition());
			dismiss();
		}
	};

	public DeviceDialog(Context context, int theme) {
		super(context, theme);
		LogUtils.trace(Log.DEBUG, "DeviceDialog", "DeviceDialog");
		mContext = context;
		mDisplay = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		setCanceledOnTouchOutside(true);
		setOnShowListener(this);
	}

	public void show(int x, int y) {
		initWindow(x, y);
		show();
	}

	private void initWindow(int x, int y) {
		Window win = getWindow();
		win.setFormat(PixelFormat.TRANSLUCENT);

		WindowManager.LayoutParams param = win.getAttributes();
		param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// |
		// WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		param.flags |= // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
		WindowManager.LayoutParams.FLAG_DIM_BEHIND
				| WindowManager.LayoutParams.FLAG_DITHER;
		// WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
		param.dimAmount = 0.0f;
		param.x = x;
		param.y = y;
		param.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		param.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// LogUtils.trace(Log.DEBUG,"DeviceDialog", "window width=" +
		// param.width + ", height=" +
		// param.height);
		param.format = PixelFormat.TRANSLUCENT;
		win.setGravity(Gravity.RIGHT | Gravity.TOP);
		win.setAttributes(param);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			switch (msg.what) {
			case DeviceListAdapter.MSG_DEVICE_CHANGED:
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				if (msg.arg1 > 0) {
					handler.removeMessages(2);
					tips.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
				}
				break;
			case 2:
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				tips.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				tipsTextView.setText(R.string.no_device);
				break;

			default:
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void initDeviceList() {
		mListView = (ListView) findViewById(R.id.device_list);
		mAdapter = new DeviceListAdapter(handler, mContext);
		mListView.setItemsCanFocus(false);
		mListView.setFocusableInTouchMode(true);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mDeviceChoiceListener);

		tips = (LinearLayout) findViewById(R.id.tips);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		tipsTextView = (TextView) findViewById(R.id.content);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_dialog);
		initWindow(0, 43);
		initDeviceList();
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "Search Device onStop");
	}

	@Override
	public void onShow(DialogInterface dialog) {

		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(),
				"DeviceDialog show, begin to Search Device");
		handler.removeMessages(2);
		Client client = Client.create();
		if (client != null) {
			LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "Search Device");
			client.searchDevice();
		}
		if (tips != null && progressBar != null && mListView != null
				&& tipsTextView != null) {
			tips.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			tipsTextView.setText(R.string.device_search);
		}
		handler.sendEmptyMessageDelayed(2, 9 * 1000);
	}

}