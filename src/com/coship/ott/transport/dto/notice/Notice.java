package com.coship.ott.transport.dto.notice;


/**
 * 公告实体类
 * */
public class Notice {
	// 评论对象 Int 1：直播节目 2：点播节目 3：频道品牌
	private int id;
	// 对象ID String
	private String title;
	// 资源名称 String
	private String content;

	// 分享时间 String 格式：YYYY-MM-DD HH:MM:SS
	private String effectiveTime;
	private String auditTime;

	public Notice() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}

	public Notice(int id, String title, String content, String effectiveTime,
			String auditTime) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.effectiveTime = effectiveTime;
		this.auditTime = auditTime;
	}

}