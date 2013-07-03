package com.coship.ott.transport.dto.vod;

/**
 * 海报实体类
 * */
public class Poster {
	// 海报ID,不超过10位的数字
	private String PosterID;
	// 所有者
	private String OwnerCode;
	// 海报类型 枚举值 0--媒资文件 1--媒资库 2--频道 3--节目单 4--应用文件 5--应用库 101—首页推荐 102—频道品牌
	// 103—专题
	private int PosterType;
	// 文件名,数字、字母和下横线
	private String FileName;
	// 上传时间,YYYY-MM-DD HH:MM:SS
	private String UploadTime;
	// 文件大小，默认0 对应天威ADI文件中字段Image_Aspect_Ratio，单位为B
	private int FileSize;
	// 状态。枚举值： 0：不可用1：可用
	private int Status;
	// 本地路径URL路径
	private String LocalPath;
	// 宽度
	private int Width;
	// 高度
	private int Height;
	// 终端类型
	private int Platform;

	public Poster() {
	}

	public Poster(String posterID, String ownerCode, int posterType,
			String fileName, String uploadTime, int fileSize, int status,
			String localPath, int width, int height, int platform) {
		super();
		PosterID = posterID;
		OwnerCode = ownerCode;
		PosterType = posterType;
		FileName = fileName;
		UploadTime = uploadTime;
		FileSize = fileSize;
		Status = status;
		LocalPath = localPath;
		Width = width;
		Height = height;
		Platform = platform;
	}

	public String getPosterID() {
		return PosterID;
	}

	public void setPosterID(String posterID) {
		PosterID = posterID;
	}

	public String getOwnerCode() {
		return OwnerCode;
	}

	public void setOwnerCode(String ownerCode) {
		OwnerCode = ownerCode;
	}

	public int getPosterType() {
		return PosterType;
	}

	public void setPosterType(int posterType) {
		PosterType = posterType;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getUploadTime() {
		return UploadTime;
	}

	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}

	public int getFileSize() {
		return FileSize;
	}

	public void setFileSize(int fileSize) {
		FileSize = fileSize;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getLocalPath() {
		return LocalPath;
	}

	public void setLocalPath(String localPath) {
		LocalPath = localPath;
	}

	public int getWidth() {
		return Width;
	}

	public void setWidth(int width) {
		Width = width;
	}

	public int getHeight() {
		return Height;
	}

	public void setHeight(int height) {
		Height = height;
	}

	public int getPlatform() {
		return Platform;
	}

	public void setPlatform(int platform) {
		Platform = platform;
	}
}