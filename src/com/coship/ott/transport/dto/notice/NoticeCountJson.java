package com.coship.ott.transport.dto.notice;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.40 查询用户分享返回数据实体类
 * */
public class NoticeCountJson extends BaseJsonBean {
	private int count;

	public NoticeCountJson() {
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public NoticeCountJson(int count) {
		super();
		this.count = count;
	}

}