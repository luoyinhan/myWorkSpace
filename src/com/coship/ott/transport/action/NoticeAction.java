package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.transport.dto.book.BooksJson;
import com.coship.ott.transport.dto.notice.NoticeCountJson;
import com.coship.ott.transport.dto.notice.NoticeJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.google.gson.reflect.TypeToken;

public class NoticeAction extends BaseAction {

	private static final String LOGTAG = "NoticeAction";

	/**
	 * 3.1.50 查询最新公告数量
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @return {@link NoticeCountJson}
	 * 
	 * */
	public NoticeCountJson queryNewNotice(String url, String lastTime) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&lastTime=").append(lastTime);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		NoticeCountJson noticeCount = null;
		try {
			noticeCount = gson.fromJson(jsonData,
					new TypeToken<NoticeCountJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法addBook转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return noticeCount;
	}

	/**
	 * 3.1.51 获取所有公告内容
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @return {@link BooksJson}
	 * 
	 * */
	public NoticeJson getNotices(String url, int pageSize, int curPage) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		NoticeJson noticeJson = null;
		try {
			noticeJson = gson.fromJson(jsonData, new TypeToken<NoticeJson>() {
			}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法queryBook转换" + jsonData
					+ "为BooksJson时出错！");
		}
		return noticeJson;
	}

}