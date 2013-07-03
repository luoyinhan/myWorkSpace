package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.constant.Constant;
import com.coship.ott.transport.dto.live.ChannelInfoJson;
import com.coship.ott.transport.dto.system.ClientVersionJson;
import com.coship.ott.transport.dto.system.SystemTimeJson;
import com.coship.ott.transport.dto.vod.HomePageContentJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.google.gson.reflect.TypeToken;

public class SystemAction extends BaseAction {

	private static final String LOGTAG = "SystemAction";

	/**
	 * 4.1 获取客户端启动画面
	 * 
	 * @param version
	 *            协议版本 String 20 必选
	 * @param homePageVersion
	 *            首页内容版本 Int 必选 当前版本，首次使用或重新获取送0
	 * @param terminalType
	 *            终端类型 Int 必选 1:PC 2:IPAD 3:IPNOE 4:ANDROID PHONE 5:ANDROID PAD
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @return HomePageContentJson
	 * 
	 * */
	public HomePageContentJson getHomePageConten(String url,
			int homePageVersion, String userCode) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(4).append("&resolution=")
				.append(Constant.RESOLUTION);
		urlBuffer.append("&homePageVersion=").append(homePageVersion)
				.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		HomePageContentJson homePageContent = null;
		try {
			homePageContent = gson.fromJson(jsonData,
					new TypeToken<HomePageContentJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getHomePageConten转换" + jsonData
					+ "为HomePageContentJson时出错！");
		}
		return homePageContent;
	}

	/**
	 * 4.40 查询更新
	 * 
	 * @param version
	 *            协议版本 String 必选
	 * @param terminalType
	 *            终端类型 Int 必选 1:PC 2:IPAD 3:IPNOE 4:ANDROID PHONE 5:ANDROID PAD
	 * @param currVersion
	 *            当前版本 String 必选 带客户端当前版本(后面带客户端保存的版本)
	 * @param userCode
	 *            用户ID String 32 可选
	 * @param userCode
	 *            appName Int 必选 1:PC 2:IPAD 3:IPNOE 4:ANDROID PHONE 5:ANDROID
	 *            PAD 6:手机摸摸看 7：PAD摸摸看8：EASYSHAREPHONE
	 * */
	public ClientVersionJson queryClientVersion(String url, int terminalType,
			String currVersion, String userCode) {
		StringBuffer urlbuf = new StringBuffer().append("?version=")
				.append("V002").append("&appName=").append(terminalType)
				.append("&terminalType=").append(Constant.TERMINAL_TYPE)
				.append("&resolution=").append(Constant.RESOLUTION)
				.append("&currVersion=").append(currVersion)
				.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlbuf.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ClientVersionJson jsonObject = null;
		try {
			jsonObject = gson.fromJson(jsonData,
					new TypeToken<ClientVersionJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法queryClientVersion转换" + jsonData
					+ "为ClientVersionJson时出错！");
		}
		return jsonObject;
	}

	/**
	 * 4.51 获取系统时间
	 * 
	 * @param resourceCode
	 *            频道ID String 必选
	 * @return {@link ChannelInfoJson}
	 * 
	 * */
	public SystemTimeJson getSystemTimeInfo(String url) {
		StringBuffer urlBuffer = urlbuf;
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		SystemTimeJson json = null;
		try {
			json = gson.fromJson(jsonData, new TypeToken<SystemTimeJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannelInfo转换" + jsonData
					+ "为ChannelInfoJson时出错！");
		}
		return json;
	}
}