package com.aidufei.protocol.remote.message;

import android.content.Context;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EpgSaxXml {
	Context base;
	int CHANNEL_INDEX = 1;
	int CHANNEL_NAME = 2;
	int CHANNEL_URL = 3;
	int point = 0;

	String channelIndex = "";
	String channelName = "";
	String channelURL = "";
	public static String content;
	static ArrayList<ChannelInfor> channleList = new ArrayList();

	public static ArrayList<ChannelInfor> parse(String xmlContent) {
		ArrayList list = new ArrayList();
		try {
			EpgSaxXml s = new EpgSaxXml();
			channleList.clear();
			InputStream stream = new ByteArrayInputStream(xmlContent
					.getBytes("UTF-8"));
			list = s.paresChannleXML(stream, "ProInfo");
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<ChannelInfor> paresChannleXML(InputStream is, String node) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(is, new DefaultHandler() {
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					super.characters(ch, start, length);
					if (EpgSaxXml.this.point == EpgSaxXml.this.CHANNEL_INDEX) {
						EpgSaxXml.this.channelIndex += new String(ch, start,
								length);
					} else if (EpgSaxXml.this.point == EpgSaxXml.this.CHANNEL_NAME) {
						EpgSaxXml.this.channelName += new String(ch, start,
								length);
					} else {
						if (EpgSaxXml.this.point != EpgSaxXml.this.CHANNEL_URL)
							return;
						EpgSaxXml.this.channelURL += new String(ch, start,
								length);
					}
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					if ("ProInfo".equals(qName)) {
						ChannelInfor curInfo = new ChannelInfor();
						curInfo.setChannelName(EpgSaxXml.this.channelName);
						EpgSaxXml.this.channelIndex = EpgSaxXml.this.channelIndex
								.trim();
						curInfo.setChannelIndex(Integer
								.parseInt(EpgSaxXml.this.channelIndex));
						curInfo.setPlayUrl(EpgSaxXml.this.channelURL);
						EpgSaxXml.channleList.add(curInfo);

						EpgSaxXml.this.point = 0;
						EpgSaxXml.this.channelIndex = "";
						EpgSaxXml.this.channelName = "";
						EpgSaxXml.this.channelURL = "";
					}
					super.endElement(uri, localName, qName);
				}

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					if (qName.equals("Index")) {
						EpgSaxXml.this.point = EpgSaxXml.this.CHANNEL_INDEX;
					} else if (qName.equals("Name")) {
						EpgSaxXml.this.point = EpgSaxXml.this.CHANNEL_NAME;
					} else {
						if (!qName.endsWith("Uri"))
							return;
						EpgSaxXml.this.point = EpgSaxXml.this.CHANNEL_URL;
					}
				}

				public void endDocument() throws SAXException {
					super.endDocument();

					for (int index = 0; index < EpgSaxXml.channleList.size(); ++index) {
						Log.e("EPG", "index="
								+ ((ChannelInfor) EpgSaxXml.channleList
										.get(index)).getChannelID()
								+ ","
								+ "name="
								+ ((ChannelInfor) EpgSaxXml.channleList
										.get(index)).getChannelName());
					}
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return channleList;
	}
}
