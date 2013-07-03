package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.43 获取关联关键词返回数据实体类
 * */
public class ResourceKeyWordJson extends BaseJsonBean {
	// 资源关键词
	private ArrayList<ResourceKeyWord> datas;

	public ResourceKeyWordJson() {
	}

	public ResourceKeyWordJson(ArrayList<ResourceKeyWord> datas) {
		super();
		this.datas = datas;
	}

	public ArrayList<ResourceKeyWord> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<ResourceKeyWord> datas) {
		this.datas = datas;
	}

}