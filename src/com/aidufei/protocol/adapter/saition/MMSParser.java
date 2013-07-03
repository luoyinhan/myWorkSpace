package com.aidufei.protocol.adapter.saition;


import java.io.IOException;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;



import android.util.Xml;

public class MMSParser {
	public static final int VOD = 1;
	public static final int VOB = 2;
	public static final int VOB_DELAY = 3;
	public static final int NONE = 0;
	
	private XmlPullParser mParser = null;
	private String mContentID = null;
	private String mTS = null;
	private String mNetwork = null;
	private String mService = null;
	private long mOffset = 0;
	private int mType = NONE;
	
	
	public MMSParser(String mms){
		buildParser(mms);
		parse();
	}
	
	private void buildParser(String mms){
		if(mms == null)
			return;
		mParser =  Xml.newPullParser();
		try {
			mParser.setInput(new StringReader(mms));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			mParser = null;
		}
	}
	
	private void parseVOBUrl(String url){
		
		 if(!url.startsWith("dvb2://")){
			return;
		 }
		 String sub = url.substring("dvb2://".length());
		 String[] val = sub.split("\\.");
		 if(val != null && val.length >= 3){
			 mType = VOB;
			 mNetwork = val[0];
			 mTS = val[1];
			 mService = val[2];
		 }
//		 if(val != null && val.length == 4){
//			 mOffset = Long.parseLong(val[3]);
//			 mType = VOB_DELAY;
//		 }
		 return;
		 
	}
	
	
	private void parseText(String text){
		
		
		XmlPullParser dataParser = Xml.newPullParser();
		try {
			dataParser.setInput(new StringReader(text));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			return;
		}
		
		int type;
		try {
			type = dataParser.getEventType();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			return;
		}
		while(type != XmlPullParser.END_DOCUMENT){
			switch(type){
				case XmlPullParser.START_TAG:
					String tag = dataParser.getName();
					if(tag != null && tag.equals("meta")){
						String name = dataParser.getAttributeValue(null, "name");
						if(name == null)
							break;
						if(name.equals("contentID")){
							mType = VOD;
							mContentID = dataParser.getAttributeValue(null,"content");
							return;
						}else if(name.equals("DVBURL")){
							String url = dataParser.getAttributeValue(null, "content");
							parseVOBUrl(url);
							return;
						}else{
							break;
						}
					}
						
				default:
					break;
			}
			try {
				type = dataParser.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return;
			}
		}
		
	}
	
	private void parseResult(){
		int type;
		try {
			type = mParser.next();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return;
		}
		switch(type){
		
		case XmlPullParser.TEXT:
			String text = mParser.getText();
			parseText(text);
			return;
		default:
			return;
			
			
		}
	}
	
	private void parse(){
		if(mParser == null )
			return;
		int type;
		try {
			type = mParser.getEventType();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			return;
		}
		while(type != XmlPullParser.END_DOCUMENT){
			switch(type){
			case XmlPullParser.START_TAG:
				String tag = mParser.getName();
				if(tag != null && tag.equals("resultContent")){
					parseResult();
					return;
				}
				default:
					break;
			}
			try {
				type = mParser.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return;
			}
		}
	}
	
	public int type(){
		return mType;
	}
	
	public String content(){
		return mContentID;
	}
	
	public String network(){
		return mNetwork;
	}
	
	public String ts(){
		return mTS;
	}
	
	public String service(){
		return mService;
	}
	
	public long offset(){
		return mOffset;
	}
}
