package com.coship.ott.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.util.Log;

/**
 * <p>
 * Title: 捕获全局异常，并输出到SD卡工具类
 * </p>
 * 
 * Description:捕获全局异常，并输出到SD卡<br>
 * 
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {
	private static final String TAG = "GlobalExceptionHandler";

	@SuppressWarnings("unused")
	private Application mApplication;

	public GlobalExceptionHandler(Application application) {
		this.mApplication = application;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String info = null;
		ByteArrayOutputStream bos = null;
		PrintStream printStream = null;
		try {
			bos = new ByteArrayOutputStream();
			printStream = new PrintStream(bos);
			ex.printStackTrace(printStream);
			byte[] data = bos.toByteArray();
			info = new String(data);
			data = null;
			LogUtils.trace(Log.ERROR, TAG, info);

			// TODO 是安全退出还是弹出提示，下一步考虑
			killApp(true);
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, TAG, e.getMessage());
			killApp(true);
		} finally {
			try {
				if (printStream != null) {
					printStream.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				killApp(true);
			}
		}
	}

	/**
	 * 彻底退出应用程序
	 * 
	 * @param killSafely
	 *            是否安全退出
	 */
	@SuppressWarnings("deprecation")
	public static void killApp(final boolean killSafely) {
		if (killSafely) {
			System.runFinalizersOnExit(true);
			System.exit(0);
		} else {
			// Dalvik VM本地方法退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
