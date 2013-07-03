package com.aidufei.mirror.control;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aidufei.protocol.remote.handle.DeviceInfo;
import com.aidufei.protocol.remote.handle.RemoteControlCenter;
import com.aidufei.protocol.remote.handle.RemoteControlCenter.RemoteConnectException;
import com.coship.ott.activity.MainTabHostActivity;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class MainActivity extends Activity{
	private static final String TAG = "com.coship.smartcontrol";

	private LinearLayout progressBar;
	private LinearLayout searchResult;
	private ListView ipList;
	private boolean hasGetBroadcastIp = false;
	private int lastConnected = -1;
	Context mContext=null;
	public static RemoteControlCenter center = null;
	private DeviceInfo devinfo = null;
	ArrayList<DeviceInfo> devList = new ArrayList<DeviceInfo>();
	String[] list = null;
	private boolean quitApp = true;
	private Button mirrorCloseBtn; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_app);
		ipList = (ListView) findViewById(R.id.ipList);
		progressBar = (LinearLayout) findViewById(R.id.searchProgress);
		searchResult = (LinearLayout) findViewById(R.id.searchResult);
		mirrorCloseBtn = (Button)findViewById(R.id.mirror_btn);
		mirrorCloseBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MainTabHostActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		 new BroadcastThread().start();
	}
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtils.trace(Log.DEBUG, TAG, "kill processs");
		android.os.Process.killProcess(android.os.Process.myPid());
	}

    @Override
	protected void onStop() {
		LogUtils.trace(Log.DEBUG, TAG, "onStop");
		super.onStop();
		if(quitApp == true ) {
			onDestroy();
		}
		else {
			finish();
		}
	}
  
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) { 
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		finish();
    		return true;
    	}
    	return super.onKeyUp(keyCode, event);
    }
    	
	Handler mHandlr = new Handler(){
		
		public void handleMessage(Message msg)
		{
			switch (msg.what) {
			case 30:
				progressBar.setVisibility(View.GONE);
				searchResult.setVisibility(View.VISIBLE);
				ipList.setDivider(null);
				ipList.setCacheColorHint(0x00000000);
				ipList.setAdapter(new IpListAdapter(list));
				break;

			default:
				break;
			}
		}
	};
	
	public class BroadcastThread extends Thread
	{
		public void run()
		{
			if (!hasGetBroadcastIp) {
				devList.clear();
				devList = RemoteControlCenter.remoteDetectIPList();
				hasGetBroadcastIp = true;
				list = new String[devList.size()];
				for(int i=0;i<devList.size();i++)
				{
					list[i] = devList.get(i).getDeviceIP();
				}
				mHandlr.sendEmptyMessage(30);
			}
		}
	}
	
	class IpListAdapter extends BaseAdapter {
		private String[] ipList;

		public IpListAdapter(String[] ipLs) {
			ipList = ipLs;
		}

		public int getCount() {
			return ipList == null ? 0 : ipList.length;
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(final int position, View view, ViewGroup parent) {
			TextView tv = null;
			if (view == null) {
				tv = new TextView(MainActivity.this);
			} else {
				tv = (TextView) view;
			}
			tv.setText(ipList[position]);
			tv.setTextSize(26);
			tv.setHeight(70);
			tv.setGravity(Gravity.CENTER);
			tv.setClickable(true);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					try {
						devinfo = devList.get(position);
						if(center!=null)
						{
							center.destroy();
						}
						center = new RemoteControlCenter(devinfo);
						center.remoteConnectToHost();
						String title= getResources().getString(R.string.network_connect);
						String tips = getResources().getString(R.string.connect) + devinfo.getDeviceIP() + getResources().getString(R.string.success);
						showDialog(MainActivity.this,title ,tips,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									//Intent intent = new Intent(MainActivity.this, Main.class);
									String uri = "http://" + devinfo.getDeviceIP() + ":8080" + "/1.ts";
									//String uri = "http://192.168.3.101:8080/1.ts";
//									Intent intent = new Intent(MainActivity.this, ControlActivity.class);
//									ArrayList<String> playlist = new ArrayList<String>();
//									playlist.add(uri);
//									intent.putExtra("selected", 0);
//									intent.putExtra("playlist", playlist);
//									quitApp = false;
//									startActivity(intent);
								}
							});
					} catch (RemoteConnectException e) {
						lastConnected = 0;
						String title = getResources().getString(R.string.network_connect);
						String tips = getResources().getString(R.string.connect) + devinfo.getDeviceIP() + getResources().getString(R.string.fail);
						showDialog(MainActivity.this,title ,
								tips, null);
						center.destroy();
					}
				}
			});
			view = tv;
			return view;
		}
	}
	public void showDialog(Context ctx,String title,String msg,DialogInterface.OnClickListener listener){
		String enterOk = getResources().getString(R.string.ok);
		AlertDialog.Builder builder = new Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(enterOk, listener);
		builder.setCancelable(true);
		builder.create().show();
	}
}
