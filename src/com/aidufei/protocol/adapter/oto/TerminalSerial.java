package com.aidufei.protocol.adapter.oto;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.Log;

import com.coship.ott.utils.LogUtils;

public class TerminalSerial {
	private static TerminalSerial terminal = null;
	private Context mContext = null;
	private String mUUID = null;

	private TerminalSerial(Context context) {
		mContext = context;
		loadUUID();
	}

	private void loadUUID() {
		mUUID = loadFromFile();
		if (mUUID == null) {
			mUUID = loadMacAddress();
			if (mUUID == null)
				mUUID = UUID.randomUUID().toString();
			if (mUUID != null)
				saveToFile();
		}
		if (mUUID == null)
			mUUID = "error";
	}

	private void saveToFile() {

		if (mUUID == null || mContext == null) {
			return;
		}
		try {
			FileOutputStream outStream = mContext.openFileOutput("uuid",
					Context.MODE_PRIVATE);
			outStream.write(mUUID.getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}

	private String loadFromFile() {

		FileInputStream inStream = null;
		ByteArrayOutputStream stream = null;
		if (mContext == null)
			return null;
		try {

			inStream = mContext.openFileInput("uuid");
			stream = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			stream.close();
			inStream.close();

			return stream.toString();

		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			if (inStream != null)
				try {
					inStream.close();
				} catch (IOException e1) {
				}
			return null;
		}

	}

	/*
	 * ****************************************************************
	 * 子函数：获得本地MAC地址
	 * ****************************************************************
	 */
	private String loadMacAddressLowVersion() {
		String result = "";
		String Mac = "";
		result = callCmd("busybox ifconfig", "HWaddr");

		// 如果返回的result == null，则说明网络不可取
		if (result == null) {
			return null;
		}

		// 对该行数据进行解析
		// 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
		if (result.length() > 0 && result.contains("HWaddr") == true) {
			Mac = result.substring(result.indexOf("HWaddr") + 6,
					result.length() - 1);
			LogUtils.trace(Log.DEBUG, "test", "Mac:" + Mac + " Mac.length: "
					+ Mac.length());

			if (Mac.length() > 1) {
				Mac = Mac.replaceAll(" ", "");
				result = "";
				String[] tmp = Mac.split(":");
				for (int i = 0; i < tmp.length; ++i) {
					result += tmp[i];
				}
			}
			LogUtils.trace(Log.INFO, "test", result + " result.length: "
					+ result.length());
		} else {
			return null;
		}
		return result;
	}

	private String callCmd(String cmd, String filter) {
		String result = null;
		String line = null;
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);

			// 执行命令cmd，只取结果中含有filter的这一行
			while ((line = br.readLine()) != null
					&& line.contains(filter) == false) {
				// result += line;
				LogUtils.trace(Log.INFO, "test", "line: " + line);
			}

			result = line;
			LogUtils.trace(Log.INFO, "test", "result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@TargetApi(9)
	private String loadMacAddress() {
		/*
		 * String ret = null; if(android.os.Build.VERSION.SDK_INT < 9){ ret =
		 * loadMacAddressLowVersion(); }else{ try { Enumeration en =
		 * NetworkInterface.getNetworkInterfaces(); while (en.hasMoreElements())
		 * { NetworkInterface intf = (NetworkInterface) en.nextElement();
		 * if(intf.isLoopback() || intf.isPointToPoint()) continue; ret = new
		 * String(intf.getHardwareAddress()); } } catch (SocketException ex) {
		 * ret = null; } } return ret;
		 */
		return null;
	}

	public static TerminalSerial getTerminalSerial(Context context) {

		if (terminal == null) {
			terminal = new TerminalSerial(context);
		}

		return terminal;
	}

	public String uuid() {
		return mUUID;
	}

	public static String localAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			if (en == null)
				return null;
			while (en.hasMoreElements()) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				if (intf.getName().equals("ppp0"))
					continue;
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				if (enumIpAddr == null)
					continue;

				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();
					// LogUtils.trace(Log.DEBUG,"Termianl IP", "local address" +
					// inetAddress.getHostAddress());
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.getHostAddress().contains(
									(CharSequence) ":"))
						return inetAddress.getHostAddress();

				}
			}

		} catch (SocketException ex) {
			LogUtils.trace(Log.ERROR, LogUtils.getTAG(), ex.toString());
		}
		return null;
	}
}