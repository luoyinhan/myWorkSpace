package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

/**
 * 3.1 首页推荐实体类
 * */
public class IndexRem {
	// 资源编号 频道品牌对应brandID 点播对应resourceCode 专题对应specialActId
	private String resourceCode;
	// 资源类型 1：频道品牌 2：点播节目 3：专题
	private int resourceType;
	// 排序
	private int rank;
	// 对应的海报，1－N个
	private ArrayList<Poster> poster;

	public IndexRem(String resourceCode, int resourceType, int rank,
			ArrayList<Poster> poster) {
		super();
		this.resourceCode = resourceCode;
		this.resourceType = resourceType;
		this.rank = rank;
		this.poster = poster;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	public int getResourceType() {
		return resourceType;
	}

	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}
}