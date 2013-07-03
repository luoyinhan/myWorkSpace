package com.coship.ott.transport.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.coship.ott.constant.Constant;
import com.coship.ott.utils.LogUtils;

public class AsyncBitmapLoader {
	private static final String TAG = "AsyncBitmapLoader";
	/**
	 * 内存图片软引用缓冲
	 */
	// private static HashMap<String, SoftReference<Bitmap>> imageCache = null;
	private static HashMap<String, Bitmap> imageCache = null;
	private File cacheDir = null;
	private static AsyncBitmapLoader mAsyncBitmapLoader = null;

	private AsyncBitmapLoader() {
		imageCache = new HashMap<String, Bitmap>();
		final String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(Constant.ROOT_ADDR + "imgCache");
			// 检查/mnt/sdcard/saition文件夹是否存在，不存在则新建文件夹
			if (!cacheDir.exists() || !cacheDir.isDirectory()) {
				cacheDir.mkdirs();
			}
		}
	}

	public static AsyncBitmapLoader getInstance() {
		if (null == mAsyncBitmapLoader) {
			mAsyncBitmapLoader = new AsyncBitmapLoader();
		}
		return mAsyncBitmapLoader;
	}

	public Bitmap loadBitmap(final ImageView imageView, final String imageURL,
			final ImageCallBack imageCallBack) {
		if (TextUtils.isEmpty(imageURL)) {
			if (null != imageView && null != imageCallBack) {
				imageCallBack.imageLoad(imageView, null);
			}
			return null;
		}
		// 在内存缓存中，则返回Bitmap对象
		if (imageCache.containsKey(imageURL)) {
			Bitmap bitmap = imageCache.get(imageURL);
			if (bitmap != null) {
				if (null != imageView && null != imageCallBack) {
					imageCallBack.imageLoad(imageView, bitmap);
				}
				return bitmap;
			}
		}
		// 加上一个对本地缓存的查找
		if (null != cacheDir) { // 本地缓存存在
			String bitmapName = imageURL
					.substring(imageURL.lastIndexOf("/") + 1);
			File[] cacheFiles = cacheDir.listFiles();
			if (null != cacheFiles) {
				int i = 0;
				for (; i < cacheFiles.length; i++) {
					if (bitmapName.equals(cacheFiles[i].getName())) {
						break;
					}
				}
				if (i < cacheFiles.length) {
					Bitmap bitmap = BitmapFactory.decodeFile(cacheDir.getPath()
							+ "/" + bitmapName);
					imageCache.put(imageURL, bitmap);
					if (bitmap != null) {
						if (null != imageView && null != imageCallBack) {
							imageCallBack.imageLoad(imageView, bitmap);
						}
						return bitmap;
					}
				}
			}
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (null != imageView && null != imageCallBack) {
					imageCallBack.imageLoad(imageView, (Bitmap) msg.obj);
				}
			}
		};

		// 如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片
		new Thread() {
			@Override
			public void run() {
				HttpClient client = CustomerHttpClient.getHttpClient();
				HttpGet request = new HttpGet(imageURL);
				Bitmap bitmap = null;
				// 发送GET请求，并将响应内容转换成字符串
				InputStream bitmapIs = null;
				try {
					HttpResponse response = client.execute(request);
					bitmapIs = new ByteArrayInputStream(
							EntityUtils.toByteArray(response.getEntity()));
					bitmap = BitmapFactory.decodeStream(bitmapIs);
					if (bitmap != null && bitmap.getHeight() > 0)
						imageCache.put(imageURL, bitmap);
					Message msg = handler.obtainMessage(0, bitmap);
					handler.sendMessage(msg);
				} catch (ClientProtocolException e1) {
					Log.d(TAG, "ClientProtocolException");

					return;
				} catch (Exception e1) {
					Log.d(TAG, "IOException");
					return;
				} finally {
					try {
						if (null != bitmapIs) {
							bitmapIs.close();
							bitmapIs = null;
						}
					} catch (IOException e) {
						Log.d(TAG, "IOException");
					}
				}
				// 把图片写入文件做缓存
				writeToFile(imageURL, bitmap);
			}

		}.start();
		return null;
	}

	private void writeToFile(final String imageURL, Bitmap bitmap) {
		if (null == cacheDir || null == bitmap) {
			return;
		}

		File bitmapFile = new File(cacheDir.getPath() + "/"
				+ imageURL.substring(imageURL.lastIndexOf("/") + 1));
		if (!bitmapFile.exists()) {
			try {
				bitmapFile.createNewFile();
			} catch (IOException e) {
				LogUtils.trace(Log.DEBUG, TAG, "writeToFile failed!");
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(bitmapFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			LogUtils.trace(Log.DEBUG, TAG, "ClientProtocolException");
			return;
		} catch (IOException e) {
			LogUtils.trace(Log.DEBUG, TAG, "ClientProtocolException");
			return;
		} finally {
			try {
				if (null != fos) {
					fos.close();
					fos = null;
				}
			} catch (IOException e) {
				LogUtils.trace(Log.DEBUG, TAG, "IOException");
			}
		}
	}

	/**
	 * 回调接口
	 */
	public interface ImageCallBack {
		public void imageLoad(ImageView imageView, Bitmap bitmap);
	}
}