package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.10 获取搜索热门字返回数据实体类
 * */
public class KeyWordJson extends BaseJsonBean {
	private ArrayList<KeyWord> datas;

	public KeyWordJson() {
	}

	public KeyWordJson(ArrayList<KeyWord> datas) {
		super();
		this.datas = datas;
	}

	public ArrayList<KeyWord> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<KeyWord> datas) {
		this.datas = datas;
	}
}