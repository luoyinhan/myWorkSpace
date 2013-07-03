package com.coship.ott.transport.dto.user;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 用户登录返回结果类
 * */
public class CheckLoginJson extends BaseJsonBean {

	private String optType;
	private String optContent;

	public String getOptContent() {
		return optContent;
	}

	public void setOptContent(String optContent) {
		this.optContent = optContent;
	}

	public CheckLoginJson() {
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public CheckLoginJson(String optType, String optContent) {
		super();
		this.optType = optType;
		this.optContent = optContent;
	}

	

}