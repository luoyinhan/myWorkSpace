package com.coship.ott.fragment;

import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.coship.ott.activity.OrderTaoCanadapter;
import com.coship.ott.activity.R;
import com.coship.ott.activity.UnOrderTaoCanadapter;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.ProductInfo;
import com.coship.ott.transport.dto.ProductInfoJson;
import com.coship.ott.utils.Session;

public class PermissionComboFragment extends Fragment {
	private TextView mUserNametx, mCardNOtx;
	private ListView mOrderList, mNotOrderList;
	private String mUserName;// 用户名
	private String mCardNO;// 绑定智能卡号
	private List<ProductInfo> orderProductInfos;// 权限套餐已订购套餐列表
	private OrderTaoCanadapter orderadapter;
	private List<ProductInfo> unorderProductInfos;// 权限套餐未订购套餐列表
	private UnOrderTaoCanadapter unorderadapter;
	private GetOrderTaoCanTask mGetOrderTaoCanTask;
	private GetNotOrderTaoCanTask mGetNotOrderTaoCanTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.permission_combo_fragment, container,
				false);
	}

	@Override
	public void onStart() {
		super.onStart();
		setupView();
		initDate();
	}

	private void setupView() {
		mUserNametx = (TextView) getActivity().findViewById(R.id.userName);
		mCardNOtx = (TextView) getActivity().findViewById(R.id.cardNumber);
		mOrderList = (ListView) getActivity().findViewById(
				R.id.order_taocan_list);
		mNotOrderList = (ListView) getActivity().findViewById(
				R.id.not_order_taocan_list);
	}

	private void initDate() {
		mUserName = Session.getInstance().getUserName();
		mUserNametx.setText(mUserName);
		mCardNO = Session.getInstance().getBindNo();
		if (TextUtils.isEmpty(mCardNO)) {
			mCardNOtx.setText("您还未绑定智能卡");
		} else {
			mCardNOtx.setText(mCardNO);
		}
		initTaoCanList();
	}

	private void initTaoCanList() {
		mGetOrderTaoCanTask = new GetOrderTaoCanTask();
		mGetOrderTaoCanTask.execute();
		mGetNotOrderTaoCanTask = new GetNotOrderTaoCanTask();
		mGetNotOrderTaoCanTask.execute();
	}

	/**
	 * 权限套餐，获取订购套餐列表
	 * 
	 */
	private class GetOrderTaoCanTask extends
			AsyncTask<Void, Void, ProductInfoJson> {

		@Override
		protected ProductInfoJson doInBackground(Void... params) {
			return new UserCenterAction().queryOrderedProduct(
					InterfaceUrls.QUERY_ORDERED_PRODUCT, mUserName);
		}

		@Override
		protected void onPostExecute(ProductInfoJson result) {
			if (result != null && result.getRet() == 0) {
				orderProductInfos = result.getDatas();
				orderadapter = new OrderTaoCanadapter(getActivity(),
						orderProductInfos, mUserName, mCardNO);
				mOrderList.setAdapter(orderadapter);
			}
		}
	}

	/**
	 * 权限套餐，获取未订购套餐列表
	 * 
	 */
	private class GetNotOrderTaoCanTask extends
			AsyncTask<Void, Void, ProductInfoJson> {
		@Override
		protected ProductInfoJson doInBackground(Void... params) {
			return new UserCenterAction().queryOrderedProduct(
					InterfaceUrls.QUERY_UNORDERED_PRODUCT, mUserName);
		}

		@Override
		protected void onPostExecute(ProductInfoJson result) {
			if (result != null && result.getRet() == 0) {
				unorderProductInfos = result.getDatas();
				unorderadapter = new UnOrderTaoCanadapter(getActivity(),
						unorderProductInfos, mUserName, mCardNO);
				mNotOrderList.setAdapter(unorderadapter);
			}
		}
	}
}
