package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.15 获取频道品牌列表返回数据实体类
 * */
public class ChannelbrandsJson extends BaseJsonBean {
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;
	// 节目列表
	private ArrayList<Channelbrand> channelbrand;
	// 节目列表
	private ArrayList<ProgramInfo> programs;

	public ChannelbrandsJson() {
	}

	public ChannelbrandsJson(int pageCount, int curPage, int pageSize,
			ArrayList<Channelbrand> channelbrand,ArrayList<ProgramInfo> programs) {
		super();
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.channelbrand = channelbrand;
		this.programs = programs;
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

	public ArrayList<Channelbrand> getChannelbrand() {
		return channelbrand;
	}

	public void setChannelbrand(ArrayList<Channelbrand> channelbrand) {
		this.channelbrand = channelbrand;
	}

	public ArrayList<ProgramInfo> getPrograms() {
		return programs;
	}

	public void setPrograms(ArrayList<ProgramInfo> programs) {
		this.programs = programs;
	}
}