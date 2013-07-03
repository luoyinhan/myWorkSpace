package com.aidufei.protocol.remote.handle;

import android.util.Log;
import java.io.DataInputStream;
import java.io.IOException;

public class mediaInfo {
	private String url;
	private String programCode;
	private String itemName;
	private String BKImage1;
	private String BKImage2;
	private String Description;
	private String Director;
	private String Actor;
	private String Region;
	private String Language;
	private String CategoryCode;
	private String Duration;
	private int RatingLevel;
	private int ItemType;
	private int Position;
	private int Status;
	private int msgType;
	static final String Tag = "MEDIA_INFO";
	private static final int MAX_PROGRAM_CODE = 128;
	private static final int MAX_ITEMNAME_CODE = 128;
	private static final int MAX_BKIMAGE_CODE = 1024;
	private static final int MAX_DESCRIPTION_CODE = 1024;
	private static final int MAX_ACTOR_CODE = 1024;
	private static final int MAX_DIRECTOR = 256;
	private static final int MAX_REGION = 128;
	private static final int MAX_LANGUAGE = 32;
	private static final int MAX_CATEGORY_CODE = 128;
	private static final int MAX_DURATION = 32;

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProgramCode() {
		return this.programCode;
	}

	public void setProgramCode(String programCode) {
		if (programCode.length() > 128)
			Log.e("MEDIA_INFO", "programCode exceed max length");
		this.programCode = programCode.substring(0, 128);
	}

	public String getItemName() {
		return this.itemName;
	}

	public void setItem_name(String itemName) {
		if (itemName.length() > 128)
			Log.e("MEDIA_INFO", "itemName exceed max length");
		this.itemName = itemName.substring(0, 128);
	}

	public String getBKImage1() {
		return this.BKImage1;
	}

	public void setBKImage1(String BKImage1) {
		if (BKImage1.length() > 1024)
			Log.e("MEDIA_INFO", "BKImage1 exceed max length");
		this.BKImage1 = BKImage1.substring(0, 1024);
	}

	public String getBKImage2() {
		return this.BKImage2;
	}

	public void setBKImage2(String BKImage2) {
		if (BKImage2.length() > 1024)
			Log.e("MEDIA_INFO", "BKImage2 exceed max length");
		this.BKImage2 = BKImage2.substring(0, 1024);
	}

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(String Description) {
		if (Description.length() > 1024)
			Log.e("MEDIA_INFO", "Description exceed max length");
		this.Description = Description.substring(0, 1024);
	}

	public String getActor() {
		return this.Actor;
	}

	public void setActor(String Actor) {
		if (Actor.length() > 1024)
			Log.e("MEDIA_INFO", "Actor exceed max length");
		this.Actor = Actor.substring(0, 1024);
	}

	public String getDirector() {
		return this.Director;
	}

	public void setDirector(String Director) {
		if (Director.length() > 256)
			Log.e("MEDIA_INFO", "Director exceed max length");
		this.Director = Director.substring(0, 256);
	}

	public String getRegion() {
		return this.Region;
	}

	public void setRegion(String Region) {
		if (Region.length() > 128)
			Log.e("MEDIA_INFO", "Region exceed max length");
		this.Region = Region.substring(0, 128);
	}

	public String getLanguage() {
		return this.Language;
	}

	public void setLanguage(String Language) {
		if (Language.length() > 32)
			Log.e("MEDIA_INFO", "Language exceed max length");
		this.Language = Language.substring(0, 32);
	}

	public String getCategoryCode() {
		return this.CategoryCode;
	}

	public void setCategoryCode(String CategoryCode) {
		if (CategoryCode.length() > 128)
			Log.e("MEDIA_INFO", "CategoryCode exceed max length");
		this.CategoryCode = CategoryCode.substring(0, 128);
	}

	public String getDuration() {
		return this.Duration;
	}

	public void setDuration(String Duration) {
		if (Duration.length() > 32)
			Log.e("MEDIA_INFO", "Duration exceed max length");
		this.Duration = Duration.substring(0, 32);
	}

	public int getRatingLevel() {
		return this.RatingLevel;
	}

