package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.dto.vod.Resource;
import com.coship.ott.transport.dto.vod.ResourceJson;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;

/**
 * 搜索结果
 */
public class SearchResultActivity extends Activity implements OnClickListener {
	private static final String TAG = "SearchResultActivity";
	// 每次查询返回数据条数
	private static final int SEARCH_PAGE_SIZE = 24;
	private Context mContext;
	private LayoutInflater mInflater;
	// 搜索结果
	private TextView searchResultView;
	private GridView resultView = null;
	// 滑动加载数据
	private ScrollLoader loader;
	// 数据适配器
	private ResultAdapter adapter;
	// 搜索结果分类
	private LinearLayout searchResultLayout;
	// 全部
	private TextView searchResultAll;
	// 点播
	private TextView searchResultVod;
	// 直播
	private TextView searchResultLive;
	private ArrayList<TextView> typeList = new ArrayList<TextView>();
	private String keyWord;
	private String userWord;
	// 查询类型
	private int queryType = 0;
	// 数据加载中
	private ProgressDialog mProgressDialog;
	private TextView recommendResult;
	protected boolean isRecommend = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		mInflater = LayoutInflater.from(mContext);
		setupView();
		keyWord = getIntent().getStringExtra("keyWord");
		userWord = getIntent().getStringExtra("keyWord");
		if (!TextUtils.isEmpty(keyWord)) {
			try {
				keyWord = URLEncoder.encode(keyWord, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				LogUtils.trace(Log.DEBUG, TAG,
						"搜索关键字转换为UTF-8格式时出现错误，信息：" + e1.getMessage());
			}
			initSearchResult(1);
		}
	}

