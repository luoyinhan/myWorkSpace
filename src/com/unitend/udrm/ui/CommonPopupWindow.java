package com.unitend.udrm.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coship.ott.activity.R;
import com.coship.ott.constant.Constant;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.BookMarkAction;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.bookmark.BookMark;
import com.coship.ott.transport.dto.bookmark.BookMarksJson;
import com.coship.ott.transport.dto.vod.AssetDetailJson;
import com.coship.ott.transport.dto.vod.AssetInfo;
import com.coship.ott.transport.dto.vod.AssetListInfo;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;

/**
 * 历史记录、推荐弹出窗口
 * */
public class CommonPopupWindow extends PopupWindow {
	public final static String TAG = "CommonPopupWindow";
	private Context mContext;
	private Handler mHandler;
	private int msgWhatForItemClick;
	private LayoutInflater mLayoutInflater;

	private BookMarkAdapter bookMarkAdapter;
	private RankingListAdapter recommandAdapter;
	private GridView popGridView;
	private ListView popListView;
	private ProgressBar mPopProgress;
	// 弹出框用途 0:显示推荐影片1：显示历史记录
	private int mType;

	public CommonPopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 创建popupWindow
	public CommonPopupWindow(Context context, View contentView, int type,
			Handler handler, int msgWhatForItemClick) {
		super(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		this.mContext = context;
		this.mType = type;
		this.mHandler = handler;
		this.msgWhatForItemClick = msgWhatForItemClick;
		this.mLayoutInflater = LayoutInflater.from(mContext);

		this.setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.pop_bg));
		this.setOutsideTouchable(true);
		this.setFocusable(true);

		// 初始化popupWindow上的listView
		if (0 == mType) {
			popGridView = (GridView) contentView.findViewById(R.id.popGridView);
			recommandAdapter = new RankingListAdapter();
			popGridView.setAdapter(recommandAdapter);
			popGridView.setOnItemClickListener(new ItemClickListener());
		} else {
			popListView = (ListView) contentView.findViewById(R.id.popList);
			bookMarkAdapter = new BookMarkAdapter();
			popListView.setAdapter(bookMarkAdapter);
			popListView.setOnItemClickListener(new ItemClickListener());
		}
		mPopProgress = (ProgressBar) contentView
				.findViewById(R.id.popLoadingBar);
	}

	class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final Message msg = Message.obtain();
			msg.what = msgWhatForItemClick;
			final Bundle data = new Bundle();
			if (0 == mType) {
				AssetListInfo assetListInfo = (AssetListInfo) recommandAdapter
						.getItemData(position);
				data.putString("resourceCode", assetListInfo.getResourceCode());
				data.putLong("shifttime", 0);
				data.putLong("shiftend", 0);
				data.putInt("playTime", assetListInfo.getPlayTime());
				data.putLong("timecode", 0);
				data.putInt("playType", 1);
				data.putString("assetID", assetListInfo.getAssetID());
				data.putString("providerID", assetListInfo.getProviderID());
				data.putString("assetName", assetListInfo.getAssetName());
				ArrayList<Poster> posters = assetListInfo.getPosterInfo();
				String imagePath = "";
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				data.putString("posterUrl", imagePath);
				msg.setData(data);
				mHandler.sendMessage(msg);

			} else {
				final BookMark bookMark = (BookMark) bookMarkAdapter
						.getItemData(position);
				String resourceCode = "";
				if (0 == bookMark.getType()) {
					resourceCode = bookMark.getResourceCode();
					if (TextUtils.isEmpty(resourceCode)) {
						resourceCode = bookMark.getCurrentResourceCode();
					}
				} else {
					resourceCode = bookMark.getCurrentResourceCode();
				}
				data.putString("resourceCode", resourceCode);
				data.putLong("shifttime", 0);
				data.putLong("shiftend", 0);
				data.putLong("timecode", bookMark.getBookMark());
				data.putInt("playType", 1);
				data.putString("assetName", bookMark.getResourceName());
				ArrayList<Poster> posters = bookMark.getPoster();
				String imagePath = "";
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				data.putString("posterUrl", imagePath);
				// 初始化影片时长
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
							AssetInfo assetInfo = result.getAssetInfo();
							data.putInt("playTime", assetInfo.getPlayTime());
							data.putString("assetID", assetInfo.getAssetID());
							data.putString("providerID",
									assetInfo.getProviderID());
						}
						msg.setData(data);
						mHandler.sendMessage(msg);
					}
				}.execute(resourceCode);
			}

		}
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		if (0 == mType) {
			getRecommand();
		} else {
			getBookMark();
		}
	}

	/**
	 * 定义一个电影信息表格视图结果列表项
	 */
	public final class ItemHolder {
		public TextView itemName;
		public TextView itemTime;
		public TextView itemDesc;
		public ImageView selectTag;
	}

	/**
	 * 获取用户历史记录（书签）
	 * */
	private void getBookMark() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<String, Void, BookMarksJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mPopProgress
						&& View.GONE == mPopProgress.getVisibility()) {
					mPopProgress.setVisibility(View.VISIBLE);
				}
			}

			@Override
			protected BookMarksJson doInBackground(String... params) {
				return new BookMarkAction().getBookMark(
						InterfaceUrls.GET_BOOKMARK, session.getUserCode(), "");
			};

			@Override
			protected void onPostExecute(BookMarksJson result) {
				if (null != mPopProgress
						&& View.VISIBLE == mPopProgress.getVisibility()) {
					mPopProgress.setVisibility(View.GONE);
				}
				if (null != result && 0 == result.getRet()) {
					bookMarkAdapter.addNewDatas(result.getBookMark());
				}
			}
		}.execute();
	}

	// 历史记录数据适配器
	class BookMarkAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder = null;
			if (convertView == null) {
				holder = new ItemHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.player_bookmark_item, null);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.bookMarkName);
				holder.itemTime = (TextView) convertView
						.findViewById(R.id.bookMarkTime);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}
			BookMark bookMark = (BookMark) this.datas.get(position);
			holder.itemName.setText(bookMark.getResourceName());
			if (bookMark.getBookMark() == Constant.PLAY_OVER) {
				holder.itemTime.setText("已播放结束");
			} else {
				holder.itemTime.setText(mContext.getString(
						R.string.bookmark_time).replace("$num",
						bookMark.getBookMark() / 60 + ""));
			}
			return convertView;
		}
	}

	private void getRecommand() {
		new AsyncTask<Void, Void, AssetListJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mPopProgress
						&& View.GONE == mPopProgress.getVisibility()) {
					mPopProgress.setVisibility(View.VISIBLE);
				}
			}

			@Override
			protected AssetListJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getAssetList(
						InterfaceUrls.GET_ASSET_LIST, 6, 1,
						session.getUserCode(), String.valueOf(1), "", "", "",
						null, null, "");
			}

			@Override
			protected void onPostExecute(AssetListJson result) {
				if (null != result && 0 == result.getRet()) {
					// 初始化数据
					RankingListAdapter adapter = new RankingListAdapter();
					adapter.notifyDataSetChanged();
					if (null != mPopProgress
							&& View.VISIBLE == mPopProgress.getVisibility()) {
						mPopProgress.setVisibility(View.GONE);
					}
					if (null != result && 0 == result.getRet()) {
						recommandAdapter.addNewDatas(result.getAssetList());
					}
				}
			};
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

	// 历史记录数据适配器
	class RankingListAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewGridViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewGridViewHolder();
				convertView = mLayoutInflater.inflate(
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
			holder.assetAnticipation.setText(asset.getSummaryShort());
			if (1 == asset.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}
}
