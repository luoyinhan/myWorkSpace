package com.coship.ott.transport.dto.auth;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.34 用户鉴权返回数据实体类
 * */
public class AuthInfoJson extends BaseJsonBean {
	// 鉴权结果
	private ArrayList<AuthInfo> authInfo;

	public AuthInfoJson() {
	}

	public AuthInfoJson(ArrayList<AuthInfo> authInfo) {
		super();
		this.authInfo = authInfo;
	}

	public ArrayList<AuthInfo> getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(ArrayList<AuthInfo> authInfo) {
		this.authInfo = authInfo;
	}
}