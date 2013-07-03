package com.coship.ott.transport.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestingUtil {
	/**
	 * 匹配邮箱
	 * 
	 * @param userMailst2
	 * @return
	 */
	public static boolean isEmail(String email) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

		Matcher matcher = pattern.matcher(email);

		System.out.println(matcher.matches());

		return matcher.matches();
	}

	/**
	 * 匹配手机号码
	 * 
	 * @param PhoneNum
	 * @return
	 */
	public static boolean isMobileNO(String PhoneNum) {
		Pattern pattern = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher M = pattern.matcher(PhoneNum);
		return M.matches();
	}

	/**
	 * 验证数字、字母、下划线
	 * 
	 * @param PhoneNum
	 * @return
	 */
	public static boolean isFitMode(String PhoneNum) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9_]+");
		Matcher M = pattern.matcher(PhoneNum);
		return M.matches();
	}
}
