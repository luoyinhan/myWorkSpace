package com.unitend.udrm.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.activity.R;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.CommentAction;
import com.coship.ott.transport.action.WeiboAction;
import com.coship.ott.transport.dto.AddCommentJson;
import com.coship.ott.transport.dto.comment.Comment;
import com.coship.ott.transport.dto.comment.CommentsJson;
import com.coship.ott.transport.dto.user.User;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.AccessTokenKeeper;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.utils.WeiboUtil;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;
import com.weibo.sdk.android.Oauth2AccessToken;

/**
 * 评论信息弹出窗口
 * */
public class CommentPopupWindow extends PopupWindow implements OnClickListener {
	public final static String TAG = "CommentPopupWindow";
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	// 根据媒资取评论信息的媒资resourceCode
	private String mResourceCode;
	private CommentAdapter commentAdapter;
	private ScrollLoader commentsLoader;
	private ListView commentsList;
	// 评论每次获取数量
	private static final int COMMENTS_PAGE_SIZE = 20;
	// 评论窗口输入框组件
	private EditText commentEditText = null;
	// 分享字数显示
	private TextView discussFontNum = null;
	private ProgressBar mPopProgress;
	private CheckBox isSendToSina;

	public CommentPopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 创建popupWindow
	public CommentPopupWindow(Context context, View contentView,
			String resourceCode) {
		super(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		this.mContext = context;
		this.mResourceCode = resourceCode;
		this.mLayoutInflater = LayoutInflater.from(mContext);

		this.setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.pop_bg));
		this.setOutsideTouchable(true);
		this.setFocusable(true);
		commentEditText = (EditText) contentView
				.findViewById(R.id.discussEditText);
		commentEditText.addTextChangedListener(new DiscussWatcher());
		// 显示评论框已输入评论字数
		discussFontNum = (TextView) contentView.findViewById(R.id.fontNum);
		discussFontNum.setText(mContext.getString(R.string.dis_num).replace(
				"$num", 0 + ""));
		// 是否同步到新浪微博
		isSendToSina = (CheckBox) contentView.findViewById(R.id.isSendToSina);
		// 发表评论按钮
		contentView.findViewById(R.id.publishComment).setOnClickListener(this);
		// 初始化popupWindow上的listView
		commentsList = (ListView) contentView
				.findViewById(R.id.popDiscussesList);
		commentAdapter = new CommentAdapter();
		commentsList.setAdapter(commentAdapter);
		commentsLoader = new ScrollLoader(mContext, commentsList,
				new CallBack() {
					@Override
					public void loadData(int pageNo) {
						getCommentByAssetId(pageNo);
					}
				});
		// 加载中
		mPopProgress = (ProgressBar) contentView
				.findViewById(R.id.popLoadingBar);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.publishComment:
			addComment();
			break;
		default:
			break;
		}
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		getCommentByAssetId(1);
	}

	/**
	 * 获取评论信息
	 * */
	private void getCommentByAssetId(final int pageNo) {
		if (1 == pageNo) {
			commentAdapter.removeAllDatas();
		}
		// 取当前播放媒资相关评论信息
		new AsyncTask<Void, Void, CommentsJson>() {
			protected void onPreExecute() {
				if (null != mPopProgress) {
					mPopProgress.setVisibility(View.VISIBLE);
				}
			};

			@Override
			protected CommentsJson doInBackground(Void... params) {
				return new CommentAction().getCommentByAssetId(
						InterfaceUrls.GET_COMMENT_BY_ASSETID, 1, mResourceCode,
						COMMENTS_PAGE_SIZE, pageNo);
			}

			@Override
			protected void onPostExecute(CommentsJson result) {
				if (null != mPopProgress) {
					mPopProgress.setVisibility(View.GONE);
				}
				if (null != result && 0 == result.getRet()) {
					ArrayList<Comment> comments = result.getComments();
					if (null == comments || 0 >= comments.size()) {
						return;
					}
					// 初始化评论数据列表
					commentAdapter.addNewDatas(comments);
					commentsLoader.setCurPage(result.getCurPage());
					commentsLoader.setPageCount(result.getPageCount());
				} else {
					Toast.makeText(mContext, "获取评论失败，请稍后再试！", Toast.LENGTH_LONG)
							.show();
				}
			}
		}.execute();
	}

	/**
	 * 定义一个电影信息表格视图结果列表项
	 */
	public final class ItemHolder {
		public CustormImageView itemImage;
		public TextView itemName;
		public TextView itemTime;
		public TextView itemDesc;
		public ImageView selectTag;
	}

	// 评论数据适配器
	class CommentAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder = null;
			if (convertView == null) {
				holder = new ItemHolder();
				convertView = mLayoutInflater.inflate(R.layout.discusser_item,
						null);
				holder.itemImage = (CustormImageView) convertView
						.findViewById(R.id.itemDiscusserPhoto);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.itemDiscusserName);
				holder.itemTime = (TextView) convertView
						.findViewById(R.id.itemDiscussTime);
				holder.itemDesc = (TextView) convertView
						.findViewById(R.id.itemDiscusserDesc);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}

			Comment comment = (Comment) datas.get(position);
			if (null == comment) {
				return convertView;
			}
			User user = comment.getUser();
			if (null != user) {
				String logo = "";
				logo = user.getLogo();
				if (TextUtils.isEmpty(logo)) {
					ArrayList<Poster> posters = comment.getPoster();
					if (null != posters && 0 < posters.size()) {
						Poster poster = posters.get(0);
						if (null != poster
								&& !TextUtils.isEmpty(poster.getLocalPath())) {
							logo = poster.getLocalPath();
						}
					}
				}
				holder.itemName.setText(user.getNickName());
				holder.itemImage.setImageHttpUrl(logo);
			}
			holder.itemTime.setText(comment.getCreatTime());
			holder.itemDesc.setText(comment.getComment());
			return convertView;
		}
	}

	/**
	 * 监听评论框输入文字长度，更新字数显示
	 * */
	class DiscussWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String text = commentEditText.getText().toString();
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

	private void addComment() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		final String comment = commentEditText.getText().toString();
		if (TextUtils.isEmpty(comment)) {
			Toast.makeText(mContext, R.string.toast_comment_null,
					Toast.LENGTH_LONG).show();
			return;
		}
		if (140 < comment.length()) {
			// 显示评论框已输入评论字数
			Toast.makeText(mContext, R.string.toast_comment_too_lang,
					Toast.LENGTH_LONG).show();
			return;
		}
		if (isSendToSina.isChecked()) {
			Oauth2AccessToken accessToken = AccessTokenKeeper
					.readAccessToken(mContext);
			if (null == accessToken
					|| TextUtils.isEmpty(accessToken.getToken())) {
				WeiboUtil.bindWeibo(mContext);
				return;
			} else {
				shareWithOutPic(comment, accessToken.getToken());
			}
		} else {
			userComment(comment);
		}
	}

	// 带图片的分享
	private void shareWithOutPic(final String comment, final String accessToken) {
		new AsyncTask<Void, Void, JSONObject>() {
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
			if (null != mPopProgress) {
				mPopProgress.setVisibility(View.GONE);
			}
			Toast.makeText(mContext, toastResourceId, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		try {
			String idstr = result.getString("idstr");
			if (!TextUtils.isEmpty(idstr)) {
				// 记录用户评论历史
				userComment(comment);
				toastResourceId = R.string.share_sucess;
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
			Toast.makeText(mContext, toastResourceId, Toast.LENGTH_SHORT)
					.show();
		}
	};

	// 提交评论内容
	private void userComment(final String comment) {
		new AsyncTask<Void, Void, AddCommentJson>() {
			protected void onPreExecute() {
				if (null != mPopProgress) {
					mPopProgress.setVisibility(View.VISIBLE);
				}
			};

			@Override
			protected AddCommentJson doInBackground(Void... params) {
				String commentUtf8 = comment;
				try {
					commentUtf8 = URLEncoder.encode(comment, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
				// 直播的评论
				return new CommentAction().userComment(
						InterfaceUrls.ADD_USER_COMMENT, Session.getInstance()
								.getUserCode(), 1, mResourceCode, commentUtf8,
						0);

			};

			@Override
			protected void onPostExecute(AddCommentJson result) {
				if (null != mPopProgress) {
					mPopProgress.setVisibility(View.GONE);
				}
				if (null != result && 0 == result.getRet()) {
					commentEditText.setText("");
					if (result.getAuditStatus().equals("2")) {
						Toast.makeText(
								mContext,
								mContext.getString(R.string.toast_comment_success),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								mContext,
								mContext.getString(R.string.toast_comment_verify),
								Toast.LENGTH_SHORT).show();
					}
					getCommentByAssetId(1);
				} else {
					Toast.makeText(mContext,
							mContext.getString(R.string.toast_comment_failed),
							Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}
}