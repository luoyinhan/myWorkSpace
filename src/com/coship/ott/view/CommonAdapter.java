package com.coship.ott.view;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommonAdapter extends BaseAdapter {
	protected ArrayList datas = new ArrayList();

	@Override
	public int getCount() {
		if (null == datas) {
			return 0;
		}
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public void addNewDatas(ArrayList addDatas) {
		if (null == addDatas) {
			return;
		}
		// 列表
		for (Object data : addDatas) {
			this.datas.add(data);
		}
		this.notifyDataSetChanged();
	}

	public void addNewDatas(Integer[] addDatas) {
		if (null == addDatas) {
			return;
		}
		// 列表
		for (Object data : addDatas) {
			this.datas.add(data);
		}
		this.notifyDataSetChanged();
	}

	public void removeAllDatas() {
		this.datas.removeAll(datas);
		this.notifyDataSetChanged();
	}

	public void removeData(Object obj) {
		this.datas.remove(obj);
		this.notifyDataSetChanged();
	}

	public Object getItemData(int position) {
		if (0 > position || position >= getCount()) {
			return null;
		}
		return this.datas.get(position);
	}
}