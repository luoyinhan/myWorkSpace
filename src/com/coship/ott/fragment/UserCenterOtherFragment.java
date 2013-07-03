package com.coship.ott.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.coship.ott.activity.BrandParticularActivity;
import com.coship.ott.activity.ParticularActivity;
import com.coship.ott.activity.ProgramParticularActivity;
import com.coship.ott.activity.R;
import com.coship.ott.activity.UserCenterTabActivity;
import com.coship.ott.constant.Constant;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.BookAction;
import com.coship.ott.transport.action.BookMarkAction;
import com.coship.ott.transport.action.CommentAction;
import com.coship.ott.transport.action.FavoriteAction;
import com.coship.ott.transport.action.ShareAction;
import com.coship.ott.transport.action.UserAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.book.Book;
import com.coship.ott.transport.dto.book.BooksJson;
import com.coship.ott.transport.dto.bookmark.BookMark;
import com.coship.ott.transport.dto.bookmark.BookMarksJson;
import com.coship.ott.transport.dto.comment.Comment;
import com.coship.ott.transport.dto.comment.CommentsJson;
import com.coship.ott.transport.dto.favourite.FavouriteAssetListJson;
import com.coship.ott.transport.dto.share.Share;
import com.coship.ott.transport.dto.share.ShareJson;
import com.coship.ott.transport.dto.vod.AssetListInfo;
import com.coship.ott.transport.dto.vod.Poster;
import com.coship.ott.transport.util.ScrollLoader;
import com.coship.ott.transport.util.ScrollLoader.CallBack;
import com.coship.ott.utils.DbHelper;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.utils.Utility;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.CustormImageView;

