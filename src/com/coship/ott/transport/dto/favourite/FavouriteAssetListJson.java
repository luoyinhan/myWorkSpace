package com.coship.ott.transport.dto.favourite;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.vod.AssetListInfo;

public class FavouriteAssetListJson extends BaseJsonBean {
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 点播
	private ArrayList<AssetListInfo> Favorite;

	public FavouriteAssetListJson() {
	}

	public FavouriteAssetListJson(int pageCount, int curPage,
			ArrayList<AssetListInfo> favorite) {
		super();
		this.pageCount = pageCount;
		this.curPage = curPage;
		Favorite = favorite;
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

	public ArrayList<AssetListInfo> getFavorite() {
		return Favorite;
	}

	public void setFavorite(ArrayList<AssetListInfo> favorite) {
		Favorite = favorite;
	}
}