package com.aidufei.protocol.remote.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CIOUtil {
	public static final String CHARSET = "UTF-8";

	public static boolean readBoolean(DataInputStream is) throws IOException {
		return is.readBoolean();
	}

	public static byte[] readBytes(DataInputStream is, int i)
			throws IOException {
		byte[] data = new byte[i];
		is.readFully(data);

		return data;
	}

	public static char readChar(DataInputStream is) throws IOException {
		return (char) readShort(is);
	}

	public static double readDouble(DataInputStream is) throws IOException {
		return Double.longBitsToDouble(readLong(is));
	}

	public static float readFloat(DataInputStream is) throws IOException {
		return Float.intBitsToFloat(readInt(is));
	}

	public static int readInt(DataInputStream is) throws IOException {
		return Integer.reverseBytes(is.readInt());
	}

	public static long readLong(DataInputStream is) throws IOException {
		return Long.reverseBytes(is.readLong());
	}

	public static short readShort(DataInputStream is) throws IOException {
		return Short.reverseBytes(is.readShort());
	}

	public static String readUTF(DataInputStream is) throws IOException {
		short s = readShort(is);
		byte[] str = new byte[s];

		is.readFully(str);

		return new String(str, "UTF-8");
	}

	public static void writeBoolean(DataOutputStream os, boolean b)
			throws IOException {
		os.writeBoolean(b);
	}

	public static void writeBytes(DataOutputStream os, byte[] data)
			throws IOException {
		os.write(data);
	}

	public static void writeChar(DataOutputStream os, char b)
			throws IOException {
		writeShort(os, (short) b);
	}

	public static void writeDouble(DataOutputStream os, double d)
			throws IOException {
		writeLong(os, Double.doubleToLongBits(d));
	}

	public static void writeFloat(DataOutputStream os, float f)
			throws IOException {
		writeInt(os, Float.floatToIntBits(f));
	}

	public static void writeInt(DataOutputStream os, int i) throws IOException {
		os.writeInt(Integer.reverseBytes(i));
	}

	public static void writeLong(DataOutputStream os, long l)
			throws IOException {
		os.writeLong(Long.reverseBytes(l));
	}

	public static void writeShort(DataOutputStream os, short s)
			throws IOException {
		os.writeShort(Short.reverseBytes(s));
	}

	public static void writeUTF(DataOutputStream os, String str)
			throws IOException {
		byte[] data = str.getBytes("UTF-8");
		writeShort(os, (short) data.length);
		os.write(data);
	}
}
