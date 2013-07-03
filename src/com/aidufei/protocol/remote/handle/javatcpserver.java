package com.aidufei.protocol.remote.handle;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
/*
public class javatcpserver extends Thread {
	String fileName11;
	static final String CRLF = "\r\n";
	private BufferedReader br;
	private Socket sockettemp;
	private Thread thread;
	private Thread threadrun;
	private long offset;
	private static final int PORT = 8077;
	private boolean sendflag;
	private boolean sendstop;
	private ArrayList acceptList;
	private ServerSocket serverSocket;
	static final String TAG = "JAVADOWNLOADSERVER";

	public javatcpserver() {
		this.fileName11 = null;

		this.sockettemp = null;

		this.offset = 0L;

		this.sendflag = false;
		this.sendstop = false;
		this.acceptList = null;
		this.serverSocket = null;
	}

	public SessionInfo AllocateSessionInfoInstance(Socket sockSession) {
		InetSocketAddress inetsocket = (InetSocketAddress) sockSession
				.getRemoteSocketAddress();
		SessionInfo session = new SessionInfo();
		session.sockSession = sockSession;
		session.strIpAddr = inetsocket.getHostName();
		Log.e("JAVADOWNLOADSERVER", "strIPAddr : " + session.strIpAddr);
		session.bAllocateFlag = true;
		session.bCloseFlag = false;
		session.bStopFlag = false;
		SessionInfo.access$102(session, sockSession);
		return session;
	}

	public void addSessionInfoInstance(SessionInfo session) {
		this.acceptList.add(session);
	}

	public void destroy() {
		for (int i = 0; i < this.acceptList.size(); i++) {
			SessionInfo sessionTmp = (SessionInfo) this.acceptList.get(i);
			removeSessionInfoInstance(sessionTmp.strIpAddr);
		}
	}

	public void removeSessionInfoInstance(String Ipaddr) {
		int i;
		for (i = 0; i < this.acceptList.size(); i++) {
			SessionInfo sessionTmp = (SessionInfo) this.acceptList.get(i);
			if (sessionTmp.strIpAddr != Ipaddr)
				continue;
			Log.e("JAVADOWNLOADSERVER", "find remove ip : " + Ipaddr);
			if ((sessionTmp.bCloseFlag) || (sessionTmp.sockSession == null))
				break;
			try {
				sessionTmp.sockSession.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sessionTmp.sockSession = null;
			sessionTmp.bCloseFlag = true;
			break;
		}

		if (i < this.acceptList.size()) {
			this.acceptList.remove(i);
		}
	}

	public int getSessionInfoInstance(String Ipaddr) {
		Log.e("JAVADOWNLOADSERVER", "accept size" + this.acceptList.size());
		if (this.acceptList.size() < 1) {
			return -1;
		}
		int i;
		for (i = 0; i < this.acceptList.size(); i++) {
			SessionInfo sessionTmp = (SessionInfo) this.acceptList.get(i);
			if (sessionTmp.strIpAddr.equals(Ipaddr) == true) {
				break;
			}
		}
		if (i < this.acceptList.size()) {
			return i;
		}

		return -1;
	}

	private boolean pretest(String str) {
		StringTokenizer s = new StringTokenizer(str);
		String temp = s.nextToken();

		if (temp.equals("GET")) {
			String file = s.nextToken();
			try {
				file = URLDecoder.decode(file, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
			String end = file.substring(file.lastIndexOf(".") + 1,
					file.length()).toLowerCase();
			if ((end.equals("jpg")) || (end.equals("jpeg"))) {
				return true;
			}
		}
		return false;
	}

	private String contentType(String fileName) {
		String fileNameTmp = fileName.toLowerCase();
		String ret = "";
		if (fileNameTmp.endsWith("txt")) {
			ret = "text/plain";
		}
		if (fileNameTmp.endsWith("gif")) {
			ret = "image/gif";
		}
		if (fileNameTmp.endsWith("jpg")) {
			ret = "image/jpg";
		}
		if (fileNameTmp.endsWith("jpeg")) {
			ret = "image/jpeg";
		}
		if (fileNameTmp.endsWith("jpe")) {
			ret = "image/jpeg";
		}
		if (fileNameTmp.endsWith("zip")) {
			ret = "application/zip";
		}
		if (fileNameTmp.endsWith("rar")) {
			ret = "application/rar";
		}
		if (fileNameTmp.endsWith("doc")) {
			ret = "application/msword";
		}
		if (fileNameTmp.endsWith("ppt")) {
			ret = "application/vnd.ms-powerpoint";
		}
		if (fileNameTmp.endsWith("xls")) {
			ret = "application/vnd.ms-excel";
		}
		if (fileNameTmp.endsWith("html")) {
			ret = "text/html";
		}
		if (fileNameTmp.endsWith("htm")) {
			ret = "text/html";
		}
		if (fileNameTmp.endsWith("tif")) {
			ret = "image/tiff";
		}
		if (fileNameTmp.endsWith("tiff")) {
			ret = "image/tiff";
		}
		if (fileNameTmp.endsWith("pdf")) {
			ret = "application/pdf";
		}
		return ret;
	}

	public String getLocalIpAddress() {
		try {
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				Enumeration enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();
					if (!inetAddress.isLoopbackAddress())
						return inetAddress.getHostAddress().toString();
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	public void serverInit() {
		this.acceptList = new ArrayList(5);
		try {
			this.serverSocket = new ServerSocket(8077);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void serverDomain() {
		int sessionNum = 0;
		try {
			serverInit();
			while (true) {
				Thread.sleep(400L);
				SessionInfo session = AllocateSessionInfoInstance(this.serverSocket
						.accept());
				SessionInfo sessionInList = null;

				sessionNum = getSessionInfoInstance(session.strIpAddr);
				if (1 <= sessionNum) {
					Log.e("JAVADOWNLOADSERVER", "sessionNum : " + sessionNum);
					sessionInList = (SessionInfo) this.acceptList
							.get(sessionNum);
					sessionInList.bStopFlag = true;
					synchronized (sessionInList.threadInfo) {
						Log.e("JAVADOWNLOADSERVER", "sessionInList"
								+ sessionInList);
						Log.e("JAVADOWNLOADSERVER", "threadInfo : "
								+ sessionInList.threadInfo);
						sessionInList.threadInfo.wait();
					}
					removeSessionInfoInstance(session.strIpAddr);
				}

				Log.e("JAVADOWNLOADSERVER", "get msg!");

				threadProcess thread = new threadProcess(session);
				session.threadInfo = thread;
				thread.start();
				addSessionInfoInstance(session);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		serverDomain();
	}

	public class threadProcess extends Thread {
		public InputStream input;
		public OutputStream output;
		public Thread threadInfo = null;
		public String fileName;
		private FileInputStream fis = null;
		private File fisFd = null;
		public javatcpserver.SessionInfo sessionInfo = null;
		private Socket Acceptsocket = null;
		private static final short MEDIA_DATA_TYPE_VIDEO = 0;
		private static final short MEDIA_DATA_TYPE_AUDIO = 1;
		public short mediaType;

		public threadProcess(javatcpserver.SessionInfo session) {
			this.threadInfo = session.threadInfo;
			this.sessionInfo = session;
			this.Acceptsocket = javatcpserver.SessionInfo.access$100(session);
		}

		private boolean responds(String[] headerLine, int totalNum) {
			String serverLine = "Server:httpServer\r\n";
			String accaptRange = "Accept-Ranges: bytes\r\n";
			String statusLine = new String();
			String contentTypeLine = new String();
			String entityBody = new String();
			String contentLengthLine = "error";
			StringTokenizer s = new StringTokenizer(headerLine[0]);
			Log.e("JAVADOWNLOADSERVER", "headerLine 0 : " + headerLine[0]);
			String temp = s.nextToken();
			String RangeLine = new String();
			boolean fileExists = true;
			boolean rangeflag = false;
			int i = 0;
			ByteBuffer msgbuf = ByteBuffer.allocate(1024);
			int responseLen = 0;

			if (temp.equals("GET")) {
				this.fileName = s.nextToken();
				try {
					this.fileName = URLDecoder.decode(this.fileName, "UTF-8");
					this.fileName = this.fileName.substring(1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Log.e("JAVADOWNLOADSERVER", "filename get str: "
						+ this.fileName);
				try {
					if (null == this.fis)
						this.fis = new FileInputStream(this.fileName);
					if (this.fisFd == null)
						this.fisFd = new File(this.fileName);
				} catch (FileNotFoundException e) {
					fileExists = false;
				}

				if (!javatcpserver.this.pretest(headerLine[0])) {
					String rgline = null;
					rangeflag = false;
					for (i = 1; i < totalNum; i++) {
						if ((null == headerLine[i]) || ("" == headerLine[i])
								|| (5 >= headerLine[i].length()))
							break;
						rgline = headerLine[i].substring(0, 5);
						Log.e("JAVADOWNLOADSERVER", "head line : "
								+ rgline.toString());

						if (!rgline.equals("Range"))
							continue;
						rangeflag = true;
						break;
					}

				}

				if (fileExists) {
					if (!rangeflag) {
						statusLine = "HTTP/1.0 200 OK\r\n";
					} else {
						statusLine = "HTTP/1.0 206 Partial Content\r\n";
						javatcpserver.access$302(javatcpserver.this, 0L);
						int firstindex = headerLine[i].indexOf("=");
						int lastindex = headerLine[i].indexOf("-");
						String range = headerLine[i].substring(firstindex + 1,
								lastindex);
						try {
							javatcpserver.access$302(javatcpserver.this, Long
									.parseLong(range.trim()));
							this.fis.skip(javatcpserver.this.offset);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					contentTypeLine = "Content-type: text/plain\r\n";
					try {
						contentLengthLine = "Content-Length:"
								+ new Integer(this.fis.available()).toString()
								+ "\r\n";
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					statusLine = "HTTP/1.0 404 Not Found\r\n";
					contentTypeLine = "text/html";
					entityBody = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>404 Not FoundfileName.html</BODY></HTML>";
				}

			}

			Log.e("JAVADOWNLOADSERVER", "statusLine :" + statusLine);
			msgbuf.put(statusLine.getBytes());
			responseLen += statusLine.length();
			msgbuf.put(serverLine.getBytes());
			responseLen += serverLine.length();
			msgbuf.put(contentTypeLine.getBytes());
			responseLen += contentTypeLine.length();
			msgbuf.put(contentLengthLine.getBytes());
			responseLen += contentLengthLine.length();

			msgbuf.put(accaptRange.getBytes());
			responseLen += accaptRange.length();
			if (fileExists) {
				if (rangeflag) {
					RangeLine = "Content-Range: bytes "
							+ javatcpserver.this.offset + "-"
							+ (this.fisFd.length() - 1L) + "/"
							+ this.fisFd.length() + "\r\n";
					msgbuf.put(RangeLine.getBytes());
					responseLen += RangeLine.length();
				}
				msgbuf.put("\r\n".getBytes());
				responseLen += "\r\n".length();
				msgbuf.rewind();
				byte[] msg = new byte[responseLen];
				msgbuf.get(msg, 0, responseLen);
				Log.e("JAVADOWNLOADSERVER", "msg :" + msgbuf.getChar());
				try {
					this.output.write(msg);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Log.e("JAVADOWNLOADSERVER", "respone msg send!");
				return true;
			}

			try {
				this.output.write(entityBody.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
			threadProcessDestroy();
			return false;
		}

		private void processRequest() {
			Log.e("JAVADOWNLOADSERVER", "2222222\n");
			boolean exitloop = false;
			String[] headerLine = new String[10];
			boolean bFileExist = false;
			try {
				int i;
				for (i = 0; i < 10; i++) {
					headerLine[i] = javatcpserver
							.access$400(javatcpserver.this).readLine();

					if ((headerLine[i].equals("\r\n"))
							|| (headerLine[i].equals(""))
							|| (headerLine[i].length() == 0)) {
						exitloop = true;
						Log.e("JAVADOWNLOADSERVER", "headerLine"
								+ headerLine[i].toString());
						break;
					}
					Log.e("JAVADOWNLOADSERVER", "headerLine : "
							+ headerLine[i].toString());
				}
				if ((null != headerLine[0]) || ("null" != headerLine[0])) {
					bFileExist = responds(headerLine, i);
				}
				Log.e("JAVADOWNLOADSERVER", "response clear!");
				if (false == bFileExist) {
					threadProcessDestroy();
				} else {
					sendBytes(this.fis, this.output);
				}
			} catch (Exception e) {
				threadProcessDestroy();
				e.printStackTrace();
			}
			threadProcessDestroy();
		}

		public void threadProcessDestroy() {
			try {
				if (null != this.input)
					this.input.close();
				if (null != this.output)
					this.output.close();
				if (null != this.fis)
					this.fis.close();
				if (null != this.Acceptsocket)
					this.Acceptsocket.close();
				this.input = null;
				this.output = null;
				this.fis = null;
				this.Acceptsocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendBytes(FileInputStream fis, OutputStream os) {
			if ((null == fis) || (null == os)) {
				return;
			}
			List list = new ArrayList();
			byte[] buffer = new byte[1448];

			int bytes = 0;

			Log.e("JAVADOWNLOADSERVER", "sendbytes!");
			try {
				while ((bytes = fis.read(buffer)) > 0) {
					if (this.sessionInfo.bStopFlag == true) {
						threadProcessDestroy();
						return;
					}

					os.write(buffer, 0, bytes);
					os.flush();
				}
				threadProcessDestroy();
			} catch (Exception e) {
				e.printStackTrace();
				threadProcessDestroy();
			}
		}

		public void run() {
			ServerSocket serverSocket = null;
			try {
				this.input = this.Acceptsocket.getInputStream();
				this.output = this.Acceptsocket.getOutputStream();
				javatcpserver.access$402(javatcpserver.this,
						new BufferedReader(new InputStreamReader(
								this.Acceptsocket.getInputStream())));
				processRequest();
			} catch (Exception e4) {
				e4.printStackTrace();
			}
		}
	}

	private class SessionInfo {
		public Socket sockSession;
		public String strIpAddr;
		public boolean bCloseFlag;
		public boolean bAllocateFlag = false;
		public boolean bStopFlag = false;
		public Thread threadInfo = null;
		private Socket socket = null;

		private SessionInfo() {
		}
	}
}
*/