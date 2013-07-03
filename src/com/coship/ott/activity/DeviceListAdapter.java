package com.coship.ott.activity;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coship.ott.utils.LogUtils;

public class DeviceListAdapter extends BaseAdapter {
	Context context;
	List<HashMap<String, Object>> mData;
	ViewHolder holder;

	public DeviceListAdapter(Context context,
			List<HashMap<String, Object>> mData) {
		this.context = context;
		this.mData = mData;
	}

	public void changeDateSet(List<HashMap<String, Object>> newDate) {
		mData.clear();
		mData.addAll(newDate);
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater
					.inflate(R.layout.qury_device_bingding, null);
			holder = new ViewHolder();
			holder.bingding_name = (TextView) convertView
					.findViewById(R.id.bingding_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			LogUtils.trace(Log.VERBOSE, "tag", "positon " + position
					+ " convertView is not null, " + convertView);
		}
		holder.bingding_name.setText("设备" + position + "："
				+ mData.get(position).get("devicename"));
		return convertView;
	}

	static class ViewHolder {

		TextView bingding_name;

	}

}
