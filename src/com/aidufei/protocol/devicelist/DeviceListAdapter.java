package com.aidufei.protocol.devicelist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aidufei.protocol.core.Client;
import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.core.DeviceList;
import com.aidufei.protocol.core.OnDeviceChangedListener;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class DeviceListAdapter extends BaseAdapter implements
		OnDeviceChangedListener {
	public static final int MSG_DEVICE_CHANGED = 0;
	public static final int MSG_CONNECTING = 1;
	public static final int MSG_CONNECTED = 2;

	private LayoutInflater mInflater;
	private Client mClient = Client.create();
	private DeviceList mDevices = null;
	private Context mContext;
	private Handler handler;

	public DeviceListAdapter(Handler handle, Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		handler = handle;
		mDevices = mClient.devices();

		if (mDevices != null) {
			mDevices.setOnDeviceChangedListener(this);
		}
	}

	public void setChoice(int current) {
		if (current < 0 || current >= mDevices.count())
			return;
		final Device remote = mDevices.get(current);
		if (remote == null || remote.address() == null)
			return;
		if (remote.state() != Device.STATE_IDLE)
			return;
		if (mClient.connect(remote)) {
			SharedPreferences settings = mContext.getSharedPreferences(
					"device_uuid", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove("uuid").commit();
			editor.putString("uuid", remote.serial());
			editor.commit();
		}
		handler.sendEmptyMessage(MSG_CONNECTING);
		// remote.setState(Device.STATE_CONNECTING);
	}

	@Override
	public synchronized int getCount() {
		return mDevices.count();
	}

	@Override
	public Object getItem(int position) {
		return mDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class DeviceHolder {
		ImageView isDeviceSelected;
		TextView name;
	}

	@Override
	public synchronized View getView(int position, View convertView,
			ViewGroup parent) {
		DeviceHolder holder;
		LogUtils.trace(Log.DEBUG, "DeviceListAdapter", "in getView: position="
				+ position);
		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.device_item, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new DeviceHolder();
			// holder.device = (ImageView)
			// convertView.findViewById(R.id.device_icon);
			holder.name = (TextView) convertView.findViewById(R.id.device_name);
			holder.isDeviceSelected = (ImageView) convertView
					.findViewById(R.id.isDeviceSelected);
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (DeviceHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.

		holder.name.setText(mDevices.get(position).name() + ":"
				+ mDevices.get(position).address());
		Device remote = mDevices.get(position);
		if (remote.state() == Device.STATE_IDLE) {
			holder.isDeviceSelected.setVisibility(View.INVISIBLE);
		} else if (remote.state() == Device.STATE_CONNECTED) {
			holder.isDeviceSelected.setVisibility(View.VISIBLE);
		} else if (remote.state() == Device.STATE_CONNECTING) {
			holder.isDeviceSelected.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	@Override
	public synchronized void onDeviceChanged(DeviceList list) {
		// mDevices = mClient.devices();
		Message msg = Message.obtain();
		msg.what = MSG_DEVICE_CHANGED;
		if (list != null) {
			msg.arg1 = list.count();
		} else {
			msg.arg1 = 0;
		}
		LogUtils.trace(Log.DEBUG, "Test", "DeviceListAdapter onDeviceChanged");
		handler.sendMessage(msg);
	}
}
