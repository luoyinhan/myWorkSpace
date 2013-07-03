package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.14 获取频道下节目列表返回数据实体类
 * */
public class ProgramInfosJson extends BaseJsonBean {
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;
	// 节目列表
	private ArrayList<ProgramInfo> program;

	public ProgramInfosJson() {
	}

	public ProgramInfosJson(int pageCount, int curPage, int pageSize,
			ArrayList<ProgramInfo> program) {
		super();
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.program = program;
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

	public ArrayList<ProgramInfo> getProgram() {
		return program;
	}

	public void setProgram(ArrayList<ProgramInfo> program) {
		this.program = program;
	}
}