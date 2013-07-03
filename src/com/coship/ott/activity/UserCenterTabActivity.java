package com.coship.ott.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coship.ott.constant.Constant;
import com.coship.ott.constant.DeviceBingDingError;
import com.coship.ott.constant.UdrmDefine;
import com.coship.ott.fragment.DeviceBingDingFragment;
import com.coship.ott.fragment.LoginListener;
import com.coship.ott.fragment.PassWordChangeFragment;
import com.coship.ott.fragment.PermissionComboFragment;
import com.coship.ott.fragment.UserCenterFragment;
import com.coship.ott.fragment.UserCenterOtherFragment;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.unitend.udrm.util.LibUDRM;
import com.unitend.udrm.util.LibUDRMException;

public class UserCenterTabActivity extends CommonViewActivity {
	private static final String TAG = "UserCenterTabActivity";
	private TextView mTitleTxt;
	private TabAdapter tabAdapter;
	public static LibUDRM mlibUDRM;
	private Session mSession;
	private String mUserNameStr, passWord;
	public static int isComing = 0;
	public static String path = Environment.getExternalStorageDirectory()
			.getPath();
	private QuryDeviceBingDing mQuryDeviceBingDing;
	public static ISLoginListener mISLoginListener;
	public static int witch = 0;
	public static int mSelect = 0;
	public static int mSelectItem = 0; // 4:我的收藏 5：我的评论 6：我的预约 7：我的分享 8：历史记录

