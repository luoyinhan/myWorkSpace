package com.coship.ott.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.coship.ott.transport.dto.ProductInfo;

public class OrderTaoCanadapter extends BaseAdapter {
	private List<ProductInfo> mDate;
	private LayoutInflater mInflater;
	private String userName, CardNum;
	private Context mContext;
	private String name, price, code;
	private int index;

	public OrderTaoCanadapter(Context mContext,
			List<ProductInfo> orderProductInfos, String name, String CardNum) {
		super();
		this.mContext = mContext;
		this.mDate = orderProductInfos;
		this.userName = name;
		this.CardNum = CardNum;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if (mDate != null) {
			return mDate.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		index = position;
		return mDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.order_taocan_item, parent,
				false);
		ViewHole holdView = new ViewHole();
		holdView.taocan = (TextView) convertView.findViewById(R.id.taocan);
		holdView.price = (TextView) convertView.findViewById(R.id.price);
		holdView.unsubscribe = (Button) convertView
				.findViewById(R.id.unsubscribe);
		name = mDate.get(position).getProductName();
		price = mDate.get(position).getPrice();
		code = mDate.get(position).getProductCode();
		holdView.taocan.setText(name);
		holdView.price.setText(price);
		holdView.unsubscribe.setText("退订");
		holdView.unsubscribe.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, OrderTaoCanActivity.class);
				intent.putExtra("userName", userName);
				intent.putExtra("name", mDate.get(position).getProductName());
				intent.putExtra("CardNum", CardNum);
				intent.putExtra("price", mDate.get(position).getPrice());
				intent.putExtra("code", mDate.get(position).getProductCode());
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	static class ViewHole {
		private TextView taocan;
		private TextView price;
		private Button unsubscribe;

	}
}
