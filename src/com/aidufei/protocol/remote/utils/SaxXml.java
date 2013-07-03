package com.aidufei.protocol.remote.utils;

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

public class SaxXml {
	int PACKAGE_NAME;
	int APP_NAME;
	int PACKAGE_ICON;
	int point;
	String packageName;
	String AppName;
	String packageIcon;
	public static String content;
	static ArrayList<AppInfo> appList = new ArrayList();

	public SaxXml() {
		this.PACKAGE_NAME = 1;
		this.APP_NAME = 2;
		this.PACKAGE_ICON = 3;
		this.point = 0;

		this.packageName = "";
		this.AppName = "";
		this.packageIcon = "";
	}

	public static ArrayList<AppInfo> parse(String xmlContent) {
		ArrayList list = new ArrayList();
		try {
			Log.e("saxml", "xmlcontent:" + xmlContent);
			SaxXml s = new SaxXml();
			appList.clear();
			InputStream stream = new ByteArrayInputStream(xmlContent.getBytes());

			list = s.paresChannleXML(stream, "appinfo");
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<AppInfo> paresChannleXML(InputStream is, String node) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(is, new DefaultHandler() {
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					super.characters(ch, start, length);

					if (SaxXml.this.point == SaxXml.this.APP_NAME) {
						SaxXml.this.AppName += new String(ch, start, length);
					} else if (SaxXml.this.point == SaxXml.this.PACKAGE_NAME) {
						SaxXml.this.packageName += new String(ch, start, length);
					} else {
						if (SaxXml.this.point != SaxXml.this.PACKAGE_ICON)
							return;
						SaxXml.this.packageIcon += new String(ch, start, length);
					}
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					if ("appinfo".equals(qName)) {
						AppInfo curInfo = new AppInfo();
						curInfo.setPackageName(SaxXml.this.packageName);
						curInfo.setAppName(SaxXml.this.AppName);
						curInfo.setPackageIcon(SaxXml.this.packageIcon
								.getBytes());

						SaxXml.appList.add(curInfo);

						SaxXml.this.point = 0;
						SaxXml.this.packageName = "";
						SaxXml.this.AppName = "";
					}
					super.endElement(uri, localName, qName);
				}

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					if (qName.equals("app_name")) {
						SaxXml.this.point = SaxXml.this.APP_NAME;
					} else if (qName.endsWith("pack_name")) {
						SaxXml.this.point = SaxXml.this.PACKAGE_NAME;
					} else {
						if (!qName.endsWith("app_icon"))
							return;
						SaxXml.this.point = SaxXml.this.PACKAGE_ICON;
					}
				}

				public void endDocument() throws SAXException {
					super.endDocument();

					for (int index = 0; index < SaxXml.appList.size(); ++index) {
						((AppInfo) SaxXml.appList.get(index))
								.setPackageIndex(index);
						Log.e("NameList", "name="
								+ ((AppInfo) SaxXml.appList.get(index))
										.getAppName());
						Log.e("NameList", "packname="
								+ ((AppInfo) SaxXml.appList.get(index))
										.getPackageName());
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return appList;
	}
}
