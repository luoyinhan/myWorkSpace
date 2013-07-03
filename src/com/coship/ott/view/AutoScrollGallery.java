package com.coship.ott.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.coship.ott.activity.RecommendActivity;
import com.coship.ott.transport.dto.vod.IndexRem;

public class AutoScrollGallery extends Gallery {

	public AutoScrollGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AutoScrollGallery(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init();
	}

	// 初始化
	private void init() {
		this.setAnimationDuration(3000);
		this.setSpacing(-1);
		this.setSelection(0);
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

	float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) { // Check if scrolling left
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else { // Otherwise scrolling right
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		// int size=RecommendActivity.headResources.size();
		// if (this.getSelectedItemPosition() == 0) {// 实现后退功能
		// this.setSelection(RecommendActivity.headResources.size()-1);
		// }
		// if(this.getSelectedItemPosition() > size-1){
		// this.setSelection(0);
		// }
		return true;

	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	/**
	 * 头部滚动适配器
	 * */
	public abstract class TopGalleryAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private List<ImageView> items = new ArrayList<ImageView>();
		private ArrayList<IndexRem> headResources = new ArrayList<IndexRem>();

		/** 存储Context */
		public TopGalleryAdapter(ArrayList<IndexRem> headResources) {
			this.headResources = headResources;
		}

		/** 获得图片数量 */
		public int getCount() {
			if (null == this.headResources) {
				return 0;
			}
			return headResources.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/** 取得显示图像View,传入数组ID值读取数组图像 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}
	}
}