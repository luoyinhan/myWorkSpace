package com.aidufei.mirror.control;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.aidufei.protocol.remote.handle.RemoteSensor;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;



public class SensorActivity extends Activity{

	private GridView touchpanel = null;
	private Context mContext=null;
	private RemoteSensor sensor = null;
	private static final String TAG = "SensorActivity";
	private Button sensor_open = null;
	private Button sensor_close = null;
	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.trace(Log.ERROR, "Main", "MainActivity oncreate");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.sensor);
		sensor_open = (Button)findViewById(R.id.sensor_open);
		sensor_close = (Button)findViewById(R.id.sensor_close);
		sensor = MainActivity.center.getRemoteSensor();
		sensor_open.setOnClickListener(new OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startSensor();
			}
		});
		
		Button sensor_close = (Button)findViewById(R.id.sensor_close);
		sensor_close.setOnClickListener(new OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeSensor();
			}
		});
		sensor_close.setEnabled(false);
	}
	
	SensorManager sensormanager;
	Sensor accSensor;
	Sensor mgcSensor = null;
	Sensor ornSensor = null;
	Sensor temSensor = null;
	SensorEventListener lsn;
	void startSensor()
	{
		if(flag)
			return;
		sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mgcSensor = sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		ornSensor = sensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		temSensor = sensormanager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
		
		lsn = new SensorEventListener() {
	
			//@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			
		}
	
		float x = 123.45678911f, y = 0, z = 0;
		//@Override
		public void onSensorChanged(SensorEvent e) {
			// TODO Auto-generated method stub
			int sensorType = 0;
			if (e.sensor == accSensor) {
				x = e.values[SensorManager.DATA_X];
				y = e.values[SensorManager.DATA_Y];
				z = e.values[SensorManager.DATA_Z];
				sensorType = RemoteSensor.SENSORS_ACCECTRTION;
				LogUtils.trace(Log.ERROR, "sensor", "[accSensor]x="+x+",y="+y+",y="+y);
			}
			if (e.sensor == ornSensor) {
				x = e.values[SensorManager.DATA_X];
				y = e.values[SensorManager.DATA_Y];
				z = e.values[SensorManager.DATA_Z];
				sensorType = RemoteSensor.SENSORS_ORIENTAION;
				//add by l00136619 2011-12-29
				LogUtils.trace(Log.ERROR, "sensor", "[SENSORS_ORIENTAION]x="+x+",y="+y+",y="+y);
			}
			if (e.sensor == mgcSensor) {
				x = e.values[SensorManager.DATA_X];
				y = e.values[SensorManager.DATA_Y];
				z = e.values[SensorManager.DATA_Z];
				sensorType = RemoteSensor.SENSORS_MAGNETIC_FIELD;
				LogUtils.trace(Log.ERROR, "sensor", "[SENSORS_MAGNETIC_FIELD]x="+x+",y="+y+",y="+y);
			}
			if (e.sensor == temSensor) {
				x = e.values[SensorManager.DATA_X];
				y = e.values[SensorManager.DATA_Y];
				z = e.values[SensorManager.DATA_Z];
				sensorType = RemoteSensor.SENSORS_TEMPRATURE;
				LogUtils.trace(Log.ERROR, "sensor", "[SENSORS_TEMPRATURE]x="+x+",y="+y+",y="+y);
			}
			
			sensor.sendSensorEvent(sensorType, x, y, z);
			}
		
		};
		sensormanager.registerListener(lsn, accSensor,
				SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(lsn, mgcSensor,
				SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(lsn, ornSensor,
				SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(lsn, temSensor,
				SensorManager.SENSOR_DELAY_GAME);
		flag =true;
		sensor_open.setEnabled(false);
		sensor_close.setEnabled(true);
	}
	
	void closeSensor()
	{
		sensormanager.unregisterListener(lsn,accSensor);
		sensormanager.unregisterListener(lsn, mgcSensor);
		sensormanager.unregisterListener(lsn, ornSensor);
		sensormanager.unregisterListener(lsn, temSensor);
		flag = false;
		sensor_close.setEnabled(false);
		sensor_open.setEnabled(true);
	}
	
	private boolean flag = false;
	protected void onDestroy() {
		if(flag)
		{
			closeSensor();
		}
		super.onDestroy();
		
	}
}
