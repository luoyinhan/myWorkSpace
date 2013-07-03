package com.coship.ott.utils;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.coship.ott.constant.Constant;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

public class WeiboUtil {
	// 授权
	public static void bindWeibo(final Context mContext) {
		Weibo mWeibo = Weibo.getInstance(Constant.CONSUMER_KEY,
				Constant.REDIRECT_URL);
		mWeibo.authorize(mContext, new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values) {
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				long uid = values.getLong("uid");
				MulScreenSharePerfance.getInstance(mContext).putValue(
						"weiboUid", uid);
				Oauth2AccessToken accessToken = new Oauth2AccessToken(token,
						expires_in);
				if (accessToken.isSessionValid()) {
					AccessTokenKeeper.keepAccessToken(mContext, accessToken);
					Toast.makeText(mContext, "认证成功", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onError(WeiboDialogError e) {
				Toast.makeText(mContext, "Auth error : " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onCancel() {
				Toast.makeText(mContext, "Auth cancel", Toast.LENGTH_LONG)
						.show();
			}

			@Override
			public void onWeiboException(WeiboException e) {
				Toast.makeText(mContext, "Auth exception : " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
