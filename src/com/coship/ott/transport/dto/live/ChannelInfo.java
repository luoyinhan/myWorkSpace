package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.vod.Poster;

/**
 * 3.8 频道实体类
 * */
public class ChannelInfo {
	// 字符 频道ID号
	private String channelID;
	// 字符 频道名称 任意字符
	private String channelName;
	// 字符 频道代码
	private String channelCode;
	// 字符 描述
	private String description;
	// 数字 高标清标志 枚举值： 0：标清 1：高清
	private int videoType;
	// 数字 付费标识 枚举值：
	// 0：免费
	// 1：收费
	private int feeType;
	// 数字 频道排序顺序 正整数
	private int resourceOrder;
	// 字符 资源编码 字母、数字和下划线的组合
	// 字符 频道类型 枚举值：
	private String ResourceCode;
	// 1：新闻类
	// 2：财经类
	// 3：…….
	private int channelType;
	// 字符 媒资的归属地市编码 枚举值，请参考总册的“地市编码表”的定义
	private String cityCode;
	// 字符 媒资的分级属性      枚举值 P – 省级媒资 C – 地市媒资
	private String gradeCode;
	// 数字
	private int networkId;
	// 数字
	private int TSID;
	// 数字 单向系统中的serviceid对应，即单向系统中的频道编号NUMBER(5)，最大值65535。
	private int serviceid;
	// 对象 posterInfo 台标对象
	private ArrayList<Poster> poster;
	// 当前节目 Program
	private ProgramInfo currentProgram;

	public ChannelInfo() {
	}

	public ChannelInfo(String channelID, String channelName,
			String channelCode, String description, int videoType, int feeType,
			int resourceOrder, String resourceCode, int channelType,
			String cityCode, String gradeCode, int networkId, int tSID,
			int serviceid, ArrayList<Poster> poster,ProgramInfo currentProgram) {
		super();
		this.channelID = channelID;
		this.channelName = channelName;
		this.channelCode = channelCode;
		this.description = description;
		this.videoType = videoType;
		this.feeType = feeType;
		this.resourceOrder = resourceOrder;
		ResourceCode = resourceCode;
		this.channelType = channelType;
		this.cityCode = cityCode;
		this.gradeCode = gradeCode;
		this.networkId = networkId;
		TSID = tSID;
		this.serviceid = serviceid;
		this.poster = poster;
		this.currentProgram=currentProgram;
	}

	public ProgramInfo getCurrentProgram() {
		return currentProgram;
	}

	public void setCurrentProgram(ProgramInfo currentProgram) {
		this.currentProgram = currentProgram;
	}

	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getFeeType() {
		return feeType;
	}

	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}

	public int getResourceOrder() {
		return resourceOrder;
	}

	public void setResourceOrder(int resourceOrder) {
		this.resourceOrder = resourceOrder;
	}

	public String getResourceCode() {
		return ResourceCode;
	}

	public void setResourceCode(String resourceCode) {
		ResourceCode = resourceCode;
	}

	public int getChannelType() {
		return channelType;
	}

	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getGradeCode() {
		return gradeCode;
	}

	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}

	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	public int getTSID() {
		return TSID;
	}

	public void setTSID(int tSID) {
		TSID = tSID;
	}

	public int getServiceid() {
		return serviceid;
	}

	public void setServiceid(int serviceid) {
		this.serviceid = serviceid;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}
}