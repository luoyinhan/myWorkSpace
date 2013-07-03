package com.coship.ott.transport.dto.vod;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.7 获取专题详情返回数据实体类
 * */
public class SpecialActInfoJson extends BaseJsonBean {
	private SpecialAct specialAct;

	public SpecialActInfoJson() {
	}

	public SpecialActInfoJson(SpecialAct specialAct) {
		super();
		this.specialAct = specialAct;
	}

	public SpecialAct getSpecialAct() {
		return specialAct;
	}

	public void setSpecialAct(SpecialAct specialAct) {
		this.specialAct = specialAct;
	}

}