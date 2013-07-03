package com.coship.ott.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.coship.ott.transport.dto.live.ChannelbrandJson;
import com.coship.ott.transport.dto.live.ProgramInfo;
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
 * 品牌节目详情页面
 */
public class BrandParticularActivity extends Activity implements
		OnClickListener {
	private Channelbrand channelbrandInfo;
	// 日志标识
	public final static String TAG = "BrandParticularActivity";
	private Context mContext = null;
	private LayoutInflater mInflater;
	// 当前品牌节目关联直播节目适配器
	private ProgramsAdapter programLAdapter;
	// 当前直播节目显示列表类型 0：回看;1：预约
	private int programShowType = 0;
	private ArrayList<ProgramInfo> programsSeeBack = new ArrayList<ProgramInfo>();
	private ArrayList<ProgramInfo> programsBook = new ArrayList<ProgramInfo>();
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
	// 当前页显示资源resourceCode
	private String brandID;
	private TextView typeSeeBack;
	private TextView typeBook;
	private TextView disscussBtn;
	private ListView programList;
	// 评论
	private View discussTap;
	private String posterUrl;
	private boolean isloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.particular_brand);
		// 添加未捕获异常监听
		mContext = BrandParticularActivity.this;
		AppManager.getAppManager().addActivity(this);
		mInflater = LayoutInflater.from(mContext);
		brandID = this.getIntent().getStringExtra("brandID");
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
		programList = (ListView) findViewById(R.id.programList);
		programLAdapter = new ProgramsAdapter();
		programList.setAdapter(programLAdapter);
		programList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ProgramInfo programInfo = (ProgramInfo) programLAdapter
						.getItemData(arg2);
				if (0 == programShowType) {
					String posterUrl = "";
					try {
						posterUrl = channelbrandInfo.getPoster().get(0)
								.getLocalPath();
					} catch (Exception e) {
						LogUtils.trace(Log.DEBUG, TAG,
								programInfo.getEventName() + "获取海报失败！");
					}
					long startTime = Utility.dealTimeToSeconds(programInfo
							.getBeginTime());
					long endTime = Utility.dealTimeToSeconds(programInfo
							.getEndTime());
					long nowTime = System.currentTimeMillis() / 1000;
					if (startTime < nowTime && nowTime < endTime) {
						// 如果大于开始时间且小于结束时间 ，正在播放,直播
						PlayerUtil.playLive(mContext,
								programInfo.getChannelResourceCode(),
								channelbrandInfo.getVideoType(),
								programInfo.getEventName(), posterUrl);
					} else {
						PlayerUtil.playShift(mContext, programInfo
								.getChannelResourceCode(), Utility
								.dealTimeToSeconds(programInfo.getBeginTime()),
								Utility.dealTimeToSeconds(programInfo
										.getEndTime()), channelbrandInfo
										.getVideoType(), programInfo
										.getEventName(), posterUrl, programInfo
										.getProgramId());
					}
				} else {
					if (0 == programInfo.getIsBook()) {
						addBook(programInfo);
					} else { // 取消预约
						delBook(programInfo);
					}
				}
			}
		});
		// 评论组件父组件
		discussTap = this.findViewById(R.id.parConDiscusses);
		discussTap.setVisibility(View.GONE);
		// 回看按钮
		typeSeeBack = (TextView) findViewById(R.id.typeSeeBack);
		typeSeeBack.setOnClickListener(this);
		// 预约按钮
		typeBook = (TextView) findViewById(R.id.typeBook);
		typeBook.setOnClickListener(this);
		// 评论按钮
		disscussBtn = (TextView) findViewById(R.id.particularDiscusses);
		disscussBtn.setOnClickListener(this);
		this.findViewById(R.id.parShare).setOnClickListener(this);
		// 返回按钮及事件处理
		this.findViewById(R.id.pCularTopBack).setOnClickListener(this);
		// 是否同步到新浪微博单选框后面的文字，增加单选框可以响应点击的范围
		this.findViewById(R.id.sendToSina).setOnClickListener(this);
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
				if (!isloading) {
					getRecommandBrands(pageNo);
				}
			}
		});
	}

	/**
	 * 信息初始化
	 * */
	private void initMsg() {
		// 获取品牌节目基本信息
		new AsyncTask<String, Void, ChannelbrandJson>() {
			@Override
			protected ChannelbrandJson doInBackground(String... params) {
				Session session = Session.getInstance();
				return new LiveAction().getChannelbrandInfo(
						InterfaceUrls.GET_CHANNELBRAND_INFO,
						session.getUserCode(), brandID);
			};

			@Override
			protected void onPostExecute(ChannelbrandJson result) {

				if (null != result && 0 == result.getRet()) {
					channelbrandInfo = result.getChannelbrand();
					// 初始化当前页面信息
					if (channelbrandInfo != null) {
						initBrandDetail(channelbrandInfo);
					}
					ArrayList<ProgramInfo> programs = channelbrandInfo
							.getProgramInfo();
					if (null != programs && 0 != programs.size()) {
						long nowTime = System.currentTimeMillis() / 1000;
						long endTime = 0l;
						long startTime = 01;
						for (ProgramInfo program : programs) {
							endTime = Utility.dealTimeToSeconds(program
									.getEndTime());
							startTime = Utility.dealTimeToSeconds(program
									.getBeginTime());
							if (nowTime > startTime) {// 如果当前时间大于结束时间为回看
								programsSeeBack.add(program);
							} else if (nowTime < startTime) { // 如果当前时间小于开始时间为预约
								programsBook.add(program);
							}
						}
						Collections.sort(programsSeeBack);
						programLAdapter.addNewDatas(programsSeeBack);
					}
				}
			};
		}.execute();
	}

	/**
	 * 初始化品牌节目基本信息
	 * */
	private void initBrandDetail(Channelbrand channelbrand) {
		// 海报
		CustormImageView channelBrandPoster = (CustormImageView) this
				.findViewById(R.id.channelBrandPoster);
		ArrayList<Poster> posters = channelbrand.getPoster();
		if (null != posters && 0 < posters.size()) {
			Poster poster = posters.get(0);
			if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
				posterUrl = poster.getLocalPath();
			}
		}
		channelBrandPoster.setImageHttpUrl(posterUrl);

		// 品牌名称
		TextView pCularAssetName = (TextView) this.findViewById(R.id.brandName);
		pCularAssetName.setText(channelbrand.getBrandName());
		// 频道名称
		TextView pCularAssetPDate = (TextView) this
				.findViewById(R.id.channelName);
		pCularAssetPDate.setText(channelbrand.getChannelName());
		// 播放时间
		TextView pCularAssetDirector = (TextView) this
				.findViewById(R.id.playDate);
		pCularAssetDirector
				.setText(channelbrand.getPalyDay() + " "
						+ channelbrand.getBeginTime() + "-"
						+ channelbrand.getEndTime());
		// 主持人
		TextView pCularAssetActors = (TextView) this
				.findViewById(R.id.brandHost);
		String host = channelbrand.getAdditionalInfo();
		if (!TextUtils.isEmpty(host)) {
			pCularAssetActors.setText(host);
		}
		// 简介
		TextView pCularAssetResume = (TextView) this
				.findViewById(R.id.brandIntro);
		String brandIntro = channelbrand.getDesc();
		if (TextUtils.isEmpty(brandIntro)) {
			findViewById(R.id.intro).setVisibility(View.INVISIBLE);
		} else {
			if (brandIntro.length() > 45) {
				pCularAssetResume.setOnClickListener(new ProgramIntroListener(
						brandIntro));
				brandIntro = brandIntro.substring(0, 45);
				brandIntro += "   详细>>";
				pCularAssetResume.setText(brandIntro);
			}
			pCularAssetResume.setText(brandIntro);
		}
		// 高标清标识
		ImageView gaoqingTag = (ImageView) this.findViewById(R.id.gaoqingTag);
		if (1 == channelbrand.getVideoType()) {
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
		case R.id.publishComment:
			addUserComment();
			break;
		case R.id.typeSeeBack:
			programShowType = 0;
			changeProgramType();
			break;
		case R.id.typeBook: // 预约按钮
			programShowType = 1;
			changeProgramType();
			break;
		case R.id.particularDiscusses: // 评论按钮
			programShowType = 2;
			changeProgramType();
			break;
		case R.id.parShare: // 分享，添加分享资源
			try {
				RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.brandParView);
				ShareWindow shareWindow = new ShareWindow(rootLayout, mContext,
						null);
				shareWindow.show(channelbrandInfo.getBrandName(), posterUrl, 3,
						brandID);
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

	private void changeProgramType() {
		// 清空列表
		programLAdapter.removeAllDatas();
		switch (programShowType) {
		case 0:
			discussTap.setVisibility(View.GONE);
			programList.setVisibility(View.VISIBLE);
			programLAdapter.addNewDatas(programsSeeBack);
			typeSeeBack.setTextColor(Color.parseColor("#484848"));
			typeSeeBack.setBackgroundResource(R.drawable.branch_btn_sel);
			typeBook.setTextColor(Color.parseColor("#7F7F7F"));
			typeBook.setBackgroundResource(R.drawable.branch_btn);
			disscussBtn.setTextColor(Color.parseColor("#7F7F7F"));
			disscussBtn.setBackgroundResource(R.drawable.branch_btn);
			break;
		case 1:
			discussTap.setVisibility(View.GONE);
			programList.setVisibility(View.VISIBLE);
			// 升序排列
			ArrayList<ProgramInfo> infos = new ArrayList<ProgramInfo>();
			for (int i = programsBook.size() - 1; i >= 0; i--) {
				infos.add(programsBook.get(i));
			}
			programLAdapter.addNewDatas(infos);
			typeBook.setTextColor(Color.parseColor("#484848"));
			typeBook.setBackgroundResource(R.drawable.branch_btn_sel);
			typeSeeBack.setTextColor(Color.parseColor("#7F7F7F"));
			typeSeeBack.setBackgroundResource(R.drawable.branch_btn);
			disscussBtn.setTextColor(Color.parseColor("#7F7F7F"));
			disscussBtn.setBackgroundResource(R.drawable.branch_btn);
			break;
		case 2:
			programList.setVisibility(View.GONE);
			discussTap.setVisibility(View.VISIBLE);
			disscussBtn.setTextColor(Color.parseColor("#484848"));
			disscussBtn.setBackgroundResource(R.drawable.branch_btn_sel);
			typeBook.setTextColor(Color.parseColor("#7F7F7F"));
			typeBook.setBackgroundResource(R.drawable.branch_btn);
			typeSeeBack.setTextColor(Color.parseColor("#7F7F7F"));
			typeSeeBack.setBackgroundResource(R.drawable.branch_btn);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取推荐品牌节目
	 * */
	private void getRecommandBrands(final int pageNo) {
		new AsyncTask<String, Void, AssetListJson>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				isloading = true;
			}

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
					// 删除重复的数据
					ArrayList<Channelbrand> assetLists = new ArrayList<Channelbrand>();
					if (result.getChannelbrandList() == null) {
						return;
					}
					for (Channelbrand info : result.getChannelbrandList()) {
						if (!brandID.equals(info.getBrandID())) {
							assetLists.add(info);
						}
					}
					recommandBrandsAdapter.addNewDatas(assetLists);
					loader.setCurPage(result.getCurPage());
					loader.setPageCount(result.getPageCount());
				}
				isloading = false;
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
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.live_recommend_item,
						null);
				holder.livePoster = (CustormImageView) convertView
						.findViewById(R.id.livePoster);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.liveName = (TextView) convertView
						.findViewById(R.id.liveName);
				holder.liveInfo = (TextView) convertView
						.findViewById(R.id.liveInfo);
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
			// 节目简介
			holder.liveInfo.setText(channelbrand.getPalyDay() + " "
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
	 * 定义一个结果列表项
	 */
	public final class Holder {
		public TextView programName;
		public TextView programDate;
		public TextView programTime;
		public TextView programStutes;
	}

	/**
	 * 品牌节目关联推荐适配器（列表显示）
	 */
	public class ProgramsAdapter extends CommonAdapter {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(
						R.layout.particular_brand_program_item, null);
				holder.programName = (TextView) convertView
						.findViewById(R.id.programName);
				holder.programDate = (TextView) convertView
						.findViewById(R.id.programDate);
				holder.programTime = (TextView) convertView
						.findViewById(R.id.programTime);
				holder.programStutes = (TextView) convertView
						.findViewById(R.id.programStutes);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			ProgramInfo program = (ProgramInfo) datas.get(position);
			// 节目名称
			holder.programName.setText(program.getEventName());
			// 节目简介
			holder.programDate.setText(program.getBeginTime().substring(0, 10));
			holder.programTime.setText(program.getBeginTime().substring(11, 16)
					+ "-" + program.getEndTime().substring(11, 16));
			long startTime = Utility.dealTimeToSeconds(program.getBeginTime());
			long endTime = Utility.dealTimeToSeconds(program.getEndTime());
			long nowTime = System.currentTimeMillis() / 1000;

			if (0 == programShowType) {
				if (startTime < nowTime && nowTime < endTime) {
					// 如果大于开始时间且小于结束时间，显示正在播放
					holder.programStutes.setText("正在播放");
				} else {
					holder.programStutes.setText("回看");
				}
			} else {
				if (0 == program.getIsBook()) {
					holder.programStutes.setText("预约");
				} else if (1 == program.getIsBook()) {
					holder.programStutes.setText("取消预约");
				}
			}
			// if (startTime < nowTime && nowTime < endTime) {
			// 如果大于开始时间且小于结束时间，显示正在播放
			// holder.programStutes.setText("正在播放");
			// }
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
						channelbrandInfo.getBrandName())
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
				Session session = Session.getInstance();
				if (!session.isLogined()) {
					UIUtility.showDialog(mContext);
					return null;
				}
				String commentUtf8 = comment;
				try {
					commentUtf8 = URLEncoder.encode(comment, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
				return new CommentAction().userComment(
						InterfaceUrls.ADD_USER_COMMENT, session.getUserCode(),
						3, brandID, commentUtf8, 0);
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
						InterfaceUrls.GET_COMMENT_BY_ASSETID, 3, brandID,
						COMMENTS_PAGE_SIZE, pageNo);
			}

			@Override
			protected void onPostExecute(CommentsJson result) {
				if (null != result && 0 == result.getRet()) {
					// 可能评论条数为0，所以放在下面判断前处理
					disscussBtn.setText(getString(R.string.discusses) + "("
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
			User user = comment.getUser();
			String logo = "";
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

	private void addBook(final ProgramInfo programInfo) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				return new BookAction().addBook(InterfaceUrls.ADD_BOOK,
						session.getUserCode(), programInfo.getProgramId(),
						programInfo.getChannelResourceCode());
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != result && 0 == result.getRet()) {
					Toast.makeText(mContext, getString(R.string.book_success),
							Toast.LENGTH_SHORT).show();
					Book mybook = new Book();
					mybook.setBeginTime(programInfo.getBeginTime());
					mybook.setBookTime(Long.toString(System.currentTimeMillis()));
					mybook.setChannelName(programInfo.getChannelName());
					mybook.setChannelResourceCode(programInfo
							.getChannelResourceCode());
					mybook.setEndTime(programInfo.getEndTime());
					mybook.setEventDate(programInfo.getEventDate());
					mybook.setEventName(programInfo.getEventName());
					mybook.setPosterInfo(programInfo.getPoster());
					mybook.setProgramId(programInfo.getProgramId());
					mybook.setUserCode(session.getUserCode());
					mybook.setVideoType(programInfo.getVideoType());
					BookNotifyService.books.add(mybook);// 加入到全局数组中
					programInfo.setIsBook(1);
					programLAdapter.notifyDataSetChanged();

				} else {
					Toast.makeText(mContext, getString(R.string.book_failed),
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
	}

	/**
	 * 删除用户记录
	 * */
	private void delBook(final ProgramInfo programInfo) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<String, Void, BaseJsonBean>() {
			@Override
			protected BaseJsonBean doInBackground(String... params) {
				return new BookAction().delBook(InterfaceUrls.DEL_BOOK,
						session.getUserCode(), programInfo.getProgramId());
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != result && 0 == result.getRet()) {
					Toast.makeText(mContext,
							getString(R.string.book_cancel_success),
							Toast.LENGTH_SHORT).show();
					ArrayList<Book> books = BookNotifyService.books;
					String progId = programInfo.getProgramId();
					for (Book info : books) {
						if (progId.equals(info.getProgramId())) {
							BookNotifyService.books.remove(info);// 移除数组中的数据
							break;
						}
					}
					programInfo.setIsBook(0);
					programLAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(mContext,
							getString(R.string.book_cancel_failed),
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
	}
}