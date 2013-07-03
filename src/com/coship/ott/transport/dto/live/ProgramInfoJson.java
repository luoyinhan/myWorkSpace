package com.coship.ott.transport.dto.live;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.19 获取直播节目详情返回数据实体类
 * */
public class ProgramInfoJson extends BaseJsonBean {
	private ProgramInfo programInfo;

	public ProgramInfoJson() {
	}

	public ProgramInfoJson(ProgramInfo programInfo) {
		super();
		this.programInfo = programInfo;
	}

	public ProgramInfo getProgramInfo() {
		return programInfo;
	}

	public void setProgramInfo(ProgramInfo programInfo) {
		this.programInfo = programInfo;
	}
}