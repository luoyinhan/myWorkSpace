package com.coship.ott.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coship.ott.activity.R;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.UserCenterAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.util.TestingUtil;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.ToastUtils;

public class PassWordChangeFragment extends Fragment implements OnClickListener {
	private TextView mUserNametx, mNewCardNametx;
	private EditText mOldPWDEdit, mNewPWDEdit, mInputPWDEdit;
	private String mOldPWD, mNewPWD, mInputPWD;
	private Button mUerSubmitbt, mUserEmptybt;
	private EditText mOldCardPWDEd, mNewCardPWDEdit, mInputCardPWDEd;
	private Button mCardSubmitbt, mCardEmptybt;
	private ToastUtils mToUtils;
	private String mUserName = UserCenterFragment.mName;
	private String mCardNO = UserCenterFragment.mBindDeviceNo;
	private ChangeUserPassWordTask mChangeUserPassWordTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pass_word_change_fragment, container,
				false);
	}

	@Override
	public void onStart() {
		super.onStart();
		setupView();
		initDate();
	}

	private void setupView() {
		mUserNametx = (TextView) getActivity().findViewById(R.id.userName);
		mOldPWDEdit = (EditText) getActivity().findViewById(R.id.oldPWDEdit);
		mNewPWDEdit = (EditText) getActivity().findViewById(R.id.newPWDEdit);
		mUserName = Session.getInstance().getUserName();
		mUserNametx.setText(mUserName);
		mInputPWDEdit = (EditText) getActivity()
				.findViewById(R.id.inputPWDEdit);
		mUerSubmitbt = (Button) getActivity().findViewById(
				R.id.userSubmitButton);
		mUserEmptybt = (Button) getActivity()
				.findViewById(R.id.userEmptyButton);
		mNewCardNametx = (TextView) getActivity().findViewById(
				R.id.new_card_num);
		mUerSubmitbt.setOnClickListener(this);
		mUserEmptybt.setOnClickListener(this);
	}

	private void initDate() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userSubmitButton:
			submitChangePSWord();
			break;
		case R.id.userEmptyButton:
			emptyUserPSWChangeED();
			break;
		case R.id.cardSubmitButton:
			break;
		case R.id.cardEmptyButton:
			emptyCardPSWChangeED();
			break;
		default:
			break;
		}
	}

	private void emptyUserPSWChangeED() {
		mOldPWDEdit.setText("");
		mNewPWDEdit.setText("");
		mInputPWDEdit.setText("");
	}

	private void emptyCardPSWChangeED() {
		mOldCardPWDEd.setText("");
		mNewCardPWDEdit.setText("");
		mInputCardPWDEd.setText("");
	}

	private void submitChangePSWord() {
		mOldPWD = mOldPWDEdit.getText().toString().trim();
		mNewPWD = mNewPWDEdit.getText().toString().trim();
		mInputPWD = mInputPWDEdit.getText().toString().trim();
		if (TextUtils.isEmpty(mOldPWD)) {
			Toast.makeText(getActivity(), "请输入原始密码", Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(mNewPWD)) {
			Toast.makeText(getActivity(), "请输入新密码", Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(mInputPWD)) {
			Toast.makeText(getActivity(), "请输入重复密码", Toast.LENGTH_SHORT).show();
			return;
		} else if (!TestingUtil.isFitMode(mOldPWD)) {
			Toast.makeText(getActivity(), "密码格式不正确", Toast.LENGTH_SHORT).show();
			return;
		} else if (!TestingUtil.isFitMode(mNewPWD)) {
			Toast.makeText(getActivity(), "新密码格式不正确", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (!TestingUtil.isFitMode(mInputPWD)) {
			Toast.makeText(getActivity(), "重复密码错误", Toast.LENGTH_SHORT).show();
			return;
		} else if (mOldPWD.length() < 6 && mOldPWD.length() > 32) {
			Toast.makeText(getActivity(), "请确保密码在6至32位之间", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (mNewPWD.length() < 6 && mNewPWD.length() > 32) {
			Toast.makeText(getActivity(), "请确保新密码在6至32位之间", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (mInputPWD.length() < 6 && mInputPWD.length() > 32) {
			Toast.makeText(getActivity(), "重复密码错误", Toast.LENGTH_SHORT).show();
			return;
		} else if (!mNewPWD.equals(mInputPWD)) {
			Toast.makeText(getActivity(), "重复密码与创建密码不一致", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mToUtils = new ToastUtils(getActivity());
		mToUtils.showToastAlong();
		mChangeUserPassWordTask = new ChangeUserPassWordTask();
		mChangeUserPassWordTask.execute();
	}

	/**
	 * 密码修改中，提交多屏看密码修改
	 * 
	 */
	private class ChangeUserPassWordTask extends
			AsyncTask<Void, Void, BaseJsonBean> {
		private String mOldPwd, mNewPwd;

		@Override
		protected BaseJsonBean doInBackground(Void... params) {
			String usernameContent = "";
			try {
				usernameContent = URLEncoder.encode(mUserName, "UTF-8");
				mOldPwd = URLEncoder.encode(mOldPWD, "UTF-8");
				mNewPwd = URLEncoder.encode(mNewPWD, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return new UserCenterAction().changePassWord(
					InterfaceUrls.MODACCOUNT_OLD_PASSWORD, usernameContent,
					mOldPwd, mNewPwd);
		}

		@Override
		protected void onPostExecute(BaseJsonBean result) {
			if (mToUtils != null) {
				mToUtils.cancel();
			}
			if (result != null) {
				if (result.getRet() == 0) {
					Toast.makeText(getActivity(), "密码修改成功！", Toast.LENGTH_SHORT)
							.show();
					Session.getInstance().setPassWord(mNewPwd);
					emptyUserPSWChangeED();
				} else {
					Toast.makeText(getActivity(), result.getRetInfo(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
