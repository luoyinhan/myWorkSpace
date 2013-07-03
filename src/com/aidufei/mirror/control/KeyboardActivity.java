package com.aidufei.mirror.control;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.aidufei.protocol.remote.handle.RemoteKeyboard;
import com.aidufei.protocol.remote.utils.KeyInfo;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

@SuppressWarnings("deprecation")
public class KeyboardActivity extends Activity implements SensorListener {

	private final static String TAG = "KeyboardActivity";
	Context mContext = null;
	Button btn_up = null;
	Button btn_down = null;
	Button btn_left = null;
	Button btn_right = null;
	Button btn_ok = null;
	Button btn_num0 = null;
	Button btn_num1 = null;
	Button btn_alpha_A = null;
	Button btn_alpha_B = null;
	Button btn_alpha_a = null;
	Button btn_alpha_b = null;
	Button btn_star = null;
	Button btn_pound = null;
	Button btn_pause = null;
	Button btn_play = null;
	Button btn_mute = null;

	Button btn_back = null;
	Button btn_home = null;
	Button btn_search = null;
	Button btn_menu = null;

	Button btn_left_keydown = null;
	Button btn_mute_keydown = null;
	Button btn_mute_keyup = null;
	RemoteKeyboard keyboard = null;

	// @Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.trace(Log.ERROR, "Main", "MainActivity oncreate");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.keyboard);
		btn_up = (Button) findViewById(R.id.key_up);
		btn_up.setOnClickListener(listener);
		btn_down = (Button) findViewById(R.id.key_down);
		btn_down.setOnClickListener(listener);
		btn_left = (Button) findViewById(R.id.key_left);
		btn_left.setOnClickListener(listener);
		btn_right = (Button) findViewById(R.id.key_right);
		btn_right.setOnClickListener(listener);
		btn_ok = (Button) findViewById(R.id.key_ok);
		btn_ok.setOnClickListener(listener);
		btn_num0 = (Button) findViewById(R.id.num_0);
		btn_num0.setOnClickListener(listener);
		btn_num1 = (Button) findViewById(R.id.num_1);
		btn_num1.setOnClickListener(listener);
		btn_alpha_A = (Button) findViewById(R.id.alpha_A);
		btn_alpha_A.setOnClickListener(listener);
		btn_alpha_B = (Button) findViewById(R.id.alpha_B);
		btn_alpha_B.setOnClickListener(listener);
		btn_alpha_a = (Button) findViewById(R.id.alpha_a);
		btn_alpha_a.setOnClickListener(listener);
		btn_alpha_b = (Button) findViewById(R.id.alpha_b);
		btn_alpha_b.setOnClickListener(listener);
		btn_star = (Button) findViewById(R.id.key_star);
		btn_star.setOnClickListener(listener);
		btn_pound = (Button) findViewById(R.id.key_pound);
		btn_pound.setOnClickListener(listener);
		btn_pause = (Button) findViewById(R.id.key_pause);
		btn_pause.setOnClickListener(listener);
		btn_play = (Button) findViewById(R.id.key_play);
		btn_play.setOnClickListener(listener);
		btn_mute = (Button) findViewById(R.id.key_mute);
		btn_mute.setOnClickListener(listener);

		btn_back = (Button) findViewById(R.id.key_back);
		btn_back.setOnClickListener(listener);
		btn_home = (Button) findViewById(R.id.key_home);
		btn_home.setOnClickListener(listener);
		btn_search = (Button) findViewById(R.id.key_search);
		btn_search.setOnClickListener(listener);
		btn_menu = (Button) findViewById(R.id.key_menu);
		btn_menu.setOnClickListener(listener);

		btn_left_keydown = (Button) findViewById(R.id.leftkeydown);
		btn_left_keydown.setOnClickListener(event_listener);
		btn_mute_keydown = (Button) findViewById(R.id.mutekeydown);
		btn_mute_keydown.setOnClickListener(event_listener);

		btn_mute_keyup = (Button) findViewById(R.id.mutekeyup);
		btn_mute_keyup.setOnClickListener(event_listener);

