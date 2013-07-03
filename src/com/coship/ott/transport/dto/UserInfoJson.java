package com.coship.ott.transport.dto;

/**
 * 根据用户ID获取用户信息
 * */
public class UserInfoJson extends BaseJsonBean {
	
	private UserInfo userInfo;
	
	public UserInfoJson() {
	}

	public UserInfoJson(int ret, String retInfo, UserInfo datas) {
		super(ret, retInfo);
		this.setUserInfo(datas);
	}

	/**
	 * @return the userInfo
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * @param userInfo the userInfo to set
	 */
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

//	private String userName;
//	private String loginTime;
//	private String userCode;
//	private String bossUserId;
//	private String nickName;
//	private String logo;
//	
//	private String sign;
//	private String email;
//	private String phone;
//	private String bindDeviceNo;
//	
//	private String remark;
//	private String token;
//
//
//	public UserInfoJson(String userName, String loginTime, String userCode, String bossUserId,
//			String nickName, String logo, String sign, String email, 
//			String phone, String bindDeviceNo,String remark,String token) {
//		super();
//		this.setUserName(userName);
//		this.setLoginTime(loginTime);
//		this.setUserCode(userCode);
//		this.setBossUserId(bossUserId);
//		this.setNickName(nickName);
//		this.setLogo(logo);
//		this.setSign(sign);
//		
//		this.setEmail(email);
//		this.setPhone(phone);
//		this.setBindDeviceNo(bindDeviceNo);	
//		this.setRemark(remark);	
//		this.setToken(token);
//
//	}
//
//	/**
//	 * @return the userName
//	 */
//	public String getUserName() {
//		return userName;
//	}
//
//	/**
//	 * @param userName the userName to set
//	 */
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
//
//	/**
//	 * @return the loginTime
//	 */
//	public String getLoginTime() {
//		return loginTime;
//	}
//
//	/**
//	 * @param loginTime the loginTime to set
//	 */
//	public void setLoginTime(String loginTime) {
//		this.loginTime = loginTime;
//	}
//
//	/**
//	 * @return the userCode
//	 */
//	public String getUserCode() {
//		return userCode;
//	}
//
//	/**
//	 * @param userCode the userCode to set
//	 */
//	public void setUserCode(String userCode) {
//		this.userCode = userCode;
//	}
//
//	/**
//	 * @return the bossUserId
//	 */
//	public String getBossUserId() {
//		return bossUserId;
//	}
//
//	/**
//	 * @param bossUserId the bossUserId to set
//	 */
//	public void setBossUserId(String bossUserId) {
//		this.bossUserId = bossUserId;
//	}
//
//	/**
//	 * @return the nickName
//	 */
//	public String getNickName() {
//		return nickName;
//	}
//
//	/**
//	 * @param nickName the nickName to set
//	 */
//	public void setNickName(String nickName) {
//		this.nickName = nickName;
//	}
//
//	/**
//	 * @return the logo
//	 */
//	public String getLogo() {
//		return logo;
//	}
//
//	/**
//	 * @param logo the logo to set
//	 */
//	public void setLogo(String logo) {
//		this.logo = logo;
//	}
//
//	/**
//	 * @return the sign
//	 */
//	public String getSign() {
//		return sign;
//	}
//
//	/**
//	 * @param sign the sign to set
//	 */
//	public void setSign(String sign) {
//		this.sign = sign;
//	}
//
//	/**
//	 * @return the email
//	 */
//	public String getEmail() {
//		return email;
//	}
//
//	/**
//	 * @param email the email to set
//	 */
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	/**
//	 * @return the bindDeviceNo
//	 */
//	public String getBindDeviceNo() {
//		return bindDeviceNo;
//	}
//
//	/**
//	 * @param bindDeviceNo the bindDeviceNo to set
//	 */
//	public void setBindDeviceNo(String bindDeviceNo) {
//		this.bindDeviceNo = bindDeviceNo;
//	}
//
//	/**
//	 * @return the phone
//	 */
//	public String getPhone() {
//		return phone;
//	}
//
//	/**
//	 * @param phone the phone to set
//	 */
//	public void setPhone(String phone) {
//		this.phone = phone;
//	}
//
//	/**
//	 * @return the remark
//	 */
//	public String getRemark() {
//		return remark;
//	}
//
//	/**
//	 * @param remark the remark to set
//	 */
//	public void setRemark(String remark) {
//		this.remark = remark;
//	}
//
//	/**
//	 * @return the token
//	 */
//	public String getToken() {
//		return token;
//	}
//
//	/**
//	 * @param token the token to set
//	 */
//	public void setToken(String token) {
//		this.token = token;
//	}

}
