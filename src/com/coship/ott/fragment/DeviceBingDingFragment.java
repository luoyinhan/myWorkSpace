package com.coship.ott.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.activity.DeviceListAdapter;
import com.coship.ott.activity.R;
import com.coship.ott.activity.UnDeviceBingDingActivity;
import com.coship.ott.activity.UserCenterTabActivity;
import com.coship.ott.constant.Constant;
import com.coship.ott.constant.DeviceBingDingError;
import com.coship.ott.constant.UdrmDefine;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.DeviceListEntity;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.ToastUtils;
import com.unitend.udrm.util.LibUDRM;
import com.unitend.udrm.util.LibUDRMException;

public class DeviceBingDingFragment extends Fragment implements OnClickListener {
	private TextView userName, cardNumber;
	private RelativeLayout ChangeCardLyout, old_card_psd_lyout,
			device_binding_layout;
	private EditText mOld_pwd_text, mNew_card, mNew_card_pwd;
	private ToastUtils mToUtils;
	private BingDingNewCardTask mBingDingNewCardTask;
	private SubmitDeviceAddCardTask mSubmitDeviceAddCardTask;
	private ListView mDeviceList;
	private static List<DeviceListEntity> mList = new ArrayList<DeviceListEntity>();
	private static List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
	private HashMap<String, Object> mItem;
	private DeviceListAdapter device_adapter;
	public LibUDRM mLibUDRM = UserCenterTabActivity.mlibUDRM;
	private String sdPath = UserCenterTabActivity.path;
	private Session mSession;
	private String mUserNameStr, passWord;
	private QuryDeviceBingDing mQuryDeviceBingDing;
	private GetDRMDeviceBingDing mGetDRMDeviceBingDing;
	private Button mBingDingDeBt;
	private ISDRMDeviceBingDing mISDRMDeviceBingDing;
	private DRMDeviceBingDing mDRMDeviceBingDing;
	private String mUserName = UserCenterFragment.mName;
	private String mCardNO = UserCenterFragment.mBindDeviceNo;
	private boolean isVerify = false;
	public static boolean fromUnbd = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.device_bingding_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		setupView();
		initDate();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void initDate() {
		mSession = Session.getInstance();
		mUserName = mSession.getUserName();
		userName.setText(mUserName);
		mCardNO = Session.getInstance().getBindNo();
		if (TextUtils.isEmpty(mCardNO)) {
			cardNumber.setText("您还未绑定智能卡");
		} else {
			cardNumber.setText(mCardNO);
		}
		mUserNameStr = mSession.getUserName();
		String PWord = mSession.getPassWord();
		passWord = NetTransportUtil.getMD5(PWord);
		if (!fromUnbd) {
			initeDeviceBingDing();
		} else {
			device_adapter.notifyDataSetChanged();
			fromUnbd = false;
			int bingDingState = mLibUDRM.UDRMAgentCheckBindStatus();
			if (bingDingState == UdrmDefine.UDRM_ERROR_OK) {
				mBingDingDeBt.setVisibility(View.GONE);
			}
		}
	}

	private void setupView() {
		userName = (TextView) getActivity().findViewById(R.id.userName);
		cardNumber = (TextView) getActivity().findViewById(R.id.cardNumber);
		Button changeCardbtn = (Button) getActivity().findViewById(
				R.id.change_card_btn);
		ChangeCardLyout = (RelativeLayout) getActivity().findViewById(
				R.id.changeCard_layout);
		// old_card_psd_lyout = (RelativeLayout) getActivity().findViewById(
		// R.id.old_card_psd_lyout);
		device_binding_layout = (RelativeLayout) getActivity().findViewById(
				R.id.device_binding_layout);
		mDeviceList = (ListView) getActivity().findViewById(
				R.id.device_binding_list);
		mBingDingDeBt = (Button) getActivity().findViewById(
				R.id.bingding_mydevice);
		changeCardbtn.setOnClickListener(this);
		mBingDingDeBt.setOnClickListener(this);
		LibUDRM.useIOMX(getActivity());
		device_adapter = new DeviceListAdapter(getActivity(), mData);
		mDeviceList.setAdapter(device_adapter);
		mDeviceList.setOnItemClickListener(unBindDeviceLister);
	}