		keyboard = MainActivity.center.getRemoteKeyboard();
	}

	OnClickListener event_listener = new OnClickListener() {

		// @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.leftkeydown:
				keyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_DPAD_LEFT,
						KeyInfo.KEY_EVENT_DOWN);
				break;

			case R.id.mutekeydown:
				keyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_MUTE,
						KeyInfo.KEY_EVENT_DOWN);
				break;

			case R.id.mutekeyup:
				keyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_MUTE,
						KeyInfo.KEY_EVENT_UP);
				break;

			default:
				break;
			}
		}

	};

	OnClickListener listener = new OnClickListener() {

		// @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int keycode = -1;
			switch (v.getId()) {
			case R.id.key_back:
				keycode = KeyInfo.KEYCODE_BACK;
				break;

			case R.id.key_home:
				keycode = KeyInfo.KEYCODE_HOME;
				break;

			case R.id.key_search:
				keycode = KeyInfo.KEYCODE_SEARCH;
				break;

			case R.id.key_menu:
				keycode = KeyInfo.KEYCODE_MENU;
				break;

			case R.id.key_up:
				keycode = KeyInfo.KEYCODE_DPAD_UP;
				break;

			case R.id.key_ok:
				keycode = KeyInfo.KEYCODE_ENTER;
				break;

			case R.id.key_down:
				keycode = KeyInfo.KEYCODE_DPAD_DOWN;
				break;

			case R.id.key_left:
				keycode = KeyInfo.KEYCODE_DPAD_LEFT;
				break;

			case R.id.key_right:
				keycode = KeyInfo.KEYCODE_DPAD_RIGHT;
				break;

			case R.id.num_0:
				keycode = KeyInfo.KEYCODE_0;
				break;

			case R.id.num_1:
				keycode = KeyInfo.KEYCODE_1;
				break;

			/*
			 * case R.id.alpha_A: keycode = KeyInfo.KEYCODE_A; break;
			 * 
			 * case R.id.alpha_B: keycode = KeyInfo.KEYCODE_B; break;
			 */
			case R.id.alpha_a:
				keycode = KeyInfo.KEYCODE_a;
				break;

			case R.id.alpha_b:
				keycode = KeyInfo.KEYCODE_b;
				break;

			/*
			 * case R.id.key_star: keycode = KeyInfo.KEYCODE_STAR; break;
			 * 
			 * case R.id.key_pound: keycode = KeyInfo.KEYCODE_WELL; break;
			 */
			case R.id.key_pause:
				keycode = KeyInfo.KEYCODE_PLAY;
				break;

			case R.id.key_play:
				keycode = KeyInfo.KEYCODE_PLAY;
				break;
			case R.id.key_mute:
				keycode = KeyInfo.KEYCODE_MUTE;
				break;

			default:
				break;
			}
			if (keycode != -1) {
				keyboard.remoteSendDownAndUpKeyCode(keycode);
			}

		}

	};

	protected void onDestroy() {
		LogUtils.trace(Log.ERROR, TAG, "KeyboardActivity Ondestroy");
		super.onDestroy();
	}

	// @Override
	protected void onResume() {
		super.onResume();
		LogUtils.trace(Log.ERROR, TAG, "register sensor");
		new Thread() {
			public void run() {
				((SensorManager) getSystemService(SENSOR_SERVICE))
						.registerListener(KeyboardActivity.this,
								SensorManager.SENSOR_ORIENTATION
										| SensorManager.SENSOR_ACCELEROMETER,
								SensorManager.SENSOR_DELAY_NORMAL);
			};
		}.start();
	}

	// @Override
	protected void onStop() {
		((SensorManager) getSystemService(SENSOR_SERVICE))
				.unregisterListener(this);
		LogUtils.trace(Log.ERROR, TAG, "unregister sensor");
		super.onStop();
	}

	// @Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	// @Override
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		synchronized (this) {
			if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
				if (flag) {
					if (y - values[1] > 10) {
						setChannelsDown();
					} else if (y - values[1] < -10) {
						LogUtils.trace(Log.ERROR, TAG, "y dec 10");
						setChannelsUp();
					}
				}
				x = values[0];
				y = values[1];
				z = values[2];
				LogUtils.trace(Log.ERROR, TAG, "loc x: " + x + " y: " + y
						+ " z: " + z);
			}
		}
	}

	private float x = 0;
	private float y = 0;
	private float z = 0;
	private boolean flag = true;

	public void setChannelsUp() {
		flag = false;
		LogUtils.trace(Log.ERROR, TAG, "setChannelsUp");
		keyboard.remoteSendDownAndUpKeyCode(KeyInfo.KEYCODE_DPAD_UP);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					flag = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void setChannelsDown() {
		flag = false;
		LogUtils.trace(Log.ERROR, TAG, "setChannelsDown");
		keyboard.remoteSendDownAndUpKeyCode(KeyInfo.KEYCODE_DPAD_DOWN);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					flag = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
