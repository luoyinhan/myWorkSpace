package com.coship.ott.transport.dto.special;

import java.util.ArrayList;
import com.coship.ott.transport.dto.vod.Poster;

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
	private String padActURL;
	// 对象 PosterInfo 对应的海报，1－N个 根据不同终端分辨率返回不同
	private ArrayList<Poster> poster;
	private String rank;

	public SpecialAct() {
	}

	public SpecialAct(String specialActId, String specialActName,
			String specialActURL, String padActURL, String rank,
			ArrayList<Poster> poster) {
		super();
		this.specialActId = specialActId;
		this.specialActName = specialActName;
		this.specialActURL = specialActURL;
		this.padActURL = padActURL;
		this.poster = poster;
		this.rank = rank;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getPadActURL() {
		return padActURL;
	}

	public void setPadActURL(String padActURL) {
		this.padActURL = padActURL;
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