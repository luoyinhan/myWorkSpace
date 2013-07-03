package com.coship.ott.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.coship.ott.activity.R;

public class LoadingDialog extends ProgressDialog {

	public LoadingDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
	}
}