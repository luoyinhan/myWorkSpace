package com.coship.ott.transport.dto.vod;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.29 获取广告返回数据实体类
 * */
public class ADJson extends BaseJsonBean {
	private String adURL;

	public ADJson() {
	}

	public ADJson(String adURL) {
		super();
		this.adURL = adURL;
	}

	public String getPicURL() {
		return adURL;
	}

	public void setPicURL(String adURL) {
		this.adURL = adURL;
	}
}