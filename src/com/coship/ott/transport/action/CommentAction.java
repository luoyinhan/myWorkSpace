package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.transport.dto.AddCommentJson;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.comment.CommentsJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.google.gson.reflect.TypeToken;

public class CommentAction extends BaseAction {

	private static final String LOGTAG = "CommentAction";

	/**
	 * 4.20 节目评论
	 * 
	 * @param userName
	 *            用户ID String 16 必选
	 * @param objType
	 *            评论对象 Int 1 必选 1：直播节目2：点播节目3：频道品牌
	 * @param objID
	 *            对象ID String 32 必选
	 * @param comment
	 *            评论信息 String 300 可选
	 * @param recommendationLevel
	 *            推荐指数 Int 可选
	 * @return {@link BaseJsonBean}
	 * 
	 * */
	public AddCommentJson userComment(String url, String userCode, int objType,
			String objID, String comment, int recommendationLevel) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&objType=")
				.append(objType).append("&objID=").append(objID)
				.append("&comment=").append(comment)
				.append("&recommendationLevel=").append(recommendationLevel);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AddCommentJson baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<AddCommentJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法UserComment转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 4.21 查看节目最近评论
	 * 
	 * @param objType
	 *            资源类型 Int 1 必选 1：直播节目 2：点播节目 3：频道品牌
	 * @param objID
	 *            资源ID String 32 必选
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @return {@link CommentsJson}
	 * 
	 * */
	public CommentsJson getCommentByAssetId(String url, int objType,
			String objID, int pageSize, int curPage) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&objType=").append(objType).append("&objID=")
				.append(objID).append("&pageSize=").append(pageSize)
				.append("&curPage=").append(curPage);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		CommentsJson commentsJson = null;
		try {
			commentsJson = gson.fromJson(jsonData,
					new TypeToken<CommentsJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法getCommentByAssetId转换"
					+ jsonData + "为CommentsJson时出错！");

		}
		return commentsJson;
	}

	/**
	 * 4.22 查看用户评论
	 * 
	 * @param userName
	 *            用户Id String 16 必选
	 * @param startTime
	 *            开始时间 String 可选 2011-02-22 04:00:00
	 * @param endtime
	 *            结束时间 String 可选 2011-02-22 04:00:00
	 * @param curPage
	 *            当前页 Int 可选
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @return {@link CommentsJson}
	 * */
	public CommentsJson getCommentByUserCode(String url, String userName,
			String startTime, String endtime, int curPage, int pageSize) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&startTime=").append(startTime).append("&endtime=")
				.append(endtime).append("&curPage=").append(curPage)
				.append("&pageSize=").append(pageSize);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		CommentsJson commentsJson = null;
		try {
			commentsJson = gson.fromJson(jsonData,
					new TypeToken<CommentsJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法getCommentByAssetId转换"
					+ jsonData + "为CommentsJson时出错！");
		}
		return commentsJson;
	}

	/**
	 * 4.24 查看用户评论
	 * 
	 * @param userName
	 *            用户Id String 16 必选
	 * @param startTime
	 *            开始时间 String 可选 2011-02-22 04:00:00
	 * @param endtime
	 *            结束时间 String 可选 2011-02-22 04:00:00
	 * @param curPage
	 *            当前页 Int 可选
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @return {@link CommentsJson}
	 * */
	public BaseJsonBean deleteComment(String url, String userName,
			String commentIds) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&commentIds=").append(commentIds);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		CommentsJson commentsJson = null;
		try {
			commentsJson = gson.fromJson(jsonData,
					new TypeToken<CommentsJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法getCommentByAssetId转换"
					+ jsonData + "为CommentsJson时出错！");
		}
		return commentsJson;
	}
}