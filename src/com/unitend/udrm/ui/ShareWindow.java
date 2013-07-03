package com.unitend.udrm.ui;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.activity.R;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserAction;
import com.coship.ott.transport.action.WeiboAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.utils.AccessTokenKeeper;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.utils.WeiboUtil;
import com.coship.ott.view.CustormImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.weibo.sdk.android.Oauth2AccessToken;

public class ShareWindow implements OnClickListener {
	public final static String TAG = "ShareWindow";
	private Context mContext;
	private String mAssetName;
	private String mPosterUrl;
	private int objType;
	private String objID;
	// 分享文字输入框
	private EditText discussEditText = null;
	// 分享字数显示
	private TextView discussFontNum = null;
	private View shareWindow;

	private ShareWindowListener mShareWindowListener;
	private CheckBox isSendPoster;
	private ProgressDialog mProgressDialog;

	public ShareWindow(ViewGroup rootView, Context context,
			ShareWindowListener shareWindowListener) throws Throwable {
		this.mContext = context;
		this.mShareWindowListener = shareWindowListener;
		if (null == rootView) {
			throw new Throwable("rootView can not be null!");
		}
		shareWindow = LayoutInflater.from(mContext).inflate(
				R.layout.share_window, null);
		LayoutParams params = new LayoutParams(650, 457);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		rootView.addView(shareWindow, params);
		// 分享窗口里面的输入框
		discussEditText = (EditText) shareWindow.findViewById(R.id.discussEdit);
		discussEditText.addTextChangedListener(new DiscussWatcher());
		// 显示评论框已输入评论字数
		discussFontNum = (TextView) shareWindow.findViewById(R.id.discussNum);
		discussFontNum.setText(mContext.getString(R.string.dis_num).replace(
				"$num", 0 + ""));
		// 是否发送截图
		isSendPoster = (CheckBox) shareWindow
				.findViewById(R.id.checkSendPoster);
		// 评论框关闭按钮
		shareWindow.findViewById(R.id.shareExitBtn).setOnClickListener(this);
		shareWindow.findViewById(R.id.shareExitBtn).requestFocus();
		shareWindow.findViewById(R.id.discussCancel).setOnClickListener(this);
		shareWindow.findViewById(R.id.discussSend).setOnClickListener(this);
		mProgressDialog = new ProgressDialog(mContext);
	}

	public void show(String assetName, String posterUrl, int objType,
			String objId) {
		this.mAssetName = assetName;
		this.mPosterUrl = posterUrl;
		this.objType = objType;
		this.objID = objId;
		discussEditText.setText(mContext.getString(R.string.share_weibo_msg)
				.replace("$name", mAssetName));
		CustormImageView posterView = (CustormImageView) shareWindow
				.findViewById(R.id.discussPoster);
		if (TextUtils.isEmpty(mPosterUrl)) {
			posterView.setBackgroundResource(0);
			isSendPoster.setEnabled(false);
			isSendPoster.setChecked(false);
		} else {
			posterView.setImageHttpUrl(mPosterUrl);
			isSendPoster.setEnabled(true);
		}
		shareWindow.setVisibility(View.VISIBLE);
		if (null != mShareWindowListener) {
			mShareWindowListener.show();
		}
	}

	// 关闭分享窗口并恢复播放
	private void dismiss() {
		shareWindow.setVisibility(View.GONE);
		discussEditText.setText("");
		if (null != mShareWindowListener) {
			mShareWindowListener.dismiss();
		}
	}

	public interface ShareWindowListener {
		void dismiss();

