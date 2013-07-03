package com.coship.ott.transport.dto.notice;

import java.util.List;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.40 查询用户分享返回数据实体类
 * */
public class NoticeJson extends BaseJsonBean {
	private int pageSize;
	private int curPage;
	private int pageCount;
	private int totalCount;
	private List<Notice> notices;

	public List<Notice> getNotices() {
		return notices;
	}

	public void setNotices(List<Notice> notices) {
		this.notices = notices;
	}

	public NoticeJson() {
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public NoticeJson(int pageSize, int curPage, int pageCount, int totalCount,
			List<Notice> notices) {
		super();
		this.pageSize = pageSize;
		this.curPage = curPage;
		this.pageCount = pageCount;
		this.totalCount = totalCount;
		this.notices = notices;
	}

}