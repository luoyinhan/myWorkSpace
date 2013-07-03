package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.vod.AssetListInfo;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.dto.vod.Catalog;
import com.coship.ott.transport.dto.vod.CatalogJson;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.dto.vod.Pram;
import com.coship.ott.transport.dto.vod.PramJson;
import com.coship.ott.transport.dto.vod.ProductInfo;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.PlayerUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;

/**
 * 点播
 */
public class VodActivity extends CommonViewActivity implements OnClickListener {
	private Context mContext;
	private LayoutInflater mInflater;
	// 每页数据数量
	private static final int VOD_PAGE_SIZE = 12;
	// 数据展示样式(列表,九宫格)
	private Button posterSwitcher = null;
	private Button listSwitcher = null;
	private GridView gridView = null;
	private ListView listView = null;
	// 分类关键字
	private String[] params = new String[] { "assetType", "OriginName",
			"PublishDate" };
	// 记忆当前选择
	// 查询类型
	private String queryType = "0";
	// 排序
	private String orderTag = "";
	// 分类
	private String assetTypeKey = "";
	// 年代
	private String publishDateKey = "";
	// 地区
	private String originNameKey = "";
	private LinearLayout vodCategories;
	// 一级上一次菜单游标，避免重复点击加载同一个
	private int menuIndex = -1;
	// 一级菜单当前游标
	private int currentMenuIndex = 0;
	// 类型
	private LinearLayout assetTypeLayout;
	// 地区
	private LinearLayout originNameLayout;
	// 年份
	private LinearLayout publishDateLayout;

