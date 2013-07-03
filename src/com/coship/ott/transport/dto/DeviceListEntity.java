package com.coship.ott.transport.dto;

public class DeviceListEntity {
	public String DRMID;
	public String MACAddr;
	public String DeviceName;

	public DeviceListEntity() {
		// TODO Auto-generated constructor stub
	}

	public String getPchDRMID() {
		return DRMID;
	}

	public void setPchDRMID(String pchDRMID) {
		this.DRMID = pchDRMID;
	}

	public String getPchMACAddr() {
		return MACAddr;
	}

	public void setPchMACAddr(String pchMACAddr) {
		this.MACAddr = pchMACAddr;
	}

	public String getPchDeviceName() {
		return DeviceName;
	}

	public void setPchDeviceName(String pchDeviceName) {
		this.DeviceName = pchDeviceName;
	}

}
