package com.coship.ott.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.vod.KeyWord;
import com.coship.ott.transport.dto.vod.KeyWordJson;
import com.coship.ott.utils.AppManager;
import com.coship.ott.view.CommonAdapter;

/**
 * 搜索
 */
public class SearchActivity extends CommonViewActivity implements
		OnClickListener {
	private LayoutInflater mLayoutInflater;
	private AutoCompleteTextView searchEdit = null;
	private Context mContext;
	private GridView hotQueryAssetNames = null;
	private ArrayAdapter<String> adapter;
	// 热搜关键字适配器
	private HotKeyWordAdapter hotKeyWordAdapter;
	private TextView titleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		mLayoutInflater = LayoutInflater.from(SearchActivity.this);
		setupView();
		initHotKeyWord();
	}

	private void setupView() {
		// 隐藏公告标题
		notice = (ImageView) findViewById(R.id.notice);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		noticeFull.setVisibility(View.INVISIBLE);
		notice.setVisibility(View.INVISIBLE);
		titleTxt = (TextView) this.findViewById(R.id.titleTxt);
		titleTxt.setText(R.string.title_search);
		searchEdit = (AutoCompleteTextView) findViewById(R.id.search_edit);
		searchEdit.setThreshold(1);
		// 添加输入监听器
		searchEdit.addTextChangedListener(new DiscussWatcher());

		// 搜索按钮
		Button searchBtn = (Button) findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);
		// 热搜关键字
		hotQueryAssetNames = (GridView) this
				.findViewById(R.id.hotQueryAssetNames);
		hotKeyWordAdapter = new HotKeyWordAdapter();
		hotQueryAssetNames.setAdapter(hotKeyWordAdapter);
		hotQueryAssetNames.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int postion, long arg3) {
				KeyWord keyWord = (KeyWord) hotKeyWordAdapter
						.getItemData(postion);
				if (null != keyWord) {
					startSearch(keyWord.getKeyWord());
				}
			}
		});
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

	/**
	 * 监听评论框输入文字长度，更新字数显示
	 * */
	class DiscussWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String text = searchEdit.getText().toString();
			try {
				text = URLEncoder.encode(text, "UTF-8");
				getRelatedKeyWord(text);
			} catch (UnsupportedEncodingException e) {
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
	 * 获取搜索联想关键字
	 * */
	private void getRelatedKeyWord(final String keyWordParam) {
		new AsyncTask<Void, Void, KeyWordJson>() {
			@Override
			protected KeyWordJson doInBackground(Void... params) {
				return new VodAction().getRelatedKeyWords(
						InterfaceUrls.GET_RELATED_KEYWORDS, keyWordParam);
			}

			@Override
			protected void onPostExecute(KeyWordJson result) {
				if (null == result || 0 != result.getRet()) {
					return;
				}
				final ArrayList<KeyWord> keyWords = result.getDatas();
				if (null == keyWords || 0 >= keyWords.size()) {
					return;
				}
				String[] relatedStrs = new String[keyWords.size()];
				KeyWord keyWordStr = null;
				for (int i = 0, len = keyWords.size(); i < len; i++) {
					keyWordStr = keyWords.get(i);
					if (null != keyWordStr) {
						relatedStrs[i] = keyWordStr.getKeyWord();
					} else {
						relatedStrs[i] = keyWordParam;
					}
				}
				// 添加关键字联想适配器
				adapter = new ArrayAdapter<String>(mContext,
						android.R.layout.simple_dropdown_item_1line,
						relatedStrs);
				searchEdit.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			};
		}.execute();
	}

	/**
	 * 初始化热搜关键字列表
	 * */
	private void initHotKeyWord() {
		new AsyncTask<Void, Void, KeyWordJson>() {

			@Override
			protected KeyWordJson doInBackground(Void... params) {
				return new VodAction().getKeyWord(InterfaceUrls.GET_KEYWORD);
			}

			@Override
			protected void onPostExecute(KeyWordJson result) {
				if (null == result || 0 != result.getRet()) {
					return;
				}
				final ArrayList<KeyWord> keyWords = result.getDatas();
				if (null == keyWords || 0 >= keyWords.size()) {
					return;
				}
				hotKeyWordAdapter.addNewDatas(keyWords);
			};
		}.execute();
	}

	// 启动搜索
	private void startSearch(String keyWord) {
		searchEdit.setText("");
		Intent intent = new Intent();
		intent.putExtra("keyWord", keyWord);
		intent.setClass(SearchActivity.this, SearchResultActivity.class);
		startActivity(intent);
	}

	// 热搜关键字适配器
	class HotKeyWordAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = (LinearLayout) mLayoutInflater.inflate(
					R.layout.search_hotqueryasset_names_item, null);
			KeyWord keyWord = (KeyWord) this.getItemData(position);
			if (null != keyWord) {
				TextView assetNameView = (TextView) convertView
						.findViewById(R.id.hotQueryAssetNameView);
				if (null != assetNameView) {
					assetNameView.setText(keyWord.getKeyWord());
				}
			}
			return convertView;
		}
	}

	// 搜索联想关键字适配
	class RelatedKeyWordAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = (LinearLayout) mLayoutInflater.inflate(
					android.R.layout.simple_dropdown_item_1line, null);
			String keyWord = (String) this.getItemData(position);
			TextView assetNameView = (TextView) convertView
					.findViewById(R.id.hotQueryAssetNameView);
			if (null != assetNameView) {
				assetNameView.setText(keyWord);
			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchBtn:
			if (searchEdit.getText().length() != 0) { // 有输入时
				startSearch(searchEdit.getText().toString());
			}
			break;
		}
	}
}