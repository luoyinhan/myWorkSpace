package com.coship.ott.transport.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.constant.Constant;
import com.coship.ott.transport.dto.live.ChannelCurrentProgramsJson;
import com.coship.ott.transport.dto.live.ChannelInfoJson;
import com.coship.ott.transport.dto.live.ChannelbrandJson;
import com.coship.ott.transport.dto.live.ChannelbrandsJson;
import com.coship.ott.transport.dto.live.ChannelsCurrentProgramsJson;
import com.coship.ott.transport.dto.live.ProgramInfoJson;
import com.coship.ott.transport.dto.live.ProgramInfosJson;
import com.coship.ott.transport.dto.live.RankingProgramsJson;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.google.gson.reflect.TypeToken;

public class LiveAction extends BaseAction {

	private static final String LOGTAG = "LiveAction";

	/**
	 * 4.8 获取资源列表
	 * 
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @param userCode
	 *            用户ID String 可选
	 * @param queryType
	 *            查询类型 Int 必选 0：按照栏目获取 1：获取影片排行 2：获取直播排行 3：获取最新更新 4：按照检索条件
	 * @param catalogId
	 *            栏目ID String 可选 queryType＝0 必选 在queryType＝1or＝4时有用
	 * @param isRecommend
	 *            是否推荐 Int 可选 0不推荐 1推荐 在queryType＝1or＝4时有用
	 * @param assetType
	 *            资源类型 Int 可选 queryType＝4 必选 获取资源分类返回 在queryType＝4时有用
	 * @param originName
	 *            发行地 String 可选 中国、美国、欧洲 在queryType＝4时有用
	 * @param publishDate
	 *            发行年 String 可选 只需年（如2010、2011等） 在queryType＝4时有用
	 * @param orderTag
	 *            排序标志 Int 可选 1：按照时间倒叙（最新） 2：按照点播次数倒叙（最热） 在queryType＝0 or 4时有用
	 * @return {@link RankingProgramsJson}
	 * */
	public RankingProgramsJson getAssetList(String url, int pageSize,
			int curPage, String userCode, int queryType, String catalogId,
			int isRecommend, int assetType, String originName,
			String publishDate, int orderTag) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage).append("&userCode=").append(userCode)
				.append("&queryType=").append(queryType).append("&catalogId=")
				.append(catalogId).append("&isRecommend=").append(isRecommend)
				.append("&assetType=").append(assetType).append("&originName=")
				.append(originName).append("&publishDate=").append(publishDate)
				.append("&orderTag=").append(orderTag);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		RankingProgramsJson recommendProgramsJson = null;
		try {
			recommendProgramsJson = gson.fromJson(jsonData,
					new TypeToken<RankingProgramsJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getAssetList转换" + jsonData
					+ "为RecommendProgramsJson时出错！");
		}
		return recommendProgramsJson;
	}

	/**
	 * 4.13 获取频道列表
	 * 
	 * @param channelVersion
	 *            系统频道版本 Int 必选 当前频道版本，首次使用或重新获取送0
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @param videoType
	 *            频道属性 String 可选 0：标清 1：高清
	 * @param feeType
	 *            收费属性 String 可选 0：免费 1：收费
	 * @param ChannelType
	 *            频道类型 String 可选 枚举值： 1：新闻类 2：财经类 3：
	 * @param cityCode
	 *            媒资的归属地市编码 String 可选
	 * @return {@link ChannelInfoJson}
	 * 
	 * */
	public ChannelInfoJson getChannels(String url, int channelVersion,
			int pageSize, int curPage, String videoType, String feeType,
			String ChannelType, String cityCode) {
		StringBuffer urlBuffer = urlbuf;
		try {
			ChannelType = URLEncoder.encode(ChannelType, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			Log.d(LOGTAG, e1.toString());
		}
		urlBuffer.append("&channelVersion=").append(channelVersion)
				.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage).append("&videoType=").append(videoType)
				.append("&feeType=").append(feeType).append("&ChannelType=")
				.append(ChannelType).append("&cityCode=").append(cityCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ChannelInfoJson channelInfoJson = null;
		try {
			channelInfoJson = gson.fromJson(jsonData,
					new TypeToken<ChannelInfoJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannels转换" + jsonData + "为ChannelInfoJson时出错！");
		}
		return channelInfoJson;
	}

	/**
	 * 4.14 获取频道下节目列表
	 * 
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @param channelResourceCode
	 *            频道ID号 String 必选
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @param beginTime
	 *            开始时间 String 可选 2011-02-22 04:00:00
	 * @param endTime
	 *            结束时间 String 可选 2011-02-23 03:59:59
	 * 
	 * @return {@link ProgramInfosJson}
	 * 
	 * */
	public ProgramInfosJson getChannelProgram(String url, String userCode,
			String channelResourceCode, int pageSize, int curPage,
			String beginTime, String endTime) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode)
				.append("&channelResourceCode=").append(channelResourceCode)
				.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage).append("&beginTime=").append(beginTime)
				.append("&endTime=").append(endTime);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ProgramInfosJson programInfosJson = null;
		try {
			programInfosJson = gson.fromJson(jsonData,
					new TypeToken<ProgramInfosJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannelProgram转换" + jsonData
					+ "为ProgramInfosJson时出错！");
		}
		return programInfosJson;
	}

	/**
	 * 4.15 获取频道品牌列表
	 * 
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @param channelID
	 *            频道ID号 int 必选
	 * @param playDay
	 *            播放日期 String 10 必选
	 * @return {@link ProgramInfoJson}
	 * 
	 * */
	public ChannelbrandsJson getChannelbrand(String url, int pageSize,
			int curPage, String userCode, String channelID, int playDay) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage).append("&userCode=").append(userCode)
				.append("&channelResourceCode=").append(channelID);
		if (playDay != 0) {
			urlBuffer.append("&PalyDay=").append(playDay);
		}
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ChannelbrandsJson channelbrandJson = null;
		try {
			channelbrandJson = gson.fromJson(jsonData,
					new TypeToken<ChannelbrandsJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannelbrand转换" + jsonData
					+ "为ChannelbrandJson时出错！");
		}
		return channelbrandJson;
	}

	/**
	 * 4.16 获取频道品牌详情
	 * 
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @param brandID
	 *            播放日期 String 10 必选
	 * 
	 * @return {@link ProgramInfoJson}
	 * 
	 * */
	public ChannelbrandJson getChannelbrandInfo(String url, String userCode,
			String brandID) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&brandID=")
				.append(brandID);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ChannelbrandJson channelbrandJson = null;
		try {
			channelbrandJson = gson.fromJson(jsonData,
					new TypeToken<ChannelbrandJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannelbrandInfo转换" + jsonData
					+ "为ChannelbrandJson时出错！");
		}
		return channelbrandJson;
	}

	/**
	 * 4.17 获取推荐直播节目
	 * 
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @return {@link ProgramInfosJson}
	 * 
	 * */
	public AssetListJson getRecommendPorgram(String url, String userCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AssetListJson programInfosJson = null;
		try {
			programInfosJson = gson.fromJson(jsonData,
					new TypeToken<AssetListJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getRecommendPorgram转换" + jsonData
					+ "为ProgramInfosJson时出错！");
		}
		return programInfosJson;
	}

	/**
	 * 4.18 获取推荐频道品牌
	 * 
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @return {@link ProgramInfoJson}
	 * 
	 * */
	public AssetListJson getRecommendChannelbrand(String url, String userCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AssetListJson channelbrandsJson = null;
		try {
			channelbrandsJson = gson.fromJson(jsonData,
					new TypeToken<AssetListJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getRecommendChannelbrand转换" + jsonData
					+ "为ChannelbrandsJson时出错！");
		}
		return channelbrandsJson;
	}

	/**
	 * 4.19 获取直播节目详情
	 * 
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @param programId
	 *            节目ID String 必选
	 * @return {@link ProgramInfoJson}
	 * 
	 * */
	public ProgramInfoJson getPorgramInfo(String url, String userCode,
			String programId) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&programId=")
				.append(programId);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ProgramInfoJson programInfoJson = null;
		try {
			programInfoJson = gson.fromJson(jsonData,
					new TypeToken<ProgramInfoJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getPorgramInfo转换" + jsonData
					+ "为ProgramInfoJson时出错！");
		}
		return programInfoJson;
	}

	/**
	 * 4.42 获取频道信息
	 * 
	 * @param resourceCode
	 *            频道ID String 必选
	 * @return {@link ChannelInfoJson}
	 * 
	 * */
	public ChannelInfoJson getChannelInfo(String url, String resourceCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&resourceCode=").append(resourceCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ChannelInfoJson json = null;
		try {
			json = gson.fromJson(jsonData, new TypeToken<ChannelInfoJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannelInfo转换" + jsonData
					+ "为ChannelInfoJson时出错！");
		}
		return json;
	}

	/**
	 * 4.43获取频道的当前节目单
	 * 
	 * @param channelResourceCode
	 *            频道ID String 必选
	 * @return {@link ChannelCurrentProgramsJson}
	 * 
	 * */
	public ChannelCurrentProgramsJson getChannelCurrentPrograms(String url,
			String channelResourceCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&channelResourceCode=").append(channelResourceCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ChannelCurrentProgramsJson json = null;
		try {
			json = gson.fromJson(jsonData,
					new TypeToken<ChannelCurrentProgramsJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannelCurrentPrograms转换" + jsonData
					+ "为ChannelCurrentProgramsJson时出错！");
		}
		return json;
	}

	/**
	 * 4.44获取多个频道的当前节目单
	 * 
	 * @param channelResourceCode
	 *            频道ID String 必选
	 * @return {@link ChannelCurrentProgramsJson}
	 * 
	 * */
	public ChannelsCurrentProgramsJson getChannelsCurrentPrograms(String url,
			String channelsResourceCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&channelResourceCodes=").append(channelsResourceCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ChannelsCurrentProgramsJson json = null;
		try {
			json = gson.fromJson(jsonData,
					new TypeToken<ChannelsCurrentProgramsJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getChannelCurrentPrograms转换" + jsonData
					+ "为ChannelCurrentProgramsJson时出错！");
		}
		return json;
	}
}