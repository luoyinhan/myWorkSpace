package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

/**
 * 专题实体类
 * */
public class SpecialAct {
	// 编号
	private String specialActId;
	// 名称
	private String specialActName;
	// 展示路径
	private String specialActURL;
	// 对象 PosterInfo 对应的海报，1－N个 根据不同终端分辨率返回不同
	private ArrayList<Poster> poster;

	public SpecialAct() {
	}

	public SpecialAct(String specialActId, String specialActName,
			String specialActURL, ArrayList<Poster> poster) {
		super();
		this.specialActId = specialActId;
		this.specialActName = specialActName;
		this.specialActURL = specialActURL;
		this.poster = poster;
	}

	public String getSpecialActId() {
		return specialActId;
	}

	public void setSpecialActId(String specialActId) {
		this.specialActId = specialActId;
	}

	public String getSpecialActName() {
		return specialActName;
	}

	public void setSpecialActName(String specialActName) {
		this.specialActName = specialActName;
	}

	public String getSpecialActURL() {
		return specialActURL;
	}

	public void setSpecialActURL(String specialActURL) {
		this.specialActURL = specialActURL;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}
}