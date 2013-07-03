package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.44 获取多个频道的当前节目单返回数据实体类
 * */
public class ChannelsCurrentProgramsJson extends BaseJsonBean {
	public ArrayList<ChannelsCurrentPrograms> channelPrograms=null;

	public ArrayList<ChannelsCurrentPrograms> getChannelPrograms() {
		return channelPrograms;
	}

	public void setChannelPrograms(
			ArrayList<ChannelsCurrentPrograms> channelPrograms) {
		this.channelPrograms = channelPrograms;
	}

	public ChannelsCurrentProgramsJson() {
		super();
	}

	public ChannelsCurrentProgramsJson(
			ArrayList<ChannelsCurrentPrograms> channelPrograms) {
		super();
		this.channelPrograms = channelPrograms;
	}
	
	
}