package com.aidufei.protocol.remote.utils;

public enum SensorTypeEnum {
	SENSORS_ACCECTRTION((short) 1), SENSORS_ORIENTAION((short) 3), SENSORS_MAGNETIC_FIELD(
			(short) 4), SENSORS_TEMPRATURE((short) 8);

	private final short type;

	private SensorTypeEnum(short type) {
		this.type = type;
	}

	public short getType() {
		return this.type;
	}
}
