package com.coship.ott.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.NoticeAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.notice.Notice;
import com.coship.ott.transport.dto.notice.NoticeJson;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.AppManager;

public class NoticeActivity extends ExpandableListActivity {
	private static final String Tag = "NoticeActivity";
	private Context mContext;
	List<String[]> mGroup;// 标题主栏目
	List<List<String>> mChild;// 内容子栏目
	ContactsInfoAdapter mAdapter;
	private List<Notice> notices;
	private ScrollLoader noticesLoader;
	private boolean isloading = false;
	private ImageView mNoticeExitBtn;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		setupView();
		initNoticeData(1);
		mAdapter = new ContactsInfoAdapter();
		getExpandableListView().setAdapter(mAdapter);
		getExpandableListView().setChildDivider(
				getResources().getDrawable(R.drawable.channel_list_divider));
		getExpandableListView().setGroupIndicator(null);
	}

	private void initNoticeData(final int pageNo) {
		// 获得公告的内容
		new AsyncTask<Void, Void, NoticeJson>() {
			@Override
			protected NoticeJson doInBackground(Void... params) {
				return new NoticeAction().getNotices(InterfaceUrls.GET_NOTICES,
						10, pageNo);
			}

			@Override
			protected void onPostExecute(NoticeJson result) {
				boolean bol = BaseJsonBean.checkResult(mContext, result);
				if (bol) {
					// 初始化公告
					notices = result.getNotices();
					noticesLoader.setCurPage(result.getCurPage());
					noticesLoader.setPageCount(result.getPageCount());
					for (Notice info : notices) {
						String[] groupInfo = { info.getTitle(),
								info.getEffectiveTime() };
						mGroup.add(groupInfo);
						List<String> childitem = new ArrayList<String>();
						String content = info.getContent();
						childitem.add(content);
						mChild.add(childitem);
					}
					mAdapter.notifyDataSetChanged();
				}
			}
		}.execute();
	}

	private void setupView() {
		mInflater = LayoutInflater.from(this);
		mNoticeExitBtn = (ImageView) findViewById(R.id.notice_exitbtn);
		mNoticeExitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				CommonViewActivity.mCount = 0;
			}
		});
		mGroup = new ArrayList<String[]>();
		mChild = new ArrayList<List<String>>();
		noticesLoader = new ScrollLoader(mContext, getExpandableListView(),
				new CallBack() {
					@Override
					public void loadData(int pageNo) {
						if (!isloading) {
							initNoticeData(pageNo);
						}
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CommonViewActivity.mCount = 0;
	}

	/**
	 * 公告头部信息
	 */
	public final class ViewHolder {
		public TextView notice_title;
		public ImageView notice_tip; // 展开图标
		public TextView notice_time;
	}

	class ContactsInfoAdapter extends BaseExpandableListAdapter {

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChild.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mChild.get(groupPosition).size();
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			String string = mChild.get(groupPosition).get(childPosition);
			return getGenericView(string);
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.notice_group, null);
				holder.notice_title = (TextView) convertView
						.findViewById(R.id.notice_title);
				holder.notice_tip = (ImageView) convertView
						.findViewById(R.id.notice_tip);
				holder.notice_time = (TextView) convertView
						.findViewById(R.id.notice_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.notice_title.setText(mGroup.get(groupPosition)[0]);
			holder.notice_time.setText("有效期：" + mGroup.get(groupPosition)[1]);
			if (isExpanded) {
				holder.notice_tip.setImageResource(R.drawable.notice_follow);
			} else {
				holder.notice_tip.setImageResource(R.drawable.notice_up);
			}
			return convertView;
		}

		public TextView getGenericView(String s) {
			// 设置宽和高
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			TextView text = new TextView(mContext);
			text.setLayoutParams(lp);
			text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			text.setPadding(30, 10, 10, 20);
			text.setTextColor(Color.parseColor("#787878"));
			text.setBackgroundResource(R.drawable.notice_child_bg);
			text.setText(s);
			return text;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}