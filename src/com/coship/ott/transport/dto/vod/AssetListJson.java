package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.live.Channelbrand;
import com.coship.ott.transport.dto.live.ProgramInfo;
import com.coship.ott.transport.dto.special.SpecialAct;

/**
 * 4.8 获取资源列表返回数据实体类
 * */
public class AssetListJson extends BaseJsonBean {
	private ArrayList<SpecialAct> specialAct;
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;
	// 栏目下资源类型 Int 1：点播 2：直播 3：专题 4：频道品牌
	private int resourceType;
	// 点播
	private ArrayList<AssetListInfo> assetList;
	// 直播
	private ArrayList<ProgramInfo> programList;
	// 专题
	private ArrayList<SpecialAct> specialActList;
	// 频道品牌
	private ArrayList<Channelbrand> channelbrandList;

	public AssetListJson() {
	}

	public AssetListJson(ArrayList<SpecialAct> specialAct, int pageCount,
			int curPage, int pageSize, int resourceType,
			ArrayList<AssetListInfo> assetList,
			ArrayList<ProgramInfo> programList,
			ArrayList<SpecialAct> specialActList,
			ArrayList<Channelbrand> channelbrandList) {
		super();
		this.specialAct = specialAct;
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.resourceType = resourceType;
		this.assetList = assetList;
		this.programList = programList;
		this.specialActList = specialActList;
		this.channelbrandList = channelbrandList;
	}

	public ArrayList<SpecialAct> getSpecialAct() {
		return specialAct;
	}

	public void setSpecialAct(ArrayList<SpecialAct> specialAct) {
		this.specialAct = specialAct;
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

	public int getResourceType() {
		return resourceType;
	}

	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
	}

	public ArrayList<AssetListInfo> getAssetList() {
		return assetList;
	}

	public void setAssetList(ArrayList<AssetListInfo> assetList) {
		this.assetList = assetList;
	}

	public ArrayList<ProgramInfo> getProgramList() {
		return programList;
	}

	public void setProgramList(ArrayList<ProgramInfo> programList) {
		this.programList = programList;
	}

	public ArrayList<SpecialAct> getSpecialActList() {
		return specialActList;
	}

	public void setSpecialActList(ArrayList<SpecialAct> specialActList) {
		this.specialActList = specialActList;
	}

	public ArrayList<Channelbrand> getChannelbrandList() {
		return channelbrandList;
	}

	public void setChannelbrandList(ArrayList<Channelbrand> channelbrandList) {
		this.channelbrandList = channelbrandList;
	}
}