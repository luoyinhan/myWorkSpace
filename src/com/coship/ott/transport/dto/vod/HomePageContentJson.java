package com.coship.ott.transport.dto.vod;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 客户端启动画面返回数据实体类
 * */
public class HomePageContentJson extends BaseJsonBean {
	private HomePageContent data;

	public HomePageContent getData() {
		return data;
	}

	public void setData(HomePageContent data) {
		this.data = data;
	}

	public HomePageContentJson(HomePageContent data) {
		super();
		this.data = data;
	}

	public HomePageContentJson() {
		super();
	}

}