package com.coship.ott.transport.dto.vod;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 广东省网专用，根据assetID获取resourceCode;
 * */
public class ResourceCodeJson extends BaseJsonBean {
	private String data;

	public ResourceCodeJson() {
	}

	public ResourceCodeJson(String data) {
		super();
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}