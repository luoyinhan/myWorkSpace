package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.favourite.FavouriteAssetListJson;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.google.gson.reflect.TypeToken;

public class FavoriteAction extends BaseAction {

	private static final String LOGTAG = "FavoriteAction";

	/**
	 * 4.23 收藏节目
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param UserName
	 *            OTT账号 String 可选
	 * @param ResourceCode
	 *            资源代码 String 必选 推荐指数 Int 可选
	 * @return {@link BaseJsonBean}
	 * 
	 * */
	public BaseJsonBean addFavorite(String url, String userCode,
			String UserName, String ResourceCode) {
		urlbuf.append("&userCode=").append(userCode).append("&resourceCode=")
				.append(ResourceCode);
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
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法addFavorite转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 4.24 查看收藏
	 * 
	 * @param userCode
	 *            用户ID String 16 必选
	 * @param userName
	 *            OTT账号 String 可选
	 * @return {@link AssetListJson}
	 * 
	 * */
	public FavouriteAssetListJson getFavorite(String url, String userCode,
			String userName) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		FavouriteAssetListJson assetListJson = null;
		try {
			assetListJson = gson.fromJson(jsonData,
					new TypeToken<FavouriteAssetListJson>() {
					}.getType());
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法getFavorite转换" + jsonData
					+ "为FavouriteAssetListJson时出错！");
		}
		return assetListJson;
	}

	/**
	 * 4.25 删除收藏
	 * 
	 * @param userCode
	 *            用户Id String 16 必选
	 * @param userName
	 *            OTT账号 String 可选
	 * @param ResourceCode
	 *            资源代码 String 必选
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean delFavorite(String url, String userCode,
			String UserName, String ResourceCode) {
		urlbuf.append("&userCode=").append(userCode).append("&ResourceCode=")
				.append(ResourceCode);
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
			LogUtils.trace(Log.DEBUG, LOGTAG, "方法delFavorite转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}
}