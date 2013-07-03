package com.coship.ott.transport.dto;

import java.util.ArrayList;

public class ProductInfoJson extends BaseJsonBean {
	private ArrayList<ProductInfo> productInfos;

	public ProductInfoJson(int ret, String retInfo,
			ArrayList<ProductInfo> datas) {
		super(ret, retInfo);
		this.productInfos = datas;
	}

	public ProductInfoJson() {
	}

	public ArrayList<ProductInfo> getDatas() {
		return productInfos;
	}

	public void setDatas(ArrayList<ProductInfo> datas) {
		this.productInfos = datas;
	}
}
