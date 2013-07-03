package com.aidufei.protocol.remote.callback;

import com.aidufei.protocol.remote.message.ChannelInfor;
import java.util.ArrayList;

public abstract interface GetEpgListCallBack {
	public abstract void returnEpgList(ArrayList<ChannelInfor> paramArrayList);
}
