package com.coship.ott.utils;

public class Session {
	/**
	 * 当前用户ID
	 * */
	private String userCode = "";
	/**
	 * 当前用户用户名
	 * */
	private String userName = "";
	private String passWord = "";
	private String nickName = "";
	private String bindNo = "";
	/** 设备的mac地址 **/
	private String macPath = "";
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getBindNo() {
		return bindNo;
	}

	public void setBindNo(String bindNo) {
		this.bindNo = bindNo;
	}

	// 用户令牌
	private String token = "";

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * 超时时间
	 * */
	private int timeOut = -1;

	public String getMacPath() {
		return macPath;
	}

	public void setMacPath(String macPath) {
		this.macPath = macPath;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	/**
	 * 当前是否已登录
	 * */
	private boolean isLogined = false;

	private static Session mSession;

	private Session() {
	}

	public static Session getInstance() {
		if (null == mSession) {
			mSession = new Session();
		}
		return mSession;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public boolean isLogined() {
		return isLogined;
	}

	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
	}

}