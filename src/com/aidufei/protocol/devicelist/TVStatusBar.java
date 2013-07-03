package com.aidufei.protocol.devicelist;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class TVStatusBar {
	private View mStatusBar = null;
	private WindowManager mWindowManager = null;
	private LayoutParams mStatusBarParams = null;
	private Context mContext = null;
	private Button mDeviceView = null;
	private Button mStatusView = null;
	private boolean mShowTV = false;
	private boolean isConnected = false;
	private Resources mResources = null;
	private DeviceDialog mDeviceDialog = null;

	private OnClickListener mDeviceClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v != mDeviceView) {
				return;
			}

			if (mDeviceDialog == null)
				return;
			if (mDeviceDialog.isShowing()) {
				mDeviceDialog.dismiss();
			} else {
				mDeviceDialog.show();
			}
		}

	};

	private OnClickListener mStatusClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v != mStatusView)
				return;
		}

	};

	public TVStatusBar(Context context, DeviceDialog dialog) {
		mContext = context;

		mDeviceDialog = dialog;

		mWindowManager = (WindowManager) context
				.getSystemService(Activity.WINDOW_SERVICE);
		mResources = (Resources) context.getResources();
		mStatusBarParams = new WindowManager.LayoutParams();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mStatusBar = inflater.inflate(R.layout.tv_status, null);

		mDeviceView = (Button) mStatusBar.findViewById(R.id.device);
		mStatusView = (Button) mStatusBar.findViewById(R.id.status);
		mDeviceView.setOnClickListener(mDeviceClickListener);
		mStatusView.setOnClickListener(mStatusClickListener);
	}

	public void setShowTVStatus(boolean shown) {
		if (mShowTV == shown)
			return;
		mShowTV = shown;

		if (mShowTV == true) {
			toTVStatus();
		} else {
			toLocalStatus();
		}
	}

	public boolean TVShown() {
		return mShowTV;
	}

	private void toTVStatus() {
		// mStatusView.setBackgroundResource(R.drawable.tv_status_selector);
		mStatusView.setText(R.string.tv_playing);
		// ColorStateList colors =
		// (ColorStateList)mResources.getColorStateList(R.color.tv_status_color_selector);
		// mStatusView.setTextColor(colors);
		// mDeviceView.setBackgroundResource(R.drawable.tv_device_icon_selector);
	}

	private void toLocalStatus() {
		// mDeviceView.setBackgroundResource(R.drawable.tv_device_in_icon_selector);

		// mStatusView.setBackgroundResource(R.drawable.disconnect);
		mStatusView.setText(R.string.tv_in);
		// ColorStateList colors =
		// (ColorStateList)mResources.getColorStateList(R.color.tv_in_out_selector);
		// mStatusView.setTextColor(colors);
	}

	// public int getHeight(){
	// return mStatusBar.getMeasuredHeight();
	// }
	//
	// public int getWidth(){
	// return mStatusBar.getMeasuredWidth();
	// }

	public void show(int x, int y, int width, int height) {
		if (mStatusBar == null)
			return;
		mStatusBarParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		mStatusBarParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_DIM_BEHIND
				| WindowManager.LayoutParams.FLAG_DITHER;
		mStatusBarParams.dimAmount = 0.0f;
		mStatusBarParams.x = x;
		mStatusBarParams.y = y;
		mStatusBarParams.width = width;
		mStatusBarParams.height = height;
		mStatusBarParams.alpha = 1.0f;
		mStatusBarParams.format = PixelFormat.TRANSLUCENT;
		LogUtils.trace(Log.DEBUG, "TVStatusBar", "window x="
				+ mStatusBarParams.x + ",y=" + mStatusBarParams.y + ",width="
				+ mStatusBarParams.width + ", height="
				+ mStatusBarParams.height);
		mStatusBarParams.gravity = Gravity.TOP | Gravity.CENTER;
		mStatusBar.setVisibility(View.INVISIBLE);
		mWindowManager.addView(mStatusBar, mStatusBarParams);
	}

	public void exit() {
		if (mStatusBar != null) {
			try {
				mStatusBar.setVisibility(View.INVISIBLE);
				mWindowManager.removeView(mStatusBar);
			} catch (Exception e) {
			}
		}
	}

	public void hide() {
		if (mStatusBar != null) {
			mStatusBar.setVisibility(View.INVISIBLE);
		}
	}

	public void show() {
		if (mStatusBar != null) {
			mStatusBar.setVisibility(View.VISIBLE);
		}
	}

	public void move(int x, int y) {
		if (mStatusBar != null) {
			mStatusBarParams.x = x;
			mStatusBarParams.y = y;
			mWindowManager.updateViewLayout(mStatusBar, mStatusBarParams);
		}
	}

}
