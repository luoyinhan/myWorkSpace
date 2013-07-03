package com.coship.ott.constant;

import android.os.Build;

import com.coship.ott.activity.R;
import com.coship.ott.transport.util.NetTransportUtil;

public class Constant {
	/**
	 * 终端类型 1:PC 2:IPAD 3:IPNOE 4:ANDROID PHONE 5:ANDROID PAD 0:ALL 1:TV 2:PC
	 * 3:PHONE 4:PAD
	 * */
	public static final int TERMINAL_TYPE = 4;
	/**
	 * 终端分辨率
	 * */

	public static final String RESOLUTION = "1280*720";
	/**
	 * 获取设备名称
	 */
	public static final String DEVICENAME = Build.MODEL;
	/**
	 * 解除绑定
	 */
	public static final int UNDEVICE_BINGDING = 0;
	/**
	 * 点播播放结束存入的书签历史记录
	 **/
	public static final long PLAY_OVER = 999999;
	/**
	 * 绑定设备已有3个，需解除一个再进行绑定
	 */
	public static final int NEET_UNBINGDING_ONE_DEVICE = 1;
	/**
	 * 绑定设备
	 */
	public static final int BINGDING_DEVICE = 2;
	public static final String DRMURL = "udrm://116.77.70.121:443/udrmsysservice/services/UdrmSysWS.UdrmSysWSHttpSoap12Endpoint/";
	// public static final String DRMURL
	// ="udrm://udrm.unitend.com/udrmsysservice/services/UdrmSysWS.UdrmSysWSHttpSoap12Endpoint";
	/**
	 * 数据接口版本号
	 * */
	public static String SERVER_ADDR = "http://116.77.70.115:8080/";
	// public static final String SERVER_ADDR = NetTransportUtil
	// .getValueFromProperties("SERVER_ADDR");
	/**
	 * 协议版本
	 * */
	public static final String DATA_INTERFACE_VERSION = NetTransportUtil
			.getValueFromProperties("DATA_INTERFACE_VERSION");

	/**
	 * 本软件根目录
	 * */
	public static final String ROOT_ADDR = "/mnt/sdcard/MulScreenPad/";
	/**
	 * 配置文件路径
	 * */
	public static final String PROPERTIES_ADDR = ROOT_ADDR
			+ "properties/MulScreenPad.properties";
	/**
	 * 日志文件路径
	 * */
	public static final String LOG_ADDR = ROOT_ADDR + "log/MulScreenPadLog.txt";
	/**
	 * 开发者的appkey，天威提供
	 * */
	public static final String CONSUMER_KEY = "2038775251";
	/**
	 * 授权成功后跳转地址，天威提供
	 * */
	public static final String REDIRECT_URL = "http://open.weibo.com/apps/2038775251/privilege/oauth";

	public static final int[] vPics = new int[] { R.drawable.v1, R.drawable.v2,
			R.drawable.v3, R.drawable.v4, R.drawable.v5, R.drawable.v6,
			R.drawable.v7, R.drawable.v8, R.drawable.v9, R.drawable.v10, };
	public static final int[] hPics = new int[] { R.drawable.h1, R.drawable.h2,
			R.drawable.h3, R.drawable.h4, R.drawable.h5, R.drawable.h6,
			R.drawable.h7, R.drawable.h8, R.drawable.h9, R.drawable.h10, };
}