	private void setupView() {
		searchResultLayout = (LinearLayout) findViewById(R.id.searchResultLayout);
		searchResultView = (TextView) findViewById(R.id.searchResult);
		recommendResult = (TextView) findViewById(R.id.recommendResult);
		// 搜索结果列表
		resultView = (GridView) findViewById(R.id.search_result_gridview);
		adapter = new ResultAdapter();
		resultView.setAdapter(adapter);
		loader = new ScrollLoader(mContext, resultView, new CallBack() {
			@Override
			public void loadData(int pageNo) {
				initSearchResult(pageNo);
			}
		});
		resultView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				Resource resource = (Resource) adapter.getItemData(position);
				// 资源类型 1：直播节目 2：点播节目
				if (1 == resource.getResourceType()) {
					intent.setClass(SearchResultActivity.this,
							ProgramParticularActivity.class);
					intent.putExtra("programId", resource.getResourceCode());
				} else if (2 == resource.getResourceType()) {
					intent.setClass(SearchResultActivity.this,
							ParticularActivity.class);
					intent.putExtra("type", resource.getType());
					intent.putExtra("resourceCode", resource.getResourceCode());
				}
				startActivity(intent);
			}
		});
		// 分类筛选按钮
		searchResultAll = (TextView) findViewById(R.id.searchResultAll);
		searchResultAll.setOnClickListener(this);
		typeList.add(searchResultAll);
		searchResultLive = (TextView) findViewById(R.id.searchResultLive);
		searchResultLive.setOnClickListener(this);
		typeList.add(searchResultLive);
		searchResultVod = (TextView) findViewById(R.id.searchResultVod);
		searchResultVod.setOnClickListener(this);
		typeList.add(searchResultVod);
		// 返回按钮
		findViewById(R.id.search_backicon).setOnClickListener(this);
		// 数据加载中
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setTitle("");
		mProgressDialog.setMessage("正在加载数据...");
	}

	/**
	 * 设置搜索结果
	 * */
	private void initSearchResult(final int pageNo) {
		new AsyncTask<Void, Void, ResourceJson>() {
			private String searchResult = getString(R.string.search_result)
					.replace("$keyWord", userWord);

			protected void onPreExecute() {
				if (null != mProgressDialog) {
					mProgressDialog.show();
				}
			};

			@Override
			protected ResourceJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().queryAssetList(
						InterfaceUrls.QUERY_ASSET_LIST, SEARCH_PAGE_SIZE,
						pageNo, keyWord, session.getUserCode(), queryType, 0);
			}

			@Override
			protected void onPostExecute(ResourceJson result) {
				if (null != mProgressDialog) {
					mProgressDialog.dismiss();
				}
				if (null == result) {
					searchResultLayout.setVisibility(View.GONE);
					searchResult = searchResult.replace("$num", 0 + "");
					searchResultView.setText(userWord + "结果为空");
					return;
				}
				// 搜索结果
				ArrayList<Resource> searchResults = result.getDatas();
				int resultCount = result.getRetCount();
				if (0 == queryType) { // 搜索全部时更新搜索结果，搜索直播和点播时，不更新搜索结果
					if (0 == result.getRet()) {// 0：表示正常，其它表示异常
						searchResult = searchResult.replace("$num", resultCount
								+ "");
					} else if (1 == result.getRet()) {// 1：表示没有搜索的相关影片，Datas数据里面是推荐资源
						isRecommend = true;
						searchResultLayout.setVisibility(View.GONE);// 隐藏按钮选择
						recommendResult.setVisibility(View.VISIBLE);
						searchResult = searchResult.replace("$num", "0");
						searchResult += getString(R.string.see_recommend);
						recommendResult.setText(searchResult);
						searchResultVod.setText("");
					} else {
						return;
					}
					if (!isRecommend) {
						searchResultView.setText(searchResult);
						searchResultAll.setText(getString(R.string.all)
								.replace("$num", resultCount + ""));
						if (0 == result.getRet()) {// 推荐信息
							searchResultVod.setText(getString(R.string.vod)
									.replace("$num",
											result.getAssetCount() + ""));
						}
						searchResultLive
								.setText(getString(R.string.live).replace(
										"$num", result.getProgramCount() + ""));
					}
				}
				adapter.addNewDatas(searchResults);
				adapter.notifyDataSetChanged();
				loader.setCurPage(result.getCurPage());
				loader.setPageCount(result.getPageCount());
			};
		}.execute();
	}

	/**
	 * 定义一个结果列表项
	 */
	public final class ResultGridViewHolder {
		public ImageView gaoqingTag;
		public CustormImageView assetPoster;
		public TextView assetName;
		public TextView assetAnticipation;
		public ImageView itemPlayBtn;
		public TextView priceTag;
	}

	/**
	 * ResultGridViewAdapter
	 */
	public class ResultAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ResultGridViewHolder holder = null;
			if (convertView == null) {
				holder = new ResultGridViewHolder();
				convertView = mInflater.inflate(
						R.layout.poster_name_search_item, null);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.priceTag = (TextView) convertView
						.findViewById(R.id.priceTag);
				holder.assetPoster = (CustormImageView) convertView
						.findViewById(R.id.assetPoster);
				holder.assetName = (TextView) convertView
						.findViewById(R.id.assetName);
				holder.itemPlayBtn = (ImageView) convertView
						.findViewById(R.id.itemPlaybtn);
				holder.assetAnticipation = (TextView) convertView
						.findViewById(R.id.assetAnticipation);
				convertView.setTag(holder);
			} else {
				holder = (ResultGridViewHolder) convertView.getTag();
			}
			Resource resource = (Resource) datas.get(position);
			if (resource != null) {
				String imagePath = "";
				ArrayList<Poster> posters = resource.getPosters();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				if (resource.getResourceType() == 1)// 直播节目
				{
					holder.assetPoster
							.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				} else {
					holder.assetPoster.setScaleType(ImageView.ScaleType.FIT_XY);
				}
				holder.assetPoster.setImageHttpUrl(imagePath);
				holder.assetName.setText(resource.getResourceName());
				if (0 == resource.getVideoType()) {
					holder.gaoqingTag.setVisibility(View.INVISIBLE);
				} else {
					holder.gaoqingTag.setVisibility(View.VISIBLE);
				}
			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_backicon:
			onBackPressed();
			break;
		// 0：全部，1：直播，2：点播
		case R.id.searchResultAll:
			if (null != adapter) {
				adapter.removeAllDatas();
			}
			queryType = 0;
			switchResultType(0);
			initSearchResult(1);
			break;
		case R.id.searchResultLive:
			if (null != adapter) {
				adapter.removeAllDatas();
			}
			queryType = 1;
			initSearchResult(1);
			switchResultType(1);
			break;
		case R.id.searchResultVod:
			if (null != adapter) {
				adapter.removeAllDatas();
			}
			queryType = 2;
			initSearchResult(1);
			switchResultType(2);
			break;
		}
	}

	/**
	 * 按全部、点播、直播来筛选搜索结果
	 * 
	 * @param type
	 *            0：全部，1：直播，2：点播
	 * */
	private void switchResultType(int type) {
		TextView tv = null;
		for (int i = 0, len = typeList.size(); i < len; i++) {
			tv = typeList.get(i);
			if (i == type) {
				tv.setBackgroundResource(R.drawable.switch_focus);
			} else {
				tv.setBackgroundResource(0);
			}
		}
	}
}