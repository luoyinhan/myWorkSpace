package com.coship.ott.transport.dto.bookmark;

import java.util.ArrayList;

import com.coship.ott.transport.dto.vod.Poster;

/**
 * 3.5 书签实体
 * */
public class BookMark {
	// 书签时间 字符
	private int bookMark;
	// 资源ID 字符
	private String resourceCode;
	// 频道ID 数字
	private String resourceName;
	// 日期 日期
	private String bookMarkDate;
	// 资源：0 单片：1 资源包
	private int type;
	// 当前媒资resourceCode
	private String currentResourceCode;
	// posterInfo 海报List
	private ArrayList<Poster> poster;
	private int videoType;

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	// 选中的状态
	private boolean isSelected = false;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public BookMark() {
	}

	public BookMark(int bookMark, String resourceCode, String resourceName,
			String bookMarkDate, int type, String currentResourceCode,
			ArrayList<Poster> poster) {
		super();
		this.bookMark = bookMark;
		this.resourceCode = resourceCode;
		this.resourceName = resourceName;
		this.bookMarkDate = bookMarkDate;
		this.type = type;
		this.currentResourceCode = currentResourceCode;
		this.poster = poster;
	}

	public int getBookMark() {
		return bookMark;
	}

	public void setBookMark(int bookMark) {
		this.bookMark = bookMark;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getBookMarkDate() {
		return bookMarkDate;
	}

	public void setBookMarkDate(String bookMarkDate) {
		this.bookMarkDate = bookMarkDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCurrentResourceCode() {
		return currentResourceCode;
	}

	public void setCurrentResourceCode(String currentResourceCode) {
		this.currentResourceCode = currentResourceCode;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}
}