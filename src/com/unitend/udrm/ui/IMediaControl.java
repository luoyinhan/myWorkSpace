package com.unitend.udrm.ui;

public interface IMediaControl {
	
	public void play(String path);
	public long getCurrentPosition();
	public long getDuration();
	public boolean isPlaying();
	public void seekTo(long mSec);
	public void pause();
	public void resume();
	public void stop();
	public void Destroy();
	public void setVolume(int volume);
	public int  getVolume();
	public int getMaxVolume();
	public int getPlayState();
	public void adjustStreamVolume(int type);
	public int getBufferingPosition();

}
