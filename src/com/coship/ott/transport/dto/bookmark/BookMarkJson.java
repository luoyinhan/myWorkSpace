package com.coship.ott.transport.dto.bookmark;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.27 查看预定返回数据实体类
 * */
public class BookMarkJson extends BaseJsonBean {
	// 节目列表
	private BookMark BookMark;

	public BookMarkJson() {
	}

	public BookMarkJson(BookMark bookMark) {
		super();
		BookMark = bookMark;
	}

	public BookMark getBookMark() {
		return BookMark;
	}

	public void setBookMark(BookMark bookMark) {
		BookMark = bookMark;
	}
}