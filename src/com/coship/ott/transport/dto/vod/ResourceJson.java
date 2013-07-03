package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.11 搜索资源列表返回数据实体类
 * */
public class ResourceJson extends BaseJsonBean {
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;
	// 搜索总条数
	private int retCount;
	// 影片条数
	private int assetCount;
	// 直播节目条数
	private int programCount;
	// 资源列表
	private ArrayList<Resource> datas;

	public ResourceJson() {
	}

	public ResourceJson(int pageCount, int curPage, int pageSize, int retCount,
			int assetCount, int programCount, ArrayList<Resource> datas) {
		super();
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.retCount = retCount;
		this.assetCount = assetCount;
		this.programCount = programCount;
		this.datas = datas;
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

	public int getRetCount() {
		return retCount;
	}

	public void setRetCount(int retCount) {
		this.retCount = retCount;
	}

	public int getAssetCount() {
		return assetCount;
	}

	public void setAssetCount(int assetCount) {
		this.assetCount = assetCount;
	}

	public int getProgramCount() {
		return programCount;
	}

	public void setProgramCount(int programCount) {
		this.programCount = programCount;
	}

	public ArrayList<Resource> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<Resource> datas) {
		this.datas = datas;
	}
}