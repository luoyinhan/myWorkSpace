package com.coship.ott.transport.dto.share;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.40 查询用户分享返回数据实体类
 * */
public class ShareJson extends BaseJsonBean {
	private ArrayList<Share> datas;

	public ShareJson() {
	}

	public ShareJson(ArrayList<Share> datas) {
		super();
		this.datas = datas;
	}

	public ArrayList<Share> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<Share> datas) {
		this.datas = datas;
	}

}