package com.coship.ott.transport.dto.bookmark;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.31 查询书签返回数据实体类(多个标签)
 * */
public class BookMarksJson extends BaseJsonBean {
	// 节目列表
	private ArrayList<BookMark> BookMark;

	public BookMarksJson() {
	}

	public BookMarksJson(ArrayList<BookMark> bookMark) {
		super();
		BookMark = bookMark;
	}

	public ArrayList<BookMark> getBookMark() {
		return BookMark;
	}

	public void setBookMark(ArrayList<BookMark> bookMark) {
		BookMark = bookMark;
	}

}