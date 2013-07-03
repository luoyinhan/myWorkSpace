package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.utils.Utility;

/**
 * 3.9 直播节目实体类
 * */
public class ProgramInfo implements Comparable<ProgramInfo> {
	// 数字 节目单ID 不超过11位的数字
	private String programId;
	// 数字 频道ID 不超过11位的数字
	private String channelID;
	// 日期 节目单日期 格式：YYYY-MM-DD
	private String eventDate;
	// 日期 开始时间 格式：YYYY-MM-DD HH:MM:SS
	private String beginTime;
	// 日期 结束时间 格式：YYYY-MM-DD HH:MM:SS
	private String endTime;
	// 字符 节目名称 任意字符
	private String eventName;
	// 字符 节目描述 任意字符
	private String eventDesc;
	// 字符 关键字 用于搜索，初始为节目名称
	private String keyWord;
	// 数字 是否被预订 0：无 1：有 如果查询时有输入用户代码，返回接口拼此数据
	private int isBook;
	// 数字 推荐标志 是否推荐 0不推荐 1推荐 按照栏目查询媒资列表时，表示是否该栏目下的推荐媒资
	private int isRecommend;
	// 高标清标识 0为标清 1为高清
	private int videoType;
	// 数字 播放次数
	private int playCount;
	// 字符 资产ID 20-21数字或字母组成
	private String assetID;
	// 字符 内容提供商ID
	private String providerID;
	// 对象 posterInfo 对应海报1－N 根据不同终端分辨率返回不同
	private ArrayList<Poster> poster;
	// 数字 播放次数
	private int playtime;

	private int status;
	private String volumeName;
	private String channelResourceCode;
	private String channelName;

	public ProgramInfo() {
	}

	public ProgramInfo(String programId, String channelID, String eventDate,
			String beginTime, String endTime, String eventName,
			String eventDesc, String keyWord, int isBook, int isRecommend,
			int videoType, int playCount, String assetID, String providerID,
			ArrayList<Poster> poster, int playtime, int status,
			String volumeName, String channelResourceCode, String channelName) {
		super();
		this.programId = programId;
		this.channelID = channelID;
		this.eventDate = eventDate;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.eventName = eventName;
		this.eventDesc = eventDesc;
		this.keyWord = keyWord;
		this.isBook = isBook;
		this.isRecommend = isRecommend;
		this.videoType = videoType;
		this.playCount = playCount;
		this.assetID = assetID;
		this.providerID = providerID;
		this.poster = poster;
		this.playtime = playtime;
		this.status = status;
		this.volumeName = volumeName;
		this.channelResourceCode = channelResourceCode;
		this.channelName = channelName;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
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

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public int getIsBook() {
		return isBook;
	}

	public void setIsBook(int isBook) {
		this.isBook = isBook;
	}

	public int getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(int isRecommend) {
		this.isRecommend = isRecommend;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}

	public int getPlaytime() {
		return playtime;
	}

	public void setPlaytime(int playtime) {
		this.playtime = playtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getVolumeName() {
		return volumeName;
	}

	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}

	public String getChannelResourceCode() {
		return channelResourceCode;
	}

	public void setChannelResourceCode(String channelResourceCode) {
		this.channelResourceCode = channelResourceCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@Override
	public int compareTo(ProgramInfo another) {
		long thisBeginTime = Utility.dealTimeToSeconds(this.beginTime);
		long anotherBeginTime = Utility.dealTimeToSeconds(another.beginTime);
		if (thisBeginTime > anotherBeginTime) {
			return -1;
		} else if (thisBeginTime == anotherBeginTime) {
			return 0;
		} else {
			return 1;
		}
	}
}