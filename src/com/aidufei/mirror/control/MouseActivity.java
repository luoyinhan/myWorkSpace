package com.aidufei.mirror.control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.aidufei.protocol.remote.handle.RemoteMouse;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class MouseActivity extends Activity {

	private final static String TAG = "MouseActivity";
	Context mContext = null;
	Button btn_mouse = null;
	Button btn_wheelup = null;
	Button btn_wheeldown = null;
	Button btn_leftKey = null;
	Button btn_rightKey = null;
	EditText step_edit = null;
	RemoteMouse mouse = null;
	private GestureDetector mGestureDetector = null;

	// @Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.trace(Log.ERROR, "Main", "MainActivity oncreate");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.mouse);
		btn_mouse = (Button) findViewById(R.id.mouse_btn);
		btn_mouse.setOnTouchListener(new OnTouchListener() {

			// @Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return mGestureDetector.onTouchEvent(event);
			}
		});

		btn_wheelup = (Button) findViewById(R.id.wheel_up);
		btn_wheelup.setOnClickListener(clicklistener);
		btn_wheeldown = (Button) findViewById(R.id.wheel_down);
		btn_wheeldown.setOnClickListener(clicklistener);
		btn_leftKey = (Button) findViewById(R.id.mouse_leftkey);
		btn_leftKey.setOnClickListener(clicklistener);
		btn_rightKey = (Button) findViewById(R.id.mouse_rightkey);
		btn_rightKey.setOnClickListener(clicklistener);
		mouse = MainActivity.center.getRemoteMouse();

		mGestureDetector = new GestureDetector(this, new OnGestureListener() {
			private long lastTime = 0L;
			private float lastSlope = 0;

			// @Override
			public boolean onSingleTapUp(MotionEvent event) {
				currX = currY = 0;
				mouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_SINGLE_CLICK);
				Log.i(TAG, "event.action = " + event.getAction());
				return true;
			}

			// @Override
			public void onShowPress(MotionEvent event) {
			}

			// @Override
			public boolean onScroll(MotionEvent event1, MotionEvent event2,
					float x, float y) {
				float currSlope = (y - currY) / ((x - currX) + 0.000000001f);
				if (lastSlope == 0) {
					lastSlope = currSlope;
				}
				if (currSlope * lastSlope > 0
						&& Math.abs(currSlope - lastSlope) < 0.2) {
					if (currSlope < 1) {
						y = currY + (x - currX) * lastSlope;
					} else {
						x = currX + (y - currY) / lastSlope;
					}
				}
				long costTime = System.currentTimeMillis() - lastTime;
				double dis = Math.sqrt((x - currX) * (x - currX) + (y - currY)
						* (y - currY));
				if (dis < 2) {
					return true;
				}
				double speed = dis / costTime;
				Log.i(TAG, "speed = " + speed + ", costTime = " + costTime);
				lastTime = System.currentTimeMillis();
				float xM = -x;
				float yM = -y;
				if (mouseFlag == true) {
					float scale = 1.0f;
					if (speed < 1) {
						scale = 3;
					} else if (speed < 2) {
						scale = 4;
					} else if (speed < 3) {
						scale = 5f;
					} else {
						scale = 6;
					}

					mouse.sendMouseMoveEvent(RemoteMouse.MOUSE_ACTION_MOVE, xM
							* scale, yM * scale);

				}

				lastSlope = currSlope;
				return true;
			}

			// @Override
			public void onLongPress(MotionEvent event) {
			}

			// @Override
			public boolean onFling(MotionEvent event1, MotionEvent event2,
					float x, float y) {
				mouseFlag = false;
				return true;
			}

			// @Override
			public boolean onDown(MotionEvent event) {
				lastTime = System.currentTimeMillis();
				mouseFlag = true;
				return true;
			}
		});

	}

	private float currX = 0;
	private float currY = 0;
	private boolean mouseFlag = true;

	OnClickListener clicklistener = new OnClickListener() {

		// @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.wheel_down:
				mouse.sendMouseWheelEvent(RemoteMouse.MOUSE_WHEEL_DOWN);
				break;

			case R.id.wheel_up:
				mouse.sendMouseWheelEvent(RemoteMouse.MOUSE_WHEEL_UP);
				break;

			case R.id.mouse_leftkey:
				mouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_SINGLE_CLICK);
				break;

			case R.id.mouse_rightkey:
				mouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_SINGLE_CLICK);
				break;

			default:
				break;
			}
		}

	};

	protected void onDestroy() {
		LogUtils.trace(Log.ERROR, "MainActivity", "MainActivity Ondestroy");
		super.onDestroy();
	}

}
