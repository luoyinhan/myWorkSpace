package com.aidufei.protocol.remote.utils;

import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketUtils {
	public Socket clientSocket = null;
	public Socket mediaSocket = null;
	public DataInputStream inData = null;
	public DataOutputStream out = null;
	public static DataInputStream mediaIn = null;
	public static DataOutputStream mediaOut = null;
	byte[] msg = new byte[8];
	public static final String TAG = "Socket";
	public static String socketIP = null;
	public static SocketUtils clientThread = null;
	public static int msgSequence = 0;
	public static int port = 8821;
	public static int LEAVE_NETWORK = 777;

	public static String hostip = "";

	public static void closeNetwork() {
		Log.e("Socket", "socketUtil close network");
		if ((clientThread == null) || (clientThread.clientSocket == null))
			return;
		try {
			if(mediaOut != null){
				mediaOut.writeInt(LEAVE_NETWORK);
				mediaOut.flush();
			}
			if (mediaIn != null) {
				mediaIn.close();
				mediaIn = null;
			}
			if (mediaOut != null) {
				mediaOut.close();
				mediaOut = null;
			}
			if (clientThread.clientSocket != null) {
				clientThread.clientSocket.close();
				clientThread.clientSocket = null;
			}
			if (clientThread.mediaSocket != null) {
				clientThread.mediaSocket.close();
				clientThread.mediaSocket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startMediaAppNetwork() throws Exception {
		int mPort = 8867;
		clientThread.mediaSocket = new Socket(socketIP, mPort);
		mediaOut = new DataOutputStream(clientThread.mediaSocket
				.getOutputStream());
		mediaIn = new DataInputStream(clientThread.mediaSocket.getInputStream());
		Log.e("Socket", "open " + socketIP + ":" + mPort + " for writing!");
		hostip = clientThread.mediaSocket.getLocalAddress().getHostAddress();
		Log.e("Socket", "local ip: " + hostip);
	}

	public static void startNetwork() throws Exception {
		clientThread = new SocketUtils();
		InetSocketAddress socketAddress = new InetSocketAddress(socketIP, port);
		clientThread.clientSocket = new Socket();
		clientThread.clientSocket.connect(socketAddress, 5000);
		clientThread.inData = new DataInputStream(clientThread.clientSocket
				.getInputStream());

		clientThread.out = new DataOutputStream(clientThread.clientSocket
				.getOutputStream());

		Log.e("Socket", "open " + socketIP + ":" + port + " for writing!");
//		startMediaAppNetwork();
	}
}
