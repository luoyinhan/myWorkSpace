package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 首页推荐资源返回数据实体类
 * */
public class RecommendResourceJson extends BaseJsonBean {
	private ArrayList<IndexRem> indexRemList;

	public RecommendResourceJson() {
	}

	public RecommendResourceJson(ArrayList<IndexRem> indexRemList) {
		super();
		this.indexRemList = indexRemList;
	}

	public ArrayList<IndexRem> getIndexRemList() {
		return indexRemList;
	}

	public void setIndexRemList(ArrayList<IndexRem> indexRemList) {
		this.indexRemList = indexRemList;
	}
}