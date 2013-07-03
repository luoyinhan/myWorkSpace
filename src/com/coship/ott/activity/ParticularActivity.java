package com.coship.ott.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.CommentAction;
import com.coship.ott.transport.action.FavoriteAction;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.AddCommentJson;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.comment.Comment;
import com.coship.ott.transport.dto.comment.CommentsJson;
import com.coship.ott.transport.dto.user.User;
import com.coship.ott.transport.dto.vod.AssetDetailJson;
import com.coship.ott.transport.dto.vod.AssetInfo;
import com.coship.ott.transport.dto.vod.AssetListInfo;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.dto.vod.ProductInfo;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.AccessTokenKeeper;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.DbHelper;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.PlayerUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.utils.WeiboUtil;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;
import com.unitend.udrm.ui.ShareWindow;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 点播内容详情页
 */
public class ParticularActivity extends Activity implements OnClickListener {
	// 日志标识
	public final static String TAG = "ParticularActivity";
	private Context mContext = null;
	private LayoutInflater mInflater;
	// 页面第二排按钮列表
	private List<TextView> tabs = new ArrayList<TextView>();
	// 页面第二排按钮内容列表
	private List<View> tabContents = new ArrayList<View>();
	// 品牌节目关联推荐适配器
	private MayBeLikeAdapter recommandAssetsAdapter;
	private ScrollLoader loader;
	// 评论页面输入框
	private EditText discussEditText = null;
	// 评论字数显示
	private TextView discussFontNum = null;
	// 评论每次获取数量
	private static final int COMMENTS_PAGE_SIZE = 20;
	// 评论列表框
	private ListView parDiscussesList = null;
	private CommentsAdapter commentsAdapter;
	private ScrollLoader commentsLoader;
	// 焦点所在位置
	private int currFocusIndex = -1;
	private GridView chapterView;
	// 资源类型：0 单片：1 资源包
	private int type;
	// 当前页显示资源resourceCode
	private String resourceCode;
	// 当前剧集
	private AssetListInfo currentChapterAsset;
	// 当前剧集resourceCode
	private String currentResourceCode;
	private AssetInfo assetInfo;
	private ChapterContentAdapter chapterAdapter;
	// 播放按钮
	private Button btnPlay;
	private String posterUrl;
	private Handler handler = new Handler();
	private int mSelectItem = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.particular_vod);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		mInflater = LayoutInflater.from(mContext);

		type = this.getIntent().getIntExtra("type", 1);
		resourceCode = this.getIntent().getStringExtra("resourceCode");
		currentResourceCode = this.getIntent().getStringExtra(
				"currentResourceCode");
		if (0 == type && TextUtils.isEmpty(resourceCode)) {
			resourceCode = currentResourceCode;
		}
		setupView();
		// 初始化影片信息
		initMsg();
		// 初始化关联推荐列表
		recommandAssetsAdapter.removeAllDatas();
		initRecommendRelResource(1);
		// 初始化评论列表信息
		getCommentByAssetId(1);
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(discussEditText.getWindowToken(), 0);

	}

	/**
	 * 获取页面元素ID
	 * */
	private void setupView() {
		// 初始化页面
		tabs = new ArrayList<TextView>();
		tabs.add((TextView) this.findViewById(R.id.particularChapter));
		tabs.add((TextView) this.findViewById(R.id.particularAssetDesc));
		tabs.add((TextView) this.findViewById(R.id.particularDiscusses));
		for (TextView textView : tabs) {
			textView.setOnClickListener(this);
		}

		// 设置按钮列表
		chapterView = (GridView) findViewById(R.id.parChapterView);
		chapterAdapter = new ChapterContentAdapter();
		chapterView.setAdapter(chapterAdapter);
		chapterView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 标记当前选择频道
				mSelectItem = arg2;
				chapterAdapter.notifyDataSetChanged();
				AssetListInfo chapterAsset = (AssetListInfo) chapterAdapter
						.getItemData(arg2);
				String posterUrl = "";
				ArrayList<Poster> posters = assetInfo.getPosterInfo();
				if (null != posters && posters.size() > 0) {
					Poster poster = posters.get(0);
					if (null != poster) {
						posterUrl = poster.getLocalPath();
					}
				}
				PlayerUtil.playVod(mContext, chapterAsset.getResourceCode(),
						assetInfo.getVideoType(), chapterAsset.getAssetName(),
						posterUrl, assetInfo.getAssetID(),
						assetInfo.getProviderID());
			}
		});
		tabContents.add(chapterView);// 剧集内容
		tabContents.add(this.findViewById(R.id.resumeScorller));// 显示的内容
		tabContents.add(this.findViewById(R.id.parConDiscusses));
		// 显示详细信息
		// initPage(0);
		// 播放按钮
		btnPlay = (Button) this.findViewById(R.id.particularPlay);
		btnPlay.requestFocus();
		btnPlay.setOnClickListener(this);
		// 加入收藏按钮及按钮事件处理
		this.findViewById(R.id.particularAddFav).setOnClickListener(this);
		// 分享按钮及按钮事件处理
		this.findViewById(R.id.parShare).setOnClickListener(this);
		// 返回按钮及事件处理
		this.findViewById(R.id.pCularTopBack).setOnClickListener(this);
		// 是否同步到新浪微博单选框后面的文字，增加单选框可以响应点击的范围
		this.findViewById(R.id.sendToSina).setOnClickListener(this);
		// 评论框
		discussEditText = (EditText) this.findViewById(R.id.discussEditText);
		discussEditText.addTextChangedListener(new DiscussWatcher());
		// 显示评论框已输入评论字数
		discussFontNum = (TextView) findViewById(R.id.fontNum);
		discussFontNum.setText(getString(R.string.dis_num).replace("$num",
				0 + ""));
		// 发表评论按钮注册监听
		findViewById(R.id.publishComment).setOnClickListener(this);
		// 评论页面评论内容列表
		parDiscussesList = (ListView) this.findViewById(R.id.parDiscussesList);
		commentsAdapter = new CommentsAdapter();
		parDiscussesList.setAdapter(commentsAdapter);
		commentsLoader = new ScrollLoader(mContext, parDiscussesList,
				new CallBack() {
					@Override
					public void loadData(int pageNo) {
						getCommentByAssetId(pageNo);
					}
				});
		// 关联推荐
		ListView resourceList = (ListView) findViewById(R.id.like_listview);
		recommandAssetsAdapter = new MayBeLikeAdapter();
		resourceList.setAdapter(recommandAssetsAdapter);
		resourceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				AssetListInfo asset = (AssetListInfo) recommandAssetsAdapter
						.getItemData(arg2);
				Intent intent = new Intent(mContext, ParticularActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("resourceType", asset.getType());
				intent.putExtra("resourceCode", asset.getResourceCode());
				startActivity(intent);
			}
		});
		loader = new ScrollLoader(mContext, resourceList, new CallBack() {
			@Override
			public void loadData(int pageNo) {
				initRecommendRelResource(pageNo);
			}
		});
	}

	/**
	 * 构建页面
	 * */
	private void initPage(int index) {
		if (index == this.currFocusIndex) { // 如果是当前所中项，则直接返回
			return;
		}

		View view = null;
		TextView textView = null;
		for (int i = 0, len = tabs.size(); i < len; i++) {
			textView = tabs.get(i);
			view = tabContents.get(i);
			if (i == index) {
				view.setVisibility(View.VISIBLE);
				textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.tab_index_tag);
				this.currFocusIndex = i;
			} else {
				view.setVisibility(View.GONE);
				textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}
		}
	}

	/**
	 * 信息初始化
	 * */
	private void initMsg() {
		// 初始化电影基本信息
		new AsyncTask<Void, Void, AssetDetailJson>() {

			@Override
			protected AssetDetailJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getAssetDetail(
						InterfaceUrls.GET_ASSETDETAIL, resourceCode,
						session.getUserCode());
			};

			@Override
			protected void onPostExecute(AssetDetailJson result) {
				if (null != result && 0 == result.getRet()) {
					assetInfo = result.getAssetInfo();
					if (assetInfo == null) {
						return;
					}
					initMovieDetail();
					// 剧集按钮
					View view = findViewById(R.id.particularChapter);
					if (0 == assetInfo.getType()) {
						view.setVisibility(View.GONE);
						initPage(1);// 显示详情
					} else {// 电视剧集
						view.setVisibility(View.VISIBLE);
						initPage(0);
						Log.e("start Time----", "" + System.currentTimeMillis());
						initChapters();
					}
				}
			};
		}.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Session session = Session.getInstance();
		if (!TextUtils.isEmpty(session.getUserCode())) {
			DbHelper dbhelper = new DbHelper(mContext);
			boolean result = dbhelper.queryData(session.getUserCode(),
					resourceCode);
			if (result) {
				findViewById(R.id.particularAddFav).setBackgroundResource(
						R.drawable.collected);
			}
			dbhelper.closeConn();
		}
	}

	/**
	 * 信息初始化
	 * */
	private void initChapters() {
		// 初始化电影基本信息
		new AsyncTask<Void, Void, AssetListJson>() {

			@Override
			protected AssetListJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getAssetListByPackageCode(
						InterfaceUrls.GET_ASSETLIST_BY_PACKAGECODE,
						Integer.MAX_VALUE, 1, resourceCode,
						session.getUserCode());
			};

			@Override
			protected void onPostExecute(AssetListJson result) {
				if (null != result && 0 == result.getRet()) {
					chapterAdapter.addNewDatas(result.getAssetList());
					if (result.getAssetList() != null
							&& result.getAssetList().size() > 0) {
						currentChapterAsset = result.getAssetList().get(0);
						Log.e("end Time----", "" + System.currentTimeMillis());
					}
				}
			};
		}.execute();
	}

	/**
	 * 初始化点播电影基本信息
	 * */
	private void initMovieDetail() {
		// 海报
		CustormImageView pCularAssetIcon = (CustormImageView) this
				.findViewById(R.id.pCularAssetIcon);
		if (assetInfo == null) {
			return;
		}
		ArrayList<Poster> posters = assetInfo.getPosterInfo();
		if (null != posters && 0 < posters.size()) {
			Poster poster = posters.get(0);
			if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
				posterUrl = poster.getLocalPath();
			}
		}
		pCularAssetIcon.setImageHttpUrl(posterUrl);
		// 影片名称
		TextView pCularAssetName = (TextView) this.findViewById(R.id.assetName);
		pCularAssetName.setText(assetInfo.getAssetName());
		// 上映时间
		TextView movie_time = (TextView) this.findViewById(R.id.movie_time);
		TextView pCularAssetPDate = (TextView) this
				.findViewById(R.id.pCularAssetPDate);
		if (TextUtils.isEmpty(assetInfo.getPublishDate())) {
			movie_time.setVisibility(View.INVISIBLE);
		}
		pCularAssetPDate.setText(assetInfo.getPublishDate());
		// 导演
		TextView movie_director = (TextView) this
				.findViewById(R.id.movie_director);
		TextView pCularAssetDirector = (TextView) this
				.findViewById(R.id.pCularAssetDirector);
		if (TextUtils.isEmpty(assetInfo.getDirector())) {
			movie_director.setVisibility(View.INVISIBLE);
		}
		pCularAssetDirector.setText(assetInfo.getDirector());
		// 主演
		TextView movie_screenwriter = (TextView) this
				.findViewById(R.id.movie_screenwriter);
		TextView pCularAssetActors = (TextView) this
				.findViewById(R.id.pCularAssetActors);
		if (TextUtils.isEmpty(assetInfo.getLeadingActor())) {
			movie_screenwriter.setVisibility(View.INVISIBLE);
		}
		pCularAssetActors.setText(assetInfo.getLeadingActor());
		// 影片类型
		TextView pmovie_type = (TextView) this.findViewById(R.id.movie_type);
		TextView pCularAssetTypes = (TextView) this
				.findViewById(R.id.pCularAssetTypes);
		if (TextUtils.isEmpty(assetInfo.getAssetTypes())) {
			pmovie_type.setVisibility(View.INVISIBLE);
		}
		pCularAssetTypes.setText(assetInfo.getAssetTypes());
		// 影片简介
		TextView pCularAssetResume = (TextView) this
				.findViewById(R.id.pCularAssetResume);
		pCularAssetResume.setText(assetInfo.getSummaryLong());
		// 高标清标识
		ImageView gaoqingTag = (ImageView) this.findViewById(R.id.gaoqingTag);
		if (1 == assetInfo.getVideoType()) {
			gaoqingTag.setVisibility(View.VISIBLE);
		} else {
			gaoqingTag.setVisibility(View.INVISIBLE);
		}
		// 价格标识
		ProductInfo product = assetInfo.getProduct();
		TextView priceTag = (TextView) this.findViewById(R.id.priceTag);
		if (null == product) {
			priceTag.setVisibility(View.GONE);
			return;
		}
		int price = product.getProductPrice();
		if (0 < price) {
			priceTag.setText((float) price / (float) 100 + "元");
			priceTag.setVisibility(View.VISIBLE);
		} else {
			priceTag.setVisibility(View.GONE);
		}
	}

	/**
	 * 单击事件监听
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.particularAssetDesc:
			initPage(1);
			break;
		case R.id.particularChapter:
			initPage(0);
			break;
		case R.id.particularDiscusses:
			initPage(2);
			break;
		case R.id.particularPlay:
			// 书签续播
			if (assetInfo == null) {
				return;
			}
			// 电视剧资源包
			if (1 == type && null != currentChapterAsset) {
				// 播放第一集
				PlayerUtil.playVod(mContext,
						currentChapterAsset.getResourceCode(),
						assetInfo.getVideoType(),
						currentChapterAsset.getAssetName(), posterUrl,
						assetInfo.getAssetID(), assetInfo.getProviderID());
			} else {
				PlayerUtil.playVod(mContext, resourceCode,
						assetInfo.getVideoType(), assetInfo.getAssetName(),
						posterUrl, assetInfo.getAssetID(),
						assetInfo.getProviderID());
			}
			break;
		case R.id.particularAddFav:
			Session session = Session.getInstance();
			// 同时存入本地数据库中
			DbHelper dbhelper = new DbHelper(mContext);
			boolean result = dbhelper.queryData(session.getUserCode(),
					resourceCode);
			if (!result) {
				boolean isSuccess = dbhelper.insertData(session.getUserCode(),
						resourceCode);
				if (isSuccess) {
					addFavourite(resourceCode);
				}
			} else {
				Toast.makeText(mContext, getString(R.string.collected),
						Toast.LENGTH_SHORT).show();
			}
			dbhelper.closeConn();// 关闭连接
			break;
		case R.id.publishComment:
			addUserComment();
			break;
		case R.id.parShare: // 站内分享，添加分享资源
			try {
				RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.assetParView);
				ShareWindow shareWindow = new ShareWindow(rootLayout, mContext,
						null);
				shareWindow.show(assetInfo.getAssetName(), posterUrl, 2,
						resourceCode);
			} catch (Throwable e) {
			}
			break;
		case R.id.pCularTopBack:
			onBackPressed();
			break;
		case R.id.sendToSina:
			// 是否分享到新浪微博
			CheckBox isSendToSina = (CheckBox) findViewById(R.id.isSendToSina);
			if (isSendToSina.isChecked()) {
				isSendToSina.setChecked(false);
			} else {
				isSendToSina.setChecked(true);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 关联节目数据适配器
	 */
	public class ChapterContentAdapter extends CommonAdapter {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			AssetListInfo chapterAsset = (AssetListInfo) datas.get(position);
			String chapterStr = (position + 1) + "";
			if (9 > position) {
				chapterStr = "0" + chapterStr;
			}
			TextView chapterView = new TextView(mContext);
			chapterView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// 设置按钮样式
			if (mSelectItem == position) {
				chapterView.setBackgroundResource(R.drawable.chapter_focus);
				currentChapterAsset = chapterAsset;
			} else {
				chapterView.setGravity(Gravity.CENTER);
				chapterView.setTextColor(Color.parseColor("#484848"));
				chapterView.setTextSize(16);
				chapterView.setText(chapterStr);
				chapterView.setBackgroundResource(R.drawable.chapter);
			}
			return chapterView;
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
			if (140 >= fontNum) {
				// 显示评论框已输入评论字数
				discussFontNum.setText(getString(R.string.dis_num).replace(
						"$num", text.length() + ""));
			} else {
				Toast.makeText(mContext, R.string.toast_comment_too_lang,
						Toast.LENGTH_SHORT).show();
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
	 * 发表评论
	 * */
	private void addUserComment() {
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
		// 是否分享到新浪微博
		CheckBox isSendToSina = (CheckBox) findViewById(R.id.isSendToSina);
		if (isSendToSina.isChecked()) {
			Oauth2AccessToken accessToken = AccessTokenKeeper
					.readAccessToken(mContext);
			if (null == accessToken
					|| TextUtils.isEmpty(accessToken.getToken())) {
				WeiboUtil.bindWeibo(mContext);
				return;
			} else {
				String commentStr = mContext.getString(
						R.string.comment_weibo_msg).replace("$name",
						assetInfo.getAssetName())
						+ comment;
				doAddUserComment(comment);
				StatusesAPI api = new StatusesAPI(accessToken);
				api.update(commentStr, "", "", new RequestListener() {
					@Override
					public void onComplete(String response) {
						LogUtils.trace(Log.DEBUG, TAG, response);
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(
										mContext,
										getString(R.string.toast_weibo_success),
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onIOException(IOException e) {

					}

					@Override
					public void onError(WeiboException e) {
						LogUtils.trace(Log.DEBUG, TAG, e.getMessage());
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(
										mContext,
										getString(R.string.toast_comment_failed),
										Toast.LENGTH_SHORT).show();
							}
						});

					}
				});
			}
		} else {
			doAddUserComment(comment);
		}
	}

	private void doAddUserComment(final String comment) {
		// 提交评论内容
		new AsyncTask<Void, Void, AddCommentJson>() {
			@Override
			protected AddCommentJson doInBackground(Void... params) {
				String commentUtf8 = comment;
				try {
					commentUtf8 = URLEncoder.encode(comment, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
				return new CommentAction()
						.userComment(InterfaceUrls.ADD_USER_COMMENT, Session
								.getInstance().getUserCode(), 2, resourceCode,
								commentUtf8, 0);
			};

			@Override
			protected void onPostExecute(AddCommentJson result) {
				if (null != result && 0 == result.getRet()) {
					discussEditText.setText("");
					if (result.getAuditStatus().equals("2")) {
						Toast.makeText(mContext,
								getString(R.string.toast_comment_success),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext,
								getString(R.string.toast_comment_verify),
								Toast.LENGTH_SHORT).show();
					}
					getCommentByAssetId(1);
				} else if (result != null) {
					Toast.makeText(mContext, result.getRetInfo(),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext,
							getString(R.string.toast_comment_failed),
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
	}

	/**
	 * 获取关联推荐影片
	 * */
	private void initRecommendRelResource(final int pageNo) {
		new AsyncTask<Void, Void, AssetListJson>() {
			@Override
			protected AssetListJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getRelateAsset(
						InterfaceUrls.GET_RELATEASSET, resourceCode,
						session.getUserCode());
			};

			@Override
			protected void onPostExecute(AssetListJson result) {
				if (null != result && 0 == result.getRet()) {
					// 删除重复的数据
					ArrayList<AssetListInfo> assetLists = new ArrayList<AssetListInfo>();
					for (AssetListInfo info : result.getAssetList()) {
						if (!resourceCode.equals(info.getResourceCode())) {
							assetLists.add(info);
						}
					}
					recommandAssetsAdapter.addNewDatas(assetLists);
					loader.setCurPage(result.getCurPage());
					loader.setPageCount(result.getPageCount());
				}
			}

		}.execute();
	}

	/**
	 * 关联节目适配器缓存
	 */
	public final class ViewGridViewHolder {
		public ImageView gaoqingTag;
		public CustormImageView assetPoster;
		public TextView assetName;
		public TextView assetAnticipation;
	}

	/**
	 * 关联节目数据适配器
	 */
	public class MayBeLikeAdapter extends CommonAdapter {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewGridViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewGridViewHolder();
				convertView = mInflater.inflate(
						R.layout.poster_name_anticip_item, null);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.assetPoster = (CustormImageView) convertView
						.findViewById(R.id.assetPoster);
				holder.assetName = (TextView) convertView
						.findViewById(R.id.assetName);
				holder.assetAnticipation = (TextView) convertView
						.findViewById(R.id.assetAnticipation);
				convertView.setTag(holder);

			} else {
				holder = (ViewGridViewHolder) convertView.getTag();
			}

			AssetListInfo asset = (AssetListInfo) datas.get(position);
			String imagePath = "";
			ArrayList<Poster> posters = asset.getPosterInfo();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			holder.assetPoster.setImageHttpUrl(imagePath);
			holder.assetName.setText(asset.getAssetName());
			String time = asset.getPublishDate();
			if (!TextUtils.isEmpty(time)) {

				holder.assetAnticipation.setText(time);
			}
			if (1 == asset.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	/**
	 * 获取评论信息
	 * */
	private void getCommentByAssetId(final int pageNo) {
		if (1 == pageNo) {
			commentsAdapter.removeAllDatas();
		}
		// 取当前播放媒资相关评论信息
		new AsyncTask<String, Void, CommentsJson>() {
			@Override
			protected CommentsJson doInBackground(String... params) {
				return new CommentAction().getCommentByAssetId(
						InterfaceUrls.GET_COMMENT_BY_ASSETID, 2, resourceCode,
						COMMENTS_PAGE_SIZE, pageNo);
			}

			@Override
			protected void onPostExecute(CommentsJson result) {
				if (null != result && 0 == result.getRet()) {
					// 可能评论条数为0，所以放在下面判断前处理
					TextView textView = (TextView) findViewById(R.id.particularDiscusses);
					textView.setText(getString(R.string.discusses) + "("
							+ result.getRetCount() + ")");
					ArrayList<Comment> comments = result.getComments();
					if (null == comments || 0 >= comments.size()) {
						return;
					}
					ArrayList<Comment> newcomments = new ArrayList<Comment>();
					for (Comment info : comments) {
						if (info != null) {
							newcomments.add(info);
						}
					}
					// 初始化评论数据列表
					commentsAdapter.addNewDatas(newcomments);
					commentsLoader.setCurPage(result.getCurPage());
					commentsLoader.setPageCount(result.getPageCount());
				} else {
					Toast.makeText(mContext, "获取评论失败，请稍后再试！",
							Toast.LENGTH_SHORT).show();
				}
			};
		}.execute(resourceCode);
	}

	/**
	 * 评论信息适配器缓存
	 */
	public final class CommentsViewHolder {
		public CustormImageView itemDiscusserPhoto;
		public TextView itemDiscusserName;
		public TextView itemDiscussTime;
		public TextView itemDiscusserDesc;
	}

	/**
	 * 评论信息数据适配器
	 */
	public class CommentsAdapter extends CommonAdapter {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			CommentsViewHolder holder = null;
			if (convertView == null) {
				holder = new CommentsViewHolder();
				convertView = mInflater.inflate(R.layout.discusser_item, null);
				holder.itemDiscusserPhoto = (CustormImageView) convertView
						.findViewById(R.id.itemDiscusserPhoto);
				holder.itemDiscusserName = (TextView) convertView
						.findViewById(R.id.itemDiscusserName);
				holder.itemDiscussTime = (TextView) convertView
						.findViewById(R.id.itemDiscussTime);
				holder.itemDiscusserDesc = (TextView) convertView
						.findViewById(R.id.itemDiscusserDesc);
				convertView.setTag(holder);

			} else {
				holder = (CommentsViewHolder) convertView.getTag();
			}

			Comment comment = (Comment) datas.get(position);
			if (null == comment) {
				return convertView;
			}
			User user = comment.getUser();
			String logo = "";
			holder.itemDiscusserName.setText("");
			holder.itemDiscussTime.setText("");
			holder.itemDiscusserDesc.setText("");
			if (null != user) {
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
				if (!TextUtils.isEmpty(user.getNickName())) {
					holder.itemDiscusserName.setText(user.getNickName());
				} else {
					holder.itemDiscusserName.setText(user.getUsrName());
				}
			}
			holder.itemDiscusserPhoto.setImageHttpUrl(logo);
			holder.itemDiscussTime.setText(comment.getCreatTime());
			holder.itemDiscusserDesc.setText(comment.getComment());
			return convertView;
		}
	}

	/**
	 * 加入收藏夹
	 * */
	private void addFavourite(final String resourceCode) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				return new FavoriteAction().addFavorite(
						InterfaceUrls.ADD_FAVORITE, session.getUserCode(),
						session.getUserName(), resourceCode);
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != result && 0 == result.getRet()) {
					findViewById(R.id.particularAddFav).setBackgroundResource(
							R.drawable.collected);
				} else {
					Toast.makeText(mContext,
							getString(R.string.collect_failed),
							Toast.LENGTH_SHORT).show();
				}
			};
		}.execute();
	}
}