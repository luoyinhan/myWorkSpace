package com.aidufei.protocol.remote.callback;

import com.aidufei.protocol.remote.handle.mediaInfo;

public abstract interface GetPlayInfoCallback {
	public abstract void returnPlayingInfo(boolean paramBoolean,
			mediaInfo parammediaInfo);
}
