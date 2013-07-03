package com.coship.ott.transport.dto.live;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.16 获取频道品牌详情返回数据实体类
 * */
public class ChannelbrandJson extends BaseJsonBean {
	private Channelbrand channelbrand;

	public ChannelbrandJson() {
	}

	public ChannelbrandJson(Channelbrand channelbrand) {
		super();
		this.channelbrand = channelbrand;
	}

	public Channelbrand getChannelbrand() {
		return channelbrand;
	}

	public void setChannelbrand(Channelbrand channelbrand) {
		this.channelbrand = channelbrand;
	}

}