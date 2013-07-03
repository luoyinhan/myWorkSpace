package com.coship.ott.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.coship.ott.service.BookNotifyService;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.LiveAction;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.live.ChannelInfo;
import com.coship.ott.transport.dto.live.ChannelInfoJson;
import com.coship.ott.transport.dto.live.Channelbrand;
import com.coship.ott.transport.dto.live.ChannelbrandJson;
import com.coship.ott.transport.dto.live.ChannelsCurrentPrograms;
import com.coship.ott.transport.dto.live.ChannelsCurrentProgramsJson;
import com.coship.ott.transport.dto.live.ProgramInfo;
import com.coship.ott.transport.dto.vod.AssetDetailJson;
import com.coship.ott.transport.dto.vod.AssetInfo;
import com.coship.ott.transport.dto.vod.AssetListInfo;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.dto.vod.Catalog;
import com.coship.ott.transport.dto.vod.CatalogJson;
import com.coship.ott.transport.dto.vod.IndexRem;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.dto.vod.ProductInfo;
import com.coship.ott.transport.dto.vod.RecommendResourceJson;
import com.coship.ott.transport.util.MulScreenSharePerfance;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.PlayerUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.view.AutoScrollGallery;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;

public class RecommendActivity extends CommonViewActivity {
	// 排行榜排行数据条数
	protected static final int RANK_DATA_SIZE = 10;
	// 推荐栏目数据总数
	protected static final int BOTTEM_GALLERY_DATA_SIZE = 10;
	// 推荐栏目数据总数
	protected static final int TOP_GALLERY_DATA_SIZE = 8;
	protected static final int CHANNEL_PAGE_SIZE = 15;
	// 头部大海报推荐资源列表
	public static ArrayList<IndexRem> headResources = new ArrayList<IndexRem>();
	private AutoScrollGallery topGallery = null;
	// 底部推荐栏目对应的资源列表
	private ArrayList<Catalog> recommendCatalogs = null;
	private Context mContext = null;
	// 右侧指示当前第几个的点点
	private LinearLayout points;
	private TextView filmNameView;
	private TextView filmNameEnView;
	private TextView director;
	private TextView movieActors;
	private TextView movietime;
	private TextView resume;
	private LayoutInflater mInflater;
	// 隐藏的直播推荐list
	private ListView liveRecommend = null;
	private LinearLayout recommendLayout;
	private ChannelInfoAdapter channelsAdapter;
	private SlidingDrawer mSlidingDrawer;
	// 频道列表当前选中行
	private int mSelectItem = 0;
	private ArrayList<ChannelInfo> channelInfo;// 所有的频道列表
	private TextView titleTxt;
	private int count = 0;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			count = 0;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend);
		mContext = this;
		// 将本次Activity加入到应用Activity堆栈中
		AppManager.getAppManager().addActivity(this);
		// 初始化页面元素
		setupView();
		// 初始化头部元素
		initHead();
		// 初始化推荐栏目列表
		initRecommendCatalog();
		// 初始化点播排行榜
		// initRanking(1);
		// 初始化直播排行榜
		// initRanking(2);
		// 启动预约到时提醒服务
		if (!checkServiceIfStart()) {
			if (Session.getInstance().isLogined()) {
				Intent service = new Intent(mContext, BookNotifyService.class);
				mContext.startService(service);
			}
		}
	}

	private void setupView() {
		mInflater = LayoutInflater.from(mContext);
		recommendLayout = (LinearLayout) findViewById(R.id.recommendLayout);
		topGallery = ((AutoScrollGallery) findViewById(R.id.topGallery));
		// 点击事件处理
		topGallery.setOnItemClickListener(toGalleryListener());
		points = ((LinearLayout) findViewById(R.id.points));
		titleTxt = (TextView) this.findViewById(R.id.titleTxt);
		titleTxt.setText(R.string.title_recommend);
		filmNameView = (TextView) this.findViewById(R.id.filmName);
		filmNameEnView = (TextView) this.findViewById(R.id.filmNameEn);
		director = (TextView) this.findViewById(R.id.director);
		movieActors = (TextView) this.findViewById(R.id.screenwriter);
		movietime = (TextView) this.findViewById(R.id.movietime);
		resume = (TextView) this.findViewById(R.id.resume);
		liveRecommend = (ListView) findViewById(R.id.live_recommend);
		mSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		MulScreenSharePerfance slidingDrawerOnOff = MulScreenSharePerfance
				.getInstance(mContext);
		if ((Boolean) slidingDrawerOnOff.getValue("onoff", "Boolean")) {
			mSlidingDrawer.animateOpen();// 打开抽屉
		}
		/* 设定SlidingDrawer被打开的事件处理 */
		mSlidingDrawer.setOnDrawerOpenListener(drawerOpenListener());
		channelsAdapter = new ChannelInfoAdapter();
		liveRecommend.setAdapter(channelsAdapter);
		liveRecommend.setOnItemClickListener(liveRecommendListener());

		findViewById(R.id.topbar_logo).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						++count;
						if (count == 3) {
							Intent intent = new Intent();
							intent.setClass(mContext,
									SystemSettinngActivity.class);
							startActivity(intent);
							count = 0;
						} else {
							handler.removeMessages(0);
							handler.sendEmptyMessageDelayed(0, 3000);
						}
					}
				});
	}

	private OnDrawerOpenListener drawerOpenListener() {
		return new SlidingDrawer.OnDrawerOpenListener() {
			public void onDrawerOpened() {
				if (channelsAdapter.getCount() <= 0) {
					channelsAdapter.removeAllDatas();// 首先清楚所有数据
					initScrollRecommend(1);
				}
			}
		};
	}

	private OnItemClickListener toGalleryListener() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				IndexRem resource = headResources.get(arg2
						% headResources.size());
				int type = resource.getResourceType();
				Intent intent = new Intent();
				switch (type) {
				case 1:// 频道品牌
					intent.setClass(mContext, BrandParticularActivity.class);
					intent.putExtra("brandID", resource.getResourceCode());
					break;
				case 2:// 点播
					intent.setClass(mContext, ParticularActivity.class);
					intent.putExtra("resourceCode", resource.getResourceCode());
					break;
				case 3:// 专题
					break;
				default:
					break;
				}
				startActivity(intent);
			}
		};
	}

	private OnItemClickListener liveRecommendListener() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// 标记当前选择频道
				mSelectItem = arg2;
				channelsAdapter.notifyDataSetChanged();
				ChannelInfo mychannelInfo = (ChannelInfo) channelsAdapter
						.getItemData(arg2);
				ProgramInfo programInfo = mychannelInfo.getCurrentProgram();
				if (programInfo != null) {
					Intent intent = new Intent(mContext,
							ProgramParticularActivity.class);
					intent.putExtra("programId", programInfo.getProgramId());
					startActivity(intent);
				}
			}
		};
	}

	/**
	 * 初始化头部滚动推荐栏
	 * */
	public void initHead() {
		new AsyncTask<Void, Void, RecommendResourceJson>() {
			@Override
			protected RecommendResourceJson doInBackground(Void... params) {
				return new VodAction()
						.getRecommendResource(InterfaceUrls.GET_RECOMMEND_RESOURCE);
			}

			@Override
			protected void onPostExecute(RecommendResourceJson result) {
				if (null != result && 0 == result.getRet()) {
					headResources = result.getIndexRemList();
					for (int i = 0, len = headResources.size(); i < len; i++) {
						points.addView(createPointImg());
					}
					topGallery.setAdapter(new TopGalleryAdapter(topGallery,
							headResources));
				}
			};
		}.execute();
		// 初始化头部大图及右边信息
		topGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// 更新右侧列表信息
				refreshMsg(position % headResources.size());
				// 更新右侧白点
				View point = null;
				for (int i = 0, len = points.getChildCount(); i < len; i++) {
					point = points.getChildAt(i);
					if (i == position % headResources.size()) {
						point.setBackgroundResource(R.drawable.point_sel);
					} else {
						point.setBackgroundResource(R.drawable.point);
					}
				}
			}

			private void refreshMsg(int position) {
				final IndexRem resource = headResources.get(position);
				// 根据资源类型来获取资源的信息 1：频道品牌 2：点播节目 3：专题
				switch (resource.getResourceType()) {
				case 1:
					// 更新右侧的信息
					updateChannelBrand(resource);
					break;
				case 2:
					// 更新右侧的信息
					updateAssetDetail(position);
					break;
				case 3:
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void updateChannelBrand(final IndexRem resource) {
		new AsyncTask<Void, Void, ChannelbrandJson>() {
			@Override
			protected ChannelbrandJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new LiveAction().getChannelbrandInfo(
						InterfaceUrls.GET_CHANNELBRAND_INFO,
						session.getUserCode(), resource.getResourceCode());
			};

			@Override
			protected void onPostExecute(ChannelbrandJson result) {
				if (null != result && 0 == result.getRet()) {
					updateBrandMsg(result.getChannelbrand());
				}
			}
		}.execute();
	}

	private void updateAssetDetail(int position) {
		new AsyncTask<String, Void, AssetDetailJson>() {
			@Override
			protected AssetDetailJson doInBackground(String... params) {
				Session session = Session.getInstance();
				return new VodAction().getAssetDetail(
						InterfaceUrls.GET_ASSETDETAIL, params[0],
						session.getUserCode());
			};

			@Override
			protected void onPostExecute(AssetDetailJson result) {
				if (null != result && 0 == result.getRet()) {
					updateMoiveMsg(result.getAssetInfo());
				}
			};
		}.execute(headResources.get(position).getResourceCode());
	}

	private void updateBrandMsg(Channelbrand channelbrand) {
		if (null == channelbrand) {
			this.findViewById(R.id.line).setVisibility(View.GONE);
			return;
		}
		// 设置媒资名称、英文名称、导演、主演、简介等信息。
		filmNameView.setText(channelbrand.getBrandName());
		filmNameEnView.setVisibility(View.GONE);
		this.findViewById(R.id.line).setVisibility(View.VISIBLE);
		director.setText(channelbrand.getChannelName());
		String host = channelbrand.getHost();
		if (!TextUtils.isEmpty(host)) {
			movieActors.setText(this.getString(R.string.host)
					+ channelbrand.getHost());
			movieActors.setVisibility(View.VISIBLE);
		} else {
			movieActors.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(channelbrand.getPalyDay())) {
			movietime.setText(this.getString(R.string.movie_time)
					+ channelbrand.getPalyDay() + " "
					+ channelbrand.getBeginTime() + "-"
					+ channelbrand.getEndTime());
			movietime.setVisibility(View.VISIBLE);
		} else {
			movietime.setVisibility(View.GONE);
		}
		resume.setText(channelbrand.getDesc());
	};

	/**
	 * 更新头部右侧的影视信息
	 * */
	private void updateMoiveMsg(final AssetInfo assetDetail) {
		if (null == assetDetail) {
			this.findViewById(R.id.line).setVisibility(View.GONE);
			return;
		}
		movieActors.setVisibility(View.VISIBLE);
		movietime.setVisibility(View.VISIBLE);
		// 设置媒资名称、英文名称、导演、主演、简介等信息。
		this.findViewById(R.id.line).setVisibility(View.VISIBLE);
		filmNameView.setText(assetDetail.getAssetName());
		filmNameEnView.setText(assetDetail.getAssetENName());
		if (!TextUtils.isEmpty(assetDetail.getDirector())) {
			director.setText(this.getString(R.string.movie_director)
					+ assetDetail.getDirector());
		} else {
			director.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(assetDetail.getLeadingActor())) {
			movieActors.setText(this.getString(R.string.movie_actors)
					+ assetDetail.getLeadingActor());
		} else {
			movieActors.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(assetDetail.getPublishDate())) {
			movietime.setText(this.getString(R.string.movie_time)
					+ assetDetail.getPublishDate());
		}
		resume.setText(assetDetail.getSummaryLong());
	}

	// 头部适配器数据缓存
	class TopGalleryItemHolder {
		public ImageView backGround;
	}

	/**
	 * 头部滚动适配器
	 * */
	public class TopGalleryAdapter extends AutoScrollGallery.TopGalleryAdapter {
		private CustormImageView galleryImage;

		public TopGalleryAdapter(AutoScrollGallery autoScrollGallery,
				ArrayList<IndexRem> headResources) {
			autoScrollGallery.super(headResources);
		}

		@Override
		public int getCount() {
			// 最多有八个
			if (null == headResources) {
				return 0;
			}
			// return Math.min(TOP_GALLERY_DATA_SIZE, headResources.size());
			return Integer.MAX_VALUE;
		}

		/** 取得显示图像View,传入数组ID值读取数组图像 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			galleryImage = new CustormImageView(mContext);
			galleryImage.setLayoutParams(new Gallery.LayoutParams(1024, 352));
			galleryImage.setScaleType(ImageView.ScaleType.FIT_XY);
			IndexRem rem = headResources.get(position % headResources.size());
			String imagePath = "";
			if (null != rem) {
				ArrayList<Poster> posters = rem.getPoster();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
			}
			galleryImage.setImageHttpUrl(imagePath);
			return galleryImage;
		}
	}

	/**
	 * 初始化推荐栏目
	 * */
	public void initRecommendCatalog() {
		new AsyncTask<Void, Void, CatalogJson>() {
			@Override
			protected CatalogJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getCatalog(InterfaceUrls.GET_CATALOG, 1,
						session.getUserCode(), 0, 5);
			}

			@Override
			protected void onPostExecute(CatalogJson result) {
				boolean bol = BaseJsonBean.checkResult(mContext, result);
				if (bol && 0 == result.getRet()) {
					recommendCatalogs = result.getCatalog();
					for (int index = 0, len = recommendCatalogs.size(); index < len; index++) {
						Catalog recommendCatalog = recommendCatalogs.get(index);
						if (null != recommendCatalog)
							initBottemGallery(index,
									recommendCatalog.getColumnName());
					}
				}
			}
		}.execute();
	}

	private synchronized void initBottemGallery(final int galleryIndex,
			final String columnName) {
		final RelativeLayout childLayout = (RelativeLayout) mInflater.inflate(
				R.layout.recommend_item, null);

		final LinearLayout recommendAssetsLayout = ((LinearLayout) childLayout
				.findViewById(R.id.recommendAsset));
		final HorizontalScrollView recBottemScrol = (HorizontalScrollView) childLayout
				.findViewById(R.id.recBottemScrol);

		TextView recommendTitle = (TextView) childLayout
				.findViewById(R.id.recommendTitle);
		recommendTitle.setText(columnName);
		recommendTitle.setPadding(0, 5, 0, 0);
		recommendLayout.addView(childLayout);

		new AsyncTask<String, Void, AssetListJson>() {
			@Override
			protected AssetListJson doInBackground(String... params) {
				Session session = Session.getInstance();
				return new VodAction().getAssetList(
						InterfaceUrls.GET_ASSET_LIST, BOTTEM_GALLERY_DATA_SIZE,
						1, session.getUserCode(), "0", params[0], "", "", "",
						"", "");
			};

			@Override
			protected void onPostExecute(AssetListJson result) {
				boolean bol = BaseJsonBean.checkResult(mContext, result);
				if (bol && 0 == result.getRet()) {
					childLayout.setVisibility(View.VISIBLE);
					ArrayList<Channelbrand> brandList = result
							.getChannelbrandList();
					ArrayList<AssetListInfo> assetList = result.getAssetList();
					if (null != brandList && 0 < brandList.size()) { // 直播推荐
						initLiveRecommend(brandList, recBottemScrol,
								recommendAssetsLayout);
					} else if (null != assetList && 0 < assetList.size()) {// 点播推荐
						initVodRecommend(assetList, recBottemScrol,
								recommendAssetsLayout);
					}
				}
			}
		}.execute(recommendCatalogs.get(galleryIndex).getColumnID());
	}

	/**
	 * 初始化底部Gallery(直播推荐) private HorizontalScrollView recBottemScrol = null;
	 * private LinearLayout recommendAssetsLayout = null;
	 * */
	private void initLiveRecommend(ArrayList<Channelbrand> channelbrands,
			HorizontalScrollView recBottemScrol,
			LinearLayout recommendAssetsLayout) {
		try {
			// 清空原来的数据并把滚动条恢复到最左边
			recommendAssetsLayout.removeAllViews();
			recBottemScrol.scrollTo(0, (int) recBottemScrol.getY());
			if (null == channelbrands) {
				return;
			}
			List<RelativeLayout> recommendAssetViews = new ArrayList<RelativeLayout>();
			LayoutInflater inflater = LayoutInflater.from(this);
			Channelbrand channelbrand = null;
			RelativeLayout childLayout = null;
			CustormImageView livePoster = null;
			for (int index = 0, len = channelbrands.size(); index < len; index++) {
				channelbrand = channelbrands.get(index);
				childLayout = (RelativeLayout) inflater.inflate(
						R.layout.live_recommend_item, null);
				// (15, 25, 15, 0);
				// (10, 0, 10, 0);
				childLayout.setPadding(10, 0, 10, 0);
				/** 设置图片 */
				livePoster = (CustormImageView) childLayout
						.findViewById(R.id.livePoster);
				String imagePath = "";
				ArrayList<Poster> posters = channelbrand.getPoster();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				livePoster.setImageHttpUrl(imagePath);

				ImageView gaoqingTag = (ImageView) childLayout
						.findViewById(R.id.gaoqingTag);
				if (1 == channelbrand.getVideoType()) {
					gaoqingTag.setVisibility(View.VISIBLE);
				} else {
					gaoqingTag.setVisibility(View.INVISIBLE);
				}
				livePoster.setOnClickListener(new RecommendProgramListener(
						index, channelbrands));
				// 节目名称
				TextView textName = (TextView) childLayout
						.findViewById(R.id.liveName);
				textName.setText(channelbrand.getBrandName());
				textName.setTextColor(Color.parseColor("#4E4E4E"));
				textName.setOnClickListener(new RecommendProgramListener(index,
						channelbrands));
				// 描述
				TextView textDesc = (TextView) childLayout
						.findViewById(R.id.liveInfo);
				textDesc.setTextColor(Color.parseColor("#8A8A8A"));
				String playday = channelbrand.getPalyDay();
				if (channelbrand.getPalyDay().length() > 3) {
					playday = playday.substring(0, 3);
				}
				textDesc.setText(playday + " " + channelbrand.getBeginTime()
						+ "-" + channelbrand.getEndTime());
				textDesc.setOnClickListener(new RecommendProgramListener(index,
						channelbrands));
				// 添加到视图中
				recommendAssetsLayout.addView(childLayout);
				// 添加到列表中
				recommendAssetViews.add(childLayout);
			}
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, LogUtils.getTAG(), e.getMessage());
		}
	}

	/**
	 * 初始化底部Gallery(点播推荐栏目)
	 * 
	 * */
	private void initVodRecommend(ArrayList<AssetListInfo> assetListInfos,
			HorizontalScrollView recBottemScrol,
			LinearLayout recommendAssetsLayout) {
		try {
			// 清空原来的数据并把滚动条恢复到最左边
			recommendAssetsLayout.removeAllViews();
			recBottemScrol.scrollTo(0, (int) recBottemScrol.getY());
			if (null == assetListInfos) {
				return;
			}

			List<RelativeLayout> recommendAssetViews = new ArrayList<RelativeLayout>();
			LayoutInflater inflater = LayoutInflater.from(this);
			RelativeLayout childLayout = null;
			CustormImageView assetPoster = null;
			TextView assetName = null;
			TextView assetAnticipation = null;
			AssetListInfo assetListInfo = null;
			ImageView gaoqingTag = null;
			for (int index = 0, len = assetListInfos.size(); index < len; index++) {
				assetListInfo = assetListInfos.get(index);

				childLayout = (RelativeLayout) inflater.inflate(
						R.layout.poster_name_anticip_item, null);
				childLayout.setPadding(10, 0, 10, 0);
				gaoqingTag = (ImageView) childLayout
						.findViewById(R.id.gaoqingTag);
				if (1 == assetListInfo.getVideoType()) {
					gaoqingTag.setVisibility(View.VISIBLE);
				} else {
					gaoqingTag.setVisibility(View.INVISIBLE);
				}
				/** 设置图片 */
				assetPoster = (CustormImageView) childLayout
						.findViewById(R.id.assetPoster);
				String imagePath = "";
				ArrayList<Poster> posters = assetListInfo.getPosterInfo();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				assetPoster.setImageHttpUrl(imagePath);
				assetPoster.setOnClickListener(new RecommendAssetListener(
						index, assetListInfos));
				// 价格标识
				ProductInfo product = assetListInfo.getProduct();
				TextView priceTag = (TextView) childLayout
						.findViewById(R.id.priceTag);
				if (null != product) {
					int price = product.getProductPrice();
					if (0 < price) {
						priceTag.setText((float) price / (float) 100 + "元");
						priceTag.setVisibility(View.VISIBLE);
					} else {
						priceTag.setVisibility(View.GONE);
					}
				}
				// 影片名称
				assetName = (TextView) childLayout.findViewById(R.id.assetName);
				assetName.setText(assetListInfo.getAssetName());
				assetName.setTextColor(Color.parseColor("#4E4E4E"));
				// 影片看点
				assetAnticipation = (TextView) childLayout
						.findViewById(R.id.assetAnticipation);
				assetAnticipation.setText(assetListInfo.getSummaryShort());
				assetAnticipation.setTextColor(Color.parseColor("#8A8A8A"));
				// 添加到视图中
				recommendAssetsLayout.addView(childLayout);
				// 添加到列表中
				recommendAssetViews.add(childLayout);
			}
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, LogUtils.getTAG(), e.getMessage());
		}
	}

	public class RecommendAssetListener implements OnClickListener {
		private int index;
		private ArrayList<AssetListInfo> recommendAssets;

		public RecommendAssetListener(int index,
				ArrayList<AssetListInfo> recommendAssets) {
			this.index = index;
			this.recommendAssets = recommendAssets;
		}

		@Override
		public void onClick(View v) {
			AssetListInfo asset = recommendAssets.get(index);
			Intent intent = new Intent();
			intent.setClass(mContext, ParticularActivity.class);
			intent.putExtra("resourceCode", asset.getResourceCode());
			startActivity(intent);
		}
	}

	public class RecommendProgramListener implements OnClickListener {
		private int index;
		private ArrayList<Channelbrand> channelbrands;

		public RecommendProgramListener(int index,
				ArrayList<Channelbrand> channelbrands) {
			this.index = index;
			this.channelbrands = channelbrands;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RecommendActivity.this,
					BrandParticularActivity.class);
			intent.putExtra("brandID", channelbrands.get(index).getBrandID());
			startActivity(intent);
		}
	}

	/**
	 * 初始化右侧直播推荐
	 * */
	private void initScrollRecommend(final int pageNo) {
		new AsyncTask<Void, Void, ChannelInfoJson>() {
			@Override
			protected ChannelInfoJson doInBackground(Void... params) {
				return new LiveAction().getChannels(
						// 其它来自服务器的分类
						InterfaceUrls.GET_CHANNELS, 0, CHANNEL_PAGE_SIZE,
						pageNo, "", "", "999", "");
			}

			@Override
			protected void onPostExecute(ChannelInfoJson result) {
				if (null != result && 0 == result.getRet()) {
					String arrayChannlid = "";
					channelInfo = result.getChannelInfo();
					for (ChannelInfo info : channelInfo) {
						arrayChannlid += info.getResourceCode() + ",";
					}
					// 去掉最后一个，号
					if (arrayChannlid.length() > 1) {
						arrayChannlid = arrayChannlid.substring(0,
								arrayChannlid.length() - 1);
					}
					// 获得所有频道的当前播放节目
					getChannelsCurrentPrograms(arrayChannlid);
				}
			}
		}.execute();
	}

	private void getChannelsCurrentPrograms(final String arrayChannlid) {
		new AsyncTask<Void, Void, ChannelsCurrentProgramsJson>() {
			@Override
			protected ChannelsCurrentProgramsJson doInBackground(Void... params) {
				return new LiveAction().getChannelsCurrentPrograms(
						// 其它来自服务器的分类
						InterfaceUrls.GET_CHANNELS_CURRENT_PROGRAMS,
						arrayChannlid);
			}

			@Override
			protected void onPostExecute(ChannelsCurrentProgramsJson result) {
				if (null == result || 0 != result.getRet()
						|| result.getChannelPrograms().size() < 1) {
				} else {
					ArrayList<ChannelsCurrentPrograms> ChannelsPrograms = result
							.getChannelPrograms();
					for (ChannelsCurrentPrograms info : ChannelsPrograms) {
						for (ChannelInfo cinfo : channelInfo) {
							String resourceCode = info.getChannelResourceCode();
							String rc = cinfo.getResourceCode();
							if (TextUtils.isEmpty(resourceCode)
									|| TextUtils.isEmpty(rc)) {
								continue;
							}
							if (resourceCode.equals(rc)) {
								cinfo.setCurrentProgram(info
										.getCurrentProgram());
							}
						}
					}
					// 初始化频道列表
					channelsAdapter.addNewDatas(channelInfo);
				}
			}
		}.execute();
	}

	/**
	 * 频道列表元素项
	 */
	public final class ViewHolder {
		public TextView live_channel_smallname;
		public CustormImageView live_channel_smallicon;
		public TextView live_channel_progrem;
		public ImageView play_btn_liverecommend;
	}

	/**
	 * 频道列表数据适配器
	 */
	public class ChannelInfoAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			final ChannelInfo channelInfo = (ChannelInfo) datas.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(mContext);
				convertView = mInflater.inflate(R.layout.live_recommend_list,
						null);
				holder.live_channel_smallname = (TextView) convertView
						.findViewById(R.id.live_channel_smallname);
				holder.live_channel_smallicon = (CustormImageView) convertView
						.findViewById(R.id.live_channel_smallicon);
				holder.live_channel_progrem = (TextView) convertView
						.findViewById(R.id.live_channel_progrem);
				holder.play_btn_liverecommend = (ImageView) convertView
						.findViewById(R.id.play_btn_liverecommend);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
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
			if (channelInfo.getCurrentProgram() != null) {
				holder.live_channel_progrem.setText(channelInfo
						.getCurrentProgram().getEventName());

				final String posterUrl = imagePath;
				holder.play_btn_liverecommend
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								PlayerUtil.playLive(mContext,
										channelInfo.getResourceCode(),
										channelInfo.getVideoType(),
										channelInfo.getChannelName(), posterUrl);
							}
						});
			}
			if (position == mSelectItem) {
				convertView.setBackgroundResource(R.drawable.channel_item_bg);
			} else {
				convertView.setBackgroundResource(0);
			}
			return convertView;
		}
	}

	private ImageView createPointImg() {
		ImageView point = new ImageView(mContext);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		point.setLayoutParams(layoutParams);
		point.setBackgroundResource(R.drawable.point);
		return point;
	}

	// 通过Service的类名来判断是否启动某个服务
	private boolean checkServiceIfStart() {
		ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
				.getRunningServices(30);
		final String musicClassName = "com.coship.ott.service.BookNotifyService";
		for (int i = 0; i < mServiceList.size(); i++) {
			if (musicClassName.equals(mServiceList.get(i).service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);

	}

}