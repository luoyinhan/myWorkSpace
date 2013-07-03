package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.5 获取系统参数返回数据实体类
 * */
public class PramJson extends BaseJsonBean {
	private ArrayList<Pram> datas;

	public PramJson() {
	}

	public PramJson(ArrayList<Pram> datas) {
		super();
		this.datas = datas;
	}

	public ArrayList<Pram> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<Pram> datas) {
		this.datas = datas;
	}
}