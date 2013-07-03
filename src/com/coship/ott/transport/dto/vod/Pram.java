package com.coship.ott.transport.dto.vod;
/**
 * 系统参数实体类
 * */
public class Pram {
	// 参数输入值
	private String pramKey;
	// 参数显示值
	private String pramValue;

	public Pram() {
	}

	public Pram(String pramKey, String pramValue) {
		super();
		this.pramKey = pramKey;
		this.pramValue = pramValue;
	}

	public String getPramKey() {
		return pramKey;
	}

	public void setPramKey(String pramKey) {
		this.pramKey = pramKey;
	}

	public String getPramValue() {
		return pramValue;
	}

	public void setPramValue(String pramValue) {
		this.pramValue = pramValue;
	}
}