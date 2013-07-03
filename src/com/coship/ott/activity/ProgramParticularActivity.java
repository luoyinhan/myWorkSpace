package com.coship.ott.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.service.BookNotifyService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.BookAction;
import com.coship.ott.transport.action.CommentAction;
import com.coship.ott.transport.action.LiveAction;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.AddCommentJson;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.book.Book;
import com.coship.ott.transport.dto.comment.Comment;
import com.coship.ott.transport.dto.comment.CommentsJson;
import com.coship.ott.transport.dto.live.Channelbrand;
import com.coship.ott.transport.dto.live.ProgramInfo;
import com.coship.ott.transport.dto.live.ProgramInfoJson;
import com.coship.ott.transport.dto.user.User;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.AccessTokenKeeper;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.PlayerUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.utils.Utility;
import com.coship.ott.utils.WeiboUtil;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;
import com.unitend.udrm.ui.ShareWindow;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 直播节目详情页面
 */
public class ProgramParticularActivity extends Activity implements
		OnClickListener {
	// 日志标识
	public final static String TAG = "LiveParticularActivity";
	private Context mContext = null;
	private LayoutInflater mInflater;
	// 品牌节目关联推荐品牌节目适配器
	private RecommandBrandsAdapter recommandBrandsAdapter;
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
	private String programId;
	private ProgramInfo programInfo;
	private String posterUrl;
	// 播放按钮
	private Button playOrBookBtn;
	private ProgressDialog mProgressDialog;
	private Boolean isloading = false;
	private Boolean isFromNotifyService = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.particular_programe);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		mInflater = LayoutInflater.from(mContext);

		programId = this.getIntent().getStringExtra("programId");
		isFromNotifyService = this.getIntent().getBooleanExtra(
				"isFromNotifyService", false);
		setupView();
		// 初始化影片信息
		initMsg();
		// 初始化关联推荐列表
		recommandBrandsAdapter.removeAllDatas();
		getRecommandBrands(1);
		// 初始化评论列表信息
		getCommentByAssetId(1);
	}

	/**
	 * 获取页面元素ID
	 * */
	private void setupView() {
		// 分享按钮及按钮事件处理
		this.findViewById(R.id.parShare).setOnClickListener(this);
		// 返回按钮注册监听
		this.findViewById(R.id.pCularTopBack).setOnClickListener(this);
		// 播放按钮注册监听
		playOrBookBtn = (Button) this.findViewById(R.id.programePlay);
		playOrBookBtn.setOnClickListener(this);
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
		recommandBrandsAdapter = new RecommandBrandsAdapter();
		resourceList.setAdapter(recommandBrandsAdapter);
		resourceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Channelbrand channelbrand = (Channelbrand) recommandBrandsAdapter
						.getItemData(arg2);
				Intent intent = new Intent(mContext,
						BrandParticularActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("brandID", channelbrand.getBrandID());
				startActivity(intent);
			}
		});
		loader = new ScrollLoader(mContext, resourceList, new CallBack() {
			@Override
			public void loadData(int pageNo) {
				getRecommandBrands(pageNo);
			}
		});
		// 数据加载中
		mProgressDialog = new ProgressDialog(mContext);
	}

	/**
	 * 信息初始化
	 * */
	private void initMsg() {
		// 初始化电影基本信息
		new AsyncTask<Void, Void, ProgramInfoJson>() {

			@Override
			protected ProgramInfoJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new LiveAction().getPorgramInfo(
						InterfaceUrls.GET_PROGRAM_INFO, session.getUserCode(),
						programId);
			};

			@Override
			protected void onPostExecute(ProgramInfoJson result) {
				if (null != result && 0 == result.getRet()) {
					// 初始化当前页面信息
					programInfo = result.getProgramInfo();
					initProgramDetail();
				}
			};
		}.execute();
	}

	/**
	 * 初始化点播电影基本信息
	 * */
	private void initProgramDetail() {
		if (null == programInfo) {
			return;
		}
		long startTime = Utility.dealTimeToSeconds(programInfo.getBeginTime());
		long endTime = Utility.dealTimeToSeconds(programInfo.getEndTime());
		long nowTime = System.currentTimeMillis() / 1000;
		if (nowTime > endTime) {// 如果大于结束时间，显示回看
			playOrBookBtn.setBackgroundResource(R.drawable.play);
		} else if (startTime < nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，显示正在播放
			playOrBookBtn.setBackgroundResource(R.drawable.play);
		} else if (startTime > nowTime) { // 如果小于开始时间，显示预约或取消预约
			if (0 == programInfo.getIsBook()) {
				// 预约
				playOrBookBtn.setBackgroundResource(R.drawable.book_btn);
			} else {
				playOrBookBtn.setBackgroundResource(R.drawable.book_canecl_btn);
			}
		}
		// 海报
		CustormImageView programPoster = (CustormImageView) this
				.findViewById(R.id.programPoster);
		ArrayList<Poster> posters = programInfo.getPoster();
		if (null != posters && 0 < posters.size()) {
			Poster poster = posters.get(0);
			if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
				posterUrl = poster.getLocalPath();
			}
		}
		programPoster.setImageHttpUrl(posterUrl);
		// 节目名称
		TextView programeName = (TextView) this.findViewById(R.id.programeName);
		programeName.setText(programInfo.getEventName());
		// 频道名称
		TextView channelName = (TextView) this.findViewById(R.id.channelName);
		channelName.setText(programInfo.getChannelName());
		// 播放时间
		TextView playDate = (TextView) this.findViewById(R.id.playDate);
		playDate.setText(programInfo.getBeginTime()
				+ "-"
				+ programInfo.getEndTime().substring(
						programInfo.getEndTime().indexOf(" ") + 1,
						programInfo.getEndTime().length()));
		// 影片简介
		TextView programIntro = (TextView) this.findViewById(R.id.programIntro);
		String programDesc = programInfo.getEventDesc();
		if (TextUtils.isEmpty(programDesc)) {
			findViewById(R.id.intro).setVisibility(View.INVISIBLE);
		} else {
			if (programDesc.length() > 45) {
				programIntro.setOnClickListener(new ProgramIntroListener(
						programDesc));
				programDesc = programDesc.substring(0, 45);
				programDesc += "   详细>>";
				programIntro.setText(programDesc);
			}
			programIntro.setText(programDesc);
		}
		ImageView gaoqingTag = (ImageView) this.findViewById(R.id.gaoqingTag);
		if (1 == programInfo.getVideoType()) {
			gaoqingTag.setVisibility(View.VISIBLE);
		} else {
			gaoqingTag.setVisibility(View.INVISIBLE);
		}
	}

	class ProgramIntroListener implements OnClickListener {
		private String programDesc;

		public ProgramIntroListener(String programDesc) {
			super();
			this.programDesc = programDesc;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, ProgramIntroActivity.class);
			intent.putExtra("programDesc", programDesc);
			startActivity(intent);
		}
	}

	/**
	 * 单击事件监听
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.programePlay:
			long startTime = Utility.dealTimeToSeconds(programInfo
					.getBeginTime());
			long endTime = Utility.dealTimeToSeconds(programInfo.getEndTime());
			long nowTime = System.currentTimeMillis() / 1000;
			if (nowTime > endTime) {// 如果大于结束时间，显示回看
				PlayerUtil.playShift(mContext,
						programInfo.getChannelResourceCode(),
						Utility.dealTimeToSeconds(programInfo.getBeginTime()),
						Utility.dealTimeToSeconds(programInfo.getEndTime()),
						programInfo.getVideoType(), programInfo.getEventName(),
						posterUrl,programId);
			} else if (startTime < nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，显示正在播放
				PlayerUtil.playLive(mContext,
						programInfo.getChannelResourceCode(),
						programInfo.getVideoType(), programInfo.getEventName(),
						posterUrl);// 調用直播播放器
			} else if (startTime > nowTime) { // 如果小于开始时间，显示预约或取消预约
				if (!isloading) {
					if (0 == programInfo.getIsBook()) {
						// 预约
						addBook(programInfo);
					} else {
						// 取消预约
						delBook(programInfo);
					}
				}
			}
			break;
		case R.id.publishComment:
			addUserComment();
			break;
		case R.id.parShare: // 站内分享，添加分享资源
			try {
				RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.programeParViewRoot);
				ShareWindow shareWindow = new ShareWindow(rootLayout, mContext,
						null);
				shareWindow.show(programInfo.getEventName(), posterUrl, 1,
						programId);
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

	private void addBook(final ProgramInfo programIn) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			protected void onPreExecute() {
				if (null == mProgressDialog) {
					mProgressDialog = new ProgressDialog(mContext);
				}
				mProgressDialog.show();
				isloading = true;
			};

			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				return new BookAction().addBook(InterfaceUrls.ADD_BOOK,
						session.getUserCode(), programIn.getProgramId(),
						programIn.getChannelResourceCode());
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != mProgressDialog) {
					mProgressDialog.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					Book mybook = new Book();
					mybook.setBeginTime(programIn.getBeginTime());
					mybook.setBookTime(Long.toString(System.currentTimeMillis()));
					mybook.setChannelName(programIn.getChannelName());
					mybook.setChannelResourceCode(programIn
							.getChannelResourceCode());
					mybook.setEndTime(programIn.getEndTime());
					mybook.setEventDate(programIn.getEventDate());
					mybook.setEventName(programIn.getEventName());
					mybook.setPosterInfo(programIn.getPoster());
					mybook.setProgramId(programIn.getProgramId());
					mybook.setUserCode(session.getUserCode());
					mybook.setVideoType(programIn.getVideoType());
					BookNotifyService.books.add(mybook);// 加入到全局数组中
					playOrBookBtn
							.setBackgroundResource(R.drawable.book_canecl_btn);
					programInfo.setIsBook(1);
					Toast.makeText(mContext, getString(R.string.book_success),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, getString(R.string.book_failed),
							Toast.LENGTH_SHORT).show();
				}
				isloading = false;
			}
		}.execute();
	}

	/**
	 * 取消预约
	 * */
	private void delBook(final ProgramInfo programInfo) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<String, Void, BaseJsonBean>() {
			protected void onPreExecute() {
				if (null == mProgressDialog) {
					mProgressDialog = new ProgressDialog(mContext);
				}
				mProgressDialog.show();
				isloading = true;
			};

			@Override
			protected BaseJsonBean doInBackground(String... params) {
				return new BookAction().delBook(InterfaceUrls.DEL_BOOK,
						session.getUserCode(), programInfo.getProgramId());
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != mProgressDialog) {
					mProgressDialog.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					ArrayList<Book> books = BookNotifyService.books;
					String progId = programInfo.getProgramId();
					for (Book info : books) {
						if (progId.equals(info.getProgramId())) {
							BookNotifyService.books.remove(info);// 移除数组中的数据
							break;
						}
					}
					playOrBookBtn.setBackgroundResource(R.drawable.book_btn);
					programInfo.setIsBook(0);
					Toast.makeText(mContext,
							getString(R.string.book_cancel_success),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext,
							getString(R.string.book_cancel_failed),
							Toast.LENGTH_SHORT).show();
				}
				isloading = false;
			}
		}.execute();
	}

	/**
	 * 获取推荐品牌节目
	 * */
	private void getRecommandBrands(final int pageNo) {
		new AsyncTask<String, Void, AssetListJson>() {
			@Override
			protected AssetListJson doInBackground(String... params) {
				Session session = Session.getInstance();
				return new VodAction().getAssetList(
						InterfaceUrls.GET_ASSET_LIST, 5, pageNo,
						session.getUserCode(), "0", params[0], "", "", "", "",
						"");
			};

			@Override
			protected void onPostExecute(AssetListJson result) {
				if (null != result && 0 == result.getRet()) {
					recommandBrandsAdapter.addNewDatas(result
							.getChannelbrandList());
					loader.setCurPage(result.getCurPage());
					loader.setPageCount(result.getPageCount());
				}
			}
		}.execute("ott_130565981453718");
	}

	/**
	 * 定义一个结果列表项
	 */
	public final class ViewHolder {
		public CustormImageView livePoster;
		public TextView liveName;
		public TextView liveInfo;
		public ImageView gaoqingTag;
	}

	/**
	 * 品牌节目关联推荐适配器（列表显示）
	 */
	public class RecommandBrandsAdapter extends CommonAdapter {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.live_recommend_item,
						null);
				holder.livePoster = (CustormImageView) convertView
						.findViewById(R.id.livePoster);
				holder.liveName = (TextView) convertView
						.findViewById(R.id.liveName);
				holder.liveInfo = (TextView) convertView
						.findViewById(R.id.liveInfo);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Channelbrand channelbrand = (Channelbrand) datas.get(position);
			// 节目海报
			String imagePath = "";
			ArrayList<Poster> posters = channelbrand.getPoster();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			holder.livePoster.setImageHttpUrl(imagePath);
			// 节目名称
			holder.liveName.setText(channelbrand.getBrandName());
			// 节目播放时间
			holder.liveInfo.setText(channelbrand.getPalyDay()
					+ channelbrand.getBeginTime() + "-"
					+ channelbrand.getEndTime());
			if (1 == channelbrand.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			return convertView;
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
						programInfo.getEventName())
						+ comment;
				doAddUserComment(commentStr);
				StatusesAPI api = new StatusesAPI(accessToken);
				api.update(commentStr, "", "", new RequestListener() {
					@Override
					public void onComplete(String response) {
						LogUtils.trace(Log.DEBUG, TAG, response);
					}

					@Override
					public void onIOException(IOException e) {
					}

					@Override
					public void onError(WeiboException e) {
						LogUtils.trace(Log.DEBUG, TAG, e.getMessage());
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
				return new CommentAction().userComment(
						InterfaceUrls.ADD_USER_COMMENT, Session.getInstance()
								.getUserCode(), 1, programId, commentUtf8, 0);
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
				} else {
					Toast.makeText(mContext,
							getString(R.string.toast_comment_failed),
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
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
						InterfaceUrls.GET_COMMENT_BY_ASSETID, 1, programId,
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
					if (null == comments || 0 > comments.size()) {
						return;
					}
					// 初始化评论数据列表
					commentsAdapter.addNewDatas(comments);
					commentsLoader.setCurPage(result.getCurPage());
					commentsLoader.setPageCount(result.getPageCount());
				} else {
					Toast.makeText(mContext, "获取评论失败，请稍后再试！",
							Toast.LENGTH_SHORT).show();
				}
			};
		}.execute();
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
			String logo = "";
			User user = comment.getUser();
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

	@Override
	public void onBackPressed() {
		if (isFromNotifyService && !MainTabHostActivity.mIsStarted) {
			// 弹出对话框
			createUpdateDialog(mContext, "退出 程序？");
		} else if (isFromNotifyService) {
			finish();
			Intent in = new Intent(mContext, MainTabHostActivity.class);
			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			in.putExtra("isCancel", true);
			this.startActivity(in);
		} else {
			super.onBackPressed();
		}
	}

	private void createUpdateDialog(final Context mContext, String messageText) {
		new AlertDialog.Builder(mContext)
				.setTitle("提示")
				.setMessage(messageText)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
						android.os.Process.killProcess(android.os.Process
								.myPid()); // 结束进程
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

}