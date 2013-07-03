package com.aidufei.protocol.remote.handle;

import android.util.Log;
import com.aidufei.protocol.remote.callback.GetAppListCallBack;
import com.aidufei.protocol.remote.utils.AppInfo;
import com.aidufei.protocol.remote.utils.SaxXml;
import com.aidufei.protocol.remote.utils.SocketUtils;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RemoteAppList {
	DataOutputStream out = null;
	private static final String TAG = "RemoteAppList";
	private static final String hisiFlag = "Hisi";
	ArrayList<AppInfo> appList = new ArrayList();
	GetAppListCallBack callback;

	public RemoteAppList(DeviceInfo device) {
		this.out = SocketUtils.mediaOut;
	}

	protected void destory() {
	}

	protected void parseResponseAppList(DataInputStream in) {
		appListMsg appMsg = new appListMsg();
		try {
			appMsg.readMsgContent(in);
			parseAppList(appMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void parseAppList(appListMsg appMsg) throws Exception {
		this.appList = SaxXml.parse(new String(appMsg.getXmlbody()));
		parseAppIcon(appMsg.getIconbody());

		if (this.callback == null)
			return;
		this.callback.returnAppList(this.appList);
	}

	protected void parseAppIcon(byte[] iconContent) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(iconContent);

		for (int i = 0; i < this.appList.size(); ++i) {
			int len = bais.read();
			byte[] buf = new byte[len];
			bais.read(buf, 0, len);
			Log.e("RemoteAppList", "len: " + len + "buf" + new String(buf));
			int iconlen = Integer.parseInt(new String(buf));
			byte[] iconbuf = new byte[iconlen];
			bais.read(iconbuf, 0, iconlen);
			AppInfo app = (AppInfo) this.appList.get(i);
			app.setPackageIcon(iconbuf);
		}
		bais.close();
	}

	public void sendLaunchAppReq(String package_name) {
		Log.e("APPList", "send launch app req: " + package_name);
		try {
			this.out.writeInt(771);
			this.out.write("Hisi".getBytes());
			this.out.writeInt(package_name.length());
			this.out.write(package_name.getBytes(), 0, package_name.length());
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendGetAppListReq(GetAppListCallBack callback) {
		Log.e("APPList", "send get applist req: ");

		this.callback = callback;
		try {
			this.out.writeInt(769);
			this.out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class appListMsg {
		boolean bSuccess;
		int bodylen;
		int xmlLen;
		byte[] xmlbody;
		byte[] iconbody;

		appListMsg() {
			this.bSuccess = false;
			this.bodylen = 0;
			this.xmlLen = 0;
		}

		public void readMsgContent(DataInputStream in) throws Exception {
			this.bodylen = in.readInt();
			this.xmlLen = in.readInt();
			int iconLen = this.bodylen - this.xmlLen;
			this.iconbody = new byte[this.bodylen - this.xmlLen];
			this.xmlbody = new byte[this.xmlLen];
			int len = 0;
			int readlen = 0;
			int remain = this.xmlLen;

			while (remain > 0) {
				readlen = in.read(this.xmlbody, len, remain);
				len += readlen;
				remain -= readlen;
			}
			Log.e("RemoteAppList", "received msg xmlLen: " + this.xmlLen
					+ "xmlbody: " + new String(this.xmlbody));

			remain = iconLen;
			len = 0;
			readlen = 0;
			while (remain > 0) {
				readlen = in.read(this.iconbody, len, remain);
				len += readlen;
				remain -= readlen;
				Log
						.e("RemoteAppList", "readlen" + readlen + "remain:"
								+ remain);
			}
			Log.e("RemoteAppList", "body: " + new String(this.iconbody));
		}

		public byte[] getXmlbody() {
			return this.xmlbody;
		}

		public byte[] getIconbody() {
			return this.iconbody;
		}
	}

	class basicMsg {
		int msgType;

		basicMsg() {
		}
	}
}
