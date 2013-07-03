package com.coship.ott.transport.dto.comment;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.21 查看节目最近评论返回数据实体类
 * */
public class CommentsJson extends BaseJsonBean {
	// 总条数
	private int retCount;
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;
	// 节目列表
	private ArrayList<Comment> Comments;

	public CommentsJson() {
	}

	public CommentsJson(int retCount, int pageCount, int curPage, int pageSize,
			ArrayList<Comment> Comments) {
		super();
		this.retCount = retCount;
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.Comments = Comments;
	}

	public int getRetCount() {
		return retCount;
	}

	public void setRetCount(int retCount) {
		this.retCount = retCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public ArrayList<Comment> getComments() {
		return Comments;
	}

	public void setComments(ArrayList<Comment> comments) {
		Comments = comments;
	}
}