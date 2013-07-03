package com.coship.ott.transport.dto.user;

public class User {
	private String userName;
	private String loginTime;
	private String userCode;
	private String bossUserId;
	private String nickName;
	private String logo;
	private String sign;
	private String email;
	private String phone;
	private String bindDeviceNo;
	private String remark;
	private String token;

	public User() {
	}

	public User(String usrName, String loginTime, String userCode,
			String bossUserId, String nickName, String logo, String sign,
			String email, String phone, String bindDeviceNo, String remark,
			String token) {
		super();
		this.userName = usrName;
		this.loginTime = loginTime;
		this.userCode = userCode;
		this.bossUserId = bossUserId;
		this.nickName = nickName;
		this.logo = logo;
		this.sign = sign;
		this.email = email;
		this.phone = phone;
		this.bindDeviceNo = bindDeviceNo;
		this.remark = remark;
		this.token = token;
	}

	public String getUsrName() {
		return userName;
	}

	public void setUsrName(String usrName) {
		this.userName = usrName;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getBossUserId() {
		return bossUserId;
	}

	public void setBossUserId(String bossUserId) {
		this.bossUserId = bossUserId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBindDeviceNo() {
		return bindDeviceNo;
	}

	public void setBindDeviceNo(String bindDeviceNo) {
		this.bindDeviceNo = bindDeviceNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}