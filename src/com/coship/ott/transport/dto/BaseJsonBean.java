package com.coship.ott.transport.dto;

import android.content.Context;
import android.widget.Toast;

import com.coship.ott.utils.ToastUtils;

/**
 * JSON返回数据基础类
 * */
public class BaseJsonBean {
	/**
	 * 结果
	 * */
	private int ret;
	/**
	 * 提示信息
	 * */
	private String retInfo;

	public BaseJsonBean() {
	}

	public BaseJsonBean(int ret, String retInfo) {
		super();
		this.ret = ret;
		this.retInfo = retInfo;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public String getRetInfo() {
		return retInfo;
	}

	public void setRetInfo(String retInfo) {
		this.retInfo = retInfo;
	}

	/**
	 * 检查JSON返回数据是否正常 <br/>
	 * 默认提示为：获取服务端数据失败失败 <br/>
	 * 
	 * @param context
	 *            mContext
	 * @param result
	 *            返回的数据格式为：BaseJsonBean
	 * @param msg
	 *            提示信息
	 * @return 数据正常为true,异常为false
	 */
	public static boolean checkResult(Context context, BaseJsonBean result) {
		boolean bol = true;
		if (null == result) {
			Toast.makeText(context, "获取服务端数据失败异常！", Toast.LENGTH_SHORT).show();
			bol = false;
		} else if (0 != result.getRet()) {
			Toast.makeText(context, "获取服务端数据失败失败：" + result.getRetInfo(),
					Toast.LENGTH_SHORT).show();
			bol = false;
		}
		return bol;
	}

	/**
	 * 检查JSON返回数据是否正常
	 * 
	 * @param context
	 *            mContext
	 * @param result
	 *            返回的数据格式为：BaseJsonBean
	 * @param msg
	 *            提示信息
	 * @return 数据正常为true,异常为false
	 */
	public static boolean checkResult(Context context, BaseJsonBean result,
			String msg) {
		boolean bol = true;
		if (null == result) {
			Toast.makeText(context, msg + "异常！", Toast.LENGTH_SHORT).show();
			bol = false;
		} else if (0 != result.getRet()) {
			Toast.makeText(context, msg + "失败：" + result.getRetInfo(),
					Toast.LENGTH_SHORT).show();
			bol = false;
		}
		return bol;
	}
}
