package com.coship.ott.transport.dto.live;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.43 获取频道的当前节目单返回数据实体类
 * */
public class ChannelCurrentProgramsJson extends BaseJsonBean {
	// 上一个节目 Program
	private ProgramInfo lastProgram;
	// 当前节目 Program
	private ProgramInfo currentProgram;
	// 下一个节目 Program
	private ProgramInfo nextProgram;

	public ChannelCurrentProgramsJson() {
	}

	public ChannelCurrentProgramsJson(ProgramInfo lastProgram,
			ProgramInfo currentProgram, ProgramInfo nextProgram) {
		super();
		this.lastProgram = lastProgram;
		this.currentProgram = currentProgram;
		this.nextProgram = nextProgram;
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