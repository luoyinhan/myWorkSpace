package com.aidufei.protocol.adapter.gdhfc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

import com.aidufei.protocol.adapter.oto.TerminalSerial;
import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.gdhfc.GDHfcRequest;
import com.aidufei.protocol.oto.Global;
import com.coship.ott.utils.LogUtils;

public class GDHfcRequestHandler implements Runnable {
	private final static int OTO_CLIENT_PORT = 13001;
	private final static int OTO_SERV_PORT = 6666;

	private GDHfcDeviceAdapter mAdapter = null;
	private boolean mFinished = false;

	private class PrivRequest extends AsyncTask<Integer, Integer, Integer> {
		public Device remote;
		public Object request;

		@Override
		protected Integer doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			syncSendRequest(remote, request);
			return 0;
		}
	}

	private DatagramSocket mSocket = null;

	public GDHfcRequestHandler(GDHfcDeviceAdapter adapter) {
		mAdapter = adapter;
		mFinished = false;
	}

	private boolean init() {
		// TODO Auto-generated method stub
		try {
			mSocket = new DatagramSocket(OTO_CLIENT_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			mSocket = null;
			return false;
		}
		if (mSocket == null)
			return false;
		return true;
	}

	public void finish() {
		mFinished = true;
	}

	private boolean syncSendRequest(Device remote, Object req) {
		if (mSocket == null)
			return false;
		if (remote.address() == null) {
			return false;
		}

		InetAddress remoteAddress = null;
		try {
			remoteAddress = InetAddress.getByName(remote.address());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			return false;
		}

		if (req instanceof GDHfcRequest) {
			GDHfcRequest request = (GDHfcRequest) req;
			byte[] msg = request.toByte();
			if (msg == null || msg.length <= 0)
				return false;
			DatagramPacket pack = new DatagramPacket(msg, msg.length,
					remoteAddress, OTO_SERV_PORT);
			try {
				mSocket.send(pack);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
		}
		return false;
	}

	public boolean sendRequest(Device remote, Object req) {
		// TODO Auto-generated method stub

		PrivRequest request = new PrivRequest();
		request.remote = remote;
		request.request = req;
		request.execute(0);
		return true;
	}

	private void handleRequest() {
		// TODO Auto-generated method stub
		if (mSocket == null)
			return;

		byte[] buf = new byte[2048];
		DatagramPacket dp = new DatagramPacket(buf, 2048);

		try {
			// mSocket.setSoTimeout(1000);
			mSocket.receive(dp);
			LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "receive data from "
					+ dp.getAddress().getHostAddress());
			// LogUtils.trace(Log.DEBUG,TAG, "received data: " + new String(buf,
			// 0, dp.getLength()));
		} catch (SocketException e) {
			return;
		} catch (IOException e) {
			return;
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, LogUtils.getTAG(), "timeout");
			return;
		}
		LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "local address ="
				+ TerminalSerial.localAddress());
		if (dp.getAddress().getHostAddress()
				.equals(TerminalSerial.localAddress())) {
			return;
		}
		GDHfcRequest req = GDHfcRequest.parse(dp.getData(), dp.getLength());

		Device remote = new GDHfcDevice(null, dp.getAddress().getHostAddress(),
				Global.TERMINAL_GDHFC_BOX);

		if (mAdapter != null && req != null) {
			mAdapter.handle(remote, req);
		}

	}

	@Override
	public void run() {
		if (init() == false)
			return;

		while (!mFinished) {
			handleRequest();
		}
	}
}
