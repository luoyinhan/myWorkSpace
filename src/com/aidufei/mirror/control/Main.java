package com.aidufei.mirror.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.aidufei.protocol.remote.callback.KeepAliveCallBack;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class Main extends Activity{

	//@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		LogUtils.trace(Log.ERROR, TAG, "Main on destory");
		if(MainActivity.center!=null)
		{
			MainActivity.center.destroy();
		}
		super.onDestroy();
	}

	private final static String TAG = "Main";
	private Context mctx;
	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main1);
		
		mctx = this;
		Button keyboard = (Button)findViewById(R.id.keyboard_btn);
		keyboard.setOnClickListener(listener);
		Button touch = (Button)findViewById(R.id.touch_btn);
		touch.setOnClickListener(listener);
		Button mouse = (Button)findViewById(R.id.mouse_btn);
		mouse.setOnClickListener(listener);
		Button sensor = (Button)findViewById(R.id.sensor_btn);
		sensor.setOnClickListener(listener);
		Button media = (Button)findViewById(R.id.media_btn);
		media.setOnClickListener(listener);
		Button appList = (Button)findViewById(R.id.app_btn);
		appList.setOnClickListener(listener);
		setTitle("Main");
		MainActivity.center.remoteKeepAliveToHost(callback);
	}
	
	/*to prevent the callback are released when activity are switched*/
	static KeepAliveCallBack callback = new KeepAliveCallBack(){

		//@Override
		public void returnConectResult(boolean arg0) {
			// TODO Auto-generated method stub
			if(!arg0)
			{
				LogUtils.trace(Log.ERROR, TAG, "network is disconnected");
			}
		}
		
	};
	OnClickListener listener = new OnClickListener()
	{
		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
			switch (v.getId()) {
			case R.id.keyboard_btn:
				i.setClass(mctx, KeyboardActivity.class);
				break;
				
			case R.id.touch_btn:
				i.setClass(mctx, TouchActivity.class);
				break;
				
			case R.id.mouse_btn:
				i.setClass(mctx, MouseActivity.class);
				break;
				
			case R.id.media_btn:
				i.setClass(mctx, MediaActivity.class);
				break;
				
			case R.id.app_btn:
				i.setClass(mctx, AppListActivity.class);
				break;
				
			case R.id.sensor_btn:
				i.setClass(mctx, SensorActivity.class);
				break;

			default:
				break;
			}
			
			startActivity(i);
		}
	};
	
}