package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.bookmark.BookMarkJson;
import com.coship.ott.transport.dto.bookmark.BookMarksJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.google.gson.reflect.TypeToken;

public class BookMarkAction extends BaseAction {

	private static final String LOGTAG = "BookAction";

	/**
	 * 4.30 记录书签
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param userName
	 *            OTT账号 String 可选
	 * @param ResourceCode
	 *            资源代码 String 20 必选
	 * @param BookMark
	 *            书签记录时间（秒） long 必选 秒 频道ID 数字 必选
	 * @return {@link BaseJsonBean}
	 * 
	 * */
	public BaseJsonBean addBookMark(String url, String userCode,
			String userName, String resourceCode, long bookMark) {
		urlbuf.append("&userCode=").append(userCode).append("&resourceCode=")
				.append(resourceCode).append("&bookMark=").append(bookMark);
		jsonData = NetTransportUtil.getContent(url, urlbuf.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<BaseJsonBean>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法addBookMark转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 4.31 查询书签
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param userName
	 *            OTT账号 String 可选
	 * @param ResourceCode
	 *            资源代码 String 20 必选
	 * @return {@link BookMarkJson}
	 * 
	 * */
	public BookMarkJson getBookMark(String url, String userCode,
			String userName, String ResourceCode) {
		urlbuf.append("&userCode=").append(userCode).append("&ResourceCode=")
				.append(ResourceCode);
		jsonData = NetTransportUtil.getContent(url, urlbuf.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BookMarkJson bookMarkJson = null;
		try {
			bookMarkJson = gson.fromJson(jsonData,
					new TypeToken<BookMarkJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法getBookMark转换" + jsonData
					+ "为BookMarkJson时出错！");
		}
		return bookMarkJson;
	}

	/**
	 * 4.31 查询书签
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param userName
	 *            OTT账号 String 可选
	 * @param ResourceCode
	 *            资源代码 String 20 必选
	 * @return {@link BookMarksJson}
	 * 
	 * */
	public BookMarksJson getBookMark(String url, String userCode,
			String userName) {
		urlbuf.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlbuf.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BookMarksJson bookMarksJson = null;
		try {
			bookMarksJson = gson.fromJson(jsonData,
					new TypeToken<BookMarksJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法getBookMark转换" + jsonData
					+ "为BookMarksJson时出错！");
		}
		return bookMarksJson;
	}

	/**
	 * 4.32 删除书签
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param userName
	 *            OTT账号 String 可选
	 * @param resourceCode
	 *            资源代码 String 20 必选
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean delBookMark(String url, String userCode,
			String userName, String resourceCode) {
		urlbuf.append("&userCode=").append(userCode).append("&resourceCode=")
				.append(resourceCode);
		jsonData = NetTransportUtil.getContent(url, urlbuf.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<BaseJsonBean>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法delBookMark转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}
}