	private ScrollLoader loader;
	private MovieAdapter movieAdapter = null;
	private MovieGridViewAdapter movieGridViewAdapter = null;
	private List<TextView> views;
	// 一级栏目列表
	private ArrayList<Catalog> catalogs;
	private int retryTimes = 0;
	private ProgressDialog mProgressDialog;
	private static final String TAG = "VodActivity";
	private LinearLayout topMenu;
	private Boolean isloading = false;
	private TextView titleTxt;
	private ScrollLoader mListLoader;
	private String mColumnId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vod);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		mInflater = LayoutInflater.from(mContext);
		setupView();
		// 初始化页面
		loadMenuCatalogData();
		// 最新. 最热 按钮
		initHotNew();
		// 初始化分类数据
		initMoreLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 隐藏公告标题
		notice = (ImageView) findViewById(R.id.notice);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		noticeFull.setVisibility(View.GONE);
		notice.setVisibility(View.GONE);
	}

	private void setupView() {
		// 隐藏公告标题
		notice = (ImageView) findViewById(R.id.notice);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		noticeFull.setVisibility(View.INVISIBLE);
		notice.setVisibility(View.INVISIBLE);
		titleTxt = (TextView) this.findViewById(R.id.titleTxt);
		titleTxt.setText(R.string.title_vod);
		topMenu = (LinearLayout) this.findViewById(R.id.topMenu);
		// 筛选按钮
		findViewById(R.id.typeMoreOpen).setOnClickListener(this);
		findViewById(R.id.typeMoreClose).setOnClickListener(this);
		// 分类筛选
		findViewById(R.id.typeSelect).setOnClickListener(this);

		vodCategories = (LinearLayout) this.findViewById(R.id.vodCategories);
		// 分类
		assetTypeLayout = (LinearLayout) this
				.findViewById(R.id.assetTypeLayout);
		originNameLayout = (LinearLayout) this
				.findViewById(R.id.originNameLayout);
		publishDateLayout = (LinearLayout) this
				.findViewById(R.id.publishDateLayout);
		// 视图切换按钮及事件处理
		posterSwitcher = (Button) findViewById(R.id.posterSwitcher);
		posterSwitcher.setOnClickListener(new ShowTypeListener(0));
		listSwitcher = (Button) findViewById(R.id.listSwitcher);
		listSwitcher.setOnClickListener(new ShowTypeListener(1));
		// 列表视图
		listView = (ListView) findViewById(R.id.movie_listview);
		movieAdapter = new MovieAdapter();
		listView.setAdapter(movieAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				AssetListInfo assetInfo = (AssetListInfo) movieAdapter
						.getItemData(arg2);
				startParticular(assetInfo.getType(),
						assetInfo.getResourceCode());
			}
		});
		mListLoader = new ScrollLoader(mContext, listView, new CallBack() {
			@Override
			public void loadData(int pageNo) {
				if (!isloading) {
					getAssetList(pageNo);
				}
			}
		});
		// 表格视图
		gridView = (GridView) findViewById(R.id.movie_gridview);
		movieGridViewAdapter = new MovieGridViewAdapter();
		gridView.setAdapter(movieGridViewAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AssetListInfo assetInfo = (AssetListInfo) movieGridViewAdapter
						.getItemData(position);
				startParticular(assetInfo.getType(),
						assetInfo.getResourceCode());
			}
		});
		loader = new ScrollLoader(mContext, gridView, new CallBack() {
			@Override
			public void loadData(int pageNo) {
				if (!isloading) {
					getAssetList(pageNo);
				}
			}
		});
		// 构建加载中窗口
		mProgressDialog = new ProgressDialog(VodActivity.this.getParent());
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setTitle("");
		mProgressDialog.setMessage("正在加载数据...");
	}

	// 取（顶部）栏目列表数据
	private void loadMenuCatalogData() {
		new AsyncTask<Void, Void, CatalogJson>() {
			protected void onPreExecute() {
				if (null != mProgressDialog) {
					mProgressDialog.show();
				}
			};

			@Override
			protected CatalogJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getCatalog(InterfaceUrls.GET_CATALOG, 2,
						session.getUserCode(), 0, 5);
			}

			@Override
			protected void onPostExecute(CatalogJson result) {
				if (null != result && 0 == result.getRet()) {
					catalogs = result.getCatalog();
					if (catalogs == null || catalogs.size() < 1) {
						if (retryTimes < 3) {
							loadMenuCatalogData();
							++retryTimes;
						} else {
							if (mProgressDialog != null) {
								mProgressDialog.dismiss();
							}
							retryTimes = 0;
							return;
						}
					}
					initLayoutContent();
					// 默认取第一个栏目的最新影片
					getNewAssets();
				} else {
					if (retryTimes < 3) {
						loadMenuCatalogData();
						++retryTimes;
					} else {
						if (mProgressDialog != null) {
							mProgressDialog.dismiss();
						}
						retryTimes = 0;
					}
				}
			};
		}.execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 解决用户中心取消登录后，销毁全部activity导致loadMenuCatalogData()时出现
		// android.view.WindowManager$BadTokenException: Unable to add window --
		// token android.os.BinderProxy@416a3428 is not valid; is your activity
		// running?的问题。
		mProgressDialog = null;
		retryTimes = 4;
	}

	/**
	 * 初始化（顶部）栏目列表
	 * */
	private void initLayoutContent() {
		// 如果没有取到数据，则返回
		if (null == catalogs || catalogs.size() < 1) {
			return;
		}

		// 顶部栏目列表
		LinearLayout vodCategories = (LinearLayout) this
				.findViewById(R.id.vodCategories);
		// int m = 0;
		// 默认栏目 的编号
		mColumnId = catalogs.get(0).getColumnID();
		int index = 0;
		for (Catalog root : catalogs) {
			TextView catalogName = createTxtView(root.getAlias());
			vodCategories.addView(catalogName);
			List<Catalog> sublist = root.getSubList();
			// 主栏目的侦听函数
			catalogName.setOnClickListener(new TopListener(sublist, index));
			if (sublist != null && !sublist.isEmpty() && index == 0) {
				// 在头部加一个全部的子栏目
				// 显示默认的二级栏目菜单
				TextView allCatalogName = null;
				allCatalogName = createTxtView("全部");
				topMenu.addView(allCatalogName);
				allCatalogName.setOnClickListener(new CatalogListener(
						currentMenuIndex, 0, sublist));
				allCatalogName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.index_tag);
				int m = 0;
				for (Catalog subCata : sublist) {
					TextView subcatalogName = createTxtView(subCata.getAlias());
					topMenu.addView(subcatalogName);
					// 子栏目侦听函数
					subcatalogName.setOnClickListener(new CatalogListener(-1,
							m++, sublist));
				}

			}
			index++;
		}

		// 设置一级栏目的焦点
		((TextView) vodCategories.getChildAt(0))
				.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.index_tag);
		// int x = 0;

		/*
		 * int len = catalogs.size(); List<String> listColumn = new
		 * ArrayList<String>();// 子栏目 List<String> listParent = new
		 * ArrayList<String>();// 父栏目 List<Integer> listid = new
		 * ArrayList<Integer>();
		 * 
		 * 
		 * for (int index = 0; index < len; index++) { catalog =
		 * catalogs.get(index); String columnId = catalog.getColumnID(); String
		 * parentId = catalog.getParentID(); listColumn.add(columnId);
		 * listParent.add(parentId); } listid.clear(); for (int i = 0; i <
		 * listParent.size(); i++) { for (int j = 0; j < listColumn.size(); j++)
		 * { if (listParent.get(i).equals(listColumn.get(j))) break; if (j ==
		 * listColumn.size() - 1) { listid.add(i); } } }
		 * 
		 * // 默认栏目 currentMenuIndex = listid.get(0);
		 * 
		 * int index = 0; for (int m = 0; m < listid.size(); m++) { catalog =
		 * catalogs.get(listid.get(m)); TextView catalogName =
		 * createTxtView(catalog.getAlias()); // 添加到列表中
		 * vodCategories.addView(catalogName); // 点击 第一级 栏目
		 * catalogName.setOnClickListener(new TopListener(listParent, listid
		 * .get(m), index++)); } // 设置一级栏目的焦点 ((TextView)
		 * vodCategories.getChildAt(0))
		 * .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
		 * R.drawable.index_tag);
		 * 
		 * // 显示默认的二级栏目菜单 int x = 0; // 在头部加一个全部的子栏目 TextView allCatalogName
		 * =null; allCatalogName = createTxtView("全部");
		 * topMenu.addView(allCatalogName);
		 * allCatalogName.setOnClickListener(new
		 * CatalogListener(currentMenuIndex, 0));
		 * allCatalogName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
		 * R.drawable.index_tag); TextView catalogName = null; for (int n = 0; n
		 * < listParent.size(); n++) { if
		 * (listColumn.get(listid.get(0)).equals(listParent.get(n))) {
		 * catalogName = createTxtView(catalogs.get(n).getAlias()); // 添加到列表中
		 * topMenu.addView(catalogName); // 修改子栏目id传递的参数
		 * catalogName.setOnClickListener(new CatalogListener(n, x + 1)); x++; }
		 * } View view = topMenu.getChildAt(0); if (null != view) { //
		 * 设置二级栏目菜单默认焦点 // ((TextView) view).setTextColor(Color.RED); }
		 */
	}

	/**
	 * 获取指定栏目下指定时间段的影片
	 * */
	private void getAssetsByCondition() {
		queryType = "4";
		orderTag = "2";
		movieAdapter.removeAllDatas();
		movieGridViewAdapter.removeAllDatas();
		getAssetList(1);
	}

	/**
	 * 获取指定栏目下最新影片
	 * */
	private void getNewAssets() {
		queryType = "0";
		orderTag = "1";
		movieAdapter.removeAllDatas();
		movieGridViewAdapter.removeAllDatas();
		getAssetList(1);
	}

	/**
	 * 获取指定栏目下最热影片
	 * */
	private void getHotAssets() {
		queryType = "0";
		orderTag = "2";
		movieAdapter.removeAllDatas();
		movieGridViewAdapter.removeAllDatas();
		getAssetList(1);
	}

	/**
	 * 获取指定（頂部）栏目下的数据
	 * 
	 * @param catalogColumnID
	 *            栏目ID
	 * */
	private void getAssetList(final int curPage) {
		if (catalogs == null || currentMenuIndex < 0
				|| currentMenuIndex > catalogs.size()) {
			return;
		}
		LogUtils.trace(Log.DEBUG, TAG, "start load assert list:"
				+ catalogs.get(currentMenuIndex).getColumnID());

		try {
			assetTypeKey = URLEncoder.encode(assetTypeKey, "UTF-8");
			originNameKey = URLEncoder.encode(originNameKey, "UTF-8");
			publishDateKey = URLEncoder.encode(publishDateKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		new AsyncTask<String, Void, AssetListJson>() {
			protected void onPreExecute() {
				if (null != mProgressDialog) {
					mProgressDialog.show();
				}
				isloading = true;
			};

			@Override
			protected AssetListJson doInBackground(String... params) {
				Session session = Session.getInstance();
				if (catalogs != null) {
					return new VodAction().getAssetList(
							InterfaceUrls.GET_ASSET_LIST, VOD_PAGE_SIZE,
							curPage, session.getUserCode(), queryType,
							mColumnId, "", assetTypeKey, originNameKey,
							publishDateKey, orderTag);
				}
				return null;
			}

			@Override
			protected void onPostExecute(AssetListJson result) {
				if (null != mProgressDialog) {
					mProgressDialog.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					// 列表视图
					movieAdapter.addNewDatas(result.getAssetList());
					// 海报视图
					movieGridViewAdapter.addNewDatas(result.getAssetList());
					loader.setCurPage(result.getCurPage());
					loader.setPageCount(result.getPageCount());
					mListLoader.setCurPage(result.getCurPage());
					mListLoader.setPageCount(result.getPageCount());

				} else {
					if (0 == loader.getCurPage()) { // 加载第一页失败（切换栏目）清除所有数据
						movieAdapter.removeAllDatas();
					}
				}
				isloading = false;
			};
		}.execute();
	}

	/**
	 * (頂部)栏目点击事件监听器
	 * */
	private class TopListener implements OnClickListener {

		private int index;// 主栏目的下标
		// private int m;
		private List<Catalog> sublist;

		// List<Integer> list2id = new ArrayList<Integer>();

		public TopListener(List<Catalog> sublist, int index) {
			this.index = index;
			this.sublist = sublist;
			// this.m = m;
		}

		@Override
		public void onClick(View v) {
			topMenu.removeAllViews();
			// list2id.clear();
			TextView catalogName = null;
			if (!isloading) {
				// currentMenuIndex = index;
				mColumnId = catalogs.get(index).getColumnID();
				initHotNew();
				getNewAssets();
				// 改变一级栏目焦点
				TextView paramView = null;
				for (int childIndex = 0, len = vodCategories.getChildCount(); childIndex < len; childIndex++) {
					paramView = (TextView) vodCategories.getChildAt(childIndex);
					if (childIndex == index) {
						paramView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
								0, R.drawable.index_tag);
					} else {
						paramView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
								0, 0);
					}
				}

				// for (int iTmp = 0; iTmp < listParent.size(); iTmp++) {
				// if (catalogs.get(index).getColumnID()
				// .equals(catalogs.get(iTmp).getParentID())) {
				// list2id.add(iTmp);
				// }
				// }

				// 在头部加一个全部的子栏目
				TextView allCatalogName = null;
				allCatalogName = createTxtView("全部");
				topMenu.addView(allCatalogName);
				// 直接传递当前父栏目的id
				allCatalogName.setOnClickListener(new CatalogListener(index, 0,
						sublist));
				for (int m = 0; m < sublist.size(); m++) {// 子栏目
					catalogName = createTxtView(sublist.get(m).getAlias());
					// 添加到列表中
					topMenu.addView(catalogName);
					// 子栏目添加侦听函数
					catalogName.setOnClickListener(new CatalogListener(-1, m,
							sublist));
				}

				// 设置二级栏目菜单默认焦点
				View menu = topMenu.getChildAt(0);
				if (menu != null) {
					((TextView) menu).setCompoundDrawablesWithIntrinsicBounds(
							0, 0, 0, R.drawable.index_tag);
				}
			}
		}
	}

	/**
	 * (2级)栏目点击事件监听器
	 * */
	private class CatalogListener implements OnClickListener {

		private int index;
		private int m;
		private List<Catalog> sublist;

		public CatalogListener(int index, int m, List<Catalog> sublist) {
			this.index = index;
			this.m = m;
			this.sublist = sublist;
		}

		@Override
		public void onClick(View v) {
			movieAdapter.removeAllDatas();
			movieGridViewAdapter.removeAllDatas();
			// 设置焦点
			TextView paramView = null;
			for (int childIndex = 0, len = topMenu.getChildCount(); childIndex < len; childIndex++) {
				paramView = (TextView) topMenu.getChildAt(childIndex);
				if (index != -1 && childIndex == 0) {
					paramView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
							R.drawable.index_tag);
				} else {
					if (childIndex == m + 1 && index == -1) {
						paramView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
								0, R.drawable.index_tag);
					} else {
						paramView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
								0, 0);
					}
				}
			}
			// currentMenuIndex = index;// 主栏目的下标id
			// if (currentMenuIndex == menuIndex) {
			// return;
			// }
			if (index != -1) {
				mColumnId = catalogs.get(index).getColumnID();// 主栏目的id
			} else {
				mColumnId = sublist.get(m).getColumnID();// 子栏目id
			}
			// 默认指定栏目的最新影片
			initHotNew();
			getNewAssets();
			// menuIndex = currentMenuIndex;
		}
	}

	/**
	 * 初始化最新最热按钮及事件处理
	 * */
	private void initHotNew() {
		views = new ArrayList<TextView>();
		views.add((TextView) findViewById(R.id.movieNew)); // 最新
		views.add((TextView) findViewById(R.id.movieHot)); // 最热
		// 添加监听器
		for (int i = 0, len = views.size(); i < len; i++) {
			views.get(i).setOnClickListener(new TypeHotNewListener(views, i));
		}
		// 默认选中最新
		views.get(0).setBackgroundResource(R.drawable.switch_focus);
		views.get(1).setBackgroundResource(0);
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
				if (viewIndex == index) {
					view.setBackgroundResource(R.drawable.switch_focus);
				} else {
					view.setBackgroundResource(0);
				}
			}
			if (!isloading) {
				if (0 == index) {// 最新影片
					isloading = true;
					getNewAssets();
				} else {// 最热影片
					isloading = true;
					getHotAssets();

				}
			}

		}
	}

	/**
	 * 数据样式(海报、列表)切换
	 * 
	 * @author 905421
	 * 
	 */
	private class ShowTypeListener implements OnClickListener {
		// 显示类型，0为列表布局，1为海报布局，默认为海报布局1
		private int showType;

		public ShowTypeListener(int showType) {
			this.showType = showType;
		}

		@Override
		public void onClick(View v) {
			if (0 == showType) { // 列表视图
				listView.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
				posterSwitcher
						.setBackgroundResource(R.drawable.poster_switcher_sel);
				listSwitcher.setBackgroundResource(R.drawable.list_switcher);

			} else if (1 == showType) { // 海报视图
				gridView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				posterSwitcher
						.setBackgroundResource(R.drawable.poster_switcher);
				listSwitcher
						.setBackgroundResource(R.drawable.list_switcher_sel);
			}
		}
	}

	// 初始化类型、地区、年份信息
	private void initMoreLayout() {
		for (int i = 0, len = params.length; i < len; i++) {
			final int index = i;
			String param = params[i];
			new AsyncTask<String, Void, PramJson>() {

				@Override
				protected PramJson doInBackground(String... params) {
					return new VodAction().getPram(InterfaceUrls.GET_PRAM,
							params[0]);
				}

				@Override
				protected void onPostExecute(PramJson result) {
					if (null != result && 0 == result.getRet()) {
						initLayout(result.getDatas(), index);
					}
				}

			}.execute(param);
		}
	}

	private void initLayout(ArrayList<Pram> datas, int typeIndex) {
		// 如果没有取到数据，则返回
		if (null == datas || datas.size() < 1) {
			return;
		}
		LinearLayout layout = null;
		switch (typeIndex) {
		case 0:
			layout = assetTypeLayout;
			assetTypeKey = datas.get(0).getPramKey();
			break;
		case 1:
			layout = originNameLayout;
			originNameKey = datas.get(0).getPramKey();
			break;
		case 2:
			layout = publishDateLayout;
			publishDateKey = datas.get(0).getPramKey();
			break;
		}

		Pram param = null;
		for (int pramIndex = 0, len = datas.size(); pramIndex < len; pramIndex++) {
			param = datas.get(pramIndex);
			TextView pramView = createTxtView(param.getPramValue());
			// 添加到列表中
			layout.addView(pramView);
			pramView.setOnClickListener(new ParamListener(typeIndex, pramIndex,
					param.getPramKey()));
		}
		((TextView) layout.getChildAt(0))
				.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.index_tag);
	};

	class ParamListener implements OnClickListener {
		private int typeIndex;
		private int pramIndex;
		private String pramKey;

		public ParamListener(int typeIndex, int pramIndex, String pramKey) {
			this.typeIndex = typeIndex;
			this.pramIndex = pramIndex;
			this.pramKey = pramKey;
		}

		@Override
		public void onClick(View v) {
			LinearLayout layout = null;
			switch (typeIndex) {
			case 0:
				layout = assetTypeLayout;
				assetTypeKey = pramKey;
				break;
			case 1:
				layout = originNameLayout;
				originNameKey = pramKey;
				break;
			case 2:
				layout = publishDateLayout;
				publishDateKey = pramKey;
				break;
			}
			// 设置焦点
			TextView paramView = null;
			for (int childIndex = 0, len = layout.getChildCount(); childIndex < len; childIndex++) {
				paramView = (TextView) layout.getChildAt(childIndex);
				if (childIndex == pramIndex) {
					paramView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
							R.drawable.index_tag);
				} else {
					paramView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
							0);
				}
			}
			getAssetsByCondition();
		}
	}

	/**
	 * 生成一个指定字符的TextView
	 * 
	 * @param text
	 *            指定字符
	 * */
	private TextView createTxtView(String text) {
		TextView catalogName = new TextView(mContext);
		catalogName.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		catalogName.setGravity(Gravity.CENTER);
		catalogName.setTextColor(Color.WHITE);
		catalogName.setPadding(20, 0, 20, 0);
		catalogName.setTextSize(14);
		catalogName.setText(text);
		return catalogName;
	}

	/**
	 * 定义一个电影信息列表视图结果列表项
	 */
	public final class ViewHolder {
		public ImageView gaoqingTag;// 高清标识
		public TextView priceTag;// 价格标识
		public CustormImageView movie_icon; // 海报
		public TextView movie_title; // 电影名称
		public TextView movie_time; // 上映时间
		public TextView movie_director; // 导演
		public TextView movie_actors; // 主演
		public TextView movie_info; // 简介
		public TextView movie_mark; // 分数
		public Button movie_playBtn; // 播放按钮
	}

	/**
	 * 电影信息列表视图监听器
	 */
	public class MovieAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.vod_list, null);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.priceTag = (TextView) convertView
						.findViewById(R.id.priceTag);
				holder.movie_icon = (CustormImageView) convertView
						.findViewById(R.id.movie_icon);
				holder.movie_title = (TextView) convertView
						.findViewById(R.id.movie_title);
				holder.movie_time = (TextView) convertView
						.findViewById(R.id.movie_time);
				holder.movie_director = (TextView) convertView
						.findViewById(R.id.movie_director);
				holder.movie_actors = (TextView) convertView
						.findViewById(R.id.movie_starring);
				holder.movie_info = (TextView) convertView
						.findViewById(R.id.movie_info);
				holder.movie_mark = (TextView) convertView
						.findViewById(R.id.movie_mark);
				holder.movie_playBtn = (Button) convertView
						.findViewById(R.id.movie_playBtn);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final AssetListInfo assetInfo = (AssetListInfo) datas.get(position);
			String imagePath = "";
			ArrayList<Poster> posters = assetInfo.getPosterInfo();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			if (!imagePath.equals("")) {
				holder.movie_icon.setImageHttpUrl(imagePath);
			} else {
				holder.movie_icon.setImageResource(0);// 先只置为空
				holder.movie_icon.setImageHttpUrl("");
			}
			// holder.movie_icon.setImageHttpUrl(imagePath);
			holder.movie_title.setText(assetInfo.getAssetName());
			String time = assetInfo.getPublishDate();
			if (TextUtils.isEmpty(time)) {
				convertView.findViewById(R.id.movieTime).setVisibility(
						View.INVISIBLE);
			} else {
				convertView.findViewById(R.id.movieTime).setVisibility(
						View.VISIBLE);
				holder.movie_time.setText(time);
			}
			String director = assetInfo.getDirector();
			if (TextUtils.isEmpty(director)) {
				convertView.findViewById(R.id.movieDirector).setVisibility(
						View.INVISIBLE);
				holder.movie_director.setText("");
			} else {
				convertView.findViewById(R.id.movieDirector).setVisibility(
						View.VISIBLE);
				holder.movie_director.setText(director);
			}
			String actor = assetInfo.getLeadingActor();
			if (TextUtils.isEmpty(actor)) {
				convertView.findViewById(R.id.movieStarring).setVisibility(
						View.INVISIBLE);
				holder.movie_actors.setText("");
			} else {
				convertView.findViewById(R.id.movieStarring).setVisibility(
						View.VISIBLE);
				holder.movie_actors.setText(actor);
			}
			holder.movie_info.setText(assetInfo.getSummaryLong());// 节目简介
			holder.movie_playBtn.setFocusable(false); // 失去聚焦
			holder.movie_playBtn.setOnClickListener(new ItemPlayListener(
					assetInfo));
			if (1 == assetInfo.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			ProductInfo product = assetInfo.getProduct();
			if (null != product) {
				int price = product.getProductPrice();
				if (0 < price) {
					holder.priceTag.setText((float) price / (float) 100 + "元");
					holder.priceTag.setVisibility(View.VISIBLE);
				} else {
					holder.priceTag.setVisibility(View.GONE);
				}
				int chargemode = product.getChargeMode();
				if (chargemode == 1 || chargemode == 3) {// 1：包月，3免费
					holder.priceTag.setVisibility(View.INVISIBLE);// 包月和免费不显示价格标签
				}
			} else {
				holder.priceTag.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	/**
	 * 定义一个电影信息表格视图结果列表项
	 */
	public final class ViewGridViewHolder {
		public ImageView gaoqingTag;
		public CustormImageView assetPoster;
		public TextView assetName;
		public TextView assetAnticipation;
		public ImageView itemPlayBtn;
		public TextView priceTag;
	}

	/**
	 * 电影信息表格视图监听器
	 */
	public class MovieGridViewAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewGridViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewGridViewHolder();
				convertView = mInflater.inflate(
						R.layout.poster_name_anticip_item, null);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.priceTag = (TextView) convertView
						.findViewById(R.id.priceTag);
				holder.assetPoster = (CustormImageView) convertView
						.findViewById(R.id.assetPoster);
				holder.assetName = (TextView) convertView
						.findViewById(R.id.assetName);
				holder.assetAnticipation = (TextView) convertView
						.findViewById(R.id.assetAnticipation);
				holder.itemPlayBtn = (ImageView) convertView
						.findViewById(R.id.itemPlaybtn);

				convertView.setTag(holder);
			} else {
				holder = (ViewGridViewHolder) convertView.getTag();
			}
			AssetListInfo assetInfo = (AssetListInfo) this.datas.get(position);
			String imagePath = "";
			ArrayList<Poster> posters = assetInfo.getPosterInfo();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			if (!imagePath.equals("")) {
				holder.assetPoster.setImageHttpUrl(imagePath);
			} else {
				holder.assetPoster.setImageResource(0);// 先只置为空
				holder.assetPoster.setImageHttpUrl("");
			}
			holder.assetName.setText(assetInfo.getAssetName());
			String summary = assetInfo.getSummaryShort();
			if (TextUtils.isEmpty(summary)) {
				summary = assetInfo.getSummaryLong();
				if (!TextUtils.isEmpty(summary)) {
					summary = summary.substring(0,
							Math.min(summary.length(), 8));
				} else {
					summary = "";
				}
			}
			holder.assetAnticipation.setText(summary);
			if (1 == assetInfo.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}

			ProductInfo product = assetInfo.getProduct();
			if (null != product) {
				int price = product.getProductPrice();
				if (0 < price) {
					holder.priceTag.setText((float) price / (float) 100 + "元");
					holder.priceTag.setVisibility(View.VISIBLE);
				} else {
					holder.priceTag.setVisibility(View.GONE);
				}
				int chargemode = product.getChargeMode();
				if (chargemode == 1 || chargemode == 3) {// 1：包月，3免费
					holder.priceTag.setVisibility(View.INVISIBLE);// 包月和免费不显示价格标签
				}
			} else {
				holder.priceTag.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}
	}

	// 推荐点播节目播放按钮事件处理
	public class ItemPlayListener implements OnClickListener {
		private AssetListInfo recommendAsset;

		public ItemPlayListener(AssetListInfo recommendAsset) {
			this.recommendAsset = recommendAsset;
		}

		@Override
		public void onClick(View v) {
			String posterUrl = "";
			ArrayList<Poster> posters = recommendAsset.getPosterInfo();
			if (null != posters && posters.size() > 0) {
				Poster poster = posters.get(0);
				if (null != poster) {
					posterUrl = poster.getLocalPath();
				}
			}
			PlayerUtil
					.playVod(mContext, recommendAsset.getResourceCode(),
							recommendAsset.getVideoType(),
							recommendAsset.getAssetName(), posterUrl,
							recommendAsset.getAssetID(),
							recommendAsset.getProviderID());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.typeMoreOpen:
			// 取年份和地区分类列表数据
			findViewById(R.id.typeMoreClose).setVisibility(View.VISIBLE);
			findViewById(R.id.typeMoreOpen).setVisibility(View.GONE);
			findViewById(R.id.typeMoveContent).setVisibility(View.VISIBLE);
			break;
		case R.id.typeMoreClose:
			// 取年份和地区分类列表数据
			findViewById(R.id.typeMoreClose).setVisibility(View.GONE);
			findViewById(R.id.typeMoreOpen).setVisibility(View.VISIBLE);
			findViewById(R.id.typeMoveContent).setVisibility(View.GONE);
			break;
		case R.id.typeSelect:
			if (View.VISIBLE == findViewById(R.id.typeMoreOpen).getVisibility()) {
				// 打开筛选列表
				findViewById(R.id.typeMoreClose).setVisibility(View.VISIBLE);
				findViewById(R.id.typeMoreOpen).setVisibility(View.GONE);
				findViewById(R.id.typeMoveContent).setVisibility(View.VISIBLE);
			} else {
				// 关闭筛选列表
				findViewById(R.id.typeMoreClose).setVisibility(View.GONE);
				findViewById(R.id.typeMoreOpen).setVisibility(View.VISIBLE);
				findViewById(R.id.typeMoveContent).setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 进入详情页面
	 * */
	private void startParticular(int type, String resourceCode) {
		Intent intent = new Intent(mContext, ParticularActivity.class);
		intent.putExtra("resourceType", type);
		intent.putExtra("resourceCode", resourceCode);
		startActivity(intent);
	}
}