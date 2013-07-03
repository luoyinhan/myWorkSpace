package com.coship.ott.transport.dto.vod;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.36 获取播放串返回数据实体类
 * */
public class PlayURLJson extends BaseJsonBean {
	private String palyURL;
	private String subID;// 订购ID
	private String productCode;// 产品编码

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public PlayURLJson() {
	}

	public PlayURLJson(String palyURL) {
		super();
		this.palyURL = palyURL;
	}

	public String getPalyURL() {
		return palyURL;
	}

	public void setPalyURL(String palyURL) {
		this.palyURL = palyURL;
	}

}