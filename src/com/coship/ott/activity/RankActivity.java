package com.coship.ott.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.live.ChannelInfo;
import com.coship.ott.transport.dto.special.SpecialAct;
import com.coship.ott.transport.dto.special.SpecialActsJson;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.PlayerUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.view.CommonAdapter;

public class RankActivity extends CommonViewActivity {
	private Context mContext = null;
	private WebView wv;
	private ListView mSpecialList;
	private SpecialListAdapter mSpecialAdapter;
	private int mSelectItem;
	private ArrayList<SpecialAct> mSpecialAct;
	private TextView titleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rank);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		titleText = (TextView) this.findViewById(R.id.titleTxt);
		titleText.setText(R.string.title_special);
		// 初始化页面元素
		// setupView();
		wv = (WebView) findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new DemoJavaScriptInterface(), "callPlay");
		// 触摸焦点起作用
		wv.requestFocus();
		setupView();
		initSpecialList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 隐藏公告标题
		notice = (ImageView) findViewById(R.id.notice);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		noticeFull.setVisibility(View.GONE);
		notice.setVisibility(View.GONE);
	}

	final class DemoJavaScriptInterface {
		DemoJavaScriptInterface() {
		}

		/**
		 * 该方法被浏览器端调用
		 */
		public void clickOnAndroid(int playType, String resourceCode,
				String beginTime, String endTime) {
			Log.e("result-", playType + " " + resourceCode + " " + beginTime
					+ " " + endTime);
			switch (playType) {
			case 1:// 点播
				PlayerUtil.playVod(mContext, resourceCode, 0, null, null, null,
						null);
				break;
			case 2:// 直播
				PlayerUtil.playLive(mContext, resourceCode, 0, null, null);
				break;
			case 3:// 回看
				if (TextUtils.isEmpty(beginTime) && TextUtils.isEmpty(endTime)) {
					PlayerUtil.playShift(mContext, resourceCode,
							Long.parseLong(beginTime), Long.parseLong(endTime),
							0, null, null, null);
				}
				break;

			default:
				break;
			}
		}
	}

	private void setupView() {
		mSpecialList = (ListView) findViewById(R.id.special_list);
		mSpecialAdapter = new SpecialListAdapter();
		mSpecialList.setAdapter(mSpecialAdapter);
		mSpecialList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectItem = position;
				mSpecialAdapter.notifyDataSetChanged();
				SpecialAct specialact = mSpecialAct.get(position);
				// 设置WevView要显示的网页：
				wv.loadUrl(specialact.getPadActURL());
			}
		});
	}

	// 获得专题列表
	private void initSpecialList() {
		new AsyncTask<Void, Void, SpecialActsJson>() {
			@Override
			protected SpecialActsJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getSpecialAct(
						InterfaceUrls.GET_SPECIALACT, session.getUserCode(),
						session.getUserName(), 1, 10);
			}

			@Override
			protected void onPostExecute(SpecialActsJson result) {
				boolean bol = BaseJsonBean.checkResult(mContext, result);
				if (bol && 0 == result.getRet()) {
					mSpecialAct = result.getSpecialAct();
					mSpecialAdapter.addNewDatas(mSpecialAct);// 将数据注入Adapter
					mSpecialAdapter.notifyDataSetChanged();
					if (mSpecialAct != null && mSpecialAct.size() > 0) {
						wv.loadUrl(mSpecialAct.get(0).getPadActURL());
					}
				}
			}
		}.execute();
	}

	/**
	 * 频道列表元素项
	 */
	public final class ViewHolder {
		public TextView special_name;
		public ImageView special_dot;
	}

	/**
	 * 频道列表数据适配器
	 */
	public class SpecialListAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			final SpecialAct specialAct = (SpecialAct) datas.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(mContext);
				convertView = mInflater
						.inflate(R.layout.special_act_list, null);
				holder.special_name = (TextView) convertView
						.findViewById(R.id.special_name);
				holder.special_dot = (ImageView) convertView
						.findViewById(R.id.special_dot);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (specialAct != null) {
				holder.special_name.setText(specialAct.getSpecialActName());
			}
			if (position == mSelectItem) {
				// 更新选中UI
				convertView.setBackgroundResource(R.drawable.special_item_bg);
				holder.special_dot.setImageResource(R.drawable.special_dot_sel);
				holder.special_name.setTextColor(Color.parseColor("#FFFFFF"));
			} else {
				convertView.setBackgroundResource(0);
				holder.special_dot.setImageResource(R.drawable.special_dot);
				holder.special_name.setTextColor(Color.parseColor("#9A9894"));
			}
			return convertView;
		}
	}

}