package com.coship.ott.utils;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

public class ToastUtils {
	private static final String TAG = "ToastUtils";

	private WeakReference<ProgressDialog> mProgress;
	public boolean isEnter = false;

	public ToastUtils(Context context, String str) {
		if (mProgress == null) {
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setIndeterminate(false);
			dialog.setMessage(str);
			dialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (KeyEvent.KEYCODE_SEARCH == keyCode
							&& event.getRepeatCount() == 0) {
						return true;
					}
					return false;

				}
			});
			mProgress = new WeakReference<ProgressDialog>(dialog);
		}

	}

	public ToastUtils(Context context) {
		if (mProgress == null) {
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setIndeterminate(false);
			dialog.setMessage("数据加载中，请稍后");
			dialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (KeyEvent.KEYCODE_SEARCH == keyCode
							&& event.getRepeatCount() == 0) {
						return true;
					}
					return false;

				}
			});
			mProgress = new WeakReference<ProgressDialog>(dialog);
		}
	}

	public boolean isRunning() {
		final ProgressDialog progressDialog = mProgress.get();

		if (progressDialog == null)
			return false;

		return progressDialog.isShowing();
	}

	public void cancel() {
		final ProgressDialog progressDialog = mProgress.get();
		try {
			// Only dismiss when valid reference and still showing
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			android.util.Log.w(
					TAG,
					"Ignoring exception while dismissing dialog: "
							+ e.toString());
		}
	}

	public void showToastAlong() {
		final ProgressDialog progressDialog = mProgress.get();

		if (progressDialog == null)
			return;

		progressDialog.show();
		progressDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (KeyEvent.KEYCODE_SEARCH == keyCode
						&& event.getRepeatCount() == 0) {
					return true;
				} else if (KeyEvent.KEYCODE_BACK == keyCode) {
					isEnter = true;
					return false;
				}
				return false;

			}
		});
	}

	public void showAttachToastAlong() {
		final ProgressDialog progressDialog = mProgress.get();

		if (progressDialog == null)
			return;

		progressDialog.show();
		progressDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (KeyEvent.KEYCODE_SEARCH == keyCode
						&& event.getRepeatCount() == 0) {
					return true;
				} else if (KeyEvent.KEYCODE_BACK == keyCode
						&& event.getRepeatCount() == 0) {
					return true;
				}
				return false;
			}
		});
	}

	public void showToastAlong(long delay) {
		showToastAlong();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				ToastUtils.this.cancel();
			}
		};
		timer.schedule(task, delay);
	}

	/*
	 * private ProgressDialog progressDialog;
	 * 
	 * public ToastUtils(Context context,String str) { progressDialog =
	 * ProgressDialog.show(context, StringResources.get("Strawberry"), str,
	 * true, true); progressDialog.setIcon(R.drawable.strawberryml); }
	 * 
	 * public boolean isRunning() { return progressDialog.isShowing(); }
	 * 
	 * public void cancel() { progressDialog.dismiss(); }
	 * 
	 * public void showToastAlong() { progressDialog.show(); }
	 */

	public static void showToast(Context context, String str) {
		Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	public static void showLongToast(Context context, String str) {
		Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
}
