package com.aidufei.protocol.remote.message;

import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ChannelSwitchMsg extends MsgHead {
	int channelID;
	byte[] channelName = new byte[120];
	boolean isSuccess;

	public ChannelSwitchMsg(int ID, String name) {
		this.channelID = ID;
		if (name != null)
			this.channelName = name.getBytes();
	}

	public ChannelSwitchMsg() {
	}

	public void setId(int ID) {
		this.channelID = ID;
	}

	public void setName(String name) {
		if (name != null)
			this.channelName = name.getBytes();
	}

	public int getId() {
		return this.channelID;
	}

	public byte[] getByteName() {
		return this.channelName;
	}

	public String getStingName() {
		return new String(this.channelName);
	}

	public void setSwitchResult(boolean ret) {
		this.isSuccess = ret;
	}

	public boolean isSwitchSuccess() {
		return this.isSuccess;
	}

	public void readInputMsg(DataInputStream in) throws Exception {
		this.channelID = in.readInt();

		this.isSuccess = in.readBoolean();
	}

	public void sendOutputMsg(DataOutputStream out) throws Exception {
		super.sendOutputMsg(out);

		out.writeInt(this.channelID);

		Log.e("ChannelSwitch", "channelName.lenth=" + this.channelName.length);

		byte[] tempString = new byte[120];

		for (int index = 0; index < this.channelName.length; ++index) {
			tempString[index] = this.channelName[index];
		}

		out.write(tempString);

		out.writeBoolean(this.isSuccess);
		out.flush();
	}

	public void print() {
		super.print();
		Log.e("DVBPlayerChannelSwitchMsg", "channelID=" + this.channelID);

		Log.e("DVBPlayerChannelSwitchMsg", "isSuccess=" + this.isSuccess);
	}
}
