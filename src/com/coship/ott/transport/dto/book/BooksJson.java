package com.coship.ott.transport.dto.book;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.27 查看预定返回数据实体类
 * */
public class BooksJson extends BaseJsonBean {
	// 节目列表
	private ArrayList<Book> books;

	public BooksJson() {
	}

	public BooksJson(ArrayList<Book> books, int pageCount, int curPage) {
		super();
		this.books = books;
	}

	public ArrayList<Book> getBooks() {
		return books;
	}

	public void setBooks(ArrayList<Book> books) {
		this.books = books;
	}
}