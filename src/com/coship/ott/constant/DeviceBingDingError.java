package com.coship.ott.constant;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.coship.ott.utils.LogUtils;
import com.unitend.udrm.util.LibUDRM;

public class DeviceBingDingError {
	private final static String TAG = "DeviceBingDingError";

	public int getDebugError(LibUDRM mLibUDRM, Context mContext) {
		int[] u32ErrorMsgDebugLength = new int[2];
		byte[] pchErrorMsgDebug = new byte[1024];
		u32ErrorMsgDebugLength[0] = 1024;
		int debugError = mLibUDRM.UDRMAgentGetDebugError(pchErrorMsgDebug,
				u32ErrorMsgDebugLength);
		String debugErrorInfo = new String(pchErrorMsgDebug, 0,
				u32ErrorMsgDebugLength[1]);
		LogUtils.trace(Log.INFO, TAG, "错误号: " + debugError + "  错误信息: "
				+ debugErrorInfo);
		if (debugError != 0) {
			if (-68 == debugError) {
				Toast.makeText(mContext, "网络异常请重试！", Toast.LENGTH_SHORT).show();
			} else if (-9 == debugError) {
				Toast.makeText(mContext, "当前您的设备还未绑定设备！", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(mContext, "当前设备绑定错误！", Toast.LENGTH_SHORT)
						.show();
				LogUtils.trace(Log.INFO, TAG, "=====>>" + "错误号: " + debugError
						+ "  错误信息: " + debugErrorInfo);
			}

		}
		return debugError;
	}
}
