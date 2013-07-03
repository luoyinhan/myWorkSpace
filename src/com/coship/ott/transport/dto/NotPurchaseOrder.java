package com.coship.ott.transport.dto;

import java.util.ArrayList;

public class NotPurchaseOrder {

	private ArrayList<ProductInfo> productInfo;
	private String url;

	public NotPurchaseOrder() {
	}

	public NotPurchaseOrder(ArrayList<ProductInfo> productInfo, String url) {
		super();
		this.productInfo = productInfo;
		this.url = url;
	}

	public ArrayList<ProductInfo> getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(ArrayList<ProductInfo> productInfo) {
		this.productInfo = productInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
