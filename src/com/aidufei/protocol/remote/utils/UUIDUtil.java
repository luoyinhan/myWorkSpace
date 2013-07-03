package com.aidufei.protocol.remote.utils;

import java.util.UUID;

public class UUIDUtil {
	public static String getRandomUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
