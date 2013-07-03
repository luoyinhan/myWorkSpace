package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

/**
 * 资源实体类
 * */
public class Resource {
	// 资源类型 1：直播节目 2：点播节目
	private int resourceType;
	// 资源编号 直播对应programGuideID 点播对应resourceCode
	private String resourceCode;
	// 资源类别 resourceType=1时必有 枚举值：（此处仅包含如下值） 1：资源包 0：资源
	private int type;
	// 资源名称 直播对应assetName 点播对应eventName
	private String resourceName;
	// 资源简介 直播对应describ 点播对应eventDesc
	private String resourceDes;
	// 频道ID resourceType=2时必有
	private int channelResourceCode;
	// 开始时间 resourceType=2时必有 格式：YYYY-MM-DD HH:MM:SS
	private String beginTime;
	// 结束时间 resourceType=2时必有 格式：YYYY-MM-DD HH:MM:SS
	private String endTime;
	// 高标清标志 枚举值：0：标清 1：高清
	private int videoType;
	// 影片时长（点播对应播放时长字段，直播忽略）
	private int playTime;
	// 海报 posterInfo
	private ArrayList<Poster> posters;

	public Resource() {
	}

	public Resource(int resourceType, String resourceCode, int type,
			String resourceName, String resourceDes, int channelResourceCode,
			String beginTime, String endTime, int videoType, int playTime,
			ArrayList<Poster> posters) {
		super();
		this.resourceType = resourceType;
		this.resourceCode = resourceCode;
		this.type = type;
		this.resourceName = resourceName;
		this.resourceDes = resourceDes;
		this.channelResourceCode = channelResourceCode;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.videoType = videoType;
		this.playTime = playTime;
		this.posters = posters;
	}

	public int getResourceType() {
		return resourceType;
	}

	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
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

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceDes() {
		return resourceDes;
	}

	public void setResourceDes(String resourceDes) {
		this.resourceDes = resourceDes;
	}

	public int getChannelResourceCode() {
		return channelResourceCode;
	}

	public void setChannelResourceCode(int channelResourceCode) {
		this.channelResourceCode = channelResourceCode;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getPlayTime() {
		return playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public ArrayList<Poster> getPosters() {
		return posters;
	}

	public void setPosters(ArrayList<Poster> posters) {
		this.posters = posters;
	}
}