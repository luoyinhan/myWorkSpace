package com.unitend.udrm.ui;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.constant.Constant;
import com.coship.ott.utils.Session;
import com.unitend.udrm.util.LibUDRM;

public class MediaControler implements IMediaControl {

	private static final String TAG = "MediaControler";

	private LibUDRM mLibUDRM = null;
	private Context mContext;
	private AudioManager mAudioManager = null;
	private String mUserNameStr;
	private String PWord;

	public MediaControler(Context context) {
		mContext = context;
		initialize(context);
	}

	private void initialize(Context context) {
		mUserNameStr = Session.getInstance().getUserName();
		PWord = Session.getInstance().getPassWord();
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mLibUDRM = DRMControl.mLibUDRM;
	}

	@Override
	public void play(String path) {
		Log.e(TAG, "play :" + path);
		if (TextUtils.isEmpty(path)) {
			return;
		}
		if (mLibUDRM == null) {
			Log.e(TAG, "mLibUDRM :" + mLibUDRM);
			return;
		}
		mLibUDRM.UDRMAgentOTTStart(mUserNameStr, PWord, Session.getInstance()
				.getMacPath(), Constant.DEVICENAME, path, path,
				mLibUDRM.mLibUDRMInstance);
	}

	@Override
	public boolean isPlaying() {
		if (mLibUDRM != null) {
			return mLibUDRM.isPlaying();
		}
		return false;
	}

	@Override
	public int getBufferingPosition() {
		if (mLibUDRM != null) {
			Log.d(TAG,
					"getBufferingPosition:"
							+ mLibUDRM.UDRMAgentOTTGetBufferingPostion());
			return mLibUDRM.UDRMAgentOTTGetBufferingPostion();
		}
		return 100;
	}

	@Override
	public int getPlayState() {
		if (mLibUDRM != null) {
			return mLibUDRM.UDRMAgentOTTGetPlayState();
		}
		return -1;
	}

	@Override
	public void adjustStreamVolume(int type) {
		if (mAudioManager != null) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, type,
					AudioManager.FLAG_SHOW_UI);
		}
	}

	@Override
	public long getDuration() {
		if (mLibUDRM != null) {
			Log.d(TAG, "getDuration:" + mLibUDRM.UDRMClientOTTGetDuration());
			return mLibUDRM.UDRMClientOTTGetDuration();
		}
		return 0;
	}

	@Override
	public long getCurrentPosition() {
		if (mLibUDRM != null) {
			Log.d(TAG,
					"getCurrentPosition:"
							+ mLibUDRM.UDRMClientOTTGetCurrentPlaybackTime());
			return mLibUDRM.UDRMClientOTTGetCurrentPlaybackTime();
		}
		return 0;
	}

	@Override
	public void seekTo(long mSec) {
		if (mLibUDRM != null) {
			Log.d(TAG, "seekTo:" + mSec);
			mLibUDRM.UDRMClientOTTSetCurrentPlaybackTime(mSec);
		}
	}

	@Override
	public void pause() {
		if (mLibUDRM != null) {
			Log.d(TAG, "pause");
			mLibUDRM.UDRMAgentOTTPause();
		}
	}

	@Override
	public void resume() {
		if (mLibUDRM != null) {
			Log.d(TAG, "resume");
			mLibUDRM.UDRMAgentOTTPause();
		}
	}

	@Override
	public void stop() {
		if (mLibUDRM != null) {
			Log.d(TAG, "stop");
			mLibUDRM.UDRMAgentOTTStop();
		}
	}

	@Override
	public void Destroy() {
		mLibUDRM = null;
	}

	@Override
	public void setVolume(int volume) {
		Log.d(TAG, "setVolume:" + volume);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}

	@Override
	public int getVolume() {
		Log.d(TAG,
				"getVolume:"
						+ mAudioManager
								.getStreamVolume(AudioManager.STREAM_MUSIC));
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	@Override
	public int getMaxVolume() {
		return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

}
