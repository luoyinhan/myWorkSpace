package com.coship.ott.transport.dto.comment;

import java.util.ArrayList;

import com.coship.ott.transport.dto.user.User;
import com.coship.ott.transport.dto.vod.Poster;

public class Comment {
	// 数字 id
	private int id;
	// 字符 用户ID
	private String userCode;
	// 字符 用户名
	private String userName;
	// 数字 资源类型 1：直播节目 2：点播节目3：频道品牌
	private int objType;
	// 字符 资源iD
	private String objID;
	// 字符 资源名称
	private String objName;
	// 字符 评论信息
	private String comment;
	// 数字 推荐指数
	private int recommendationLevel;
	// 日期 评论时间 格式：YYYY-MM-DD HH:MM:SS
	private String creatTime;
	private int auditStatus;
	// 对象 posterInfo 对应资源海报
	private ArrayList<Poster> poster;
	// 高标清
	private int videoType;
	// 0 单个剧集 1电视剧资源包
	private int type;
	// 电视资源包第一集的id
	private String chapterResourceCode;

	// 用户信息
	private User user;
	private boolean isSelected = false;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Comment() {
	}

	public Comment(int id, String userCode, String userName, int objType,
			String objID, String objName, String comment,
			int recommendationLevel, String creatTime, int auditStatus,
			ArrayList<Poster> poster, User user, int type,
			String chapterResourceCode) {
		super();
		this.id = id;
		this.userCode = userCode;
		this.userName = userName;
		this.objType = objType;
		this.objID = objID;
		this.objName = objName;
		this.comment = comment;
		this.recommendationLevel = recommendationLevel;
		this.creatTime = creatTime;
		this.auditStatus = auditStatus;
		this.poster = poster;
		this.user = user;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getRecommendationLevel() {
		return recommendationLevel;
	}

	public void setRecommendationLevel(int recommendationLevel) {
		this.recommendationLevel = recommendationLevel;
	}

	public String getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}
}