package com.coship.ott.utils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * 
 * @author 905421
 * 
 */
public class Utility {

	public static ArrayList<String> getEpgDateStrings() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// 获取今天是星期几
		int nowWeekIndex = c.get(Calendar.DAY_OF_WEEK) - 1;

		ArrayList<String> dateStrs = new ArrayList<String>();
		// 周五、周六、周日可获取下周的EPG信息，周五以前不显示
		boolean isShowNextWeek = nowWeekIndex > 4 ? true : false;
		String now = new SimpleDateFormat("E").format(c.getTime()).replace(
				"星期", "周");
		// 共显示多少天的EPG,如果要显示下周，则为前七天加本周剩余天数再加下周七天，
		// 如果不显示下周，则为前七天加本周剩余天数（前七天不包括今天）
		int dayCount = isShowNextWeek ? 7 * 3 - (nowWeekIndex - 1)
				: 7 * 2 - (nowWeekIndex - 1);
		// 要显示的那一天的日期
		c.add(Calendar.DATE, -7);
		String dateStr = "";
		for (int i = 0; i < dayCount; i++) {
			c.add(Calendar.DATE, 1);
			if (i != 6) {
				dateStr = new SimpleDateFormat("yyyy-MM-dd E(dd)").format(
						c.getTime()).replace("星期", "周");
			} else {
				dateStr = new SimpleDateFormat("yyyy-MM-dd E(yyyy-MM-dd)")
						.format(c.getTime()).replace("星期", "周");
				dateStr = dateStr.replace(now, "今天");
			}
			dateStrs.add(dateStr);
		}
		return dateStrs;
	}

	/**
	 * 获取当前日期
	 */
	public static String getDay() {
		return getDay(0);
	}

	public static String getDay(int num) {
		String pattern = "yyyy-MM-dd";
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, num);
		return new SimpleDateFormat(pattern).format(c.getTime());
	}

	public static String getWeekDay(int num) {
		String pattern = "E";
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, num);
		return new SimpleDateFormat(pattern).format(c.getTime());
	}

	public static int getNowWeekIndex() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// 获取今天是星期几
		int weekday = c.get(Calendar.DAY_OF_WEEK);
		return weekday;
	}

	/**
	 * 将时间的秒全转化为 时分秒
	 * 
	 * @param timeMs
	 *            秒数
	 * @return 00:00:00(时:分:秒)
	 */
	public static String stringForTime(int totalSeconds) {
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		StringBuilder formatBuilder = new StringBuilder();
		Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
		formatBuilder.setLength(0);
		if (hours > 0) {
			return formatter.format("%02d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/**
	 * 将秒转化为年月日
	 * 
	 * @param timeMs
	 *            秒数
	 * @return 2012-10-25 01:00:00(yyyy-MM-dd HH:mm:ss)
	 */
	public static String dateStrForTime(long totalSeconds) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(totalSeconds * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(gc.getTime());
	}

	public static String dealTime(String date) {
		String time = "";
		Pattern p = Pattern.compile("(\\d{1,2}):(\\d{1,2})");
		Matcher m = p.matcher(date);
		if (m.find()) {
			time = m.group(1) + ":" + m.group(2);
		}
		return time;
	}

	/**
	 * 日期转化为字符串
	 * */
	public static String dealTime(Date date) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * 日期转化为秒
	 * */
	public static long dealTimeToSeconds(String dateStr) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar calendar = Calendar.getInstance();

		try {
			Date date = sdf.parse(dateStr);
			calendar.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return calendar.getTimeInMillis() / 1000;
	}

	/**
	 * 把两个数字的商转化为百分比
	 * */
	public static String numToBFB(int numInt1, int numInt2) {
		String bfb = "";
		float numFloat1 = (float) numInt1;
		float numFloat2 = (float) numInt2;
		float result = numFloat1 / numFloat2 * 100;
		bfb = String.valueOf(result);
		if (bfb.length() > 2) {
			bfb = bfb.substring(0, 2);
		}
		return "(" + bfb + "%)";
	}

	/**
	 * 将数字字符转换为星期 (1:星期一；2：星期二)
	 * */
	public static String numToWeek(String week) {
		int curWeek = Integer.parseInt(week);
		String weekChinese[] = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六",
				"星期天" };
		return weekChinese[curWeek - 1];
	}

	/**
	 * 数组由大到小排序 冒泡排序法
	 * */
	public static ArrayList<Integer> maxToMinSort(ArrayList<Integer> indexs) {
		int tem;
		for (int i = 0; i < indexs.size() - 1; i++) {
			for (int j = 0; j < indexs.size() - 1 - i; j++) {
				if (indexs.get(j) < indexs.get(j + 1)) {
					tem = indexs.get(j);
					indexs.set(j, indexs.get(j + 1));
					indexs.set(j + 1, tem);
				}
			}
		}
		return indexs;
	}
}