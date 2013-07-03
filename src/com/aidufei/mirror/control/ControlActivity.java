//package com.aidufei.mirror.control;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//
//import com.aidufei.protocol.remote.handle.RemoteKeyboard;
//import com.aidufei.protocol.remote.handle.RemoteSensor;
//import com.aidufei.protocol.remote.handle.RemoteTouch;
//import com.aidufei.protocol.remote.utils.KeyInfo;
//import com.aidufei.protocol.remote.utils.MultiTouchInfo;
//import com.coship.ott.activity.R;
//
//
//
//public class ControlActivity extends PlayerActivity {
//
//	private static final String TAG = "com.coship.smartcontrol";//"ControlActivity";
//	private Context mContext = null;
//	
//	private RemoteKeyboard remoteKeyboard = null;
//	private RemoteTouch remoteTouch = null;
//	private RemoteSensor remoteSensor = null;
//	
//	private boolean sensorStarted = false;
//	private SensorManager sensorManager;
//	private Sensor accSensor = null;
//	private Sensor mgcSensor = null;
//	private Sensor ornSensor = null;
//	private Sensor temSensor = null;
//	private SensorEventListener sensorListener;
//	private boolean sensorOrientationReversal = false;
//	private boolean sensorFirstEvent = true;
//
//	private int touchMoveCount = 0;
//	private MultiTouchInfo touchInfo = new MultiTouchInfo();
//
//	private final int MENU_SENSOR = 1;
//	private final int MENU_QUIT = 2;
//	private final int MENU_HOME = 3;
//	private final int MENU_MENU = 4;
//	private final int MENU_PORTAL = 5;
//	
//	private boolean potraitScreen = true; //Potrait screen for Phone and Landscape screen for Pad
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		
//		final Window win = getWindow();
//		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        //win.setFlags(0x80000000, 0x80000000); //home key
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        
//		
//		super.onCreate(savedInstanceState);
//		mContext = this;
//
//		remoteKeyboard = MainActivity.center.getRemoteKeyboard();
//		remoteTouch = MainActivity.center.getRemoteTouch();
//		remoteSensor = MainActivity.center.getRemoteSensor();
//		
//		//startSensor();
//	}
//	
//	
//
//	public boolean onCreateOptionsMenu(Menu menu) {
//	       
//        menu.add(Menu.NONE, MENU_HOME, Menu.NONE, getResources().getString(R.string.remote_home));
//        menu.add(Menu.NONE, MENU_MENU, Menu.NONE, getResources().getString(R.string.remote_menu));
//        menu.add(Menu.NONE, MENU_PORTAL, Menu.NONE, getResources().getString(R.string.remote_portal));
//        menu.add(Menu.NONE, MENU_SENSOR, Menu.NONE, getResources().getString(R.string.menu_enable_sensor));
//        menu.add(Menu.NONE, MENU_QUIT, Menu.NONE, getResources().getString(R.string.quit));
//
//        return true;
//	}
//	
//	public boolean onOptionsItemSelected(MenuItem item) {
//		Resources resources = getResources();
//		switch(item.getItemId()) {
//			case MENU_HOME: {
//				remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_HOME, KeyInfo.KEY_EVENT_DOWN);
//				remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_HOME, KeyInfo.KEY_EVENT_UP);
//				break;
//			}
//			case MENU_MENU: {
//				remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_MENU, KeyInfo.KEY_EVENT_DOWN);
//				remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_MENU, KeyInfo.KEY_EVENT_UP);
//				break;
//			}
//			case MENU_PORTAL: {
//				remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_PORTAL, KeyInfo.KEY_EVENT_DOWN);
//				remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_PORTAL, KeyInfo.KEY_EVENT_UP);
//				break;
//			}
//			case MENU_SENSOR: {
//				if(sensorStarted) {
//					stopSensor();
//					item.setTitle(R.string.menu_enable_sensor);
//				}
//				else {
//					startSensor();
//					item.setTitle(R.string.menu_disable_sensor);
//				}
//				break;
//			}
//			case MENU_QUIT: {
//				finish();
//				android.os.Process.killProcess(android.os.Process.myPid());
//				break;
//			}
//		}
//		return true;	
//	}
//	
//	
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) { 
//    	if(keyCode == KeyEvent.KEYCODE_BACK) {
//    		remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_BACK, KeyInfo.KEY_EVENT_DOWN);
//    		return true;
//    	}
//    	return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) { 
//    	if(keyCode == KeyEvent.KEYCODE_BACK) {
//    		remoteKeyboard.remoteSendDownOrUpKeyCode(KeyInfo.KEYCODE_BACK, KeyInfo.KEY_EVENT_UP);
//    		return true;
//    	}
//    	return super.onKeyUp(keyCode, event);
//    }
//    
//    
//	protected int mapingTouchPanelPointWidth(float x)
//	{
//		int ret_value = 0;
//		
//		if(mDisplayWidth > 0) {
//			ret_value = (((int)x)*1280)/mDisplayWidth;
//		}else {
//			ret_value = (int)x;		
//		}
//		return ret_value;
//	}
//	
//	protected int mapingTouchPanelPointHeight(float y)
//	{
//		int ret_value = 0;
//		if(mDisplayHeight > 0) {
//			ret_value = (((int)y)*720)/mDisplayHeight;
//		} else {
//			ret_value = (int)y;
//		}
//		return ret_value;
//	}
//	
//	protected void handleTwoPointTouch(MotionEvent event)
//	{
//		int x1 = mapingTouchPanelPointWidth(event.getX(0));
//		int y1 = mapingTouchPanelPointHeight(event.getY(0));
//		int x2 = mapingTouchPanelPointWidth(event.getX(1));
//		int y2 = mapingTouchPanelPointHeight(event.getY(1));
//
//		touchInfo.setFingerNum(2);
//		Log.d(TAG, "two touch event is: "+event.getAction());
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_POINTER_1_DOWN:
//			touchInfo.setFingerInfo(0, x1, y1, 1);
//			touchInfo.setFingerInfo(1, x2, y2, 1);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		case MotionEvent.ACTION_POINTER_1_UP:
//			touchInfo.setFingerInfo(0, x1, y1, 0);
//			touchInfo.setFingerInfo(1, x2, y2, 1);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		case MotionEvent.ACTION_MOVE:
//			if(touchMoveCount !=1)
//			{
//				touchMoveCount++;
//				break;
//			}
//			touchMoveCount = 0;
//			touchInfo.setFingerInfo(0, x1, y1, 1);
//			touchInfo.setFingerInfo(1, x2, y2, 1);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		case MotionEvent.ACTION_POINTER_2_DOWN:
//			touchInfo.setFingerInfo(0, x1, y1, 1);
//			touchInfo.setFingerInfo(1, x2, y2, 1);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		case MotionEvent.ACTION_POINTER_2_UP:
//			touchInfo.setFingerInfo(0, x1, y1, 0);
//			touchInfo.setFingerInfo(1, x2, y2, 1);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		default:
//			break;
//		}
//		
//	}
//
//	protected void handleOnePointTouch(MotionEvent event)
//	{
//		int x1 = mapingTouchPanelPointWidth(event.getX());
//		int y1 = mapingTouchPanelPointHeight(event.getY());
//		
//
//		touchInfo.setFingerNum(1);
//		Log.d(TAG, "one touch event is: "+event.getAction());
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			touchInfo.setFingerInfo(0, x1, y1, 1);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		case MotionEvent.ACTION_UP:
//			touchInfo.setFingerInfo(0, x1, y1, 0);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		case MotionEvent.ACTION_MOVE:
//			if(touchMoveCount !=1)
//			{
//				touchMoveCount++;
//				break;
//			}
//			touchMoveCount = 0;
//			touchInfo.setFingerInfo(0, x1, y1, 1);
//			remoteTouch.sendMultiTouchEvent(touchInfo);
//			Log.d(TAG, "send touch event");
//			break;
//			
//		default:
//			break;
//		}
//		
//	}
//	
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//		int points = event.getPointerCount();
//		Log.d(TAG, "touch point touchMoveCount:"+points);
//		if(points == 2)
//		{
//			handleTwoPointTouch(event);
//		}
//		else if(points == 1)
//		{
//			handleOnePointTouch(event);
//		}
//
//		/*
//		float x = event.getX();
//		float y = event.getY();
//	
//		float sendX = x / MWidth;
//		float sendY = y / MHeight;
//		
//		switch (event.getAction()) {
//		
//		case MotionEvent.ACTION_DOWN:
//			touch.sendTouchEvent(RemoteTouch.TOUCH_EVENT_DOWN, sendX, sendY);
//			break;
//		case MotionEvent.ACTION_MOVE:
//			float currSlope = (y - lastY)/((x - lastX) + 0.000000001f);
//			if(lastSlope == 0){
//				lastSlope = currSlope;
//			}
//			if(currSlope * lastSlope > 0 && Math.abs(currSlope - lastSlope) < 0.2){
//				if(currSlope < 1){
//					y = lastY + (x - lastX) * lastSlope;
//				}else{
//					x = lastX + (y - lastY) / lastSlope;
//				}
//			}
//			double distance = Math.sqrt((x - lastX)*(x - lastX) + (y - lastY)*(y - lastY)); 
//			Log.i(TAG, " distance = " + distance);
//			if(distance > 2){
//				sendX = x / MWidth;
//				sendY = y / MHeight;
//				touch.sendTouchEvent(RemoteTouch.TOUCH_EVENT_MOVE, sendX, sendY);
//				lastSlope = currSlope;
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			touch.sendTouchEvent(RemoteTouch.TOUCH_EVENT_UP, sendX, sendY);
//			break;
//		}
//		
//		lastX = x;
//		lastY = y;
//		Log.d("relative ordinate","lastX:"+lastX+" lastY:"+lastY);*/
//		return true;
//
//	}
//
//	void startSensor()
//	{
//		if(sensorStarted)
//			return;
//		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//		accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		mgcSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//		ornSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//		temSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
//		
//		sensorListener = new SensorEventListener() {
//	
//			//@Override
//			public void onAccuracyChanged(Sensor sensor, int accuracy) {
//				// TODO Auto-generated method stub
//			}
//		
//			float x = 123.45678911f, y = 0, z = 0;
//			//@Override
//			public void onSensorChanged(SensorEvent e) {
//				// TODO Auto-generated method stub
//				int sensorType = 0;
//				float dX = e.values[SensorManager.DATA_X];
//				float dY = e.values[SensorManager.DATA_Y];
//				float dZ = e.values[SensorManager.DATA_Z];
//				if (e.sensor == accSensor) {
//					if(potraitScreen) {
//						x = dY;
//						y = dX;
//					} else {
//						x = dX;
//						y = dY;					
//					}
//					z = dZ;
//					sensorType = RemoteSensor.SENSORS_ACCECTRTION;
//					if(sensorFirstEvent) {
//						if(z > 0) {
//							sensorOrientationReversal = true;
//						}
//						sensorFirstEvent = false;
//					}
//					if(sensorOrientationReversal) {
//						x = -x;
//						y = -y;					
//					}
//					Log.d("sensor","[accSensor]x="+x+",y="+y+",z="+z);
//				}
//				if (e.sensor == ornSensor) {
//					x = dX;
//					if(potraitScreen) {
//						y = dZ;
//						z = dY;						
//					} else {
//						y = dY;
//						z = dZ;											
//					}
//					if(sensorOrientationReversal) {
//						z = -z;
//					}
//					sensorType = RemoteSensor.SENSORS_ORIENTAION;
//					Log.d("sensor","[SENSORS_ORIENTAION]x="+x+",y="+y+",z="+z);
//				}
//				if (e.sensor == mgcSensor) {
//					x = dX;
//					y = dY;
//					z = dZ;
//					sensorType = RemoteSensor.SENSORS_MAGNETIC_FIELD;
//					Log.d("sensor","[SENSORS_MAGNETIC_FIELD]x="+x+",y="+y+",z="+z);
//				}
//				if (e.sensor == temSensor) {
//					x = dX;
//					y = dY;
//					z = dZ;
//					sensorType = RemoteSensor.SENSORS_TEMPRATURE;
//					Log.d("sensor","[SENSORS_TEMPRATURE]x="+x+",y="+y+",z="+z);
//				}
//				
//				remoteSensor.sendSensorEvent(sensorType, x, y, z);
//			}
//			
//		};
//		
//		sensorManager.registerListener(sensorListener, accSensor,
//				sensorManager.SENSOR_DELAY_GAME);
//		sensorManager.registerListener(sensorListener, mgcSensor,
//				sensorManager.SENSOR_DELAY_GAME);
//		sensorManager.registerListener(sensorListener, ornSensor,
//				sensorManager.SENSOR_DELAY_GAME);
//		sensorManager.registerListener(sensorListener, temSensor,
//				sensorManager.SENSOR_DELAY_GAME);
//		sensorStarted =true;
//	}
//	
//	void stopSensor()
//	{
//		if(sensorStarted == false) {
//			return;
//		}
//		sensorManager.unregisterListener(sensorListener, accSensor);
//		sensorManager.unregisterListener(sensorListener, mgcSensor);
//		sensorManager.unregisterListener(sensorListener, ornSensor);
//		sensorManager.unregisterListener(sensorListener, temSensor);
//		sensorStarted = false;
//	}
//
//	public void onDestroy() {
//		Log.d(TAG, "onDestroy");
//		super.onDestroy();
//	}
//
//	@Override
//	public void onStop() {
//		Log.d(TAG, "onStop");
//		super.onStop();
//		//onDestroy();
//		if(sensorStarted)
//		{
//			stopSensor();
//		}
//		remoteKeyboard = null;
//		remoteTouch = null;
//		remoteSensor = null;
//		Log.d(TAG, "finish");
//		finish();
//		Log.d(TAG, "killProcess");
//		android.os.Process.killProcess(android.os.Process.myPid());
//	}
//}
