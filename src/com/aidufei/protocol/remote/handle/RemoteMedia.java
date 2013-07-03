package com.aidufei.protocol.remote.handle;

import android.util.Log;
import com.aidufei.protocol.remote.callback.GetPlayInfoCallback;
import com.aidufei.protocol.remote.utils.SocketUtils;
import java.io.DataOutputStream;

public class RemoteMedia {
	private static final String TAG = "RemoteMedia";
	private GetPlayInfoCallback getPlayInfo;
	private final String HISI_FLAG = "Hisi";
	private boolean isAlive = false;

	public RemoteMedia(DeviceInfo dev) {
	}

	protected void destory() {
	}

	protected boolean getNetworkState() {
		return this.isAlive;
	}

	protected void setNetworkState(boolean isAlive) {
		this.isAlive = isAlive;
	}

	protected void sendKeepAliveMsg() throws Exception {
		SocketUtils.mediaOut.writeInt(775);
		SocketUtils.mediaOut.write("Hisi".getBytes());
		SocketUtils.mediaOut.flush();
	}

	protected void sendKeepMediaAlive() throws Exception {
		this.isAlive = false;
		Log.e("RemoteMedia", "send keep alive");

		for (int i = 0; i < 3; ++i) {
			sendKeepAliveMsg();
			Thread.sleep(2000L);
		}
	}

	protected void returnPlayingInfo(mediaInfo info) {
		if (this.getPlayInfo == null)
			return;
		this.getPlayInfo.returnPlayingInfo(true, info);
	}

	public void remotePlayMediaUrl(String programCode, int mediaType) {
		sendPlayMedia(programCode, mediaType);
	}

	public void getMediaInfo(String programCode, GetPlayInfoCallback callback) {
		this.getPlayInfo = callback;
		sendGetPlayInfo(programCode);
	}

	private void sendGetPlayInfo(String programCode) {
		try {
			Log.e("RemoteMedia", "send get media info:" + programCode);
			SocketUtils.mediaOut.writeInt(773);
			SocketUtils.mediaOut.write("Hisi".getBytes());
			byte[] buf = programCode.getBytes();
			SocketUtils.mediaOut.writeInt(buf.length);
			SocketUtils.mediaOut.write(buf);
			SocketUtils.mediaOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendPlayMedia(String url, int mediaType) {
		int urlLen = 0;
		urlLen = url.length();
		Log.e("RemoteMedia", "send play media urlen:" + urlLen + " url:" + url);
		try {
			SocketUtils.mediaOut.writeInt(772);
			SocketUtils.mediaOut.write("Hisi".getBytes());
			SocketUtils.mediaOut.writeInt(mediaType);
			SocketUtils.mediaOut.writeInt(urlLen);
			SocketUtils.mediaOut.write(url.getBytes());
			SocketUtils.mediaOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
