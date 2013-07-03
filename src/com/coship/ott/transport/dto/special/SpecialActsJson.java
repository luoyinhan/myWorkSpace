package com.coship.ott.transport.dto.special;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.6 获取专题列表返回数据实体类
 * */
public class SpecialActsJson extends BaseJsonBean {
	private ArrayList<SpecialAct> datas;
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;

	public SpecialActsJson() {
	}

	public SpecialActsJson(ArrayList<SpecialAct> specialAct, int pageCount,
			int curPage, int pageSize) {
		super();
		this.datas = specialAct;
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
	}

	public ArrayList<SpecialAct> getSpecialAct() {
		return datas;
	}

	public void setSpecialAct(ArrayList<SpecialAct> specialAct) {
		this.datas = specialAct;
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
}