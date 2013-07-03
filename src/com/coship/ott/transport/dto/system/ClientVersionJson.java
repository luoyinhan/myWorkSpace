package com.coship.ott.transport.dto.system;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.38 查询更新返回数据实体类
 * */
public class ClientVersionJson extends BaseJsonBean {
	private String clientVersion;
	private int forceFlag;
	private String downloadUrl;
	private String descn;

	public String getDescn() {
		return descn;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}

	public int getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(int forceFlag) {
		this.forceFlag = forceFlag;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public ClientVersionJson() {
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public ClientVersionJson(String clientVersion, int forceFlag,
			String downloadUrl, String descn) {
		super();
		this.clientVersion = clientVersion;
		this.forceFlag = forceFlag;
		this.downloadUrl = downloadUrl;
		this.descn = descn;
	}

}