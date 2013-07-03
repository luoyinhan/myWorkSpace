package com.coship.ott.transport.dto.system;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.51 获取系统时间
 * */
public class SystemTimeJson extends BaseJsonBean {
	private String dateTime;

	public SystemTimeJson() {
	}

	public SystemTimeJson(String dateTime) {
		super();
		this.dateTime = dateTime;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
}