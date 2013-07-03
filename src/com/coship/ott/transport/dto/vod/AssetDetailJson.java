package com.coship.ott.transport.dto.vod;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.12 影片资源详情返回数据实体类
 * */
public class AssetDetailJson extends BaseJsonBean {
	private AssetInfo assetInfo;

	public AssetDetailJson() {
	}

	public AssetDetailJson(AssetInfo assetInfo) {
		super();
		this.assetInfo = assetInfo;
	}

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}
}