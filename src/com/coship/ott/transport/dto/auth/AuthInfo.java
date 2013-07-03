package com.coship.ott.transport.dto.auth;

/**
 * 3.6 用户鉴权结果实体类
 * */
public class AuthInfo {

	// 字符 资源ID 数字、字母、下划线的组合；

	private String resourceID;
	// 字符 资源ID 数字、字母、下划线的组合；
	private String resourceCode;
	// 数字 订购关系ID (若存在订购关系则需要返回此字段)
	private String subID;

	// 字符 产品编码 数字、字母、下划线的组合；
	private String productCode;
	// 字符 Timestamp，时间戳 YYYYMMDDHHMMSS 如20091021095501
	private String ts;
	// 字符 用户访问标识，必须，摘要字符串，算法为：请求消息携带有ResourceID则采用下列公式：
	// LinkID=Hex(MD5(UserCode+ProductCode+ResourceID+TS+Key))
	// 字段ProductCode、ResourceID若为空则取空串计算，Key为AAA计算及验证摘要时使用。
	// 数字、字母的组合
	private String linkID;
	// 时间 订购关系失效时间 默认时间格式
	private String disableTime;
	// 时间 订购关系生效时间 默认时间格式
	private String enableTime;
	// 字符 优惠策略编码 字母、数字和下划线的组合
	private String policyCode;
	// 数字 产品价格，以分为单位 (此字段为保留字段)
	private int price;

	public AuthInfo() {
	}

	public AuthInfo(String resourceID, String resourceCode, String subID,
			String productCode, String ts, String linkID, String disableTime,
			String enableTime, String policyCode, int price) {
		super();
		this.resourceID = resourceID;
		this.resourceCode = resourceCode;
		this.subID = subID;
		this.productCode = productCode;
		this.ts = ts;
		this.linkID = linkID;
		this.disableTime = disableTime;
		this.enableTime = enableTime;
		this.policyCode = policyCode;
		this.price = price;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

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

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getLinkID() {
		return linkID;
	}

	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}

	public String getDisableTime() {
		return disableTime;
	}

	public void setDisableTime(String disableTime) {
		this.disableTime = disableTime;
	}

	public String getEnableTime() {
		return enableTime;
	}

	public void setEnableTime(String enableTime) {
		this.enableTime = enableTime;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}