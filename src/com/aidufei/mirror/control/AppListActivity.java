package com.aidufei.mirror.control;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aidufei.protocol.remote.callback.GetAppListCallBack;
import com.aidufei.protocol.remote.handle.RemoteAppList;
import com.aidufei.protocol.remote.utils.AppInfo;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;


public class AppListActivity extends Activity{
	
	Context mcontext;
	ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
	ProgressBar progress = null;
	RemoteAppList mAppList = null;
	GridView gridv = null;
	private static final String TAG = "RemoteAppActivity";
	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.trace(Log.ERROR, "Main", "MainActivity oncreate");
		super.onCreate(savedInstanceState);
		mcontext = this;
		setContentView(R.layout.remote_app);
		mAppList = MainActivity.center.getRemoteAppControl();
		initAppData();
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
	
	GetAppListCallBack callback = new GetAppListCallBack()
	{
		//@Override
		public void returnAppList(ArrayList<AppInfo> arg0) 
		{
			// TODO Auto-generated method stub
			appList = arg0;
			mHandlr.sendEmptyMessage(41);
		}
	};
	
	void initAppData()
	{
		/*get app list with icon*/
		mAppList.sendGetAppListReq(callback);
	}
	
	public void displayGridView()
	{
		GridView grid_view = (GridView)findViewById(R.id.grid_view);
		grid_view.setColumnWidth(grid_view.getWidth()/4);
		ImageAdapter adapter = new ImageAdapter(mcontext);
		grid_view.setAdapter(adapter);
		ProgressBar progress = (ProgressBar)findViewById(R.id.progress);
		progress.setVisibility(View.GONE);//Gone
		grid_view.setVisibility(View.VISIBLE);
		grid_view.setOnItemClickListener(new OnItemClickListener() {

			//@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				LogUtils.trace(Log.ERROR, TAG, "click on the icon");
				AppInfo app = appList.get(arg2);
				LogUtils.trace(Log.ERROR, TAG, "app name: "+app.getAppName());
				mAppList.sendLaunchAppReq(app.getPackageName());
			}
		});
	}

	
	Handler mHandlr = new Handler(){
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
				case 41:
					/*parse xml*/
					LogUtils.trace(Log.ERROR, TAG, "message handlr UPDATE_APP_LIST");
					displayGridView();
					break;
					
					default:
						break;
			}
			
		}
	};
	
	class ImageAdapter extends BaseAdapter
	{
		Context mctx;
		LayoutInflater layout_inflater = null;
		
		private class GridItem{
			public ImageView image;
			public TextView text;
		}
		public ImageAdapter(Context ctx)
		{
			this.mctx = ctx;
			layout_inflater = LayoutInflater.from(mctx);
		}
		
		//@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appList.size();
		}

		//@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		//@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		//@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			GridItem item = null;
			if(convertView==null)
			{
				item = new GridItem();
				convertView = layout_inflater.inflate(R.layout.app_item, null);
				item.image = (ImageView)convertView.findViewById(R.id.app_icon);
				item.text = (TextView)convertView.findViewById(R.id.app_name);
				convertView.setTag(item);
			}
			else
			{
				item = (GridItem)convertView.getTag();
			}
			
			AppInfo app = appList.get(position);
			byte[] icon = app.getPackageIcon();
			Bitmap bm = BitmapFactory.decodeByteArray(icon, 0, icon.length);
			item.image.setImageBitmap(bm);
			String name = appList.get(position).getPackageName();
			int index = name.lastIndexOf('.');
			if(index!= -1)
				name = name.substring(index+1);
			item.text.setText(name);
			return convertView;
		}
		
	}
}
