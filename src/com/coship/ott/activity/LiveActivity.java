package com.coship.ott.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.service.BookNotifyService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.BookAction;
import com.coship.ott.transport.action.LiveAction;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.book.Book;
import com.coship.ott.transport.dto.live.ChannelCurrentProgramsJson;
import com.coship.ott.transport.dto.live.ChannelInfo;
import com.coship.ott.transport.dto.live.ChannelInfoJson;
import com.coship.ott.transport.dto.live.Channelbrand;
import com.coship.ott.transport.dto.live.ChannelbrandsJson;
import com.coship.ott.transport.dto.live.ProgramInfo;
import com.coship.ott.transport.dto.live.ProgramInfosJson;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.dto.vod.Pram;
import com.coship.ott.transport.dto.vod.PramJson;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.PlayerUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.utils.Utility;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;
import com.coship.ott.view.LoadingDialog;

/**
 * 直播
 */
public class LiveActivity extends CommonViewActivity implements OnClickListener {
	private LayoutInflater mInflater;
	// 频道列表
	private ExpandableListView channelListview = null;
	// 频道分类当前选中行
	private int mSelectGroupItem = -1;
	// 当前频道分类选中频道所在行数
	private int mSelectListItem = -1;
	// 根据频道分类获取频道列表已完成个数
	private int alreadyGetTypeCount = 0;
	private ChannelInfoAdapter channelsAdapter;
	// 当前选中频道ID
	private String curChannelResourceCode;
	private String posterUrl = "";
	private ProgressDialog mProgressDialog;
	// EPG信息
	private String nowDay = Utility.getDay();
	private ArrayList<ChannelInfo> mChannelInfo = null;
	private Map<String, ArrayList<ChannelInfo>> channelMap = new HashMap<String, ArrayList<ChannelInfo>>();

	private ChannelBrandListAdapter channelBrandsAdapter;
	private ChannelBrandAdapter recoBrandAdapter;
	private ScrollLoader channelBrandsLoader;
	private int weekIndex = Utility.getNowWeekIndex();
	private String weekDay = Utility.getWeekDay(0);
	private ListView channelBrandView;
	private ScrollLoader recommandBrandLoader;

	private ListView epgListView = null;
	private ScrollLoader channelsLoader;
	private EpgAdapter epgAdapter = null;

