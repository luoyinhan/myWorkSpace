package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

/**
 * 影片详情实体类
 * */
public class AssetInfo {
	// 数字 资源编码 作唯一主键
	private String resourceCode;
	// 数字 资源类型 0为电影，1为系列剧
	private int type;
	// 字符 资源名称 任意字符
	private String assetName;
	// 字符 资源英文名 英文字母、数字与下划线组合
	private String assetENName;
	// 字符 语言 如：汉语英语俄语法语德语日语
	private String mLName;
	// 字符 字幕如：汉语英语俄语法语德语日语 对天威ADI中字段Caption_Language
	private String captionName;
	// 字符 资源分类名称，多个以’,’分离 对天威ADI中字段Category，值如“连续剧”；名称中不能包含”,”
	private String assetTypes;
	// 字符 产地代码 任意字符 对天威ADI中字段ProducingArea
	private String originName;
	// 字符 发行商名称 任意字符
	private String issuerName;
	// 字符 声道 枚举值：默认1 1 立体声 2 单声道
	private String trackType;
	// 字符 集数，默认1 正整数，对天威ADI中字段PositiveTotal
	private int chapters;
	// 字符 观赏级别 枚举值：默认0 0 无分级 1 成人
	private String viewLevel;
	// 字符 剧情简介 任意字符
	private String describ;
	// 字符 导演 任意字符
	private String director;
	// 字符 主演 任意字符
	private String leadingActor;
	// 字符 编剧 任意字符
	private String screenWriter;
	// 字符 奖项 任意字符
	private String prize;
	// 字符 搜索关键字，不同关键字之间以分号（;）隔开 例如：王宝强;暴力;戏剧 关键字中不能包含”；”
	private String keyWord;
	// 数字 推荐指数 数字型 从1开始 数字
	private int recommendationLevel;
	// 日期 发行日期 格式：YYYY-MM-DD
	private String publishDate;
	// 字符 媒资分类编码，可有多个以”,”隔开
	private String assetTypeIds;
	// 字符 媒资的内容提供商 ProviderID 由数字字符下划线组成
	private String providerID;
	// 字符 媒资的资产ID 20-21数字或字母组成
	private String assetID;
	// 字符 摘要_长 任意字符
	private String summaryLong;
	// 字符 摘要_中 任意字符
	private String summaryMedium;
	// 字符 摘要_短 任意字符
	private String summaryShort;
	// 字符 发行年份 String "yyyy"
	private String year;
	// 数字 片长 以秒计算的片长数字，对应天威ADI字段Run_Time
	private int playTime;
	// 字符 高清标识 枚举值：0：标清1：高清
	private int videoType;
	// 数字 是否推荐 0不推荐1推荐
	private int isRecommend;
	// 数字 播放次数
	private int playCount;
	// 对象 posterInfo 对应海报1－N
	private ArrayList<Poster> posterInfo;
	// 对象 ProductInfo关联产品0-1
	private ProductInfo product;

	public AssetInfo() {
	}

	public AssetInfo(String resourceCode, int type, String assetName,
			String assetENName, String mLName, String captionName,
			String assetTypes, String originName, String issuerName,
			String trackType, int chapters, String viewLevel, String describ,
			String director, String leadingActor, String screenWriter,
			String prize, String keyWord, int recommendationLevel,
			String publishDate, String assetTypeIds, String providerID,
			String assetID, String summaryLong, String summaryMedium,
			String summaryShort, String year, int playTime, int videoType,
			int isRecommend, int playCount, ArrayList<Poster> posterInfo,
			ProductInfo product) {
		super();
		this.resourceCode = resourceCode;
		this.type = type;
		this.assetName = assetName;
		this.assetENName = assetENName;
		this.mLName = mLName;
		this.captionName = captionName;
		this.assetTypes = assetTypes;
		this.originName = originName;
		this.issuerName = issuerName;
		this.trackType = trackType;
		this.chapters = chapters;
		this.viewLevel = viewLevel;
		this.describ = describ;
		this.director = director;
		this.leadingActor = leadingActor;
		this.screenWriter = screenWriter;
		this.prize = prize;
		this.keyWord = keyWord;
		this.recommendationLevel = recommendationLevel;
		this.publishDate = publishDate;
		this.assetTypeIds = assetTypeIds;
		this.providerID = providerID;
		this.assetID = assetID;
		this.summaryLong = summaryLong;
		this.summaryMedium = summaryMedium;
		this.summaryShort = summaryShort;
		this.year = year;
		this.playTime = playTime;
		this.videoType = videoType;
		this.isRecommend = isRecommend;
		this.playCount = playCount;
		this.posterInfo = posterInfo;
		this.product = product;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getAssetENName() {
		return assetENName;
	}

	public void setAssetENName(String assetENName) {
		this.assetENName = assetENName;
	}

	public String getmLName() {
		return mLName;
	}

	public void setmLName(String mLName) {
		this.mLName = mLName;
	}

	public String getCaptionName() {
		return captionName;
	}

	public void setCaptionName(String captionName) {
		this.captionName = captionName;
	}

	public String getAssetTypes() {
		return assetTypes;
	}

	public void setAssetTypes(String assetTypes) {
		this.assetTypes = assetTypes;
	}

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getTrackType() {
		return trackType;
	}

	public void setTrackType(String trackType) {
		this.trackType = trackType;
	}

	public int getChapters() {
		return chapters;
	}

	public void setChapters(int chapters) {
		this.chapters = chapters;
	}

	public String getViewLevel() {
		return viewLevel;
	}

	public void setViewLevel(String viewLevel) {
		this.viewLevel = viewLevel;
	}

	public String getDescrib() {
		return describ;
	}

	public void setDescrib(String describ) {
		this.describ = describ;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getLeadingActor() {
		return leadingActor;
	}

	public void setLeadingActor(String leadingActor) {
		this.leadingActor = leadingActor;
	}

	public String getScreenWriter() {
		return screenWriter;
	}

	public void setScreenWriter(String screenWriter) {
		this.screenWriter = screenWriter;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public int getRecommendationLevel() {
		return recommendationLevel;
	}

	public void setRecommendationLevel(int recommendationLevel) {
		this.recommendationLevel = recommendationLevel;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getAssetTypeIds() {
		return assetTypeIds;
	}

	public void setAssetTypeIds(String assetTypeIds) {
		this.assetTypeIds = assetTypeIds;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public String getSummaryLong() {
		return summaryLong;
	}

	public void setSummaryLong(String summaryLong) {
		this.summaryLong = summaryLong;
	}

	public String getSummaryMedium() {
		return summaryMedium;
	}

	public void setSummaryMedium(String summaryMedium) {
		this.summaryMedium = summaryMedium;
	}

	public String getSummaryShort() {
		return summaryShort;
	}

	public void setSummaryShort(String summaryShort) {
		this.summaryShort = summaryShort;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getPlayTime() {
		return playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(int isRecommend) {
		this.isRecommend = isRecommend;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public ArrayList<Poster> getPosterInfo() {
		return posterInfo;
	}

	public void setPosterInfo(ArrayList<Poster> posterInfo) {
		this.posterInfo = posterInfo;
	}

	public ProductInfo getProduct() {
		return product;
	}

	public void setProduct(ProductInfo product) {
		this.product = product;
	}
}