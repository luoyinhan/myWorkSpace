package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.vod.Poster;

/**
 * 3.1 频道品牌实体类
 * */
public class Channelbrand {
	// 数字 键值 不超过11位的数字
	private String brandID;
	// 数字 频道ID 不超过11位的数字
	private String channelResourceCode;
	// 字符 频道名称
	private String channelName;
	// 字符 品牌名称 格式：YYYY-MM-DD
	private String brandName;
	// 字符 播放时间 星期5
	private String PalyDay;
	// 日期 开始时间 格式： HH:MM
	private String beginTime;
	// 日期 结束时间 格式： HH:MM
	private String endTime;
	// 字符 主持人
	private String host;
	// 字符 节目简介 任意字符
	private String Desc;
	// 字符 节目备注 对该节目的一句话描述
	private String remark;
	// 数字 品牌类型 1.综艺 2.影视
	private int brandType;
	// 400 字符 附加的内容 如果品牌类型为综艺，该字段存主持人host=主持人 如果品牌累i系那个为影视，该字段存导演、演员
	// Director=***||leadingActor=***(中间用双竖线分割)
	private String additionInfo;
	// 数字 有效标志 枚举值： 0：不可用 1：可用
	private int Status;
	// 字符 高清标识 枚举值： 0：标清 1：高清
	private int videoType;
	// 对象 PosterInfo 对应的海报，1－N个 根据不同终端分辨率返回不同
	private ArrayList<Poster> poster;
	// 关联的节目信息，按照brandName查询节目列表
	private ArrayList<ProgramInfo> programInfo;

	public Channelbrand() {
	}

	public Channelbrand(String brandID, String channelResourceCode,
			String channelName, String brandName, String palyDay,
			String beginTime, String endTime, String host, String Desc,
			String remark, int brandType, String additionalInfo, int status,
			int videoType, ArrayList<Poster> poster,
			ArrayList<ProgramInfo> programInfo) {
		super();
		this.brandID = brandID;
		this.channelResourceCode = channelResourceCode;
		this.channelName = channelName;
		this.brandName = brandName;
		PalyDay = palyDay;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.host = host;
		this.Desc = Desc;
		this.remark = remark;
		this.brandType = brandType;
		this.additionInfo = additionalInfo;
		Status = status;
		this.videoType = videoType;
		this.poster = poster;
		this.programInfo = programInfo;
	}

	public String getBrandID() {
		return brandID;
	}

	public void setBrandID(String brandID) {
		this.brandID = brandID;
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

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getPalyDay() {
		return PalyDay;
	}

	public void setPalyDay(String palyDay) {
		PalyDay = palyDay;
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getBrandType() {
		return brandType;
	}

	public void setBrandType(int brandType) {
		this.brandType = brandType;
	}

	public String getAdditionalInfo() {
		return additionInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionInfo = additionalInfo;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}

	public ArrayList<ProgramInfo> getProgramInfo() {
		return programInfo;
	}

	public void setProgramInfo(ArrayList<ProgramInfo> programInfo) {
		this.programInfo = programInfo;
	}
}