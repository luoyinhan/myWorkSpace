package com.coship.ott.transport.dto.vod;

/**
 * 3.8 资源关键词实体类
 * */
public class ResourceKeyWord {
	// 数字 键值
	private int id;
	// 10 关键词
	private String keyWord;
	// 50 字符 全拼
	private String fullSpelling;
	// 50 字符 简拼
	private String simpleSpelling;
	// 1 数字 状态
	private int status;

	public ResourceKeyWord() {
	}

	public ResourceKeyWord(int id, String keyWord, String fullSpelling,
			String simpleSpelling, int status) {
		super();
		this.id = id;
		this.keyWord = keyWord;
		this.fullSpelling = fullSpelling;
		this.simpleSpelling = simpleSpelling;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getFullSpelling() {
		return fullSpelling;
	}

	public void setFullSpelling(String fullSpelling) {
		this.fullSpelling = fullSpelling;
	}

	public String getSimpleSpelling() {
		return simpleSpelling;
	}

	public void setSimpleSpelling(String simpleSpelling) {
		this.simpleSpelling = simpleSpelling;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}