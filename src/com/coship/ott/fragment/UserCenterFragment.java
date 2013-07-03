package com.coship.ott.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.activity.R;
import com.coship.ott.activity.UserCenterTabActivity;
import com.coship.ott.constant.Constant;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.action.WeiboAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.UserInfoJson;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.transport.util.TestingUtil;
import com.coship.ott.utils.AccessTokenKeeper;
import com.coship.ott.utils.Base64;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.AccountAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;

public class UserCenterFragment extends Fragment implements OnClickListener {
	private static final String TAG = "UserCenterFragment";
	private TextView mUserName, mWeibo_address, mUuser_nickname,
			mUser_introduction, mUser_mail, mUser_phone;
	private ImageView mHead_img;
	private Button mBingdweiobt, mChange_message, mChangehead;
	private static int SELECT_PICTURE = 10001;
	private static String IMAGE_PATH = "/sdcard/headimage.jpg";
	private File tempFile;
	private String logo = "";
	private GetUserInfoTask mGetUserInfoTask;
	private UpHeadPictureTask mUpHeadPictureTask;
	private DownLoadHeadPicture mDownLoadHeadPicture;
	private String mUserNameStr;
	private String mNickName, mSign, mEmail, mPhone;
	private static String NickName, Sign, Email, Phone;
	private TableLayout mMessge_change, mUser_messge;
	private ToastUtils mToUtils;
	private static EditText userNickname, userIntroduce, userMail, userPhone;
	private SubmitChangeMessgeTask mSuMtChanMe;
	private ProgressDialog mProgress = null;
	private static final int MSG_UPDATE_WEIBO = 1;
	private String weiboName;
	public static String mName = "";
	public static String mBindDeviceNo = "";
	public static boolean isEditMessage = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 更新已绑定的微博信息
			if (MSG_UPDATE_WEIBO == msg.what) {
				Bundle data = msg.getData();
				weiboName = data.getString("name");
				if (null != mWeibo_address) {
					mWeibo_address.setText(getString(
							R.string.already_bind_weibo).replace("$name",
							weiboName));
				}
				if (null != mBingdweiobt) {
					mBingdweiobt.setText(getActivity().getString(
							R.string.cancel_bind));
				}
				if (null != mProgress) {
					mProgress.dismiss();
				}
			}
		};
	};

	private UserCenterTabActivity mUserCenterTabActivity;

	public void setUserCenterTabActivity(
			UserCenterTabActivity UserCenterTabActivity) {
		this.mUserCenterTabActivity = UserCenterTabActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.user_center_fragment, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupView();
		initDate();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isEditMessage) {
			ChangeUserMessge();
			userNickname.setText(NickName);
			userIntroduce.setText(Sign);
			userMail.setText(Email);
			userPhone.setText(Phone);
		}
	}

	private void initDate() {
		this.tempFile = new File(IMAGE_PATH);
		Session session = Session.getInstance();
		mUserNameStr = session.getUserName();
		NetTransportUtil.getMD5(session.getPassWord());
		mGetUserInfoTask = new GetUserInfoTask(false);
		mGetUserInfoTask.execute();
		mProgress = new ProgressDialog(getActivity());
		mProgress.setMessage("正在加载数据...");
		long uid = (Long) MulScreenSharePerfance.getInstance(getActivity())
				.getValue("weiboUid", "Long");
		initWeiboMsg(uid);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// 初始化用户绑定weibo信息
	private void initWeiboMsg(long uid) {
		final Oauth2AccessToken accessToken = AccessTokenKeeper
				.readAccessToken(getActivity());
		if (0 < uid) {
			getWeiboMsgByUid(uid, accessToken);
		} else {
			AccountAPI accountApi = new AccountAPI(accessToken);
			mProgress.show();
			accountApi.getUid(new RequestListener() {
				@Override
				public void onIOException(IOException e) {
					mProgress.dismiss();
				}

				@Override
				public void onError(WeiboException e) {
					mProgress.dismiss();
				}

				@Override
				public void onComplete(String response) {
					long uid = 0l;
					try {
						JSONObject obj = new JSONObject(response);
						uid = obj.getLong("uid");
					} catch (JSONException e1) {
						mProgress.dismiss();
						return;
					}
					getWeiboMsgByUid(uid, accessToken);
				}
			});
		}
	}

	private void getWeiboMsgByUid(long uid, final Oauth2AccessToken accessToken) {
		UsersAPI api = new UsersAPI(accessToken);
		api.show(uid, new RequestListener() {
			@Override
			public void onIOException(IOException e) {
				mProgress.dismiss();
			}

			@Override
			public void onError(WeiboException e) {
				mProgress.dismiss();
			}

			@Override
			public void onComplete(String response) {
				try {
					JSONObject obj = new JSONObject(response);
					Message msg = Message.obtain();
					msg.what = MSG_UPDATE_WEIBO;
					Bundle data = new Bundle();
					data.putString("name", obj.getString("name"));
					msg.setData(data);
					mHandler.sendMessage(msg);
				} catch (JSONException e1) {
					LogUtils.trace(Log.DEBUG, getTag(), e1.getMessage());
				}
				mProgress.dismiss();
			}
		});
	}

	private void setupView() {
		mUser_messge = (TableLayout) getView().findViewById(R.id.user_messge);
		mMessge_change = (TableLayout) getView().findViewById(
				R.id.messge_change);
		mUserName = (TextView) getView().findViewById(R.id.userName);
		mHead_img = (ImageView) getView().findViewById(R.id.head_img);
		mWeibo_address = (TextView) getView().findViewById(R.id.weibo_address);
		mBingdweiobt = (Button) getView().findViewById(R.id.returnButton);
		mChangehead = (Button) getView().findViewById(R.id.changehead);
		mChange_message = (Button) getView().findViewById(R.id.change_message);
		mUuser_nickname = (TextView) getView().findViewById(R.id.user_nickname);
		mUser_introduction = (TextView) getView().findViewById(
				R.id.user_introduction);
		mUser_mail = (TextView) getView().findViewById(R.id.user_mail);
		mUser_phone = (TextView) getView().findViewById(R.id.user_phone);
		mBingdweiobt.setText("绑定微博");
		mBingdweiobt.setOnClickListener(this);
		mChangehead.setOnClickListener(this);
		mChange_message.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.returnButton:
			if (!TextUtils.isEmpty(weiboName)) {
				cancelBind();
			} else {
				bindWeibo();
			}
			break;
		case R.id.changehead:
			ChangeHead();
			break;
		case R.id.change_message:
			isEditMessage = true;
			ChangeUserMessge();
			break;
		case R.id.go_back:
			isEditMessage = false;
			mUser_messge.setVisibility(View.VISIBLE);
			mMessge_change.setVisibility(View.GONE);
			break;
		case R.id.helpSubmitButton:
			submitChangemessge();
			break;
		case R.id.helpEmptyButton:
			userNickname.setText("");
			userIntroduce.setText("");
			userMail.setText("");
			userPhone.setText("");
			break;
		default:
			break;
		}
	}

	/**
	 * 微博绑定
	 */
	private void bindWeibo() {
		Weibo mWeibo = Weibo.getInstance(Constant.CONSUMER_KEY,
				Constant.REDIRECT_URL);
		mWeibo.accessToken = null;
		mWeibo.authorize(getActivity(), new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values) {
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				long uid = values.getLong("uid");
				MulScreenSharePerfance.getInstance(getActivity()).putValue(
						"weiboUid", uid);
				Oauth2AccessToken accessToken = new Oauth2AccessToken(token,
						expires_in);
				if (accessToken.isSessionValid()) {
					AccessTokenKeeper.keepAccessToken(getActivity(),
							accessToken);
					initWeiboMsg(uid);
					Toast.makeText(getActivity(),
							getString(R.string.bind_success),
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onError(WeiboDialogError e) {
				LogUtils.trace(Log.DEBUG, getTag(), e.getMessage());
				Toast.makeText(getActivity(), getString(R.string.bind_failed),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				LogUtils.trace(Log.DEBUG, getTag(), "Auth cancel");
				Toast.makeText(getActivity(), getString(R.string.bind_failed),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onWeiboException(WeiboException e) {
				LogUtils.trace(Log.DEBUG, getTag(),
						"Auth exception : " + e.getMessage());
				Toast.makeText(getActivity(), getString(R.string.bind_failed),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 解除微博绑定
	 */
	private void cancelBind() {
		final Oauth2AccessToken accessToken = AccessTokenKeeper
				.readAccessToken(getActivity());
		if (null == accessToken || TextUtils.isEmpty(accessToken.getToken())) {
			doWithCancelResult();
			return;
		}
		new AsyncTask<String, Void, JSONObject>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mProgress && !mProgress.isShowing()) {
					mProgress.show();
				}
			}

			@Override
			protected JSONObject doInBackground(String... params) {
				return new WeiboAction().endSession(accessToken.getToken());
			};

			@Override
			protected void onPostExecute(JSONObject result) {
				if (null != mProgress && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				if (null != result) {
					try {
						String error = result.getString("error");
						if (!TextUtils.isEmpty(error)) {
							LogUtils.trace(Log.DEBUG, getTag(),
									result.toString());
							return;
						}
					} catch (JSONException e) {
					}

					try {
						String screen_name = result.getString("screen_name");
						if (!TextUtils.isEmpty(screen_name)) {
							doWithCancelResult();
						}
					} catch (Exception e) {
					}
				}
			}
		}.execute();
	}

	private void doWithCancelResult() {
		CookieSyncManager cookieSyncManager = CookieSyncManager
				.createInstance(getActivity());
		cookieSyncManager.startSync();
		CookieManager.getInstance().setCookie("https://open.weibo.cn/", null);
		CookieManager.getInstance().removeAllCookie();// 清除服务端缓存
		MulScreenSharePerfance.getInstance(getActivity()).putValue("weiboUid",
				0l);
		AccessTokenKeeper.clear(getActivity());

		mWeibo_address.setText(getString(R.string.no_bind_weibo));
		mBingdweiobt.setText("绑定微博");
		weiboName = null;
		Toast.makeText(getActivity(), getString(R.string.cancel_bind_success),
				Toast.LENGTH_SHORT).show();
	}

	// 提交修改用户信息
	private void submitChangemessge() {
		String nickname = userNickname.getText().toString().trim();
		String sign = userIntroduce.getText().toString().trim();
		String email = userMail.getText().toString().trim();
		String phone = userPhone.getText().toString().trim();
		// if ("".equals(nickname) && "".equals(sign) && "".equals(email)
		// && "".equals(phone)) {
		// Toast.makeText(getActivity(), "请正确填写相应内容", Toast.LENGTH_SHORT)
		// .show();
		// return;
		// }
		if (!TextUtils.isEmpty(email) && !TestingUtil.isEmail(email)) {
			Toast.makeText(getActivity(), "请输入正确的邮箱", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (!TextUtils.isEmpty(phone) && !TestingUtil.isMobileNO(phone)) {
			Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		mToUtils = new ToastUtils(getActivity());
		mToUtils.showToastAlong();
		mSuMtChanMe = new SubmitChangeMessgeTask(nickname, sign, email, phone);
		mSuMtChanMe.execute();
	}

	private void ChangeUserMessge() {
		mUser_messge.setVisibility(View.GONE);
		mMessge_change.setVisibility(View.VISIBLE);
		Button go_back = (Button) getView().findViewById(R.id.go_back);
		userNickname = (EditText) getView().findViewById(R.id.userNicknameet);
		userIntroduce = (EditText) getView().findViewById(R.id.userIntroduceet);
		userMail = (EditText) getView().findViewById(R.id.userMailet);
		userPhone = (EditText) getView().findViewById(R.id.userPhoneet);
		Button submitButton = (Button) getView().findViewById(
				R.id.helpSubmitButton);
		Button EmptyButton = (Button) getView().findViewById(
				R.id.helpEmptyButton);
		go_back.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		EmptyButton.setOnClickListener(this);
		userNickname.setText(mNickName);
		userIntroduce.setText(mSign);
		userMail.setText(mEmail);
		userPhone.setText(mPhone);
	}

	private void ChangeHead() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		mUserCenterTabActivity.startActivityForResult(intent, SELECT_PICTURE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_PICTURE) {
			if (data == null) {
				return;
			}
			Bitmap photo = data.getParcelableExtra("data");
			try {
				savaBitmap(photo);
				logo = Base64.encodeBase64File(tempFile);

			} catch (Exception e) {
				e.printStackTrace();
			}
			mUpHeadPictureTask = new UpHeadPictureTask(photo);
			mUpHeadPictureTask.execute();
		}
	}

	/**
	 * 把图片放入到sdCard中
	 * 
	 * @param bitmap
	 */
	public void savaBitmap(Bitmap bitmap) {
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(tempFile);
			// 把Bitmap对象解析成流
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			LogUtils.trace(Log.DEBUG, getTag(), "========>>" + e.getMessage());

		}
	}

	/**
	 * 个人信息获取数据
	 * 
	 * @author admin
	 * 
	 */
	private class GetUserInfoTask extends AsyncTask<Void, Void, UserInfoJson> {
		private boolean isChangeHead = false;

		public GetUserInfoTask(boolean isChangeHead) {
			super();
			this.isChangeHead = isChangeHead;
		}

		@Override
		protected UserInfoJson doInBackground(Void... params) {
			String usernameContent = "";
			try {
				usernameContent = URLEncoder.encode(mUserNameStr, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return new UserCenterAction().getUserInformation(
					InterfaceUrls.GET_USERINFO, usernameContent);
		}

		@Override
		protected void onPostExecute(UserInfoJson result) {
			if (result != null && result.getRet() == 0) {
				mNickName = result.getUserInfo().getNickName();
				mSign = result.getUserInfo().getSign();
				mEmail = result.getUserInfo().getEmail();
				mPhone = result.getUserInfo().getPhone();
				mName = result.getUserInfo().getUserName();
				mBindDeviceNo = result.getUserInfo().getBindDeviceNo();
				String logoUrl = result.getUserInfo().getLogo();
				resetUserMessge();
				if (isChangeHead) {
					getSDCardHeadPicture();
				} else {
					if (null != logoUrl && !logoUrl.equals("")) {
						mDownLoadHeadPicture = new DownLoadHeadPicture();
						mDownLoadHeadPicture.execute(logoUrl);
					} else {
						getSDCardHeadPicture();
					}
				}
			}
		}

		private void getSDCardHeadPicture() {
			// TODO Auto-generated method stub
			if (tempFile.exists()) {
				try {
					mHead_img.setBackgroundDrawable(Drawable
							.createFromPath(IMAGE_PATH));
					mHead_img.setImageDrawable(Drawable
							.createFromPath(IMAGE_PATH));
				} catch (Exception e) {
					LogUtils.trace(Log.INFO, getTag(),
							"====e=======>>" + e.getMessage());
				}
			}
		}

		private void resetUserMessge() {

			mUserName.setText(mUserNameStr);
			mUuser_nickname.setText(mNickName);
			mUser_introduction.setText(mSign);
			mUser_mail.setText(mEmail);
			mUser_phone.setText(mPhone);
		}
	}

	/**
	 * 加载图片
	 * 
	 */
	private class DownLoadHeadPicture extends AsyncTask<String, Void, Drawable> {
		String logoUrl = "";

		@Override
		protected Drawable doInBackground(String... params) {
			logoUrl = params[0];
			return loadImageFromNetwork(params[0]);
		}

		private Drawable loadImageFromNetwork(String imageUrl) {
			Drawable drawable = null;
			try {
				drawable = Drawable.createFromStream(
						new URL(imageUrl).openStream(), "image.gif");
			} catch (IOException e) {
				LogUtils.trace(Log.DEBUG, getTag(), "" + e.getMessage());
			}
			if (drawable == null) {
				LogUtils.trace(Log.DEBUG, TAG, "null drawable");
			}
			return drawable;
		}

		@Override
		protected void onPostExecute(Drawable drawable) {
			if (drawable == null) {
				Drawable draw = Drawable.createFromPath(tempFile
						.getAbsolutePath());
				mHead_img.setImageDrawable(null);
				mHead_img.setBackgroundDrawable(draw);
			} else {

				mHead_img.setBackgroundDrawable(null);
				mHead_img.setImageDrawable(null);
				mHead_img.setImageDrawable(drawable);
			}
		}

	}

	/**
	 * 个人中心修改资料提交
	 * 
	 */
	private class SubmitChangeMessgeTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private String nickname, sign, email, phone;

		public SubmitChangeMessgeTask(String nickname, String sign,
				String email, String phone) {
			super();
			this.nickname = nickname;
			this.sign = sign;
			this.email = email;
			this.phone = phone;
		}

		@Override
		protected BaseJsonBean doInBackground(Void... params) {

			String usernameContent = "";
			try {
				usernameContent = URLEncoder.encode(mUserNameStr, "UTF-8");
				nickname = URLEncoder.encode(nickname, "UTF-8");
				sign = URLEncoder.encode(sign, "UTF-8");
				email = URLEncoder.encode(email, "UTF-8");
				phone = URLEncoder.encode(phone, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			logo = logo.replace("+", "%2B");
			return new UserCenterAction().modifyUserInfo(
					InterfaceUrls.MODIFY_USER_INFO, usernameContent, nickname,
					logo, sign, email, phone, "");

		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (mToUtils != null) {
				mToUtils.cancel();
			}
			if (null != result && 0 == result.getRet()) {
				mUser_messge.setVisibility(View.VISIBLE);
				mMessge_change.setVisibility(View.GONE);
				GetUserInfoTask mGetUserInfoTask = new GetUserInfoTask(false);
				mGetUserInfoTask.execute();
				Toast.makeText(getActivity(), R.string.change_message_succes,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.change_message_err,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/**
	 * 上传头像
	 * 
	 */
	private class UpHeadPictureTask extends AsyncTask<Void, Void, BaseJsonBean> {
		private Bitmap photo;
		private String nickname, sign, email, phone;

		public UpHeadPictureTask(Bitmap photo) {
			super();
			this.photo = photo;
		}

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			String usernameContent = "";
			BaseJsonBean resultJson = null;
			try {
				usernameContent = URLEncoder.encode(mUserNameStr, "UTF-8");
				nickname = URLEncoder.encode(mNickName, "UTF-8");
				sign = URLEncoder.encode(mSign, "UTF-8");
				email = mEmail;
				phone = mPhone;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			logo = logo.replace("+", "%2B");
			String interfaceName = InterfaceUrls.MODIFY_USER_INFO;
			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append("?version=")
					.append(Constant.DATA_INTERFACE_VERSION)
					.append("&terminalType=").append(Constant.TERMINAL_TYPE)
					.append("&resolution=").append(Constant.RESOLUTION);
			urlBuffer.append("&userName=").append(usernameContent)
					.append("&nickName=").append(nickname).append("&Logo=")
					.append(logo).append("&sign=").append(sign)
					.append("&Email=").append(email).append("&Phone=")
					.append(phone).append("&remark=").append("");
			String paramStr = urlBuffer.toString();
			String requestUrl;
			String authKey = null;
			String url = Constant.SERVER_ADDR + InterfaceUrls.MODIFY_USER_INFO;
			if (-1 != interfaceName.indexOf("http://") // 请求第三方接口
					|| -1 != interfaceName.indexOf("https://")) {
				requestUrl = interfaceName + paramStr;
			} else {
				requestUrl = Constant.SERVER_ADDR + interfaceName + paramStr;
				requestUrl = requestUrl.replace(" ", "%20");
				authKey = NetTransportUtil.getMD5(url + "aidufei");
				requestUrl += ("&authKey=" + authKey);
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("version", Constant.DATA_INTERFACE_VERSION);
			map.put("terminalType", String.valueOf(Constant.TERMINAL_TYPE));
			map.put("resolution", Constant.RESOLUTION);
			map.put("userName", usernameContent);
			map.put("nickName", nickname);
			map.put("Logo", logo);
			map.put("Email", email);
			map.put("Phone", phone);
			map.put("remark", "");
			map.put("authKey", authKey);
			String result = "";
			try {
				result = NetTransportUtil.post(url, map);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			LogUtils.trace(Log.DEBUG, getTag(), "-result>>" + result);
			try {
				Gson gson = new Gson();
				resultJson = gson.fromJson(result,
						new TypeToken<BaseJsonBean>() {
						}.getType());
			} catch (Exception e) {
				LogUtils.trace(Log.DEBUG, getTag(), "方法getFeedBack转换" + result
						+ "为FeedBackJson时出错！");
			}
			return resultJson;
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (result != null) {
				if (result.getRet() == 0) {
					Drawable drawable = Drawable.createFromPath(tempFile
							.getAbsolutePath());
					mHead_img.setImageDrawable(drawable);
					mHead_img.setBackgroundDrawable(drawable);
					Toast.makeText(getActivity(), "更改头像成功", Toast.LENGTH_SHORT)
							.show();
					mGetUserInfoTask = new GetUserInfoTask(true);
					mGetUserInfoTask.execute();
				} else {
					Toast.makeText(getActivity(), result.getRetInfo(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (userNickname != null) {
			NickName = userNickname.getText().toString().trim();
			Sign = userIntroduce.getText().toString().trim();
			Email = userMail.getText().toString().trim();
			Phone = userPhone.getText().toString().trim();
		}
	}

}
