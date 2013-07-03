package com.coship.ott.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.coship.ott.transport.util.UpdateUtils;

public class FirstUseActivity extends Activity implements OnGestureListener {
	private Context mContext;
	private Button begin_use;
	private Button guide_next;
	private GestureDetector detector;
	private ViewFlipper flipper;
	private int mIndexHelpPic = 0;
	private int[] mBgList = { R.drawable.guide_page1, R.drawable.guide_page2,
			R.drawable.guide_page3, R.drawable.guide_page4 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_use);
		mContext = this;
		flipper = (ViewFlipper) this.findViewById(R.id.viewflipper);
		flipper.addView(addImageByID(mBgList[mIndexHelpPic]),
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
		detector = new GestureDetector(this);
		guide_next = (Button) findViewById(R.id.btn_guide);
		begin_use = (Button) findViewById(R.id.btn_beginuse);
		guide_next.setOnClickListener(guideNextClickListener());
		begin_use.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startApp();
			}
		});
	}

	private OnClickListener guideNextClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				mIndexHelpPic++;
				if (mIndexHelpPic == mBgList.length - 1) {
					begin_use.setVisibility(View.VISIBLE);
					guide_next.setVisibility(View.GONE);
				} else if (mIndexHelpPic >= mBgList.length) {
					mIndexHelpPic = mBgList.length - 1;
				}
				flipperAnimation();
			}
		};
	}

	private void startApp() {
		MainTabHostActivity.setFirstUseState(mContext,
				UpdateUtils.getVerName(mContext));
		Intent in = new Intent(mContext, MainTabHostActivity.class);
		in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除以前的所有activity
		in.putExtra("isCancel", true);
		mContext.startActivity(in);
		finish();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 120) {
			mIndexHelpPic++;
			if (mIndexHelpPic == mBgList.length - 1) {
				begin_use.setVisibility(View.VISIBLE);
				guide_next.setVisibility(View.GONE);
			} else if (mIndexHelpPic >= mBgList.length) {
				mIndexHelpPic = mBgList.length - 1;
				startApp();// 开始应用
				return true;
			}
			flipperAnimation();
		} else if (e1.getX() - e2.getX() < -120) {
			mIndexHelpPic--;
			if (mIndexHelpPic < mBgList.length - 1 && mIndexHelpPic >= 0) {
				begin_use.setVisibility(View.GONE);
				guide_next.setVisibility(View.VISIBLE);
			} else if (mIndexHelpPic < 0) {
				mIndexHelpPic = 0;
				return true;
			}
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.flipper.showPrevious();
			return true;
		}
		return true;
	}

	private void flipperAnimation() {
		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));
		flipper.addView(addImageByID(mBgList[mIndexHelpPic]),
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
		flipper.showNext();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	private View addImageByID(int id) {
		ImageView img = new ImageView(this);
		img.setImageResource(id);
		img.setAdjustViewBounds(true);
		img.setScaleType(ImageView.ScaleType.FIT_XY);
		return img;
	}
}
