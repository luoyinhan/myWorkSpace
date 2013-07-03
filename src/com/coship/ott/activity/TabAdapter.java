package com.coship.ott.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class TabAdapter extends BaseAdapter {
	private Integer[] tabIds;
	private Integer[] tabFocusIds;
	private LayoutInflater mInflate;
	private Context mContext;
	private int index=0;

	public TabAdapter(Integer[] tabIds, Integer[] tabFocusIds, Context mContext) {
		super();
		this.tabIds = tabIds;
		this.tabFocusIds = tabFocusIds;
		this.mContext = mContext;
		mInflate = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tabIds.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		index=position;
		return tabIds[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TabItem item = null;
		if (convertView == null) {
			item = new TabItem();
			convertView = mInflate.inflate(R.layout.usercenter_tab_item, null);
			item.tabView = (ImageView) convertView.findViewById(R.id.tabView);
			convertView.setTag(item);
		} else {
			item = (TabItem) convertView.getTag();
		}
		if (UserCenterTabActivity.mSelect==position) {
			item.tabView.setBackgroundResource(tabFocusIds[position]);
			convertView.setBackgroundResource(R.drawable.channel_list_sel);
		} else {
			item.tabView.setBackgroundResource(tabIds[position]);
			convertView.setBackgroundResource(R.drawable.channel_item_bg);
		}
		return convertView;
	}

	/**
	 * 列表项
	 */
	public  final class TabItem {
		public  ImageView tabView;
	}
}