	// 频道列表每页显示多少条
	private static final int CHANNEL_PAGE_SIZE = 1000;
	// 品牌节目每页显示多少条
	protected static final int CHANNEL_BRAND_PAGE_SIZE = 6;
	// 频道介绍
	private CustormImageView channelLogo;
	private TextView channelName;
	private TextView nowProgramName;
	private TextView nowProgramTime;
	private TextView nextProgramName;
	private List<TextView> weekViewList;
	private LoadingDialog mLoadingDialog;
	private boolean isloading = false;
	private boolean isReloading = false;
	private boolean weekbtnPressed = false;
	private TextView titleTxt;
	private int selectitem;
	protected boolean protypeLoading = false;
	protected ArrayList<Pram> channelTypeList = new ArrayList<Pram>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.livesecond);
		AppManager.getAppManager().addActivity(this);
		// 设置系统PixelFormat为UNKNOWN
		getWindow().setFormat(PixelFormat.UNKNOWN);
		setupView();
		// 获取精彩推荐
		getRecommandBrand(1);
		// 获取频道分类列表
		getParm();
	}

	private void setupView() {
		mInflater = LayoutInflater.from(this);
		this.findViewById(R.id.liveRecommandItem).setOnClickListener(this);
		titleTxt = (TextView) this.findViewById(R.id.titleTxt);
		titleTxt.setText(R.string.title_live);
		// 精彩推荐频道品牌列表
		GridView liveRCView = (GridView) findViewById(R.id.liveRCView);
		recoBrandAdapter = new ChannelBrandAdapter();
		liveRCView.setAdapter(recoBrandAdapter);
		liveRCView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Channelbrand brand = (Channelbrand) recoBrandAdapter
						.getItemData(arg2);
				Intent intent = new Intent(mContext,
						BrandParticularActivity.class);
				intent.putExtra("brandID", brand.getBrandID());
				startActivity(intent);
			}
		});
		recommandBrandLoader = new ScrollLoader(mContext, liveRCView,
				new CallBack() {
					@Override
					public void loadData(int pageNo) {
						if (!isloading) {
							getRecommandBrand(pageNo);
						}
					}
				});

		channelListview = (ExpandableListView) findViewById(R.id.live_channel_listview);
		channelsAdapter = new ChannelInfoAdapter();
		channelListview.setAdapter(channelsAdapter);
		channelListview.setGroupIndicator(null);
		channelListview.setChildDivider(getResources().getDrawable(
				R.drawable.channel_list_divider));
		channelListview.setOnGroupClickListener(channelListGroupListener());
		channelListview.setOnChildClickListener(channelListChildListener());
		channelsLoader = new ScrollLoader(mContext, channelListview,
				new CallBack() {
					@Override
					public void loadData(int pageNo) {
						getChannels(mSelectGroupItem, pageNo);
					}
				});
		// 频道品牌
		channelBrandView = (ListView) findViewById(R.id.brandView);
		channelBrandsAdapter = new ChannelBrandListAdapter();
		channelBrandView.setAdapter(channelBrandsAdapter);
		channelBrandView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Object data = channelBrandsAdapter.getItemData(arg2);
				if (data instanceof Channelbrand) {
					data = (Channelbrand) data;
				} else {
					data = (ProgramInfo) data;
				}
			}
		});
		channelBrandsLoader = new ScrollLoader(mContext, channelBrandView,
				new CallBack() {
					@Override
					public void loadData(int pageNo) {
						if (!isReloading) {
							getChannelbrand(pageNo);
						}
					}
				});
		// EPG列表
		epgListView = (ListView) findViewById(R.id.live_epg_listview);
		epgAdapter = new EpgAdapter();
		epgListView.setAdapter(epgAdapter);
		epgListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectitem = position;
				ProgramInfo programInfo = (ProgramInfo) epgAdapter
						.getItemData(position);
				Intent intent = new Intent(mContext,
						ProgramParticularActivity.class);
				intent.putExtra("programId", programInfo.getProgramId());
				startActivity(intent);
				epgAdapter.notifyDataSetChanged();
			}
		});

		channelLogo = (CustormImageView) findViewById(R.id.channelLogo);
		channelName = (TextView) findViewById(R.id.channelName);
		nowProgramName = (TextView) findViewById(R.id.nowProgramName);
		nowProgramTime = (TextView) findViewById(R.id.nowProgramTime);
		nextProgramName = (TextView) findViewById(R.id.nextProgramName);
		findViewById(R.id.livePlay).setOnClickListener(this);
		// EPG左右箭头
		findViewById(R.id.leftArrow).setOnClickListener(this);
		findViewById(R.id.rightArrow).setOnClickListener(this);
		// 数据加载中
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.setMessage("正在加载数据...");
		mLoadingDialog.setCanceledOnTouchOutside(false);
		//
		initTodayTomo();
		// 初始化EPG表头日期列表
		initWeekButtons();
	}

	private OnGroupClickListener channelListGroupListener() {
		return new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// mSelectGroupItem = groupPosition;
				// mSelectListItem = 0;
				// refreshPageData();
				// findViewById(R.id.liveRecommand).setVisibility(View.GONE);
				// findViewById(R.id.liveContent).setVisibility(View.VISIBLE);
				return false;// 点击没有响应
			}
		};
	}

	private OnChildClickListener channelListChildListener() {
		return new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				channelListview.setSelectedChild(groupPosition, childPosition,
						true);
				if (!protypeLoading) {
					nowDay = Utility.getDay();
					// channelsAdapter.notifyDataSetChanged();
					mSelectListItem = childPosition;
					mSelectGroupItem = groupPosition;
					refreshPageData();
					// 当前频道分类选中频道所在行数
					findViewById(R.id.liveContent).setVisibility(View.VISIBLE);
					findViewById(R.id.liveRecommand).setVisibility(View.GONE);
					channelsAdapter.notifyDataSetChanged();
				}
				return false;
			}
		};
	}

	/**
	 * 切换频道后刷新品牌节目、EPG
	 * */
	private void refreshPageData() {
		if (null == channelTypeList || channelTypeList.size() < 1) {
			return;
		}
		if (mSelectGroupItem == -1) {
			return;
		}
		Pram channelTypePram = channelTypeList.get(mSelectGroupItem);
		if (null == channelTypePram) {
			return;
		}
		String channelTypeName = channelTypePram.getPramKey();
		ArrayList<ChannelInfo> channels = channelMap.get(channelTypeName);

		if (null == channels || channels.size() == 0) {
			return;
		}
		ChannelInfo channelInfo = channels.get(mSelectListItem);
		if (null == channelInfo) {
			// 清空以前的所有的数据
			channelLogo.setImageResource(0);
			channelLogo.setVisibility(View.INVISIBLE);
			channelName.setText("");
			epgAdapter.removeAllDatas();
			nowProgramName.setText("正在播放：" + "");
			nowProgramTime.setText("");
			nextProgramName.setText("即将播放：" + "");
			channelBrandsAdapter.removeAllDatas();
			return;
		} else {
			curChannelResourceCode = channelInfo.getResourceCode();
			ArrayList<Poster> posters = channelInfo.getPoster();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					posterUrl = poster.getLocalPath();
				}
			}
			// 设置当前频道Logo和名称
			channelLogo.setVisibility(View.VISIBLE);
			channelLogo.setImageHttpUrl(posterUrl);
			channelName.setText(channelInfo.getChannelName());
			// 获取当前正在节目和下一个节目
			nowProgramName.setText("正在播放：" + "");
			nowProgramTime.setText("");
			nextProgramName.setText("即将播放：" + "");
			getChannelCurrentProgram();
			// 更新EPG列表
			nowDay = Utility.getDay();
			epgAdapter.removeAllDatas();
			weekbtnPressed = false;
			getChannelProgram(1);
			// 更新焦点
			TextView textView = null;
			// 设置今天焦点
			for (int i = 0, len = weekViewList.size(); i < len; i++) {
				textView = weekViewList.get(i);
				// 设置焦点
				if (i == 6) {
					textView.setTextColor(Color.WHITE);
					textView.setBackgroundResource(R.drawable.list_middle_focus);
				} else {
					textView.setTextColor(Color.parseColor("#898989"));
					textView.setBackgroundResource(0);
				}
			}
			// 品牌节目
			channelBrandsAdapter.removeAllDatas();
			getChannelbrand(1);
		}
	}

	private void initWeekButtons() {
		weekViewList = new ArrayList<TextView>();
		// 初始化星期及时期按钮容器
		LinearLayout liveWeekContainer = (LinearLayout) this
				.findViewById(R.id.liveWeekContainer);
		ArrayList<String> dateStrs = Utility.getEpgDateStrings();
		liveWeekContainer.removeAllViews();
		weekViewList.removeAll(weekViewList);
		LinearLayout layout = null;
		String dateStr = "";
		for (int i = 0, len = dateStrs.size(); i < len; i++) {
			dateStr = dateStrs.get(i);
			layout = (LinearLayout) mInflater.inflate(R.layout.live_text_item,
					null);
			TextView textView = (TextView) layout
					.findViewById(R.id.itemTextView);
			textView.setText(dateStr.substring(11, dateStr.length()));
			if (i == 6) {
				// 去除年份信息
				String todayDate = "";
				todayDate = textView.getText().toString();
				todayDate = todayDate.substring(8, todayDate.length() - 1);
				todayDate = "今天(" + todayDate + ")";
				textView.setText(todayDate);
			}
			// 点击事件
			textView.setOnClickListener(new WeekButtonListener(i, weekViewList,
					dateStr.substring(0, 10)));
			weekViewList.add(textView);
			liveWeekContainer.addView(layout);
		}
		// 设置今天焦点
		TextView nowWeekDay = weekViewList.get(6);
		nowWeekDay.setTextColor(Color.WHITE);
		nowWeekDay.setBackgroundResource(R.drawable.list_middle_focus);
	}

	@Override
	protected void onResume() {
		super.onResume();
		epgAdapter.removeAllDatas();
		refreshPageData();
		// 隐藏公告标题
		notice = (ImageView) findViewById(R.id.notice);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		noticeFull.setVisibility(View.GONE);
		notice.setVisibility(View.GONE);
	}

	/**
	 * 星期按钮点击事件处理
	 */
	private class WeekButtonListener implements OnClickListener {

		private int index;
		private List<TextView> mWeekViewList;
		private String dateStr;

		public WeekButtonListener(int index, List<TextView> weekViewList,
				String dateStr) {
			this.index = index;
			this.mWeekViewList = weekViewList;
			this.dateStr = dateStr;
		}

		@Override
		public void onClick(View v) {
			// 更新EPG列表
			nowDay = this.dateStr;
			epgAdapter.removeAllDatas();
			weekbtnPressed = true;
			if (!isloading) {
				getChannelProgram(1);
				// 更新焦点
				TextView textView = null;
				for (int i = 0, len = this.mWeekViewList.size(); i < len; i++) {
					textView = this.mWeekViewList.get(i);
					// 设置焦点
					if (i == this.index) {
						textView.setTextColor(Color.WHITE);
						textView.setBackgroundResource(R.drawable.list_middle_focus);
					} else {
						textView.setTextColor(Color.parseColor("#898989"));
						textView.setBackgroundResource(0);
					}
				}
			}
		}
	}

	/**
	 * 获取频道分类列表
	 * */
	private void getParm() {
		new AsyncTask<String, Void, PramJson>() {
			@Override
			protected PramJson doInBackground(String... params) {
				return new VodAction().getPram(InterfaceUrls.GET_PRAM,
						params[0]);
			}

			@Override
			protected void onPostExecute(PramJson result) {
				if (null != result && 0 == result.getRet()) {
					channelTypeList = result.getDatas();
					// channelsAdapter.notifyDataSetChanged();
					for (int paramIndex = 0, len = channelTypeList.size(); paramIndex < len; paramIndex++) {
						getChannels(paramIndex, 0);
					}
				}
			}
		}.execute("ChannelType");
	}

	/**
	 * 获取频道列表数据
	 */
	private void getChannels(final int channelIndex, final int pageNo) {
		new AsyncTask<Void, Void, ChannelInfoJson>() {
			@Override
			protected void onPreExecute() {
				protypeLoading = true;
			}

			@Override
			protected ChannelInfoJson doInBackground(Void... params) {
				String channelType = channelTypeList.get(channelIndex)
						.getPramKey();
				return new LiveAction().getChannels(
						// 获取频道列表
						InterfaceUrls.GET_CHANNELS, 0, CHANNEL_PAGE_SIZE,
						pageNo, "", "", channelType, "");
			}

			@Override
			protected void onPostExecute(ChannelInfoJson result) {
				if (null != result && 0 == result.getRet()) {
					mChannelInfo = result.getChannelInfo();
					// 初始化频道列表
					if (null != mChannelInfo && null != channelTypeList
							&& channelTypeList.size() > channelIndex) {
						String channelTypeName = channelTypeList.get(
								channelIndex).getPramKey();
						if (!TextUtils.isEmpty(channelTypeName)) {
							channelMap.put(channelTypeName, mChannelInfo);
						}
					}
				}
				protypeLoading = false;
				// ++alreadyGetTypeCount;
				if (channelIndex == 0) {
					// int groupCount = channelsAdapter.getGroupCount();
					// for (int i = 0; i < groupCount; i++) {
					channelListview.expandGroup(0);
					// }
				}
				// channelsAdapter.notifyDataSetChanged();
				if (alreadyGetTypeCount == channelTypeList.size()) {
					// refreshPageData();
				}
				channelsAdapter.notifyDataSetChanged();
			}
		}.execute();
	}

	/**
	 * 初始化最新最热按钮及事件处理
	 * */
	private void initTodayTomo() {
		List<TextView> views = new ArrayList<TextView>();
		views.add((TextView) findViewById(R.id.liveToday)); // 今天
		views.add((TextView) findViewById(R.id.liveTomo)); // / 明天
		// 添加监听器
		for (int i = 0, len = views.size(); i < len; i++) {
			views.get(i).setOnClickListener(new TypeHotNewListener(views, i));
		}
		// 默认选中最新
		views.get(0).setBackgroundResource(R.drawable.list_middle_focus);
	}

	class TypeHotNewListener implements OnClickListener {
		private List<TextView> views;
		private int index;

		public TypeHotNewListener(List<TextView> views, int index) {
			this.views = views;
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			TextView view = null;
			for (int viewIndex = 0, len = views.size(); viewIndex < len; viewIndex++) {
				view = views.get(viewIndex);
				TextView textView = (TextView) views.get(viewIndex);
				if (viewIndex == index) {
					view.setBackgroundResource(R.drawable.list_middle_focus);

					textView.setTextColor(Color.WHITE);
				} else {
					view.setBackgroundResource(0);
					textView.setTextColor(Color.GRAY);
				}
			}
			recoBrandAdapter.removeAllDatas();
			if (!isloading) {
				if (0 == index) {// 精彩推荐（今天）
					weekIndex = Utility.getNowWeekIndex();
					weekDay = Utility.getWeekDay(0);
					getRecommandBrand(1);
				} else {// 精彩推荐（明天）
					weekIndex = Utility.getNowWeekIndex() + 1;
					if (weekIndex == 8) {// 如果是周日，则置为1
						weekIndex = 1;
					}
					weekDay = Utility.getWeekDay(1);
					getRecommandBrand(1);
				}
			}
		}
	}

	private void getRecommandBrand(final int pageNo) {
		new AsyncTask<Void, Void, ChannelbrandsJson>() {
			protected void onPreExecute() {
				if (null == mProgressDialog) {
					mProgressDialog = new ProgressDialog(mContext);
					mProgressDialog.setMessage("正在加载数据...");
					mProgressDialog.setCanceledOnTouchOutside(false);
				}
				try {
					mProgressDialog.show();
				} catch (Exception e) {
				}
				isloading = true;
			};

			@Override
			protected ChannelbrandsJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new LiveAction().getChannelbrand(
						InterfaceUrls.GET_CHANNEL_BRAND, 12, pageNo,
						session.getUserCode(), "", weekIndex);// 1为周日，7为周六
			}

			@Override
			protected void onPostExecute(ChannelbrandsJson result) {
				if (null != mProgressDialog) {
					try {
						mProgressDialog.dismiss();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				if (null != result && 0 == result.getRet()) {
					// 海报
					recoBrandAdapter.addNewDatas(result.getChannelbrand());
					recommandBrandLoader.setCurPage(result.getCurPage());
					recommandBrandLoader.setPageCount(result.getPageCount());
				}
				isloading = false;
			}
		}.execute();
	}

	/**
	 * 获取频道品牌列表
	 * */
	private void getChannelbrand(final int pageNo) {
		new AsyncTask<Void, Void, ChannelbrandsJson>() {
			protected void onPreExecute() {
				mProgressDialog.show();
				isReloading = true;
			};

			@Override
			protected ChannelbrandsJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new LiveAction().getChannelbrand(
						InterfaceUrls.GET_CHANNEL_BRAND,
						CHANNEL_BRAND_PAGE_SIZE, pageNo, session.getUserCode(),
						curChannelResourceCode, 0);
			}

			@Override
			protected void onPostExecute(ChannelbrandsJson result) {
				if (null != result && 0 == result.getRet()) {
					ArrayList<Channelbrand> brands = result.getChannelbrand();
					ArrayList<ProgramInfo> programs = result.getPrograms();
					if (null != brands && brands.size() > 0) {
						channelBrandsAdapter.addNewDatas(brands);
					} else if (null != programs && programs.size() > 0) {
						channelBrandsAdapter.addNewDatas(programs);
					}
					channelBrandsLoader.setCurPage(result.getCurPage());
					channelBrandsLoader.setPageCount(result.getPageCount());
				}
				mProgressDialog.dismiss();
				isReloading = false;
			}
		}.execute();
	};

	/**
	 * 获取当前正在播放节目和下一个节目
	 */
	private void getChannelCurrentProgram() {
		new AsyncTask<Void, Void, ChannelCurrentProgramsJson>() {
			@Override
			protected ChannelCurrentProgramsJson doInBackground(Void... params) {
				return new LiveAction().getChannelCurrentPrograms(
						InterfaceUrls.GET_CURRENT_PROGRAM,
						curChannelResourceCode);
			}

			@Override
			protected void onPostExecute(ChannelCurrentProgramsJson result) {
				if (null != result && 0 == result.getRet()) {
					ProgramInfo curProgramInfo = result.getCurrentProgram();
					if (null == curProgramInfo) {
						return;
					}
					try {
						nowProgramName.setText("正在播放："
								+ curProgramInfo.getEventName());
						nowProgramTime.setText(curProgramInfo.getBeginTime()
								.substring(11)
								+ "----"
								+ curProgramInfo.getEndTime().substring(11));
					} catch (Exception e) {
					}
					ProgramInfo nextProgramInfo = result.getNextProgram();
					if (null == nextProgramInfo) {
						return;
					}
					try {
						nextProgramName.setText("即将播放："
								+ result.getNextProgram().getEventName());
					} catch (Exception e) {
					}
				}
			}
		}.execute();
	}

	/**
	 * 获取EPG列表数据
	 */
	private void getChannelProgram(final int pageNo) {
		new AsyncTask<Void, Void, ProgramInfosJson>() {
			protected void onPreExecute() {
				if (null == mProgressDialog) {
					mProgressDialog = new ProgressDialog(mContext);
					mProgressDialog.setCanceledOnTouchOutside(false);
				}
				mProgressDialog.show();
				isloading = true;
			};

			@Override
			protected ProgramInfosJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new LiveAction().getChannelProgram(
						InterfaceUrls.GET_CHANNEL_PROGRAM,
						session.getUserCode(), curChannelResourceCode,
						Integer.MAX_VALUE, pageNo, nowDay + " 00:00:00", nowDay
								+ " 23:59:59");
			}

			@Override
			protected void onPostExecute(ProgramInfosJson result) {
				if (null != mProgressDialog) {
					mProgressDialog.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					ArrayList<ProgramInfo> infos = result.getProgram();
					// 初始化EPG列表及星期按钮列表
					epgAdapter.addNewDatas(infos);
					scrollToNowProgram(infos);// 滑动到当前正在播放的节目
				}
				isloading = false;
				HorizontalScrollView sv = (HorizontalScrollView) findViewById(R.id.scrollView);
				if (!weekbtnPressed) {
					sv.scrollBy(-139 * 6, 0);
					sv.scrollBy(139 * 6, 0);
				}
			}
		}.execute();
	}

	// 滑动到当前正在播放的节目
	private void scrollToNowProgram(ArrayList<ProgramInfo> infos) {
		int position = 0;
		if (nowDay.equals(Utility.getDay())) {
			for (ProgramInfo info : infos) {
				long startTime = Utility.dealTimeToSeconds(info.getBeginTime());
				long endTime = Utility.dealTimeToSeconds(info.getEndTime());
				long nowTime = System.currentTimeMillis() / 1000;

				if (startTime < nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，显示正在播放
					break;
				}
				position++;
			}
			selectitem = position;
			epgListView.setSelectionFromTop(position, 0);
		}
	};

	private void addBook(final ProgramInfo programInfo) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			protected void onPreExecute() {
				mLoadingDialog.show();
			};

			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				return new BookAction().addBook(InterfaceUrls.ADD_BOOK,
						session.getUserCode(), programInfo.getProgramId(),
						programInfo.getChannelResourceCode());
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != mLoadingDialog) {
					mLoadingDialog.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
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
					Toast.makeText(mContext, getString(R.string.book_success),
							Toast.LENGTH_SHORT).show();
					programInfo.setIsBook(1);
					epgAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(LiveActivity.this,
							getString(R.string.book_failed), Toast.LENGTH_SHORT)
							.show();
				}
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
				mLoadingDialog.show();
			};

			@Override
			protected BaseJsonBean doInBackground(String... params) {
				return new BookAction().delBook(InterfaceUrls.DEL_BOOK,
						session.getUserCode(), programInfo.getProgramId());
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != mLoadingDialog) {
					mLoadingDialog.dismiss();
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
					Toast.makeText(LiveActivity.this,
							getString(R.string.book_cancel_success),
							Toast.LENGTH_SHORT).show();
					programInfo.setIsBook(0);
					epgAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(LiveActivity.this,
							getString(R.string.book_cancel_failed),
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
	}

	/**
	 * EPG结果列表项
	 */
	public final class ViewEpgHolder {
		public TextView epgStartTime;
		public TextView epgProgrameName;
		public TextView epgProgrameState;
	}

	/**
	 * EPG数据注入
	 */
	public class EpgAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewEpgHolder holder = null;
			if (convertView == null) {
				holder = new ViewEpgHolder();
				convertView = mInflater.inflate(R.layout.epg_list, null);
				holder.epgStartTime = (TextView) convertView
						.findViewById(R.id.epgStartTime);
				holder.epgProgrameName = (TextView) convertView
						.findViewById(R.id.epgProgrameName);
				holder.epgProgrameState = (TextView) convertView
						.findViewById(R.id.epgProgrameState);
				convertView.setTag(holder);
			} else {
				holder = (ViewEpgHolder) convertView.getTag();
			}

			ProgramInfo programInfo = (ProgramInfo) datas.get(position);
			String beginTime = programInfo.getBeginTime();
			try {
				beginTime = beginTime.substring(11, 16);
			} catch (Exception e) {
			}
			holder.epgStartTime.setText(beginTime);
			holder.epgProgrameName.setText(programInfo.getEventName());

			long startTime = Utility.dealTimeToSeconds(programInfo
					.getBeginTime());
			long endTime = Utility.dealTimeToSeconds(programInfo.getEndTime());
			long nowTime = System.currentTimeMillis() / 1000;

			if (nowTime > endTime) {// 如果大于结束时间，显示回看
				holder.epgProgrameState.setText("回看");
				holder.epgStartTime.setTextColor(Color.parseColor("#3a3a3b"));
				holder.epgProgrameName
						.setTextColor(Color.parseColor("#3a3a3b"));
				holder.epgProgrameState.setTextColor(Color
						.parseColor("#3a3a3b"));

			} else if (startTime < nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，显示正在播放
				holder.epgProgrameState.setText("正在播放");
				holder.epgStartTime.setTextColor(Color.parseColor("#830000"));
				holder.epgProgrameName
						.setTextColor(Color.parseColor("#830000"));
				holder.epgProgrameState.setTextColor(Color
						.parseColor("#830000"));
				epgListView.setSelectionFromTop(position, 0);

			} else if (startTime > nowTime) { // 如果小于开始时间，显示预约或取消预约
				if (0 == programInfo.getIsBook()) {
					holder.epgProgrameState.setText("预约");
				} else if (1 == programInfo.getIsBook()) {
					holder.epgProgrameState.setText("取消预约");
				}
				holder.epgStartTime.setTextColor(Color.parseColor("#898989"));
				holder.epgProgrameName
						.setTextColor(Color.parseColor("#898989"));
				holder.epgProgrameState.setTextColor(Color
						.parseColor("#898989"));
			}
			holder.epgStartTime.setOnClickListener(new EPGDetailListener(
					programInfo));
			holder.epgProgrameName.setOnClickListener(new EPGDetailListener(
					programInfo));
			holder.epgProgrameState.setOnClickListener(new EPGPlayListener(
					programInfo));
			if (selectitem == position) {
				convertView.setBackgroundResource(R.drawable.epgiteback);
			} else {
				convertView.setBackgroundResource(0);
			}
			return convertView;
		}
	}

	// 播放、预定
	class EPGPlayListener implements OnClickListener {
		private ProgramInfo mProgramInfo;

		public EPGPlayListener(ProgramInfo programInfo) {
			this.mProgramInfo = programInfo;
		}

		@Override
		public void onClick(View v) {
			Pram channelTypePram = channelTypeList.get(mSelectGroupItem);
			if (null == channelTypePram) {
				return;
			}
			String channelTypeName = channelTypePram.getPramKey();
			ArrayList<ChannelInfo> channels = channelMap.get(channelTypeName);
			if (null == channels || channels.size() == 0) {
				return;
			}
			ChannelInfo channelInfo = channels.get(mSelectListItem);
			long startTime = Utility.dealTimeToSeconds(mProgramInfo
					.getBeginTime());
			long endTime = Utility.dealTimeToSeconds(mProgramInfo.getEndTime());
			long nowTime = System.currentTimeMillis() / 1000;
			if (nowTime > endTime) {// 如果大于结束时间，显示回看
				PlayerUtil.playShift(mContext, curChannelResourceCode,
						startTime, endTime, channelInfo.getVideoType(),
						mProgramInfo.getEventName(), posterUrl,
						mProgramInfo.getProgramId());
			} else if (startTime < nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，显示正在播放
				PlayerUtil.playLive(mContext, curChannelResourceCode,
						channelInfo.getVideoType(),
						mProgramInfo.getEventName(), posterUrl);
			} else if (startTime > nowTime) { // 如果小于开始时间，显示预约或取消预约
				if (0 == mProgramInfo.getIsBook()) {
					addBook(mProgramInfo);
				} else if (1 == mProgramInfo.getIsBook()) {
					// 取消预约
					delBook(mProgramInfo);
				}
			}
		}
	}

	// 到详情页
	class EPGDetailListener implements OnClickListener {
		private ProgramInfo mProgramInfo;

		public EPGDetailListener(ProgramInfo programInfo) {
			this.mProgramInfo = programInfo;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext,
					ProgramParticularActivity.class);
			intent.putExtra("programId", mProgramInfo.getProgramId());
			startActivity(intent);
		}
	}

	/**
	 * 频道列表元素项
	 */
	public final class ViewHolder {
		public TextView live_channel_smallname;
		public CustormImageView live_channel_smallicon;
		public LinearLayout liveCategories;
		public TextView live_name;
		public ImageView open_up_falg; // 展开图标
	}

	/**
	 * 频道列表数据适配器
	 */
	public class ChannelInfoAdapter extends BaseExpandableListAdapter {

		@SuppressWarnings("rawtypes")
		protected ArrayList datas = new ArrayList();

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			String id = channelTypeList.get(groupPosition).getPramKey();
			return channelMap.get(id).get(childPosition);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (groupPosition >= channelTypeList.size()) {
				return 0;
			}
			String id = channelTypeList.get(groupPosition).getPramKey();
			if (channelMap.get(id) == null) {
				return 0;
			}
			return channelMap.get(id).size();

		}

		@Override
		public Object getGroup(int groupPosition) {
			return channelTypeList.get(groupPosition).toString();
		}

		@Override
		public int getGroupCount() {
			return channelTypeList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.live_top, null);
				holder.liveCategories = (LinearLayout) convertView
						.findViewById(R.id.liveCategories);
				holder.live_name = (TextView) convertView
						.findViewById(R.id.live_name);
				// holder.open_up_falg = (ImageView) convertView
				// .findViewById(R.id.rightImage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.live_name.setText(channelTypeList.get(groupPosition)
					.getPramValue());
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.live_list, null);
				holder.live_channel_smallname = (TextView) convertView
						.findViewById(R.id.live_channel_smallname);
				holder.live_channel_smallicon = (CustormImageView) convertView
						.findViewById(R.id.live_channel_smallicon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String id = channelTypeList.get(groupPosition).getPramKey();
			ChannelInfo channelInfo = channelMap.get(id).get(childPosition);
			String imagePath = "";
			ArrayList<Poster> posters = channelInfo.getPoster();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}

			holder.live_channel_smallicon.setImageHttpUrl(imagePath);
			holder.live_channel_smallname.setText(channelInfo.getChannelName());
			if (mSelectGroupItem == groupPosition
					&& mSelectListItem == childPosition) {
				convertView.setBackgroundResource(R.drawable.qietu);
			} else {
				convertView.setBackgroundResource(0);
			}
			return convertView;
		}

		public void onClickasd(View v) {

		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		channelMap.clear();
	}

	/**
	 * 频道品牌列表元素项
	 */
	public final class ChannelBrandViewHolder {
		public ImageView gaoqingTag;
		public CustormImageView livePoster;
		public TextView liveName;
		public TextView brandName;
		public TextView brandDesc;
		public TextView channelName;
		public TextView brandTime;
	}

	/**
	 * 频道品牌列表数据适配器
	 */
	public class ChannelBrandAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChannelBrandViewHolder holder = null;
			if (convertView == null) {
				holder = new ChannelBrandViewHolder();
				convertView = mInflater.inflate(R.layout.live_poster_name_item,
						null);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.livePoster = (CustormImageView) convertView
						.findViewById(R.id.livePoster);
				holder.liveName = (TextView) convertView
						.findViewById(R.id.liveName);
				holder.brandName = (TextView) convertView
						.findViewById(R.id.brandName);
				holder.brandDesc = (TextView) convertView
						.findViewById(R.id.brandDesc);
				holder.channelName = (TextView) convertView
						.findViewById(R.id.channelName);
				holder.brandTime = (TextView) convertView
						.findViewById(R.id.brandTime);
				convertView.setTag(holder);
			} else {
				holder = (ChannelBrandViewHolder) convertView.getTag();
			}

			Object data = datas.get(position);
			if (null == data) {
				return convertView;
			}
			if (data instanceof Channelbrand) {
				final Channelbrand brand = (Channelbrand) data;
				String imagePath = "";
				ArrayList<Poster> posters = brand.getPoster();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				holder.livePoster.setImageHttpUrl(imagePath);
				holder.brandName.setText(brand.getBrandName());
				holder.brandDesc.setText(brand.getDesc());
				holder.channelName.setText(brand.getChannelName());
				holder.brandTime.setText(weekDay + " " + brand.getBeginTime()
						+ "-" + brand.getEndTime());
				if (1 == brand.getVideoType()) {
					holder.gaoqingTag.setVisibility(View.VISIBLE);
				} else {
					holder.gaoqingTag.setVisibility(View.INVISIBLE);
				}
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								BrandParticularActivity.class);
						intent.putExtra("brandID", brand.getBrandID());
						startActivity(intent);
					}
				});
			} else {
				final ProgramInfo programInfo = (ProgramInfo) data;
				String imagePath = "";
				ArrayList<Poster> posters = programInfo.getPoster();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				holder.livePoster.setImageHttpUrl(imagePath);
				holder.brandName.setText(programInfo.getEventName());
				holder.brandDesc.setText(programInfo.getEventDesc());
				holder.channelName.setText("");
				holder.brandTime.setText(programInfo.getBeginTime() + "-"
						+ programInfo.getEndTime());
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								ProgramParticularActivity.class);
						intent.putExtra("programId", programInfo.getProgramId());
						startActivity(intent);
					}
				});
			}
			return convertView;
		}
	}

	/**
	 * 频道品牌列表数据适配器
	 */
	public class ChannelBrandListAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("xue", "=========>>");
			ChannelBrandViewHolder holder = null;
			if (convertView == null) {
				holder = new ChannelBrandViewHolder();
				convertView = mInflater.inflate(R.layout.live_poster_name_item,
						null);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.livePoster = (CustormImageView) convertView
						.findViewById(R.id.livePoster);
				holder.liveName = (TextView) convertView
						.findViewById(R.id.liveName);
				holder.brandName = (TextView) convertView
						.findViewById(R.id.brandName);
				holder.brandDesc = (TextView) convertView
						.findViewById(R.id.brandDesc);
				holder.channelName = (TextView) convertView
						.findViewById(R.id.channelName);
				holder.brandTime = (TextView) convertView
						.findViewById(R.id.brandTime);
				convertView.setTag(holder);
			} else {
				holder = (ChannelBrandViewHolder) convertView.getTag();
			}

			Object data = datas.get(position);
			if (null == data) {
				return convertView;
			}
			if (data instanceof Channelbrand) {
				final Channelbrand brand = (Channelbrand) data;
				String imagePath = "";
				ArrayList<Poster> posters = brand.getPoster();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				holder.livePoster.setImageHttpUrl(imagePath);
				holder.brandName.setText(brand.getBrandName());
				holder.brandDesc.setText(brand.getDesc());
				holder.channelName.setText(brand.getChannelName());
				String palyDay = brand.getPalyDay();
				if (!TextUtils.isEmpty(palyDay)) {
					holder.brandTime.setText(Utility.numToWeek(palyDay
							.substring(0, 1))
							+ " "
							+ brand.getBeginTime()
							+ "-" + brand.getEndTime());
				}
				if (1 == brand.getVideoType()) {
					holder.gaoqingTag.setVisibility(View.VISIBLE);
				} else {
					holder.gaoqingTag.setVisibility(View.INVISIBLE);
				}
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								BrandParticularActivity.class);
						intent.putExtra("brandID", brand.getBrandID());
						startActivity(intent);
					}
				});
			} else {
				final ProgramInfo programInfo = (ProgramInfo) data;
				String imagePath = "";
				ArrayList<Poster> posters = programInfo.getPoster();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				holder.livePoster.setImageHttpUrl(imagePath);
				holder.brandName.setText(programInfo.getEventName());
				holder.brandDesc.setText(programInfo.getEventDesc());
				Pram channelTypePram = channelTypeList.get(mSelectGroupItem);
				if (null != channelTypePram) {
					String channelTypeName = channelTypePram.getPramKey();
					ArrayList<ChannelInfo> channels = channelMap
							.get(channelTypeName);
					if (null != channels || channels.size() != 0) {
						ChannelInfo channelInfo = channels.get(mSelectListItem);
						holder.channelName
								.setText(channelInfo.getChannelName());
					}
				}
				holder.brandTime.setText(Utility.getWeekDay(0)
						+ " "
						+ programInfo.getBeginTime().split(" ")[1].substring(0,
								5)
						+ "-"
						+ programInfo.getEndTime().split(" ")[1]
								.substring(0, 5));
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								ProgramParticularActivity.class);
						intent.putExtra("programId", programInfo.getProgramId());
						startActivity(intent);
					}
				});
			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.livePlay:
			Pram channelTypePram = channelTypeList.get(mSelectGroupItem);
			if (null == channelTypePram) {
				return;
			}
			String channelTypeName = channelTypePram.getPramKey();
			ArrayList<ChannelInfo> channels = channelMap.get(channelTypeName);
			if (null == channels || channels.size() == 0) {
				return;
			}
			ChannelInfo channelInfo = channels.get(mSelectListItem);
			if (null == channelInfo) {
				return;
			}
			PlayerUtil.playLive(mContext, curChannelResourceCode,
					channelInfo.getVideoType(), channelInfo.getChannelName(),
					posterUrl);
			break;
		case R.id.liveRecommandItem:
			findViewById(R.id.liveContent).setVisibility(View.GONE);
			findViewById(R.id.liveRecommand).setVisibility(View.VISIBLE);
			// 数据更新
			recoBrandAdapter.removeAllDatas();
			getRecommandBrand(1);
			mSelectGroupItem = -1;
			mSelectListItem = -1;
			channelsAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
}
