package com.coship.ott.transport.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aidufei.protocol.remote.callback.GetAppListCallBack;
import com.coship.ott.constant.Constant;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.NetStateManager;
import com.weibo.sdk.android.util.Utility;

public class NetTransportUtil {
	private static String serverUrl = null;
	private static String TAG = "NetTransportUtil";
	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient;

	public static void setRequestUrl(String url) {
		serverUrl = url;
	}

	public static String getRequestUrl() {
		return serverUrl;
	}

	static {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
	}

	/**
	 * 从url中得到json内容
	 * 
	 * @param urlname
	 * @return String
	 */
	public static String getContent(String interfaceName, String paramStr) {
		String message = "";
		String requestUrl = "";
		if (TextUtils.isEmpty(interfaceName)) {
			return message;
		}
		try {
			HttpClient client = CustomerHttpClient.getHttpClient();
			if (-1 != interfaceName.indexOf("http://") // 请求第三方接口
					|| -1 != interfaceName.indexOf("https://")) {
				requestUrl = interfaceName + paramStr;
			} else {
				if (TextUtils.isEmpty(serverUrl)) {
					serverUrl = Constant.SERVER_ADDR;
				}
				requestUrl = serverUrl + interfaceName + paramStr;
				requestUrl = requestUrl.replace(" ", "%20");
				String authKey = getMD5(requestUrl + "aidufei");
				requestUrl += ("&authKey=" + authKey);
			}
			HttpGet request = new HttpGet(requestUrl);
			// 发送GET请求，并将响应内容转换成字符串
			HttpResponse response = client.execute(request);
			message = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (IOException e) {
			LogUtils.trace(Log.DEBUG, "HttpClient", "数据读取失败！url: " + requestUrl);
		}
		return message;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 * @return String 已收藏到“收藏网页中（e）
	 */
	public static String post(String url, Map<String, String> rawParams) {
		try {
			// 编码参数
			List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
			for (String key : rawParams.keySet()) {
				formparams.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					CHARSET);

			// 创建POST请求
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			// 发送请求
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("请求失败");
			}
			HttpEntity resEntity = response.getEntity();
			return (resEntity == null) ? null : EntityUtils.toString(resEntity,
					CHARSET);
		} catch (UnsupportedEncodingException e) {
			LogUtils.trace(Log.WARN, TAG, e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			LogUtils.trace(Log.WARN, TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("连接失败", e);
		}
	}

	public static synchronized HttpClient getHttpClient() {
		if (null == customerHttpClient) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 1000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, 2000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 4000);

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			customerHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return customerHttpClient;
	}

	/**
	 * 对str进行MD5加密
	 * */
	public static String getMD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] byteDigest = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < byteDigest.length; offset++) {
				i = byteDigest[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString();
			// 16位的加密
			// return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 产生11位的boundary
	 */
	static String getBoundry() {
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; t++) {
			long time = System.currentTimeMillis() + t;
			if (time % 3 == 0) {
				_sb.append((char) time % 9);
			} else if (time % 3 == 1) {
				_sb.append((char) (65 + time % 26));
			} else {
				_sb.append((char) (97 + time % 26));
			}
		}
		return _sb.toString();
	}

	private static final String BOUNDARY = getBoundry();
	private static final String MP_BOUNDARY = "--" + BOUNDARY;
	private static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String HTTPMETHOD_POST = "POST";
	public static final String HTTPMETHOD_GET = "GET";

	/**
	 * 
	 * @param url
	 *            服务器地址
	 * @param method
	 *            "GET"or “POST”
	 * @param params
	 *            存放参数的容器
	 * @param file
	 *            文件路径，如果 是发送带有照片的微博的话，此参数为图片在sdcard里的绝对路径
	 * @return 响应结果
	 * @throws WeiboException
	 */
	public static String openUrl(String url, String method,
			WeiboParameters params, String file, byte[] imageposter) {
		String result = "";
		try {
			HttpClient client = CustomerHttpClient.getHttpClient();
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					NetStateManager.getAPN());
			if (method.equals(HTTPMETHOD_GET)) {
				url = url + "?" + Utility.encodeUrl(params);
				HttpGet get = new HttpGet(url);
				request = get;
			} else if (method.equals(HTTPMETHOD_POST)) {
				HttpPost post = new HttpPost(url);
				request = post;
				byte[] data = null;
				String _contentType = params.getValue("content-type");

				bos = new ByteArrayOutputStream();
				if (!TextUtils.isEmpty(file)) {
					paramToUpload(bos, params);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA
							+ "; boundary=" + BOUNDARY);
					Utility.UploadImageUtils.revitionPostImageSize(file);
					imageContentToUpload(bos, imageposter);
				} else {
					if (_contentType != null) {
						params.remove("content-type");
						post.setHeader("Content-Type", _contentType);
					} else {
						post.setHeader("Content-Type",
								"application/x-www-form-urlencoded");
					}

					String postParam = Utility.encodeParameters(params);
					data = postParam.getBytes("UTF-8");
					bos.write(data);
				}
				data = bos.toByteArray();
				bos.close();
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(url);
			}
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			result = readHttpResponse(response);
			if (statusCode != 200) {
				LogUtils.trace(Log.DEBUG, TAG,
						"message: openUrl occours Exception, statusCode : "
								+ statusCode + ", response: " + result);
			}
			return result;
		} catch (IOException e) {
			LogUtils.trace(Log.DEBUG, TAG,
					"message: openUrl occours IOException, " + e.getMessage());
			return result;
		}
	}

	/**
	 * 读取HttpResponse数据
	 * 
	 * @param response
	 * @return
	 */
	private static String readHttpResponse(HttpResponse response) {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null
					&& header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
		return result;
	}

	private static void paramToUpload(OutputStream baos, WeiboParameters params) {
		String key = "";
		for (int loc = 0; loc < params.size(); loc++) {
			key = params.getKey(loc);
			StringBuilder temp = new StringBuilder(10);
			temp.setLength(0);
			temp.append(MP_BOUNDARY).append("\r\n");
			temp.append("content-disposition: form-data; name=\"").append(key)
					.append("\"\r\n\r\n");
			temp.append(params.getValue(key)).append("\r\n");
			byte[] res = temp.toString().getBytes();
			try {
				baos.write(res);
			} catch (IOException e) {
				LogUtils.trace(
						Log.DEBUG,
						TAG,
						"message: paramToUpload occours IOException, "
								+ e.getMessage());
			}
		}
	}

	private static void imageContentToUpload(OutputStream out, byte[] imgpath) {
		if (imgpath == null) {
			return;
		}
		StringBuilder temp = new StringBuilder();
		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"pic\"; filename=\"")
				.append("news_image").append("\"\r\n");
		String filetype = "image/png";
		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		byte[] res = temp.toString().getBytes();
		FileInputStream input = null;
		try {
			out.write(res);
			// input = new FileInputStream(imgpath);
			// byte[] buffer = new byte[1024 * 50];
			// while (true) {
			// int count = input.read(buffer);
			// if (count == -1) {
			// break;
			// }
			// out.write(buffer, 0, count);
			// }
			out.write(imgpath);
			out.write("\r\n".getBytes());
			out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			LogUtils.trace(
					Log.DEBUG,
					TAG,
					"message: imageContentToUpload occours IOException, "
							+ e.getMessage());
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (IOException e) {
					LogUtils.trace(Log.DEBUG, TAG,
							"message: imageContentToUpload occours IOException, "
									+ e.getMessage());
				}
			}
		}
	}

	/**
	 * 从配置文件里读取指定内容
	 * 
	 * @param key
	 *            要读取配置的键值
	 * */
	public static String getValueFromProperties(String key) {
		String value = "";
		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(new File(
					Constant.PROPERTIES_ADDR))));
			value = properties.getProperty(key);
			LogUtils.trace(Log.DEBUG, "getValueFromProperties",
					"PROPERTIES_ADDR: " + Constant.PROPERTIES_ADDR + ",key: "
							+ key + ",value: " + value);
		} catch (Exception e) {
			LogUtils.trace(Log.DEBUG, "getValueFromProperties", "读取配置文件时出错！"
					+ Constant.PROPERTIES_ADDR + ",key: " + key);
			if ("SERVER_ADDR".equals(key)) {
				value = "http://116.77.70.115:8080/";
				// value = "http://172.21.11.201:90/";// 测试网
			} else if ("DATA_INTERFACE_VERSION".equals(key)) {
				value = "V001";
			}
		}
		return value;
	}

	public static boolean testIfNetWorkReachable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == connectivity) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0, len = info.length; i < len; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}