package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.4 获取栏目列表返回数据实体类
 * */
public class CatalogJson extends BaseJsonBean {
	private ArrayList<Catalog> Catalog;

	public CatalogJson() {
	}

	public CatalogJson(ArrayList<Catalog> catalog) {
		super();
		Catalog = catalog;
	}

	public ArrayList<Catalog> getCatalog() {
		return Catalog;
	}

	public void setCatalog(ArrayList<Catalog> catalog) {
		Catalog = catalog;
	}
}