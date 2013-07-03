package com.coship.ott.transport.util;

import android.content.Context;
import android.content.SharedPreferences;

public class MulScreenSharePerfance {
	private static final String PREFS_NAME = "MulScreenSharePerfance";
	private static Context mContext;
	private SharedPreferences sharedPreferences;
	public static MulScreenSharePerfance mSharePerfance;
	public static final String HEAD_IMAGE_PATH = "HEAD_IMAGE_PATH";
	private MulScreenSharePerfance() {
		if (null != mContext) {
			sharedPreferences = mContext.getSharedPreferences(PREFS_NAME, 0);
		}
	}

	public static MulScreenSharePerfance getInstance(Context context) {
		mContext = context;
		if (null == mSharePerfance) {
			mSharePerfance = new MulScreenSharePerfance();
		}
		return mSharePerfance;
	}

	public Object getValue(String key, String type) {
		if ("Boolean".equals(type)) {
			return sharedPreferences.getBoolean(key, false);
		} else if ("Float".equals(type)) {
			return sharedPreferences.getFloat(key, 0.0f);
		} else if ("Integer".equals(type)) {
			return sharedPreferences.getInt(key, 0);
		} else if ("Long".equals(type)) {
			return sharedPreferences.getLong(key, 0L);
		} else if ("String".equals(type)) {
			return sharedPreferences.getString(key, "");
		}
		return null;
	}

	/**
	 * 从配置文件里读取值
	 * 
	 * @param key
	 *            键，String
	 * @param value
	 *            值，基本类型
	 * */
	public void putValue(String key, Object value) {
		// 保存当前已经更新的MID
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		}
		editor.commit();
	}
}