	public void setRatingLevel(int RatingLevel) {
		this.RatingLevel = RatingLevel;
	}

	public int getItemType() {
		return this.ItemType;
	}

	public void setItemType(int ItemType) {
		this.ItemType = ItemType;
	}

	public int getPosition() {
		return this.Position;
	}

	public void setPosition(int Position) {
		this.Position = Position;
	}

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int Status) {
		this.Status = Status;
	}

	public void setMsgType(int type) {
		this.msgType = type;
	}

	protected void readMediaInfo(DataInputStream in) {
		try {
			this.Status = in.readInt();
			int programCodeLen = in.readInt();
			if (programCodeLen > 0) {
				byte[] buf = new byte[programCodeLen];
				in.read(buf);
				this.programCode = new String(buf);
			}

			this.ItemType = in.readInt();
			int itemNameLen = in.readInt();
			if (itemNameLen > 0) {
				byte[] buf = new byte[itemNameLen];
				in.read(buf);
				this.itemName = new String(buf);
			}

			int BKImage1Len = in.readInt();
			if (BKImage1Len > 0) {
				byte[] buf = new byte[BKImage1Len];
				in.read(buf);
				this.BKImage1 = new String(buf);
			}

			int BKImage2Len = in.readInt();
			if (BKImage2Len > 0) {
				byte[] buf = new byte[BKImage2Len];
				in.read(buf);
				this.BKImage2 = new String(buf);
			}

			this.RatingLevel = in.readInt();

			int DescriptionLen = in.readInt();
			if (DescriptionLen > 0) {
				byte[] buf = new byte[DescriptionLen];
				in.read(buf);
				this.Description = new String(buf);
			}

			int ActorLen = in.readInt();
			if (ActorLen > 0) {
				byte[] buf = new byte[ActorLen];
				in.read(buf);
				this.Actor = new String(buf);
			}

			int DirectorLen = in.readInt();
			if (DirectorLen > 0) {
				byte[] buf = new byte[DirectorLen];
				in.read(buf);
				this.Director = new String(buf);
			}

			int RegionLen = in.readInt();
			if (RegionLen > 0) {
				byte[] buf = new byte[RegionLen];
				in.read(buf);
				this.Region = new String(buf);
			}

			int LanguageLen = in.readInt();
			if (LanguageLen > 0) {
				byte[] buf = new byte[LanguageLen];
				in.read(buf);
				this.Language = new String(buf);
			}

			int CategoryCodeLen = in.readInt();
			if (CategoryCodeLen > 0) {
				byte[] buf = new byte[CategoryCodeLen];
				in.read(buf);
				this.CategoryCode = new String(buf);
			}

			int DurationLen = in.readInt();
			if (DurationLen > 0) {
				byte[] buf = new byte[DurationLen];
				in.read(buf);
				this.Duration = new String(buf);
			}

			this.Position = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void mediaInfoPrint() {
		Log.e("MEDIA_INFO", "recv media info status:" + this.Status);
		Log.e("MEDIA_INFO", "programCode:" + this.programCode);
		Log.e("MEDIA_INFO", "ItemType:" + this.ItemType);
		Log.e("MEDIA_INFO", "itemName:" + this.itemName);
		Log.e("MEDIA_INFO", "BKImage1:" + this.BKImage1);
		Log.e("MEDIA_INFO", "BKImage2:" + this.BKImage2);
		Log.e("MEDIA_INFO", "Description:" + this.Description);
		Log.e("MEDIA_INFO", "Actor:" + this.Actor);
		Log.e("MEDIA_INFO", "Director:" + this.Director);
		Log.e("MEDIA_INFO", "Region:" + this.Region);
		Log.e("MEDIA_INFO", "Language" + this.Language);
		Log.e("MEDIA_INFO", "Duration" + this.Duration);
		Log.e("MEDIA_INFO", "Position" + this.Position);
		Log.e("MEDIA_INFO", "RatingLevel:" + this.RatingLevel);
		Log.e("MEDIA_INFO", "CategoryCode:" + this.CategoryCode);
	}
}
