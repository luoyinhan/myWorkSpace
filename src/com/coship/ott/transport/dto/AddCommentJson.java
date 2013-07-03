package com.coship.ott.transport.dto;

/**
 * 根据用户ID获取用户信息
 * */
public class AddCommentJson extends BaseJsonBean {
	
	private String auditStatus;
	
	public AddCommentJson() {
	}

	public AddCommentJson(int ret, String retInfo, String datas) {
		super(ret, retInfo);
		this.setAuditStatus(datas);
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	

}