public class UserCenterOtherFragment extends Fragment implements
		OnClickListener {
	private GridView gridView = null;
	private MovieGridViewAdapter gridViewAdapter = null;
	private ImageView mEditBtn, mDeleteBtn, mDeleteAllBtn, mEditCancel;
	private ArrayList<Integer> indexs = new ArrayList<Integer>();
	private ProgressDialog mProgress = null;
	// 批量删除时当前删除的个数
	private int delIndex = 0;
	private Session mSession;
	private LayoutInflater mInflater;
	// 状态，0为展示状态，1为编辑状态（选择要删除的文件）
	private int showStatus = 0;
	private LinearLayout mEditingLayout;
	private int mSelectItem = UserCenterTabActivity.mSelectItem;
	private ScrollLoader loader;
	private boolean isloading = false;
	private Context mContext;
	private Integer[] tabIds = new Integer[] { R.drawable.tab_user_msg,
			R.drawable.tab_user_device, R.drawable.tab_user_taocan,
			R.drawable.tab_change_password, R.drawable.tab_user_collect,
			R.drawable.tab_user_discuss, R.drawable.tab_user_book,
			R.drawable.tab_user_share, R.drawable.tab_user_history };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.collection_fragment, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		setupView();
		initDate();
	}

	private void setupView() {
		mContext = getActivity();
		mInflater = LayoutInflater.from(getActivity());
		mProgress = new ProgressDialog(getActivity());
		mProgress.setMessage("正在加载数据...");
		mProgress.setCanceledOnTouchOutside(false);
		gridView = (GridView) getView().findViewById(R.id.movie_gridview);
		gridViewAdapter = new MovieGridViewAdapter();
		gridView.setAdapter(gridViewAdapter);
		mEditBtn = (ImageView) getView().findViewById(R.id.editBtn);
		// 删除按钮
		mDeleteBtn = (ImageView) getView().findViewById(R.id.deleteBtn);
		// 清空按钮
		mDeleteAllBtn = (ImageView) getView().findViewById(R.id.deleteAllBtn);
		// 取消编辑按钮
		mEditCancel = (ImageView) getView().findViewById(R.id.editCancel);
		mEditingLayout = (LinearLayout) getView().findViewById(
				R.id.editingLayout);
		mEditBtn.setOnClickListener(this);
		mDeleteBtn.setOnClickListener(this);
		mDeleteAllBtn.setOnClickListener(this);
		mEditCancel.setOnClickListener(this);
		gridView.setOnItemClickListener(new ItemClickListener());
		loader = new ScrollLoader(getActivity(), gridView, new CallBack() {
			@Override
			public void loadData(int pageNo) {
				// 当为评论时则打开翻页
				if (!isloading && mSelectItem == 5) {
					getCommentByUserCode(pageNo);
				}
			}
		});
	}

	private void initDate() {
		mSession = Session.getInstance();
		switch (mSelectItem) {
		case 4:
			getFavorite();
			break;
		case 5:
			getCommentByUserCode(1);
			break;
		case 6:
			queryBook();
			break;
		case 7:
			queryUserShare();
			break;
		case 8:
			getBookMark();
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editBtn:
			changeShow();
			break;
		case R.id.deleteBtn:
			delRecords();
			break;
		case R.id.deleteAllBtn:
			createDalog();
			break;
		case R.id.editCancel:
			changeShow();
			break;

		default:
			break;
		}
	}

	private void createDalog() {
		new AlertDialog.Builder(mContext).setTitle("提示")
				.setMessage("确认清空所有记录？该操作无法恢复！").setIcon(null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delRecord("");
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	// 改变编辑和展示状态
	private void changeShow() {
		if (0 == showStatus) { // 展示状态
			showStatus = 1;
			mEditBtn.setVisibility(View.GONE);
			mEditingLayout.setVisibility(View.VISIBLE);
		} else { // 编辑状态
			showStatus = 0;
			mEditingLayout.setVisibility(View.GONE);
			mEditBtn.setVisibility(View.VISIBLE);
		}
		gridViewAdapter.notifyDataSetChanged();
	}

	private void delRecords() {
		int tabId = tabIds[mSelectItem];
		switch (tabId) {
		case R.drawable.tab_user_collect: // 我的收藏
			AssetListInfo obj = null;
			for (int index : indexs) {
				obj = (AssetListInfo) gridViewAdapter.getItemData(index);
				delRecord(obj.getResourceCode());
			}
			break;
		case R.drawable.tab_user_discuss:// 我的评论
			Comment comment = null;
			for (int index : indexs) {
				comment = (Comment) gridViewAdapter.getItemData(index);
				delRecord("" + comment.getId());
			}
			break;
		case R.drawable.tab_user_book:// 我的预约
			Book book = null;
			for (int index : indexs) {
				book = (Book) gridViewAdapter.getItemData(index);
				delRecord(book.getProgramId());
			}
			break;
		case R.drawable.tab_user_share:// 我的分享
			Share share = new Share();
			for (int index : indexs) {
				share = (Share) gridViewAdapter.getItemData(index);
				delRecord(share.getId());
			}
			break;
		case R.drawable.tab_user_history:// 历史记录
			BookMark bookMark = null;
			for (int index : indexs) {
				bookMark = (BookMark) gridViewAdapter.getItemData(index);
				delRecord(bookMark.getCurrentResourceCode());
			}
			break;
		}
	}

	/**
	 * 删除用户记录
	 * */
	private void delRecord(final String resoruceCode) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<String, Void, BaseJsonBean>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mProgress && !mProgress.isShowing()) {
					mProgress.show();
				}
			}

			@Override
			protected BaseJsonBean doInBackground(String... params) {
				int tabId = tabIds[mSelectItem];
				switch (tabId) {
				case R.drawable.tab_user_collect: // 我的收藏
					// 删除本地数据
					DbHelper dbhelper = new DbHelper(mContext);
					boolean isSuc = false;
					if (resoruceCode.equals("")) {
						isSuc = dbhelper.deleteAllData();
					} else {
						isSuc = dbhelper.deleteData(session.getUserCode(),
								resoruceCode);
					}
					dbhelper.closeConn();
					if (isSuc) {
						return new FavoriteAction().delFavorite(
								InterfaceUrls.DEL_FAVOURITE,
								session.getUserCode(), session.getUserName(),
								resoruceCode);
					} else {
						return null;
					}
				case R.drawable.tab_user_discuss:// 我的评论
					return new CommentAction().deleteComment(
							InterfaceUrls.DEL_USER_COMMENTS,
							session.getUserName(), resoruceCode);
				case R.drawable.tab_user_book:// 我的预约
					return new BookAction().delBook(InterfaceUrls.DEL_BOOK,
							session.getUserCode(), resoruceCode);
				case R.drawable.tab_user_share:// 我的分享
					return new ShareAction().deleteShare(
							InterfaceUrls.DEL_USER_SHARE,
							session.getUserCode(), resoruceCode);
				case R.drawable.tab_user_history:// 历史记录
					return new BookMarkAction().delBookMark(
							InterfaceUrls.DEL_BOOKMARK, session.getUserCode(),
							session.getUserName(), resoruceCode);
				}
				return null;
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != result) {
					if (!TextUtils.isEmpty(resoruceCode)) {
						delIndex++;
						if (delIndex == indexs.size()) {
							Toast.makeText(mContext,
									getString(R.string.del_success),
									Toast.LENGTH_SHORT).show();
							int tem = 0;
							// 冒泡法将数组由大到小排序
							indexs = Utility.maxToMinSort(indexs);
							for (int index : indexs) {
								gridViewAdapter.removeData(gridViewAdapter
										.getItemData(index));
							}
							// 重置删除标识删除
							delIndex = 0;
							indexs.removeAll(indexs);
						}
					} else {
						Toast.makeText(mContext,
								getString(R.string.del_success),
								Toast.LENGTH_SHORT).show();
						gridViewAdapter.removeAllDatas();
					}
				}
				if (null != mProgress && mProgress.isShowing()) {
					mProgress.dismiss();
				}
			}
		}.execute();
	}

	/**
	 * 获取用户收藏内容
	 * */
	private void getFavorite() {
		// 取媒资信息
		new AsyncTask<String, Void, FavouriteAssetListJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mProgress && !mProgress.isShowing()) {
					isloading = true;
					mProgress.show();
				}
			}

			@Override
			protected FavouriteAssetListJson doInBackground(String... params) {
				return new FavoriteAction().getFavorite(
						InterfaceUrls.GET_FAVOURITE, mSession.getUserCode(),
						mSession.getUserName());
			};

			@Override
			protected void onPostExecute(FavouriteAssetListJson result) {
				if (null != mProgress && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					gridViewAdapter.removeAllDatas();
					gridViewAdapter.addNewDatas(result.getFavorite());
				}
			}
		}.execute();
	}

	/**
	 * 获取用户评论内容
	 * */
	private void getCommentByUserCode(final int pageNo) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(getActivity());
			return;
		}
		new AsyncTask<String, Void, CommentsJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mProgress && !mProgress.isShowing()) {
					mProgress.show();
					isloading = true;
				}
			}

			@Override
			protected CommentsJson doInBackground(String... params) {
				return new CommentAction().getCommentByUserCode(
						InterfaceUrls.GET_COMMENT_BY_USERCODE,
						session.getUserName(), "", "", pageNo, 15);
			};

			@Override
			protected void onPostExecute(CommentsJson result) {
				if (null != mProgress && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					if (pageNo == 1) {
						gridViewAdapter.removeAllDatas();
					}
					gridViewAdapter.addNewDatas(result.getComments());
					loader.setCurPage(result.getCurPage());
					loader.setPageCount(result.getPageCount());
				}
				isloading = false;
			}
		}.execute();
	}

	/**
	 * 获取用户预约内容
	 * */
	private void queryBook() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(getActivity());
			return;
		}
		// 取媒资信息
		new AsyncTask<String, Void, BooksJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mProgress && !mProgress.isShowing()) {
					mProgress.show();
					isloading = true;
				}
			}

			@Override
			protected BooksJson doInBackground(String... params) {
				return new BookAction().queryBook(InterfaceUrls.QUERY_BOOK,
						session.getUserCode());
			};

			@Override
			protected void onPostExecute(BooksJson result) {
				if (null != mProgress && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					// 海报
					gridViewAdapter.removeAllDatas();
					gridViewAdapter.addNewDatas(result.getBooks());
				}
				isloading = false;
			}
		}.execute();
	}

	/**
	 * 获取用户分享内容
	 * */
	private void queryUserShare() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(getActivity());
			return;
		}
		new AsyncTask<String, Void, ShareJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mProgress && !mProgress.isShowing()) {
					mProgress.show();
					isloading = true;
				}
			}

			@Override
			protected ShareJson doInBackground(String... params) {
				return new UserAction().queryUserShare(
						InterfaceUrls.QUERY_USER_SHARE, session.getUserCode(),
						session.getUserName());
			};

			@Override
			protected void onPostExecute(ShareJson result) {
				if (null != mProgress && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					gridViewAdapter.removeAllDatas();
					gridViewAdapter.addNewDatas(result.getDatas());
				}
				isloading = false;
			}
		}.execute();
	}

	/**
	 * 获取用户历史记录（书签）
	 * */
	private void getBookMark() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(getActivity());
			return;
		}
		new AsyncTask<String, Void, BookMarksJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != mProgress && !mProgress.isShowing()) {
					mProgress.show();
					isloading = true;
				}
			}

			@Override
			protected BookMarksJson doInBackground(String... params) {
				return new BookMarkAction().getBookMark(
						InterfaceUrls.GET_BOOKMARK, session.getUserCode(),
						session.getUserName());
			};

			@Override
			protected void onPostExecute(BookMarksJson result) {
				if (null != mProgress && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				if (null != result && 0 == result.getRet()) {
					gridViewAdapter.removeAllDatas();
					gridViewAdapter.addNewDatas(result.getBookMark());
				}
				isloading = false;
			}
		}.execute();
	}

	class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object obj = gridViewAdapter.getItemData(position);
			ImageView selectTag = (ImageView) view.findViewById(R.id.selectTag);
			if (obj instanceof AssetListInfo) { // 我的收藏，暂时只有点播
				AssetListInfo asset = (AssetListInfo) obj;
				if (1 == showStatus) {
					if (!asset.isSelected()) {
						asset.setSelected(true);// 状态修改为选中
						indexs.add(position);
						selectTag.setBackgroundResource(R.drawable.sel_tag_sel);
					} else {
						asset.setSelected(false);// 状态修改为未选中
						if (indexs.size() > 0) {
							for (int i = 0; i < indexs.size(); i++) {
								if (indexs.get(i) == position) {
									indexs.remove(i);
								}
							}
						}
						selectTag.setBackgroundResource(R.drawable.sel_tag);
					}
				} else {
					startParticular(2, asset.getResourceCode(), 0);
				}

			} else if (obj instanceof Comment) {// 我的评论
				Comment comment = (Comment) obj;
				if (1 == showStatus) {
					if (!comment.isSelected()) {
						comment.setSelected(true);// 状态修改为选中
						indexs.add(position);
						selectTag.setBackgroundResource(R.drawable.sel_tag_sel);
					} else {
						comment.setSelected(false);// 状态修改为未选中
						if (indexs.size() > 0) {
							for (int i = 0; i < indexs.size(); i++) {
								if (indexs.get(i) == position) {
									indexs.remove(i);
								}
							}
						}
						selectTag.setBackgroundResource(R.drawable.sel_tag);
					}
				} else {
					startParticular(comment.getObjType(), comment.getObjID(), 0);
				}

			} else if (obj instanceof Book) { // 我的预定，只有直播节目
				Book book = (Book) obj;
				if (1 == showStatus) {
					if (!book.isSelected()) {
						book.setSelected(true);// 状态修改为选中
						indexs.add(position);
						selectTag.setBackgroundResource(R.drawable.sel_tag_sel);
					} else {
						book.setSelected(false);// 状态修改为未选中
						if (indexs.size() > 0) {
							for (int i = 0; i < indexs.size(); i++) {
								if (indexs.get(i) == position) {
									indexs.remove(i);
								}
							}
						}
						selectTag.setBackgroundResource(R.drawable.sel_tag);
					}
				} else {
					startParticular(1, book.getProgramId(), 0);
				}

			} else if (obj instanceof Share) {// 我的分享
				Share share = (Share) obj;
				if (1 == showStatus) {
					if (!share.isSelected()) {
						share.setSelected(true);// 状态修改为选中
						indexs.add(position);
						selectTag.setBackgroundResource(R.drawable.sel_tag_sel);
					} else {
						share.setSelected(false);// 状态修改为未选中
						if (indexs.size() > 0) {
							for (int i = 0; i < indexs.size(); i++) {
								if (indexs.get(i) == position) {
									indexs.remove(i);
								}
							}
						}
						selectTag.setBackgroundResource(R.drawable.sel_tag);
					}
				} else {
					startParticular(share.getObjType(), share.getObjID(), 0);
				}

			} else if (obj instanceof BookMark) {// 历史记录，暂时只有点播
				BookMark bookMark = (BookMark) obj;
				if (1 == showStatus) {
					if (!bookMark.isSelected()) {
						bookMark.setSelected(true);// 状态修改为选中
						indexs.add(position);
						selectTag.setBackgroundResource(R.drawable.sel_tag_sel);
					} else {
						bookMark.setSelected(false);// 状态修改为未选中
						if (indexs.size() > 0) {
							for (int i = 0; i < indexs.size(); i++) {
								if (indexs.get(i) == position) {
									indexs.remove(i);
								}
							}
						}
						selectTag.setBackgroundResource(R.drawable.sel_tag);
					}
				} else {
					startParticular(bookMark.getType(),
							bookMark.getResourceCode(),
							bookMark.getCurrentResourceCode());
				}
			}
		}
	};

	/**
	 * 
	 * @param type
	 *            数字 资源类型 1：直播节目 2：点播节目3：频道品牌
	 * */
	private void startParticular(int type, String resourceCode, int timeCode) {
		Intent intent = new Intent();
		switch (type) {
		case 1:
			intent.setClass(getActivity(), ProgramParticularActivity.class);
			intent.putExtra("programId", resourceCode);
			break;
		case 2:
			intent.setClass(getActivity(), ParticularActivity.class);
			intent.putExtra("resourceCode", resourceCode);
			intent.putExtra("timeCode", timeCode);
			break;
		case 3:
			intent.setClass(getActivity(), BrandParticularActivity.class);
			intent.putExtra("brandID", resourceCode);
			break;
		default:
			return;
		}
		startActivity(intent);
	}

	// 点播(单片或者电视剧)
	private void startParticular(int type, String resourceCode,
			String currentResourceCode) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), ParticularActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("resourceCode", resourceCode);
		intent.putExtra("currentResourceCode", currentResourceCode);
		startActivity(intent);
	}

	/**
	 * 电影信息表格视图监听器
	 */
	public class MovieGridViewAdapter extends CommonAdapter {
		@Override
		public int getCount() {
			if (null == datas || 0 == datas.size()) {
				return 0;
			}
			return datas.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewGridViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewGridViewHolder();
				convertView = mInflater.inflate(
						R.layout.poster_name_anticip_item, null);
				holder.assetPoster = (CustormImageView) convertView
						.findViewById(R.id.assetPoster);
				holder.assetName = (TextView) convertView
						.findViewById(R.id.assetName);
				holder.createTime = (TextView) convertView
						.findViewById(R.id.assetAnticipation);
				holder.text3 = (TextView) convertView.findViewById(R.id.text3);
				holder.selectTag = (ImageView) convertView
						.findViewById(R.id.selectTag);
				holder.gaoqingTag = (ImageView) convertView
						.findViewById(R.id.gaoqingTag);
				holder.priceTag = (TextView) convertView
						.findViewById(R.id.priceTag);
				convertView.setTag(holder);
			} else {
				holder = (ViewGridViewHolder) convertView.getTag();
			}

			if (0 == showStatus) { // 展示状态
				holder.selectTag.setVisibility(View.GONE);
			} else { // 编辑状态
				holder.selectTag.setBackgroundResource(R.drawable.sel_tag);
				holder.selectTag.setVisibility(View.VISIBLE);
			}
			switch (mSelectItem) {
			case 4: // 我的收藏
				return getFavouriteView(position, convertView, holder);
			case 5:// 我的评论
				return getCommentView(position, convertView, holder);
			case 6:// 我的预约
				return getBookView(position, convertView, holder);
			case 7:// 我的分享
				return getShareView(position, convertView, holder);
			case 8:// 历史记录
				return getBookMarkView(position, convertView, holder);
			}
			return convertView;
		}

		// 我的收藏
		private View getFavouriteView(int position, View convertView,
				ViewGridViewHolder holder) {
			final AssetListInfo assetInfo = (AssetListInfo) this.datas
					.get(position);
			String imagePath = "";
			if (assetInfo != null) {
				ArrayList<Poster> posters = assetInfo.getPosterInfo();
				if (null != posters && 0 < posters.size()) {
					Poster poster = posters.get(0);
					if (null != poster
							&& !TextUtils.isEmpty(poster.getLocalPath())) {
						imagePath = poster.getLocalPath();
					}
				}
				if (1 == assetInfo.getVideoType()) {
					holder.gaoqingTag.setVisibility(View.VISIBLE);
				} else {
					holder.gaoqingTag.setVisibility(View.INVISIBLE);
				}
				int price = assetInfo.getProduct().getProductPrice();
				if (0 < price) {
					holder.priceTag.setText((float) price / (float) 100 + "元");
					holder.priceTag.setVisibility(View.VISIBLE);
				} else {
					holder.priceTag.setVisibility(View.GONE);
				}
				holder.assetPoster.setImageHttpUrl(imagePath);
				holder.assetName.setText(assetInfo.getAssetName());
				holder.createTime.setText(assetInfo.getSummaryShort());
				holder.text3.setVisibility(View.GONE);
			}
			return convertView;
		}

		// 我的评论
		private View getCommentView(int position, View convertView,
				ViewGridViewHolder holder) {
			Comment comment = (Comment) this.datas.get(position);
			if (null == comment) {
				return convertView;
			}
			String imagePath = "";
			ArrayList<Poster> posters = comment.getPoster();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			if (1 == comment.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			holder.assetPoster.setImageHttpUrl(imagePath);
			holder.assetName.setText(comment.getObjName());
			try {
				holder.createTime.setText(comment.getCreatTime().split(" ")[0]);
			} catch (Exception e) {
			}
			holder.text3.setVisibility(View.VISIBLE);
			String commentStr = comment.getComment();
			int len = Math.min(7, commentStr.length());
			holder.text3.setText(commentStr.substring(0, len));
			return convertView;
		}

		// 我的预约
		private View getBookView(int position, View convertView,
				ViewGridViewHolder holder) {
			Book book = (Book) this.datas.get(position);
			String imagePath = "";
			ArrayList<Poster> posters = book.getPosterInfo();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			if (1 == book.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			holder.assetPoster.setImageHttpUrl(imagePath);
			holder.assetName.setText(book.getEventName());
			holder.createTime.setText(book.getBeginTime());
			holder.text3.setVisibility(View.GONE);
			return convertView;
		}

		// 我的分享
		private View getShareView(int position, View convertView,
				ViewGridViewHolder holder) {
			Share share = (Share) this.datas.get(position);
			String imagePath = "";
			ArrayList<Poster> posters = share.getPoster();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			if (1 == share.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			holder.assetPoster.setImageHttpUrl(imagePath);
			holder.assetName.setText(share.getObjName());
			holder.createTime.setText(share.getShareTime().split(" ")[0]);
			holder.text3.setVisibility(View.GONE);
			return convertView;
		}

		// 历史记录
		private View getBookMarkView(int position, View convertView,
				ViewGridViewHolder holder) {
			BookMark bookMark = (BookMark) this.datas.get(position);
			String imagePath = "";
			ArrayList<Poster> posters = bookMark.getPoster();
			if (null != posters && 0 < posters.size()) {
				Poster poster = posters.get(0);
				if (null != poster && !TextUtils.isEmpty(poster.getLocalPath())) {
					imagePath = poster.getLocalPath();
				}
			}
			if (1 == bookMark.getVideoType()) {
				holder.gaoqingTag.setVisibility(View.VISIBLE);
			} else {
				holder.gaoqingTag.setVisibility(View.INVISIBLE);
			}
			holder.assetPoster.setImageHttpUrl(imagePath);
			holder.assetName.setText(bookMark.getResourceName());
			holder.createTime.setText(bookMark.getBookMarkDate().split(" ")[0]);
			holder.text3.setVisibility(View.VISIBLE);
			if (bookMark.getBookMark() == Constant.PLAY_OVER) {
				holder.text3.setText("已播放结束");
			} else {
				holder.text3.setText(getString(R.string.bookmark_time).replace(
						"$num", bookMark.getBookMark() / 60 + ""));
			}
			return convertView;
		}
	}

	/**
	 * 定义一个电影信息表格视图结果列表项
	 */
	public final class ViewGridViewHolder {
		public CustormImageView assetPoster;
		public TextView assetName;
		public TextView createTime;
		public TextView text3;
		public ImageView selectTag;
		public ImageView gaoqingTag;
		public TextView priceTag;

	}
}
