package com.coship.ott.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

	public static ProgressDialog progressDialog;
	public static boolean registering = false;
	private static AlertDialog alert;

	public static void showProgress(Context context, String message) {

		try {

			hideProgress();

			if (!registering) {
				progressDialog = ProgressDialog.show(context, "", message);
			}

		} catch (Exception ex) {
		}
	}

	public static void hideProgress() {

		if (progressDialog != null && !registering) {
			try {
				progressDialog.dismiss();
			} catch (Exception ex) {
			}
			progressDialog = null;
		}
	}

	public static void alertDialog(String title, String message,
			Context context, DialogInterface.OnClickListener clickListener) {

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("确定", clickListener)
				.setNegativeButton("取消", null);
		alert = alertBuilder.create();
		alert.setTitle(title);
		alert.show();
	}

	public static void dismissAlert() {

		if (alert != null) {
			alert.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
			alert = null;
		}
	}
}