		void show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.discussCancel: // 分享关闭按钮
		case R.id.shareExitBtn: { // 分享关闭按钮
			dismiss();
			InputMethodManager imm = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(discussEditText.getWindowToken(), 0);
			break;
		}
		case R.id.discussSend:
			shareToWeibo();
			break;
		default:
			break;
		}
	}

	/**
	 * 监听评论框输入文字长度，更新字数显示
	 * */
	class DiscussWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String text = discussEditText.getText().toString();
			int fontNum = text.length();
			// 显示评论框已输入评论字数
			discussFontNum.setText(mContext.getString(R.string.dis_num)
					.replace("$num", fontNum + ""));
			if (140 < fontNum) {
				Toast.makeText(mContext, R.string.toast_comment_too_lang,
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	}

	/**
	 * 分享到新浪微博
	 * */
	public void shareToWeibo() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		final String comment = discussEditText.getText().toString();
		if (TextUtils.isEmpty(comment)) {
			Toast.makeText(mContext, R.string.toast_comment_null,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (140 < comment.length()) {
			// 显示评论框已输入评论字数
			Toast.makeText(mContext, R.string.toast_comment_too_lang,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final Oauth2AccessToken accessToken = AccessTokenKeeper
				.readAccessToken(mContext);
		if (null == accessToken || TextUtils.isEmpty(accessToken.getToken())) {
			WeiboUtil.bindWeibo(mContext);
			return;
		}
		if (isSendPoster.isChecked()) {
			final String poster = mPosterUrl;
			// try {
			// poster = mPosterUrl;
			//
			// // poster = Constant.ROOT_ADDR + "imgCache" + "/"
			// // + mPosterUrl.substring(mPosterUrl.lastIndexOf("/") + 1);
			// } catch (Exception e) {
			// }
			// 如果获取到图片
			if (!TextUtils.isEmpty(poster)) {
				ImageLoader.getInstance().loadImage(poster,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								if (loadedImage != null) {
									byte[] imagePoster = Bitmap2Bytes(loadedImage);
									shareWithPic(comment, poster,
											accessToken.getToken(), imagePoster);
								}
							}
						});
			} else {
				shareWithOutPic(comment, accessToken.getToken());
			}
		} else {
			shareWithOutPic(comment, accessToken.getToken());
		}
	}

	// 带图片的分享
	private void shareWithPic(final String comment, final String picFilePath,
			final String accessToken, final byte[] imageposter) {
		new AsyncTask<Void, Void, JSONObject>() {
			protected void onPreExecute() {
				if (null != mProgressDialog) {
					mProgressDialog.show();
				}
			};

			@Override
			protected JSONObject doInBackground(Void... params) {
				return new WeiboAction().shareWithPic(comment, picFilePath, "",
						"", accessToken, imageposter);
			};

			@Override
			protected void onPostExecute(JSONObject result) {
				doWithShareResult(comment, result);
			}
		}.execute();
	}

	// 不带图片的分享
	private void shareWithOutPic(final String comment, final String accessToken) {
		new AsyncTask<Void, Void, JSONObject>() {
			protected void onPreExecute() {
				if (null != mProgressDialog) {
					mProgressDialog.show();
				}
			};

			@Override
			protected JSONObject doInBackground(Void... params) {
				return new WeiboAction().shareWithOutPic(comment, "", "",
						accessToken);
			};

			@Override
			protected void onPostExecute(JSONObject result) {
				doWithShareResult(comment, result);
			}
		}.execute();
	}

	private void doWithShareResult(final String comment, JSONObject result) {
		int toastResourceId = R.string.share_failed;
		if (null == result) {
			if (null != mProgressDialog) {
				mProgressDialog.dismiss();
			}
			Toast.makeText(mContext, toastResourceId, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		try {
			String idstr = result.getString("idstr");
			if (!TextUtils.isEmpty(idstr)) {
				// 记录分享历史
				addShareResource(comment);
				toastResourceId = R.string.share_sucess;
				dismiss();
			}
		} catch (JSONException e) {
			try {
				int errorCode = result.getInt("error_code");
				if (20019 == errorCode) {
					toastResourceId = R.string.share_failed_error;
				}
			} catch (JSONException exception) {
			}
		} finally {
			if (null != mProgressDialog) {
				mProgressDialog.dismiss();
			}
			Toast.makeText(mContext, toastResourceId, Toast.LENGTH_SHORT)
					.show();
		}
	};

	/**
	 * 发布共享资源
	 * */
	private void addShareResource(String shareContent) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				return new UserAction().addUserShare(
						InterfaceUrls.ADD_USER_SHARE, session.getUserCode(),
						session.getUserName(), objType, objID);
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != result) {
					LogUtils.trace(Log.DEBUG, TAG, result.getRetInfo());
				} else {
					LogUtils.trace(Log.DEBUG, TAG, "addShareResource failed!");
				}
			};
		}.execute();
	}

	public boolean isShow() {
		if (View.VISIBLE == shareWindow.getVisibility()) {
			return true;
		}
		return false;
	}

	/**
	 * 把Bitmap转Byte
	 * 
	 * @Author HEH
	 * @EditTime 2010-07-19 上午11:45:56
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}