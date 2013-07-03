package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.8 获取资源列表返回数据实体类（直播排行）
 * */
public class RankingProgramsJson extends BaseJsonBean {
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;
	// 栏目下资源类型 Int 1：点播 2：直播 3：专题 4：频道品牌
	private int resourceType;
	// 节目列表
	private ArrayList<Channelbrand> channelbrandList;

	public RankingProgramsJson() {
	}

	public RankingProgramsJson(int pageCount, int curPage, int pageSize,
			ArrayList<Channelbrand> channelbrandList, int resourceType) {
		super();
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.channelbrandList = channelbrandList;
		this.resourceType = resourceType;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public ArrayList<Channelbrand> getChannelbrandList() {
		return channelbrandList;
	}

	public void setChannelbrandList(ArrayList<Channelbrand> channelbrandList) {
		this.channelbrandList = channelbrandList;
	}

	public int getResourceType() {
		return resourceType;
	}

	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
	}
}