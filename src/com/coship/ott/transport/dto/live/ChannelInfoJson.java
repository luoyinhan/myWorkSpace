package com.coship.ott.transport.dto.live;

import java.util.ArrayList;

import com.coship.ott.transport.dto.BaseJsonBean;

/**
 * 4.13 获取频道列表返回数据实体类
 * */
public class ChannelInfoJson extends BaseJsonBean {
	// 总页数
	private int pageCount;
	// 当前页 当前显示的第几页
	private int curPage;
	// 每页显示数 每页显示的条数
	private int pageSize;
	// 系统频道版本 Int
	private int channelVersion;
	// 频道 channelInfo
	private ArrayList<ChannelInfo> channelInfo;

	public ChannelInfoJson() {
	}

	public ChannelInfoJson(int pageCount, int curPage, int pageSize,
			int channelVersion, ArrayList<ChannelInfo> channelInfo) {
		super();
		this.pageCount = pageCount;
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.channelVersion = channelVersion;
		this.channelInfo = channelInfo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getChannelVersion() {
		return channelVersion;
	}

	public void setChannelVersion(int channelVersion) {
		this.channelVersion = channelVersion;
	}

	public ArrayList<ChannelInfo> getChannelInfo() {
		return channelInfo;
	}

	public void setChannelInfo(ArrayList<ChannelInfo> channelInfo) {
		this.channelInfo = channelInfo;
	}
}