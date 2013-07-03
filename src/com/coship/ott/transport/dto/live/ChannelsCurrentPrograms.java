package com.coship.ott.transport.dto.live;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.44 获取多个频道的当前节目单返回数据实体类
 * */
public class ChannelsCurrentPrograms extends BaseJsonBean {
	// 当前频道的id
	private String channelResourceCode;
	// 上一个节目 Program
	private ProgramInfo lastProgram;
	
	// 当前节目 Program
	private ProgramInfo currentProgram;
	// 下一个节目 Program
	private ProgramInfo nextProgram;

//	public ChannelsCurrentPrograms() {
//	}
//
//	public ChannelsCurrentPrograms(String channelResourceCode,
//			ProgramInfo lastProgram, ProgramInfo currentProgram,
//			ProgramInfo nextProgram) {
//		super();
//		this.channelResourceCode = channelResourceCode;
//		this.lastProgram = lastProgram;
//		this.currentProgram = currentProgram;
//		this.nextProgram = nextProgram;
//	}

	public String getChannelResourceCode() {
		return channelResourceCode;
	}

	public void setChannelResourceCode(String channelResourceCode) {
		this.channelResourceCode = channelResourceCode;
	}
	
	public ProgramInfo getLastProgram() {
		return lastProgram;
	}

	public void setLastProgram(ProgramInfo lastProgram) {
		this.lastProgram = lastProgram;
	}

	public ProgramInfo getCurrentProgram() {
		return currentProgram;
	}

	public void setCurrentProgram(ProgramInfo currentProgram) {
		this.currentProgram = currentProgram;
	}

	public ProgramInfo getNextProgram() {
		return nextProgram;
	}

	public void setNextProgram(ProgramInfo nextProgram) {
		this.nextProgram = nextProgram;
	}
}