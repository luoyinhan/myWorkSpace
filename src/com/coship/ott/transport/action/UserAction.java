package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.constant.Constant;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.auth.AuthInfoJson;
import com.coship.ott.transport.dto.share.ShareJson;
import com.coship.ott.transport.dto.user.LoginJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.Session;
import com.google.gson.reflect.TypeToken;

public class UserAction extends BaseAction {

	private static final String LOGTAG = "FavoriteAction";

	/**
	 * 4.33 用户登录
	 * 
	 * @param userName
	 *            OTT账号 String 必选
	 * @param passwd
	 *            用户密码 String 20 必选
	 * @return {@link loginJson}
	 * 
	 * */
	public LoginJson login(String url, String userName, String passwd) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalName=").append("AndroidPad")
				.append("&resolution=").append(Constant.RESOLUTION);
		urlBuffer.append("&userName=").append(userName).append("&passwd=")
				.append(passwd);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		LoginJson loginJson = null;
		try {
			loginJson = gson.fromJson(jsonData, new TypeToken<LoginJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法login转换" + jsonData + "为LoginJson时出错！");
		}
		return loginJson;
	}

	/**
	 * 4.34 用户鉴权
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param resourceCode
	 *            资源代码 String 64 必选 可以是频道也可以是影片
	 * @param playType
	 *            播放类型 枚举 2 必选 枚举值：1 点播(调用接口Aa-20接口)2 直播(批量鉴权接口)
	 * @return {@link AuthInfoJson}
	 * 
	 * */
	public AuthInfoJson auth(String url, String userCode, String resourceCode,
			int playType) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode)
				.append("&resourceCode=").append(resourceCode)
				.append("&playType=").append(playType);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AuthInfoJson authInfoJson = null;
		try {
			authInfoJson = gson.fromJson(jsonData,
					new TypeToken<AuthInfoJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法auth转换" + jsonData + "为AuthInfoJson时出错！");
		}
		return authInfoJson;
	}

	/**
	 * 4.37 用户反馈
	 * 
	 * @param userCode
	 *            用户Id String 16 必选
	 * @param feedbackType
	 *            反馈类型 Int 1 可选
	 * @param feedback
	 *            反馈信息 String 300 必选
	 * @param phone
	 *            联系电话 String 32 可选
	 * @param email
	 *            联系邮箱 String 32 可选
	 * @param qq
	 *            联系QQ String 20 可选
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean addUserFeedBack(String url, String userCode,
			int feedbackType, String feedback, String phone, String email,
			String qq) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode)
				.append("&feedbackType=").append(feedbackType)
				.append("&feedback=").append(feedback).append("&phone=")
				.append(phone).append("&email=").append(email).append("&qq=")
				.append(qq);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<BaseJsonBean>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法addUserFeedBack转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 4.39 记录用户分享
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param UserName
	 *            OTT账号 String 可选
	 * @param objType
	 *            评论对象 Int 1 必选 1：直播节目2：点播节目3：频道品牌
	 * @param objID
	 *            对象ID String 32 必选
	 * @return {@link ShareJson}
	 * */
	public BaseJsonBean addUserShare(String url, String userCode,
			String UserName, int objType, String objID) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&objType=")
				.append(objType).append("&objID=").append(objID);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<BaseJsonBean>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法addUserShare转换" + jsonData + "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 4.40 查询用户分享
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param userName
	 *            OTT账号 String 可选
	 * @return {@link ShareJson}
	 * */
	public ShareJson queryUserShare(String url, String userCode, String userName) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ShareJson shareJson = null;
		try {
			shareJson = gson.fromJson(jsonData, new TypeToken<ShareJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法addUserShare转换" + jsonData + "为ShareJson时出错！");
		}
		return shareJson;
	}
}