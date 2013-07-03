package com.coship.ott.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

/**
 * <p>
 * Title: 自定义的Log工具类
 * </p>
 * 
 * Description:自定义的Log工具类 <br>
 * 如：LogUtils.trace(Log.DEBUG, LogUtils.getTAG(), "-- onCreate --");<br/>
 * LogUtils.trace(Log.INFO, LogUtils.getTAG(), "-- onCreate --");<br/>
 * LogUtils.trace(Log.ERROR, LogUtils.getTAG(), "-- onCreate --");<br/>
 * 
 * 
 */
@SuppressLint("UseSparseArrays")
public final class LogUtils {

	/** Log开关，开发中开启，发布时关闭(DDMS) */
	public static boolean gLogOff = true;
	/** Log写入文件级别（当设置为Log.ASSERT时日志不写入文件） */
	public static int gLever = Log.ERROR; // 开发中用Log.DEBUG，发布用Log.ERROR级别
	/** 存放日志的目录地址 */
	private static final String TVPADSCREEN_LOG_DIR = Environment
			.getExternalStorageDirectory().toString() + "/MulScreenPad/Log/";// MulScreenPad/log/MulScreenPadLog.txt

	private static String gFileName = "log.txt";
	/** 日志的保存时间(天为单位) */
	private static final int CLEAR_LOG_DATE = 30;

	private static Map<Integer, String> lmap = null;

	/**
	 * 自定义的Log输出方法
	 * 
	 * @param tag
	 *            TAG
	 * @param msg
	 *            Log内容
	 * @param type
	 *            Log类型
	 */
	public static void trace(int type, String tag, String msg) {

		// DDMS 形式
		if (gLogOff) {
			if (TextHelper.isEmpty(msg)) {
				msg = "未知异常";
			}
			switch (type) {
			case Log.VERBOSE:
				Log.v(tag, msg);
				break;
			case Log.DEBUG:
				Log.d(tag, msg);
				break;
			case Log.INFO:
				Log.i(tag, msg);
				break;
			case Log.WARN:
				Log.w(tag, msg);
				break;
			case Log.ERROR:
				Log.e(tag, msg);
				break;
			}
		}

		// 文件形式
		if (gLever <= type) {
			addLog(type, tag, msg);
		}
	}

	/**
	 * 将Log内容写入文件并存放到SD中指定的目录
	 * 
	 * @param log
	 * @return
	 */
	public static boolean addLog(int type, String tag, String log) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return false;
		try {
			if (lmap == null) {
				lmap = new HashMap<Integer, String>();
				lmap.put(Log.VERBOSE, " VERBOSE ");
				lmap.put(Log.DEBUG, " DEBUG ");
				lmap.put(Log.INFO, " INFO ");
				lmap.put(Log.WARN, " WARN ");
				lmap.put(Log.ERROR, " ERROR ");
			}

			log = "\r\n" + TextHelper.getDateSS(new Date()) + lmap.get(type)
					+ tag + " --> " + log;
			gFileName = "log-" + TextHelper.getDateDD(new Date()) + ".txt";

			String savePathStr = TVPADSCREEN_LOG_DIR;
			String saveFileNameS = gFileName;

			recordLog(savePathStr, saveFileNameS, log, true);
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, "LogUtils", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 功能：记录日志<br>
	 * 
	 * @param savePathStr
	 *            保存日志路径
	 * @param saveFileNameS
	 *            保存日志文件名
	 * @param saveDatarStr
	 *            保存日志数据
	 * @param saveTypeStr
	 *            保存类型，fals为覆盖保存，true为在原来文件后添加保存
	 */
	public static void recordLog(String savePathStr, String saveFileNameS,
			String saveDatarStr, boolean saveTypeStr) {
		try {
			String savePath = savePathStr;
			String saveFileName = saveFileNameS;
			String saveData = saveDatarStr;
			boolean saveType = saveTypeStr;

			// 准备要保存的文件目录
			createSDDir(savePath);

			File saveFile = new File(savePath + "/" + saveFileName);
			if (!saveType && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();

				// 保存结果到文件
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && saveFile.exists()) {
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && !saveFile.exists()) {
				saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}
		} catch (IOException e) {
			// TODO 是否还需要处理
			// recordLog(savePathStr, saveFileNameS, saveDatarStr, saveTypeStr);
		} catch (Exception e) {
			// recordLog(savePathStr, saveFileNameS, saveDatarStr, saveTypeStr);
		}

	}

	/**
	 * 清除过期的日志文件
	 * 
	 * log fileName = log-2012-12-06.txt;
	 * 
	 */
	public static void delLogs() {
		// 构建文件对象
		File dir = new File(TVPADSCREEN_LOG_DIR);
		// 得到改文件夹下所有文件
		File[] files = dir.listFiles();
		if (files != null) {

			java.text.SimpleDateFormat formatDD = new SimpleDateFormat(
					"yyyy-MM-dd");
			GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
			gc.setTime(new Date());
			gc.add(Calendar.DATE, -CLEAR_LOG_DATE);
			String strTime = "log-" + formatDD.format(gc.getTime());

			for (int i = 0, len = files.length; i < len; i++) {
				String lastModified = files[i].getName();
				if (lastModified.compareTo(strTime) < 0) {
					LogUtils.trace(Log.DEBUG, "DeleteFile", "delete file:"
							+ files[i].getName());
					files[i].delete();
				}
			}
		}
	}

	/**
	 * 创建文件夹目录
	 * 
	 * @param dir
	 * @return
	 */
	private static File createSDDir(String dir) {
		File folder = new File(dir);
		// 如果没有相应的文件夹，则创建
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}

	/**
	 * 处理程序全局异常，并记录日志
	 * 
	 * @param application
	 */
	public static void processGlobalException(Application application) {
		if (application != null) {
			GlobalExceptionHandler handler = new GlobalExceptionHandler(
					application);
			Thread.setDefaultUncaughtExceptionHandler(handler);
		}
	}

	/**
	 * 获取输出日志的方法TAG信息
	 */
	public static String getTAG() {
		StringBuffer sb = new StringBuffer();

		StackTraceElement[] stacks = new Throwable().getStackTrace();
		int stacksLen = stacks.length;
		sb.append(stacks[1].getClassName()).append(".")
				.append(stacks[1].getMethodName()).append(" : ")
				.append(stacks[1].getLineNumber() + "\n\t\t\t\t");

		return sb.toString();
	}
}
