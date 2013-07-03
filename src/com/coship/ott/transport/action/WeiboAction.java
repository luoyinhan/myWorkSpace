package com.coship.ott.transport.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.coship.ott.transport.util.NetTransportUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.unitend.udrm.ui.ShareWindow;
import com.weibo.sdk.android.WeiboParameters;

public class WeiboAction {
	/**
	 * 访问微博服务接口的地址
	 */
	public static final String API_SERVER = "https://open.weibo.cn/2";

	/**
	 * 退出登录
	 * 
	 * @param listener
	 */
	public JSONObject endSession(String access_token) {
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append("access_token=").append(access_token);
		String response = NetTransportUtil.getContent(API_SERVER
				+ "/account/end_session.json?", urlBuffer.toString());
		try {
			JSONObject obj = new JSONObject(response);
			return obj;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * 发布一条新微博(连续两次发布的微博不可以重复)
	 * 
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字。
	 * @param lat
	 *            纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon
	 *            经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 */
	public JSONObject shareWithOutPic(String content, String lat, String lon,
			String access_token) {
		WeiboParameters params = new WeiboParameters();
		params.add("status", content);
		params.add("access_token", access_token);
		if (!TextUtils.isEmpty(lon)) {
			params.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.add("lat", lat);
		}
		String response = NetTransportUtil.openUrl(API_SERVER
				+ "/statuses/update.json", NetTransportUtil.HTTPMETHOD_POST,
				params, null, null);
		try {
			JSONObject obj = new JSONObject(response);
			return obj;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * 上传图片并发布一条新微博，此方法会处理urlencode
	 * 
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字
	 * @param file
	 *            要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M。
	 * @param lat
	 *            纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon
	 *            经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 */
	public JSONObject shareWithPic(String content, String file, String lat,
			String lon, String access_token, byte[] imageposter) {
		WeiboParameters params = new WeiboParameters();
		params.add("status", content);
		params.add("access_token", access_token);
		params.add("pic", file);
		if (!TextUtils.isEmpty(lon)) {
			params.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.add("lat", lat);
		}
		String response = NetTransportUtil.openUrl(API_SERVER
				+ "/statuses/upload.json", NetTransportUtil.HTTPMETHOD_POST,
				params, file, imageposter);
		try {
			JSONObject obj = new JSONObject(response);
			return obj;
		} catch (JSONException e) {
			return null;
		}
	}

}
