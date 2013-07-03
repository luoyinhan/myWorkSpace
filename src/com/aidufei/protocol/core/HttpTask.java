package com.aidufei.protocol.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.coship.ott.utils.LogUtils;

public abstract class HttpTask extends AsyncTask<String, Integer, Integer> {
	public static final int ERROR_NONE = 0;
	public static final int ERROR_REQUEST = -1;
	public static final int ERROR_CONNECT = -2;
	public static final int ERROR_RESPONSE = -3;
	public static final int ERROR_EXCEPTION = -4;

	private String mRequest = null;

	@Override
	protected void onPreExecute() {
		mRequest = buildRequest();
	}

	@Override
	protected Integer doInBackground(String... params) {

		if (params == null || params[0] == null || mRequest == null) {
			return ERROR_REQUEST;
		}

		mRequest = "http://" + params[0] + mRequest;

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(mRequest);
			HttpResponse response = client.execute(get);
			if (response == null)
				return ERROR_RESPONSE;
			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				long total = entity.getContentLength();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int count = 0;
				int length = -1;
				while ((length = is.read(buf)) != -1) {
					baos.write(buf, 0, length);
					count += length;
					// 调用publishProgress公布进度,最后onProgressUpdate方法将被执行
					publishProgress((int) ((count / (float) total) * 100));
				}

				String resp = new String(baos.toByteArray());

				if (parseResponse(resp) == true) {
					return ERROR_NONE;
				}

			}
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, LogUtils.getTAG(), e.getMessage());
			return ERROR_EXCEPTION;
		}
		return ERROR_RESPONSE;
	}

	@Override
	protected void onProgressUpdate(Integer... progresses) {
		updateProgress(progresses[0]);
	}

	// onPostExecute方法用于在执行完后台任务后更新UI,显示结果
	@Override
	protected void onPostExecute(Integer result) {
		if (result == null)
			return;
		if (result < 0) {
			updateError(result);
			return;
		}
		handleResponse();
	}

	protected abstract String buildRequest();

	protected abstract boolean parseResponse(String response);

	protected abstract void updateProgress(int progress);

	protected abstract void handleResponse();

	protected abstract void updateError(int error);

}
