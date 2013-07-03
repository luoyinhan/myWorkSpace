package com.coship.ott.transport.dto.share;

import java.util.ArrayList;

import com.coship.ott.transport.dto.vod.Poster;

/**
 * 用户分享实体类
 * */
public class Share {
	// 评论对象 Int 1：直播节目 2：点播节目 3：频道品牌
	private int objType;
	// 对象ID String
	private String objID;
	// 资源名称 String
	private String objName;

	// 分享时间 String 格式：YYYY-MM-DD HH:MM:SS
	private String shareTime;
	// 对应资源海报 对象
	private ArrayList<Poster> poster;
	private int videoType;
	private String id;
	private int type;
	private String chapterResourceCode;

	// 选中的状态
	private boolean isSelected = false;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Share() {
	}

	public Share(int objType, String objID, String objName, String shareTime,
			ArrayList<Poster> poster, String id, int type,
			String chapterResourceCode) {
		super();
		this.objType = objType;
		this.objID = objID;
		this.objName = objName;
		this.shareTime = shareTime;
		this.poster = poster;
		this.id = id;
		this.type = type;
		this.chapterResourceCode = chapterResourceCode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getChapterResourceCode() {
		return chapterResourceCode;
	}

	public void setChapterResourceCode(String chapterResourceCode) {
		this.chapterResourceCode = chapterResourceCode;
	}

	public int getObjType() {
		return objType;
	}

	public void setObjType(int objType) {
		this.objType = objType;
	}

	public String getObjID() {
		return objID;
	}

	public void setObjID(String objID) {
		this.objID = objID;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getShareTime() {
		return shareTime;
	}

	public void setShareTime(String shareTime) {
		this.shareTime = shareTime;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}