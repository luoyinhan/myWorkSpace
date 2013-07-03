package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

/**
 * 影片资源（包）实体类
 * */
public class AssetListInfo {
	// 数字 资源包编码 作为资源的唯一编码，由Sequences自动生成
	private String resourceCode;
	// 数字 资源类别 枚举值：（此处仅包含如下值）1：资源包 0：资源
	private int type;
	// 数字 资源ID
	private String assetID;
	// 字符 资源包名称 影片名称只能由中文、字母、数字、下划线、英文左右小括号 '('')' 组成
	private String assetName;
	// 字符 资源包英文名 英文字母、数字与下划线组合
	private String assetENName;
	// 字符 资源包分类名称，多个以’,’分离
	private String assetTypes;
	// 字符 产地代码 任意字符
	private String originName;
	// 数字 总集数，默认1 正整数
	private int chapters;
	// 字符 导演 任意字符
	private String director;
	// 字符 主演 任意字符
	private String leadingActor;
	// 字符 奖项 任意字符
	private String prize;
	// 字符 剧情简介 任意字符
	private String describ;
	// 字符 剧情简介 详情
	private String summaryLong;
	// 字符 剧情简介
	private String summaryMedium;
	// 字符 剧情简介 一句话简介
	private String summaryShort;
	// 字符 搜索关键字，不同关键字之间以分号（;）隔开 例如：王宝强;暴力;戏剧 关键字中不能包含”；”
	private String keyWord;
	// 数字 推荐指数 数字型 从1开始 数字1到10
	private int recommendationLevel;
	// 日期 发行日期 格式：YYYY-MM-DD
	private String publishDate;
	// 字符 媒资包分类编码，可有多个以”,”隔开
	private String assetTypeIds;
	// 字符 内容提供商ID
	private String providerID;
	// 字符 内容展示类型 ，
	// 电视连续剧或系列剧Series,体育Sports,音乐Music,广告Ad,电视连续短剧Miniseries,电影Movie,新闻New,其它Other
	private String showType;
	// 字符 电视连续剧、系列剧标识 1：电视连续剧2：系列剧
	private String series;
	// 字符 发行年份 String "yyyy"
	private String year;
	// 推荐标志 是否推荐 0不推荐1推荐 按照栏目查询媒资列表时，表示是否该栏目下的推荐媒资
	private int isRecommend;
	// 时长
	private int playTime;
	// 字符 高清标识 枚举值：0：标清1：高清
	private int videoType;
	// 对象 PosterInfo对应的海报，1－N个根据不同终端分辨率返回不同
	private ArrayList<Poster> posterInfo;
	// 对象 ProduceInfo对应的产品，1个
	private ProductInfo product;
	// 是否被选中
	private boolean isSelected = false;
	private String chapterResourceCode;

	public AssetListInfo() {
	}

	public AssetListInfo(String resourceCode, int type, String assetID,
			String assetName, String assetENName, String assetTypes,
			String originName, int chapters, String director,
			String leadingActor, String prize, String describ,
			String summaryLong, String summaryMedium, String summaryShort,
			String keyWord, int recommendationLevel, String publishDate,
			String assetTypeIds, String providerID, String showType,
			String series, String year, int isRecommend, int playTime,
			int videoType, ArrayList<Poster> posterInfo, ProductInfo product,
			String chapterResourceCode) {
		super();
		this.resourceCode = resourceCode;
		this.type = type;
		this.assetID = assetID;
		this.assetName = assetName;
		this.assetENName = assetENName;
		this.assetTypes = assetTypes;
		this.originName = originName;
		this.chapters = chapters;
		this.director = director;
		this.leadingActor = leadingActor;
		this.prize = prize;
		this.describ = describ;
		this.summaryLong = summaryLong;
		this.summaryMedium = summaryMedium;
		this.summaryShort = summaryShort;
		this.keyWord = keyWord;
		this.recommendationLevel = recommendationLevel;
		this.publishDate = publishDate;
		this.assetTypeIds = assetTypeIds;
		this.providerID = providerID;
		this.showType = showType;
		this.series = series;
		this.year = year;
		this.isRecommend = isRecommend;
		this.playTime = playTime;
		this.videoType = videoType;
		this.posterInfo = posterInfo;
		this.product = product;
		this.chapterResourceCode = chapterResourceCode;
	}

	public String getChapterResourceCode() {
		return chapterResourceCode;
	}

	public void setChapterResourceCode(String chapterResourceCode) {
		this.chapterResourceCode = chapterResourceCode;
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

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
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

	public int getChapters() {
		return chapters;
	}

	public void setChapters(int chapters) {
		this.chapters = chapters;
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

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public String getDescrib() {
		return describ;
	}

	public void setDescrib(String describ) {
		this.describ = describ;
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

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(int isRecommend) {
		this.isRecommend = isRecommend;
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

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}