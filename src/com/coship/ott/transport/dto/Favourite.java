package com.coship.ott.transport.dto;

public class Favourite {
	private String userCode;
	private String resourceCode;

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	public Favourite(String userCode, String resourceCode) {
		super();
		this.userCode = userCode;
		this.resourceCode = resourceCode;
	}

	public Favourite() {
		super();
	}

}