	private Integer[] tabIds = new Integer[] { R.drawable.tab_user_msg,
			R.drawable.tab_user_device, R.drawable.tab_user_taocan,
			R.drawable.tab_change_password, R.drawable.tab_user_collect,
			R.drawable.tab_user_discuss, R.drawable.tab_user_book,
			R.drawable.tab_user_share, R.drawable.tab_user_history };
	private Integer[] tabFocusIds = new Integer[] { R.drawable.tab_user_msg_f,
			R.drawable.tab_user_device_f, R.drawable.tab_user_taocan_f,
			R.drawable.tab_change_password_f, R.drawable.tab_user_collect_f,
			R.drawable.tab_user_discuss_f, R.drawable.tab_user_book_f,
			R.drawable.tab_user_share_f, R.drawable.tab_user_history_f };
	private UserCenterFragment mUserFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center_tab);
		int type = getIntent().getIntExtra("type", 0);
		// witch = getIntent().getIntExtra("witch", 0);
		// mSelect = 0;
		witch = mSelect;
		AppManager.getAppManager().addActivity(this);
		setupView();
		mSession = Session.getInstance();
		mUserNameStr = mSession.getUserName();
		String PWord = mSession.getPassWord();
		passWord = NetTransportUtil.getMD5(PWord);
		setDeviceDucument();
		// 初始化Fragment跳转
		// initTabFragment();
	}

	private void setupView() {
		// 隐藏公告标题
		notice = (ImageView) findViewById(R.id.notice);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		noticeFull.setVisibility(View.INVISIBLE);
		notice.setVisibility(View.INVISIBLE);
		mTitleTxt = (TextView) this.findViewById(R.id.titleTxt);
		mTitleTxt.setText(R.string.title_userZone);
		ListView tabList = (ListView) findViewById(R.id.tabList);
		tabAdapter = new TabAdapter(tabIds, tabFocusIds,
				UserCenterTabActivity.this);
		tabList.setAdapter(tabAdapter);
		tabList.setOnItemClickListener(new OnTabItemClickListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 隐藏公告标题
		if (!UserCenterFragment.isEditMessage) {
			initTabFragment();
		}
	}

	private void initTabFragment() {
		notice = (ImageView) findViewById(R.id.notice);
		noticeFull = (RelativeLayout) findViewById(R.id.notice_full);
		noticeFull.setVisibility(View.GONE);
		notice.setVisibility(View.GONE);
		if (mSession.isLogined()) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			switch (witch) {
			case 0:
				mUserFragment = new UserCenterFragment();
				mUserFragment
						.setUserCenterTabActivity(UserCenterTabActivity.this);
				transaction.replace(R.id.userMsg, mUserFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 1:
				DeviceBingDingFragment deFragment = new DeviceBingDingFragment();
				transaction.replace(R.id.userMsg, deFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 2:
				PermissionComboFragment pFragment = new PermissionComboFragment();
				transaction.replace(R.id.userMsg, pFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 3:
				PassWordChangeFragment pChangeFragment = new PassWordChangeFragment();
				transaction.replace(R.id.userMsg, pChangeFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 4:
				UserCenterOtherFragment coFragment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, coFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 5:
				UserCenterOtherFragment mydiscussFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, mydiscussFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 6:
				UserCenterOtherFragment myOrderFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, myOrderFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 7:
				UserCenterOtherFragment myShareFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, myShareFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 8:
				UserCenterOtherFragment myhistoryFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, myhistoryFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;

			default:
				break;
			}
		} else {
			isComing = 1;
			UIUtility.showDialog(mContext);
			return;
		}
	}

	private void setDeviceDucument() {
		mQuryDeviceBingDing = new QuryDeviceBingDing();
		mQuryDeviceBingDing.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (null != mUserFragment) {
			mUserFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	public class OnTabItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mSelect = arg2;
			showUserCenterTab(arg2);
			tabAdapter.notifyDataSetChanged();
		}

		private void showUserCenterTab(int position) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			switch (position) {
			case 0:
				witch = 0;
				mUserFragment = new UserCenterFragment();
				mUserFragment
						.setUserCenterTabActivity(UserCenterTabActivity.this);
				transaction.replace(R.id.userMsg, mUserFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 1:
				witch = 1;
				DeviceBingDingFragment deFragment = new DeviceBingDingFragment();
				transaction.replace(R.id.userMsg, deFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 2:
				witch = 2;
				PermissionComboFragment pFragment = new PermissionComboFragment();
				transaction.replace(R.id.userMsg, pFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 3:
				witch = 3;
				PassWordChangeFragment pChangeFragment = new PassWordChangeFragment();
				transaction.replace(R.id.userMsg, pChangeFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 4:
				mSelectItem = 4;
				witch = 4;
				UserCenterOtherFragment coFragment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, coFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 5:
				mSelectItem = 5;
				witch = 5;
				UserCenterOtherFragment mydiscussFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, mydiscussFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 6:
				mSelectItem = 6;
				witch = 6;
				UserCenterOtherFragment myOrderFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, myOrderFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 7:
				mSelectItem = 7;
				witch = 7;
				UserCenterOtherFragment myShareFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, myShareFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case 8:
				mSelectItem = 8;
				witch = 8;
				UserCenterOtherFragment myhistoryFagment = new UserCenterOtherFragment();
				transaction.replace(R.id.userMsg, myhistoryFagment);
				transaction.addToBackStack(null);
				transaction.commit();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 查询设备绑定列表
	 * 
	 */
	private class QuryDeviceBingDing extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				LibUDRM.useIOMX(false);
				mlibUDRM = LibUDRM.getInstance();
				mlibUDRM.UDRMAgentDeviceLocalPath(path + "/.UDRM");
			} catch (LibUDRMException e) {
				e.printStackTrace();
			}
			return mlibUDRM
					.UDRMAgentSetEnv(mUserNameStr, passWord,
							mSession.getMacPath(), Constant.DEVICENAME,
							Constant.DRMURL);
		}

		@Override
		protected void onPostExecute(Integer state) {
			if (state == UdrmDefine.UDRM_ERROR_OK) {
				LogUtils.trace(Log.DEBUG, TAG, "设置环境变量成功");
			} else {
				new DeviceBingDingError().getDebugError(mlibUDRM,
						UserCenterTabActivity.this);
			}
		}
	}

	final class ISLoginListener implements LoginListener {

		@Override
		public void isLogin(boolean islogin) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			mUserFragment = new UserCenterFragment();
			mUserFragment.setUserCenterTabActivity(UserCenterTabActivity.this);
			transaction.replace(R.id.userMsg, mUserFragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
		}
	}
}
