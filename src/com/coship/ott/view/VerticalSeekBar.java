package com.coship.ott.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class VerticalSeekBar extends View {

	private Context mContext;
	private AudioManager mAudioManager; // 音量
	private Bitmap focusBmp, normalBmp;
	private int width, height;
	private float curVolume;
	// 播控条最大音量
	private float maxVolume = 10;
	// 设备最大音量
	private int deviceMaxVolume;
	private float rate = 0.0f;
	private OnVolumeChangedListener mOnVolumeChangedListener;

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initBitmap(context, attrs);
		initVolume();
	}

	/**
	 * 从配置文件里获取表示音量的图片
	 * */
	private void initBitmap(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.VerticalSeekBar);
		// 浅色的声音条
		Drawable normalDrawable = a
				.getDrawable(R.styleable.VerticalSeekBar_normal);
		if (null != normalDrawable) {
			normalBmp = drawableToBitmap(normalDrawable);
		} else {
			normalBmp = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.volume);
		}
		// 深色的声音条
		Drawable focusDrawable = a
				.getDrawable(R.styleable.VerticalSeekBar_focus);
		if (null != focusDrawable) {
			focusBmp = drawableToBitmap(focusDrawable);
		} else {
			focusBmp = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.volume_sel);
		}
		width = focusBmp.getWidth();
		height = focusBmp.getHeight();
	}

	private void initVolume() {
		// 获取系统声音服务
		mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		deviceMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// 只有10个
		rate = deviceMaxVolume / maxVolume;
		curVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
		LogUtils.trace(Log.DEBUG, "initVolume", "curVolume: " + curVolume);
		// 解决初始化时，音量最大，但拖动条显示音量为0的问题。
		curVolume = maxVolume - curVolume / rate;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY();
		float n = y / height / 2;
		n = Math.min(n, maxVolume);
		n = Math.max(0, n);
		if (curVolume != n) {
			curVolume = n;
			invalidate();
			if (mOnVolumeChangedListener != null) {
				mOnVolumeChangedListener
						.setYourVolume((int) (rate * (maxVolume - curVolume)));
			}
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < maxVolume; i++) {// 音量背景
			if (i < curVolume) {
				canvas.drawBitmap(normalBmp, null, new Rect(0, 2 * i * height,
						width, 2 * i * height + height), null);
			} else {
				canvas.drawBitmap(focusBmp, null, new Rect(0, 2 * i * height,
						width, 2 * i * height + height), null);
			}
		}
		super.onDraw(canvas);
	}

	public void setIndex(int n) {
		curVolume = maxVolume - (int) (n / rate);
		invalidate();
	}

	public int getVolume() {
		return (int) Math.floor(curVolume);
	}

	public void setOnVolumeChangedListener(OnVolumeChangedListener l) {
		mOnVolumeChangedListener = l;
	}

	public interface OnVolumeChangedListener {
		public void setYourVolume(int index);
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
}