package com.coship.ott.transport.dto.book;

import java.util.ArrayList;

import com.coship.ott.transport.dto.vod.Poster;

/**
 * 3.4 用户预订实体类
 * */
public class Book {
	// 字符 用户ID
	private String userCode;
	// 数字 节目单ID 不超过11位的数字
	private String programId;
	// 数字 频道ID 不超过11位的数字
	private String channelResourceCode;
	// 日期 节目单日期 接口关联查询
	private String eventDate;
	// 日期 开始时间 接口关联查询
	private String beginTime;
	// 日期 结束时间 接口关联查询
	private String endTime;
	// 字符 节目名称 接口关联查询
	private String eventName;
	// 日期 预订时间 预订时写入的系统时间
	private String bookTime;
	// 字符 节目名称 接口关联查询
	private String channelName;
	// 高标清标识 0为标清，1为高清
	private int videoType;
	
	private int productPrice;

	// 对象 posterInfo 接口关联查询
	private ArrayList<Poster> poster;
	// 选中的状态
	private boolean isSelected = false;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Book() {
	}

	public Book(String userCode, String programId, String channelResourceCode,
			String eventDate, String beginTime, String endTime,
			String eventName, String bookTime, String channelName,
			int videoType, ArrayList<Poster> posterInfo) {
		super();
		this.userCode = userCode;
		this.programId = programId;
		this.channelResourceCode = channelResourceCode;
		this.eventDate = eventDate;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.eventName = eventName;
		this.bookTime = bookTime;
		this.channelName = channelName;
		this.videoType = videoType;
		this.poster = posterInfo;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getChannelResourceCode() {
		return channelResourceCode;
	}

	public void setChannelResourceCode(String channelResourceCode) {
		this.channelResourceCode = channelResourceCode;
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

	public String getBookTime() {
		return bookTime;
	}

	public void setBookTime(String bookTime) {
		this.bookTime = bookTime;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public ArrayList<Poster> getPosterInfo() {
		return poster;
	}

	public void setPosterInfo(ArrayList<Poster> posterInfo) {
		this.poster = posterInfo;
	}
	
	public int getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(int productPrice) {
		this.productPrice = productPrice;
	}
}