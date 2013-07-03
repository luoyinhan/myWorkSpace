package com.coship.ott.transport.dto.user;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 用户登录返回结果类
 * */
public class LoginJson extends BaseJsonBean {

	private String userCode;
	private String token;

	public LoginJson() {
	}

	public LoginJson(String userCode, String token) {
		super();
		this.userCode = userCode;
		this.token = token;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}