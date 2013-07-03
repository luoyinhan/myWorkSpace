package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.book.BooksJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.google.gson.reflect.TypeToken;

public class BookAction extends BaseAction {

	private static final String LOGTAG = "BookAction";

	/**
	 * 4.26 预定节目
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param programId
	 *            节目单ID 数字 必选
	 * @param channelResourceCode
	 *            频道ID 数字 必选
	 * @return {@link BaseJsonBean}
	 * 
	 * */
	public BaseJsonBean addBook(String url, String userCode, String programId,
			String channelResourceCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&programId=")
				.append(programId).append("&ChannelResourceCode=")
				.append(channelResourceCode);
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
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法addBook转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 4.27 查看预定
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @return {@link BooksJson}
	 * 
	 * */
	public BooksJson queryBook(String url, String userCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BooksJson booksJson = null;
		try {
			booksJson = gson.fromJson(jsonData, new TypeToken<BooksJson>() {
			}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法queryBook转换" + jsonData
					+ "为BooksJson时出错！");
		}
		return booksJson;
	}

	/**
	 * 4.28 删除预订
	 * 
	 * @param userCode
	 *            用户Id String 16 必选
	 * @param programId
	 *            节目单ID 数字 必选
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean delBook(String url, String userCode, String programId) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&programId=")
				.append(programId);
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
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法delBook转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}
}