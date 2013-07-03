package com.coship.ott.transport.util;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import com.coship.ott.activity.R;

public class ScrollLoader {
	private Context mContext;
	private AbsListView mView;
	private CallBack mCallBack;
	// 是否正在加载数据
	private boolean isLoadingData;
	// 当前页页码
	private int curPage;
	// 总页数
	private int pageCount;

	private boolean isScorllDown = true;

	public ScrollLoader(Context context, AbsListView listView, CallBack callBack) {
		this.mContext = context;
		this.mView = listView;
		this.mCallBack = callBack;
		this.mView.setOnScrollListener(onScrollListener);
	}

	// 滚动事件监听，用于检测滑动到底部时启动数据加载线程
	protected OnScrollListener onScrollListener = new OnScrollListener() {
		// 加载数据页数 1:往后加载一页 -1：往前加载一页
		private int mFirstVisibleItem = 0;
		private int mVisibleItemCount = 0;
		private int mTotalItemCount = 0;

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) { // 是否停止滑动
				return;
			}
			if (mFirstVisibleItem + mVisibleItemCount != mTotalItemCount) {// 判断是否滑动到底部
				return;
			}
			if (isLoadingData) { // 是否正在加载数据
				return;
			}
			if (curPage < pageCount) {
				// 数据加载
				mCallBack.loadData(curPage + 1);
			} else if (curPage >= pageCount/* && isScorllDown */) { // 是否是最后一页
				curPage = pageCount;
				Toast.makeText(mContext, R.string.is_last_page,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (mFirstVisibleItem < firstVisibleItem) {
				isScorllDown = true;
			} else {
				isScorllDown = false;
			}
			mFirstVisibleItem = firstVisibleItem;
			mVisibleItemCount = visibleItemCount;
			mTotalItemCount = totalItemCount;
		}
	};

	public boolean isLoadingData() {
		return isLoadingData;
	}

	public void setLoadingData(boolean isLoadingData) {
		this.isLoadingData = isLoadingData;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public interface CallBack {
		public void loadData(int pageNo);
	}
}