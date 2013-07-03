package com.aidufei.protocol.remote.message;

import com.aidufei.protocol.remote.utils.CIOUtil;
import java.io.DataOutputStream;
import java.io.IOException;

public class AcessDeviceStatusRequest extends Request {
	public final short MUAP_MSGTYPE_AcessDeviceStatus = 1;
	public static final short ACESSDEVICE_LEAVE_NETWORK = 1024;
	public static final short ACESSDEVICE_JOIN_NETWORK = 1025;
	public static final short ACESSDEVICE_LIVE_PACKAGE = 1026;
	private short deviceStatus = 0;
	private byte[] deviceIP = new byte[16];

	public AcessDeviceStatusRequest() {
		this.head.setMsgType(MUAP_MSGTYPE_AcessDeviceStatus);
		this.head.setSendModlueName((short) 1);
		this.head.setRcvModlueName((short) 2);
		short msgLen = 30;
		this.head.setMsgLen(msgLen);
		this.head.setRsv((short) 0);
		this.head.setSeq((short) 0);
	}

	public void setDeviceStatus(short deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public void setDeviceIP(byte[] ip) {
		System.arraycopy(ip, 0, this.deviceIP, 0, ip.length);
	}

	protected void sendRequest(DataOutputStream out) throws IOException {
		CIOUtil.writeShort(out, this.deviceStatus);
		CIOUtil.writeBytes(out, this.deviceIP);
		out.flush();
	}
}
