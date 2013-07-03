package com.aidufei.remote;

public class SensorMessage {
	private short dataType;
	private short sensorType;
	private short accuracy;
	private float pointX;
	private float pointY;
	private float pointZ;

	public SensorMessage() {
		this.dataType = 0;

		this.sensorType = 1;
		this.accuracy = 0;
	}

	public short getDataType() {
		return this.dataType;
	}

	public void setDataType(short dataType) {
		this.dataType = dataType;
	}

	public short getSensorType() {
		return this.sensorType;
	}

	public void setSensorType(short sensorType) {
		this.sensorType = sensorType;
	}

	public short getAccuracy() {
		return this.accuracy;
	}

	public void setAccuracy(short accuracy) {
		this.accuracy = accuracy;
	}

	public float getPointX() {
		return this.pointX;
	}

	public void setPointX(float pointX) {
		this.pointX = pointX;
	}

	public float getPointY() {
		return this.pointY;
	}

	public void setPointY(float pointY) {
		this.pointY = pointY;
	}

	public float getPointZ() {
		return this.pointZ;
	}

	public void setPointZ(float pointZ) {
		this.pointZ = pointZ;
	}
}
