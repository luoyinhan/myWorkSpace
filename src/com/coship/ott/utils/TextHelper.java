package com.coship.ott.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;
import android.util.Log;

/**
 * <p>
 * Title: 文本处理工具类
 * </p>
 * 
 * Description:自定义的Log工具类<br>
 * 
 */
public final class TextHelper {
	// private static final String TAG = "TextHelper";

	/**
	 * 字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null)
			return true;
		else
			return TextUtils.isEmpty(str.trim());
	}

	/**
	 * 判断对象是否为空(obj,map,list,set等对象)
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNull(Object obj) {
		if (obj == null || obj.equals(""))
			return true;

		if (obj instanceof Map) {
			Map map = (Map) obj;
			if (map.size() < 1)
				return true;
		} else if (obj instanceof List) {
			List list = (List) obj;
			if (list.size() < 1)
				return true;
		} else if (obj instanceof Set) {
			Set set = (Set) obj;
			if (set.size() < 1)
				return true;
		}
		return false;
	}

	/**
	 * 测试字符串长度
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static int strLenth(String str) {
		int size = 0;
		if (str != null) {
			for (int i = 0, len = str.length(); i < len; i++) {
				char ch = str.charAt(i);
				if (ch > 0 && ch < 128)
					size += 1;
				else
					size += 2;
			}
		}
		return size;
	}

	public enum DateFormater {

		NORMAL("yyyy-MM-dd HH:mm"), DD("yyyy-MM-dd"), SS("yyyy-MM-dd HH:mm:ss");

		private String value;

		private DateFormater(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

	/**
	 * 
	 * 获得格式化后的日期 SS("yyyy-MM-dd HH:mm:ss")
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateSS(Date date) {
		// TODO 发送消息的时间与服务器时间同步
		long datess = date.getTime();

		final SimpleDateFormat dateformat = new SimpleDateFormat(
				DateFormater.SS.getValue());
		return dateformat.format(new Date(datess));
	}

	/**
	 * 获得格式化后的日期 DD("yyyy-MM-dd")
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateDD(Date date) {
		final SimpleDateFormat dateformat = new SimpleDateFormat(
				DateFormater.DD.getValue());
		return dateformat.format(date);
	}

	/**
	 * 计算二个时间差
	 * 
	 * @param endTime
	 * @param beginTime
	 * @return X天X小时X分之前
	 */
	public static String betweenTime(java.util.Date endTime, String beginTime) {

		try {
			SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date end = endTime;
			java.util.Date begin;
			begin = dfs.parse(beginTime);

			long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
			long day1 = between / (24 * 3600);
			long hour1 = between % (24 * 3600) / 3600;
			long minute1 = between % 3600 / 60;
			// long second1 = between % 60 / 60;
			// System.out.println("" + day1 + "天" + hour1 + "小时" + minute1 +
			// "分");
			String betweenTime = "";
			if (day1 > 0) {
				betweenTime += day1 + "天";
			} else if (hour1 > 0) {
				betweenTime += hour1 + "小时";
			} else if (minute1 > 0) {
				betweenTime += minute1 + "分";
			} else {
				betweenTime += "1分";
			}

			if (!betweenTime.equals("")) {
				betweenTime += "之前";
			}
			return betweenTime;
		} catch (ParseException e) {
			LogUtils.trace(Log.ERROR, "betweenTime", e.getMessage());
			return "";
		}

	}
}
