package com.aidufei.mirror.control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.aidufei.protocol.remote.handle.RemoteTouch;
import com.aidufei.protocol.remote.utils.MultiTouchInfo;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;



public class TouchActivity extends Activity{

	private ImageView touchpanel = null;
	private Context mContext=null;
	private RemoteTouch touch = null;
	private int MWidth = 768;
	private int MHeight = 407;
	private static final String TAG = "TouchActivity";
	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.trace(Log.ERROR, "Main", "MainActivity oncreate");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.touch);
		touchpanel = (ImageView)findViewById(R.id.touchimageviewgrid);
		LogUtils.trace(Log.ERROR, TAG, "touch pannel height:"+MHeight+"width:"+MWidth);
		touchpanel.setOnTouchListener(listener);
		touch = MainActivity.center.getRemoteTouch();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		
	}
	

	
	protected int mapingTouchPanelPointHeight(float y)
	{
		int ret_value = 0;
		
		ret_value = (((int)y)*720)/MHeight;
		return ret_value;
	}
	
	protected int mapingTouchPanelPointWidth(float x)
	{
		int ret_value = 0;
		
		ret_value = (((int)x)*1280)/MWidth;
		return ret_value;
	}
	
	protected void handleTwoPointTouch(MotionEvent event)
	{
		int x1 = mapingTouchPanelPointWidth(event.getX(0));
		int y1 = mapingTouchPanelPointHeight(event.getY(0));
		int x2 = mapingTouchPanelPointWidth(event.getX(1));
		int y2 = mapingTouchPanelPointHeight(event.getY(1));

		info.setFingerNum(2);
		LogUtils.trace(Log.ERROR, TAG, "two touch event is: "+event.getAction());
		switch (event.getAction()) {
		case MotionEvent.ACTION_POINTER_1_DOWN:
			info.setFingerInfo(0, x1, y1, 1);
			info.setFingerInfo(1, x2, y2, 1);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG, "send touch event");
			break;
			
		case MotionEvent.ACTION_POINTER_1_UP:
			info.setFingerInfo(0, x1, y1, 0);
			info.setFingerInfo(1, x2, y2, 1);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG, "send touch event");
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(count !=1)
			{
				count++;
				break;
			}
			count = 0;
			info.setFingerInfo(0, x1, y1, 1);
			info.setFingerInfo(1, x2, y2, 1);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG, "send touch event");
			break;
			
		case MotionEvent.ACTION_POINTER_2_DOWN:
			info.setFingerInfo(0, x1, y1, 1);
			info.setFingerInfo(1, x2, y2, 1);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG, "send touch event");
			break;
			
		case MotionEvent.ACTION_POINTER_2_UP:
			info.setFingerInfo(0, x1, y1, 0);
			info.setFingerInfo(1, x2, y2, 1);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG, "send touch event");
			break;
			
		default:
			break;
		}
		
	}

	MultiTouchInfo info = new MultiTouchInfo();
	private int count = 0;
	protected void handleOnePointTouch(MotionEvent event)
	{
		int x1 = mapingTouchPanelPointWidth(event.getX());
		int y1 = mapingTouchPanelPointHeight(event.getY());
		

		info.setFingerNum(1);
		LogUtils.trace(Log.ERROR, TAG, "one touch event is: "+event.getAction());
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			info.setFingerInfo(0, x1, y1, 1);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG, "send touch event");
			break;
			
		case MotionEvent.ACTION_UP:
			info.setFingerInfo(0, x1, y1, 0);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG,"send touch event");
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(count !=1)
			{
				count++;
				break;
			}
			count = 0;
			info.setFingerInfo(0, x1, y1, 1);
			touch.sendMultiTouchEvent(info);
			LogUtils.trace(Log.ERROR, TAG,"send touch event"); 
			break;
			
		default:
			break;
		}
		
	}
	
	OnTouchListener listener = new OnTouchListener()
	{
		//float lastX = 0;
		//float lastY = 0;
		//float lastSlope = 0;
		//@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int points = event.getPointerCount();
			LogUtils.trace(Log.ERROR, TAG,"touch point count:"+points); 
			if(points == 2)
			{
				handleTwoPointTouch(event);
			}
			else if(points == 1)
			{
				handleOnePointTouch(event);
			}

			/*float x = event.getX();
			float y = event.getY();
		
			float sendX = x / MWidth;
			float sendY = y / MHeight;
			
			switch (event.getAction()) {
			
			case MotionEvent.ACTION_DOWN:
				touch.sendTouchEvent(RemoteTouch.TOUCH_EVENT_DOWN, sendX, sendY);
				break;
			case MotionEvent.ACTION_MOVE:
				float currSlope = (y - lastY)/((x - lastX) + 0.000000001f);
				if(lastSlope == 0){
					lastSlope = currSlope;
				}
				if(currSlope * lastSlope > 0 && Math.abs(currSlope - lastSlope) < 0.2){
					if(currSlope < 1){
						y = lastY + (x - lastX) * lastSlope;
					}else{
						x = lastX + (y - lastY) / lastSlope;
					}
				}
				double distance = Math.sqrt((x - lastX)*(x - lastX) + (y - lastY)*(y - lastY)); 
				Log.i(TAG, " distance = " + distance);
				if(distance > 2){
					sendX = x / MWidth;
					sendY = y / MHeight;
					touch.sendTouchEvent(RemoteTouch.TOUCH_EVENT_MOVE, sendX, sendY);
					lastSlope = currSlope;
				}
				break;
			case MotionEvent.ACTION_UP:
				touch.sendTouchEvent(RemoteTouch.TOUCH_EVENT_UP, sendX, sendY);
				break;
			}
			
			lastX = x;
			lastY = y;
			Log.e("relative ordinate","lastX:"+lastX+" lastY:"+lastY);*/
			return true;

		}
		
	};
	
}
