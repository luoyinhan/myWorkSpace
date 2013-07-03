package com.coship.ott.transport.dto.vod;

/**
 * 热门字实体类
 * */
public class KeyWord {
	// 热门字
	private String keyWord;

	public KeyWord() {
	}

	public KeyWord(String keyWord) {
		super();
		this.keyWord = keyWord;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
}