	/**
	 * 初始化设备绑定
	 */
	private void initeDeviceBingDing() {
		if (mLibUDRM == null) {
			mQuryDeviceBingDing = new QuryDeviceBingDing();
			mQuryDeviceBingDing.execute();
		} else {
			mToUtils = new ToastUtils(getActivity(), "正在加载绑定列表...");
			mToUtils.showToastAlong(5000);
			mGetDRMDeviceBingDing = new GetDRMDeviceBingDing();
			mGetDRMDeviceBingDing.execute();
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
				mLibUDRM = LibUDRM.getInstance();
				mLibUDRM.UDRMAgentDeviceLocalPath(sdPath + "/.UDRM");
			} catch (LibUDRMException e) {
				e.printStackTrace();
			}
			return mLibUDRM
					.UDRMAgentSetEnv(mUserNameStr, passWord,
							mSession.getMacPath(), Constant.DEVICENAME,
							Constant.DRMURL);
		}

		@Override
		protected void onPostExecute(Integer state) {
			if (state == UdrmDefine.UDRM_ERROR_OK) {
				mGetDRMDeviceBingDing = new GetDRMDeviceBingDing();
				mGetDRMDeviceBingDing.execute();
			} else {
				new DeviceBingDingError()
						.getDebugError(mLibUDRM, getActivity());
			}
		}

	}

	/**
	 * 获取DRM设备绑定
	 * 
	 */
	private class GetDRMDeviceBingDing extends AsyncTask<Void, Void, Integer> {

		byte devicelist[] = new byte[2048];

		@Override
		protected Integer doInBackground(Void... params) {
			return mLibUDRM.UDRMAgentGetBindedDeviceList(mUserNameStr,
					devicelist);
		}

		@Override
		protected void onPostExecute(Integer DeviceListState) {
			if (mToUtils != null) {
				mToUtils.cancel();
			}
			mList.clear();
			mData.clear();
			if (DeviceListState == UdrmDefine.UDRM_ERROR_OK) {
				String devlist = new String(devicelist, 0, 2048);
				String[] devItem = devlist.split("\\^&&&");
				for (int i = 0; i < devItem.length; i++) {
					String[] item = devItem[i].split("&\\^\\^\\^");
					if (item.length != 6) {
						continue;
					}
					DeviceListEntity listEntity = new DeviceListEntity();
					listEntity.setPchDeviceName(item[4]);// device name;
					listEntity.setPchDRMID(item[2]);
					listEntity.setPchMACAddr(item[3]);
					mList.add(listEntity);
				}
				for (int i = 0; i < mList.size(); i++) {
					mItem = new HashMap<String, Object>();
					DeviceListEntity listEntity = mList.get(i);
					mItem.put("id", listEntity.getPchDRMID());
					mItem.put("macaddr", listEntity.getPchMACAddr());
					mItem.put("devicename", listEntity.getPchDeviceName());
					mData.add(mItem);
				}
				mDeviceList.setVisibility(View.VISIBLE);
				device_adapter.notifyDataSetChanged();
			} else {
				mDeviceList.setVisibility(View.GONE);
				int errr = new DeviceBingDingError().getDebugError(mLibUDRM,
						getActivity());
				LogUtils.trace(Log.ERROR, getTag(), "获取列表出错了" + errr);
				Toast.makeText(getActivity(), "获取列表失败", Toast.LENGTH_SHORT)
						.show();
			}
			int bingDingState = mLibUDRM.UDRMAgentCheckBindStatus();
			if (bingDingState == UdrmDefine.UDRM_ERROR_OK) {
				mBingDingDeBt.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 绑定列表点击事件 DRM
	 * */
	private final OnItemClickListener unBindDeviceLister = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			if (device_adapter.getItemId(position) == position) {
				DeviceListEntity listEntity = mList.get(position);
				String ID = listEntity.getPchDRMID();
				String MACAddr = listEntity.getPchMACAddr();
				String DeviceName = listEntity.getPchDeviceName();
				Intent intent = new Intent(getActivity(),
						UnDeviceBingDingActivity.class);
				intent.putExtra("ID", ID);
				intent.putExtra("MACAddr", MACAddr);
				intent.putExtra("DeviceName", DeviceName);
				intent.putExtra("type", Constant.UNDEVICE_BINGDING); // 解除绑定
				getActivity().startActivity(intent);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_card_btn:
			changCard();
			break;
		case R.id.submit_btn:// 提交更换智能卡
			submitNewCardMessge();
			break;
		case R.id.cancel_btn:
			ChangeCardLyout.setVisibility(View.GONE);
			device_binding_layout.setVisibility(View.VISIBLE);
			break;
		case R.id.bingding_mydevice:
			bingDingMyPad();
			break;

		default:
			break;
		}
	}

	private void bingDingMyPad() {
		if (device_adapter.getCount() >= 3) {
			// 绑定设备已满3个，需解除一个绑定来绑定本机
			Toast.makeText(getActivity(), "您已经绑定了3个设备，需解除一个绑定来绑定本机",
					Toast.LENGTH_SHORT).show();
		} else {
			mToUtils = new ToastUtils(getActivity(), "正在绑定设备...");
			mToUtils.showToastAlong();
			mISDRMDeviceBingDing = new ISDRMDeviceBingDing();
			mISDRMDeviceBingDing.execute();
		}
	}

	private void changCard() {
		ChangeCardLyout.setVisibility(View.VISIBLE);
		device_binding_layout.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(mCardNO)) {
			// old_card_psd_lyout.setVisibility(View.VISIBLE);
		}
		// mOld_pwd_text = (EditText) getActivity()
		// .findViewById(R.id.old_pwd_text);
		mNew_card = (EditText) getActivity().findViewById(R.id.new_card);
		mNew_card_pwd = (EditText) getActivity()
				.findViewById(R.id.new_card_pwd);
		Button submit_btn = (Button) getActivity()
				.findViewById(R.id.submit_btn);
		Button cancel_btn = (Button) getActivity()
				.findViewById(R.id.cancel_btn);
		submit_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
	}

	// 提交重绑智能卡
	private void submitNewCardMessge() {
		// String oldCardPS = mOld_pwd_text.getText().toString().trim();
		String newCardNo = mNew_card.getText().toString().trim();
		String newCardPS = mNew_card_pwd.getText().toString().trim();
		// if (!TextUtils.isEmpty(UserCenterFragment.mBindDeviceNo)) {
		// if (TextUtils.isEmpty(oldCardPS)) {
		// Toast.makeText(getActivity(), "请输入原智能卡密码", Toast.LENGTH_SHORT)
		// .show();
		// return;
		// }
		// }
		if (TextUtils.isEmpty(newCardNo)) {
			Toast.makeText(getActivity(), "请输入新智能卡号", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (TextUtils.isEmpty(newCardPS)) {
			Toast.makeText(getActivity(), "请输入新智能卡密码", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mToUtils = new ToastUtils(getActivity(), "正在绑定新智能卡...");
		mToUtils.showToastAlong();
		// if (TextUtils.isEmpty(oldCardPS)) {
		// 校验新智能卡号和密码
		// mBingDingNewCardTask = new BingDingNewCardTask(1, null, newCardNo,
		// newCardPS);
		// mBingDingNewCardTask.execute();
		// 校验智能卡和密码
		VerifyBindDevicenoCardInfoTask VerifyBindDevicenoCardInfo = new VerifyBindDevicenoCardInfoTask(
				null, newCardNo, newCardPS);
		VerifyBindDevicenoCardInfo.execute();
		// }
		// else {
		// mSubmitDeviceAddCardTask = new SubmitDeviceAddCardTask(-1, mCardNO,
		// oldCardPS, newCardNo, newCardPS);
		// mSubmitDeviceAddCardTask.execute();
		// }
	}

	/**
	 * 本机是否绑定
	 * 
	 */
	private class ISDRMDeviceBingDing extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			return mLibUDRM.UDRMAgentCheckBindStatus();
		}

		@Override
		protected void onPostExecute(Integer bindState) {
			if (bindState == UdrmDefine.UDRM_ERROR_OK) {
				if (mToUtils != null) {
					mToUtils.cancel();
				}
				Toast.makeText(getActivity(), "您的设备已绑定,无需再次绑定",
						Toast.LENGTH_LONG).show();
			} else {
				mDRMDeviceBingDing = new DRMDeviceBingDing();
				mDRMDeviceBingDing.execute();
			}
		}

	}

	/**
	 * DRM设备绑定
	 * 
	 */
	private class DRMDeviceBingDing extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			int deviceBingDingState = mLibUDRM.UDRMAgentBindDevice(
					mUserNameStr, passWord, mSession.getMacPath(),
					Constant.DEVICENAME);
			return deviceBingDingState;
		}

		@Override
		protected void onPostExecute(Integer deviceBingDingState) {
			if (mToUtils != null) {
				mToUtils.cancel();
			}
			if (deviceBingDingState == UdrmDefine.UDRM_ERROR_OK) {
				int type = Constant.BINGDING_DEVICE; // 绑定本机
				Intent intent = new Intent(getActivity(),
						UnDeviceBingDingActivity.class);
				intent.putExtra("type", type);
				intent.putExtra("MACAddr", mSession.getMacPath());
				intent.putExtra("DeviceName", Constant.DEVICENAME);
				getActivity().startActivity(intent);
			} else {
				new DeviceBingDingError()
						.getDebugError(mLibUDRM, getActivity());
			}
		}
	}

	/**
	 * 绑定智能卡
	 * 
	 * @author admin
	 * @optType 0：卸载原智能卡 1：绑定新智能卡
	 */
	private class BingDingNewCardTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private int optType;
		private String oldCardPwd;
		private String new_Card;
		private String new_Card_Pwd;

		public BingDingNewCardTask(int optType, String oldCardPwd,
				String new_Card, String new_Card_Pwd) {
			super();
			this.optType = optType;
			this.oldCardPwd = oldCardPwd;
			this.new_Card = new_Card;
			this.new_Card_Pwd = new_Card_Pwd;
		}

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			try {
				if (optType == 0) {
					oldCardPwd = URLEncoder.encode(oldCardPwd, "UTF-8");
				}
				new_Card = URLEncoder.encode(new_Card, "UTF-8");
				new_Card_Pwd = URLEncoder.encode(new_Card_Pwd, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			BaseJsonBean baseJson = null;
			if (optType == 1) {
				baseJson = new UserCenterAction().deviceBindCardOrNot(
						InterfaceUrls.ACCOUNT_BIND, mUserName, optType,
						new_Card, new_Card_Pwd);
			} else {
				if (isVerify) {
					baseJson = new UserCenterAction().deviceBindCardOrNot(
							InterfaceUrls.ACCOUNT_BIND, mUserName, optType,
							UserCenterFragment.mBindDeviceNo, oldCardPwd);

				} else {
					// 校验智能卡和密码
					VerifyBindDevicenoCardInfoTask VerifyBindDevicenoCardInfo = new VerifyBindDevicenoCardInfoTask(
							oldCardPwd, new_Card, new_Card_Pwd);
					VerifyBindDevicenoCardInfo.execute();
				}
			}
			return baseJson;
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {

			if (result != null) {
				if (result.getRet() == 0) {
					if (optType == 0) {
						mBingDingNewCardTask = new BingDingNewCardTask(1,
								oldCardPwd, new_Card, new_Card_Pwd);
						mBingDingNewCardTask.execute();
					} else {
						if (mToUtils != null) {
							mToUtils.cancel();
						}
						Toast.makeText(getActivity(), "绑定成功！",
								Toast.LENGTH_SHORT).show();
						ChangeCardLyout.setVisibility(View.GONE);
						device_binding_layout.setVisibility(View.VISIBLE);
						cardNumber.setText(new_Card);
						UserCenterFragment.mBindDeviceNo = new_Card;
						// 更新用户的Code为新智能卡号
						Session.getInstance().setUserCode(new_Card);
					}
				} else {
					if (mToUtils != null) {
						mToUtils.cancel();
					}
					Toast.makeText(getActivity(), result.getRetInfo(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 校验智能卡及密码
	 * 
	 */
	private class VerifyBindDevicenoCardInfoTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private String carPwd = "";
		private String new_Card = "";
		private String new_Card_Pwd = "";

		public VerifyBindDevicenoCardInfoTask(String carPwd, String new_Card,
				String new_Card_Pwd) {
			super();
			this.carPwd = carPwd;
			this.new_Card = new_Card;
			this.new_Card_Pwd = new_Card_Pwd;
		}

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			return new UserCenterAction().validateCombineDevice(
					InterfaceUrls.VALIDATE_COMBINE_DEVICE, new_Card,
					new_Card_Pwd);
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (result == null || result.getRet() != 0) {
				if (mToUtils != null) {
					mToUtils.cancel();
				}
				Toast.makeText(getActivity(), result.getRetInfo(),
						Toast.LENGTH_LONG).show();
			} else {
				// isVerify = true;
				// 校验之后，重绑智能卡
				// BingDingNewCardTask bingDingNewCardTask = new
				// BingDingNewCardTask(
				// 0, carPwd, new_Card, new_Card_Pwd);
				// bingDingNewCardTask.execute();
				changeCardNum(new_Card);// 重绑智能卡
			}
		}
	}

	// 用户更换智能卡
	private void changeCardNum(final String newCard) {
		new AsyncTask<Void, Void, BaseJsonBean>() {
			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				String enCodeCard = "";
				try {
					enCodeCard = URLEncoder.encode(newCard, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				BaseJsonBean baseJson = null;
				baseJson = new UserCenterAction().changeCardNum(
						InterfaceUrls.CHANGE_CARD_NUM, enCodeCard, Session
								.getInstance().getUserName(), Session
								.getInstance().getPassWord());
				return baseJson;
			}

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (mToUtils != null) {
					mToUtils.cancel();
				}
				if (null != result && 0 == result.getRet()) {
					Toast.makeText(getActivity(), "绑定成功！", Toast.LENGTH_SHORT)
							.show();
					ChangeCardLyout.setVisibility(View.GONE);
					device_binding_layout.setVisibility(View.VISIBLE);
					cardNumber.setText(newCard);
					UserCenterFragment.mBindDeviceNo = newCard;
				} else {
					String tips = "绑定失败.";
					if (result != null) {
						tips = result.getRetInfo();
					}
					Toast.makeText(getActivity(), tips, Toast.LENGTH_SHORT)
							.show();
				}
			}
		}.execute();
	};

	/**
	 * 校验原智能卡和密码
	 * 
	 */
	private class SubmitDeviceAddCardTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private int tag;
		private String oldCardNo;
		private String oldCardPS;
		private String newCardNo;
		private String newCardPS;

		public SubmitDeviceAddCardTask(int tag, String oldCardNo,
				String oldCardPS, String newCardNo, String newCardPS) {
			super();
			this.tag = tag;
			this.oldCardNo = oldCardNo;
			this.oldCardPS = oldCardPS;
			this.newCardNo = newCardNo;
			this.newCardPS = newCardPS;
		}

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			BaseJsonBean baseJson = new UserCenterAction()
					.validateCombineDevice(
							InterfaceUrls.VALIDATE_COMBINE_DEVICE, oldCardNo,
							oldCardPS);
			return baseJson;
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (result != null) {
				if (result.getRet() == 0) {
					mBingDingNewCardTask = new BingDingNewCardTask(0,
							oldCardPS, newCardNo, newCardPS);
					mBingDingNewCardTask.execute();
				} else {
					if (mToUtils != null) {
						mToUtils.cancel();
					}
					Toast.makeText(getActivity(), result.getRetInfo(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}
