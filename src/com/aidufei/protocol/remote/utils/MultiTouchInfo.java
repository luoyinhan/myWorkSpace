package com.aidufei.protocol.remote.utils;

import android.util.Log;
import java.util.ArrayList;

public class MultiTouchInfo {
	public static final int MAX_FINGER_NUM = 5;
	private int fingerNum = 0;
	private ArrayList<FingerInfo> fingers = new ArrayList();

	public MultiTouchInfo() {
		for (int i = 0; i < 5; ++i) {
			this.fingers.add(new FingerInfo());
		}
	}

	public void setFingerNum(int num) {
		this.fingerNum = num;
	}

	public int getFingerNum() {
		return this.fingerNum;
	}

	public void setFingerInfo(int index, int x, int y, int press) {
		FingerInfo info = (FingerInfo) this.fingers.get(index);
		info.setX(x);
		info.setY(y);
		info.setPress(press);
	}

	public FingerInfo getFingerInfo(int index) {
		return (FingerInfo) this.fingers.get(index);
	}

	public void print() {
		Log.e("multiTouchInfo", "finger Num: " + this.fingerNum);
		for (int i = 0; i < 5; ++i) {
			FingerInfo info = (FingerInfo) this.fingers.get(i);
			Log.e("multiTouchInfo", "finger " + i + " x: " + info.getX()
					+ " y: " + info.getY() + " press: " + info.getPress());
		}
	}
}
