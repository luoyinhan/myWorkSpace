package com.aidufei.protocol.remote.callback;

import com.aidufei.protocol.remote.utils.AppInfo;
import java.util.ArrayList;

public abstract interface GetAppListCallBack {
	public abstract void returnAppList(ArrayList<AppInfo> paramArrayList);
}
