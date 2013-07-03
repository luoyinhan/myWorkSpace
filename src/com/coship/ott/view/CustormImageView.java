package com.coship.ott.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.coship.ott.activity.R;
import com.coship.ott.constant.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 可以直接设置http://地址的ImageView，会自己去下载图片并做本地缓存。
 * */
public class CustormImageView extends ImageView {
	ImageLoader mImageLoader = ImageLoader.getInstance();

	public CustormImageView(Context context) {
		super(context);
	}

	public CustormImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setImageHttpUrl(String url) {
		mImageLoader.displayImage(url, this);
	}

}