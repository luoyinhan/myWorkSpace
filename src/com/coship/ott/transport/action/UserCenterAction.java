package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.constant.Constant;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.ProductInfoJson;
import com.coship.ott.transport.dto.UserInfoJson;
import com.coship.ott.transport.dto.user.CheckLoginJson;
import com.coship.ott.transport.dto.user.FeedBackJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.google.gson.reflect.TypeToken;

/**
 * 用户中心调用接口
 * */
public class UserCenterAction extends BaseAction {

	private static final String LOGTAG = "FeedBackAction";

	/**
	 * 建议与反馈
	 * 
	 * @return {@link FeedBackJson}
	 * */
	public FeedBackJson getFeedBack(String url, String userCode,
			int feedbackType, String feedbackContent, String phone,
			String mail, String qq) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&feedbackType=").append(feedbackType)
				.append("&userCode=").append(userCode).append("&feedback=")
				.append(feedbackContent).append("&email=").append(mail)
				.append("&qq=").append(qq).append("&Phone=").append(phone);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		FeedBackJson feedbackContentJson = null;
		try {
			feedbackContentJson = gson.fromJson(jsonData,
					new TypeToken<FeedBackJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getFeedBack转换" + jsonData + "为FeedBackJson时出错！");
		}
		return feedbackContentJson;
	}

	/**
	 * 2.4.2 效验绑定设备
	 * 
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean validateCombineDevice(String url, String bindDeviceno,
			String servicePassWord) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&bindDeviceno=").append(bindDeviceno)
				.append("&servicePassWord=").append(servicePassWord);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getFeedBack转换" + jsonData + "为FeedBackJson时出错！");
		}
		return resultJson;
	}

	/**
	 * 验证用户名
	 * 
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean validateUserName(String url, String userName) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(Constant.TERMINAL_TYPE)
				.append("&resolution=").append(Constant.RESOLUTION);
		urlBuffer.append("&userName=").append(userName);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getFeedBack转换" + jsonData + "为FeedBackJson时出错！");
		}
		return resultJson;
	}

	/**
	 * 2.4.2 注册
	 * 
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean userRegister(String url, String userName,
			String mPasswd, String mNickName, String mLogo, String mSign,
			String mEmail, String mPhone, String mBindDeviceno, String mRemark) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(Constant.TERMINAL_TYPE)
				.append("&resolution=").append(Constant.RESOLUTION);
		urlBuffer.append("&userName=").append(userName).append("&passwd=")
				.append(mPasswd).append("&nickName=").append(mNickName)
				.append("&logo=").append(mLogo).append("&sign=").append(mSign)
				.append("&email=").append(mEmail).append("&phone=")
				.append(mPhone).append("&bindDeviceno=").append(mBindDeviceno)
				.append("&remark=").append(mRemark);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getFeedBack转换" + jsonData + "为FeedBackJson时出错！");
		}
		return resultJson;
	}

	/**
	 * 校验用户名
	 * 
	 * @return {@link UserInfoJson}
	 * */
	/*
	 * public UserInfoJson verifyAccountName(String url, String userName) {
	 * StringBuffer urlBuffer = urlbuf;
	 * urlBuffer.append("&userName=").append(userName); jsonData =
	 * NetTransportUtil.getContent(url, urlBuffer.toString()); if
	 * (TextUtils.isEmpty(jsonData)) { return null; } UserInfoJson resultJson =
	 * null; try { resultJson = gson.fromJson(jsonData, new
	 * TypeToken<UserInfoJson>() { }.getType()); } catch (Exception e) {
	 * Log.d(LOGTAG, "方法getUserInformation转换" + jsonData + "为UserInfoJson时出错！");
	 * } return resultJson; }
	 *//**
	 * 校验智能卡及智能卡密码
	 * 
	 * @return {@link UserInfoJson}
	 * */
	/*
	 * public UserInfoJson verifyBindDevicenoCardInfo(String url, String
	 * userName) { StringBuffer urlBuffer = urlbuf;
	 * urlBuffer.append("&bindDeviceno=").append(userName);
	 * urlBuffer.append("&servicePassWord=").append(userName); jsonData =
	 * NetTransportUtil.getContent(url, urlBuffer.toString()); if
	 * (TextUtils.isEmpty(jsonData)) { return null; } UserInfoJson resultJson =
	 * null; try { resultJson = gson.fromJson(jsonData, new
	 * TypeToken<UserInfoJson>() { }.getType()); } catch (Exception e) {
	 * Log.d(LOGTAG, "方法getUserInformation转换" + jsonData + "为UserInfoJson时出错！");
	 * } return resultJson; }
	 */
	/**
	 * 用户信息查询
	 * 
	 * @return {@link UserInfoJson}
	 * */
	public UserInfoJson getUserInformation(String url, String userName) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userName=").append(userName);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		Log.i("xue", "===jsonData=>>" + jsonData);
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		UserInfoJson resultJson = null;
		Log.d(LOGTAG, "" + jsonData);
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<UserInfoJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getUserInformation转换" + jsonData
					+ "为UserInfoJson时出错！");
		}
		// Log.d(LOGTAG,
		// "1:"+resultJson.getUserInfo().getBindDeviceNo()+"2:"+resultJson.getUserInfo().getEmail());
		return resultJson;
	}

	/**
	 * 未定套餐
	 * 
	 * @return {@link ProductInfoJson}
	 * */
	public ProductInfoJson queryOrderedProduct(String url, String userName) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userName=").append(userName);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ProductInfoJson productInfoJson = null;
		try {
			productInfoJson = gson.fromJson(jsonData,
					new TypeToken<ProductInfoJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法queryOrderedProduct转换" + jsonData
					+ "为ProductInfoJson时出错！");
		}
		return productInfoJson;
	}

	/**
	 * 已定套餐
	 * 
	 * @return {@link ProductInfoJson}
	 * */
	public ProductInfoJson queryUnorderedProduct(String url, String userName) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userName=").append(userName);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ProductInfoJson productInfoJson = null;
		try {
			productInfoJson = gson.fromJson(jsonData,
					new TypeToken<ProductInfoJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法queryUnorderedProduct转换" + jsonData
					+ "为ProductInfoJson时出错！");
		}
		return productInfoJson;
	}

	/**
	 * 退定套餐
	 * 
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean cancelOrderedProduct(String url, String userName,
			String productCode, String reason) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userName=").append(userName).append("&productcode=")
				.append(productCode).append("&reason=").append(reason);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<BaseJsonBean>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法cancelOrderedProduct转换" + jsonData
					+ "为baseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 订购套餐
	 * 
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean createOrderProduct(String url, String userName,
			String productCode, String developer, String remark) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userName=").append(userName).append("&productcode=")
				.append(productCode).append("&developer=").append(developer)
				.append("&remark=").append(remark);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<BaseJsonBean>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法cancelOrderedProduct转换" + jsonData
					+ "为baseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 智能卡绑定
	 * 
	 * @param url
	 * @param userName
	 * @param optType
	 *            0：解绑 1：绑定
	 * @param bindDeviceno
	 * @param passwd
	 * @return
	 */
	public BaseJsonBean deviceBindCardOrNot(String url, String userName,
			final int optType, final String bindDeviceno, final String passwd) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userName=").append(userName).append("&optType=")
				.append(optType).append("&bindDeviceno=").append(bindDeviceno)
				.append("&passwd=").append(passwd);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		Log.i("xue", "====绑定======>>" + jsonData);
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean baseJsonBean = null;
		try {
			baseJsonBean = gson.fromJson(jsonData,
					new TypeToken<BaseJsonBean>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法deviceBindCardOrNot转换" + jsonData
					+ "为baseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 修改用户信息
	 * 
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean modifyUserInfo(String url, String userName,
			String nickname, String logo, String sign, String email,
			String Phone, String remark) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(Constant.TERMINAL_TYPE)
				.append("&resolution=").append(Constant.RESOLUTION);
		urlBuffer.append("&userName=").append(userName).append("&nickName=")
				.append(nickname).append("&Logo=").append(logo)
				.append("&sign=").append(sign).append("&Email=").append(email)
				.append("&Phone=").append(Phone).append("&remark=")
				.append(remark);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		Log.i("xue", "==BaseJsonBean==jsonData=====>>" + jsonData);
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getFeedBack转换" + jsonData + "为FeedBackJson时出错！");
		}
		return resultJson;
	}

	/**
	 * 修改密码
	 * 
	 * @param url
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public BaseJsonBean changePassWord(String url, String userName,
			String oldPassword, String newPassword) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(Constant.TERMINAL_TYPE)
				.append("&resolution=").append(Constant.RESOLUTION);
		urlBuffer.append("&userName=").append(userName).append("&oldPassword=")
				.append(oldPassword).append("&newPassword=")
				.append(newPassword);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		Log.i("xue", "==oldPassword===>>" + oldPassword);
		Log.i("xue", "=====改密码====>>" + jsonData);
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法changePassWord转换" + jsonData + "为BaseJsonBean时出错！");
		}
		return resultJson;
	}

	/**
	 * 重置密码操作
	 * 
	 * @param url
	 * @param userName
	 * @param newPassword
	 * @return
	 */
	public BaseJsonBean restPassWord(String url, String userName,
			String newPassword) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(Constant.TERMINAL_TYPE)
				.append("&resolution=").append(Constant.RESOLUTION);
		urlBuffer.append("&userName=").append(userName).append("&newPassword=")
				.append(newPassword);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		LogUtils.trace(Log.INFO, "xue", "=====重置密码操作======>>" + jsonData);
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法restPassWord转换" + jsonData + "为BaseJsonBean时出错！");
		}
		return resultJson;
	}

	/**
	 * 校验用户信息
	 * 
	 * @param url
	 * @param verifyType
	 * @param userName
	 * @param bindDeviceno
	 * @param servicePassWord
	 * @param remark
	 * @return BaseJsonBean
	 */
	public BaseJsonBean validateUserInfo(String url, int verifyType,
			String userName, String bindDeviceno, String servicePassWord,
			String remark) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(Constant.TERMINAL_TYPE)
				.append("&resolution=").append(Constant.RESOLUTION);
		urlBuffer.append("&verifyType=").append(verifyType)
				.append("&userName=").append(userName).append("&bindDeviceno=")
				.append(bindDeviceno).append("&servicePassWord=")
				.append(servicePassWord).append("&remark=").append(remark);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法restPassWord转换" + jsonData + "为BaseJsonBean时出错！");
		}
		return resultJson;
	}

	/**
	 * 修改智能卡密码
	 * 
	 * @param url
	 * @param bindDeviceno
	 * @param oldPassword
	 * @param newPassword
	 * @return BaseJsonBean
	 */
	public BaseJsonBean changeCardWord(String url, String bindDeviceno,
			String oldPassword, String newPassword) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&bindDeviceno=").append(bindDeviceno)
				.append("&oldPassword=").append(oldPassword)
				.append("&newPassword=").append(newPassword);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法changePassWord转换" + jsonData + "为BaseJsonBean时出错！");
		}
		return resultJson;
	}

	/**
	 * 检测用户是否同时在登录
	 * 
	 * @param url
	 * @param bindDeviceno
	 * @param oldPassword
	 * @param newPassword
	 * @return BaseJsonBean
	 */
	public CheckLoginJson checkUer(String url, String userName, String token) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION).append("&userName=")
				.append(Session.getInstance().getUserName());
		urlBuffer.append("&token=").append(token);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		CheckLoginJson resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData,
					new TypeToken<CheckLoginJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法changePassWord转换" + jsonData + "为BaseJsonBean时出错！");
		}
		return resultJson;
	}
	
	/**
	 * 重绑智能卡
	 * 
	 * @param url
	 * @param bindDeviceno
	 * @param username
	 * @param userpwd
	 * @return BaseJsonBean
	 */
	public BaseJsonBean changeCardNum(String url, String bindDeviceno,String userName,String userPwd
			) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&bindDeviceno=").append(bindDeviceno).append("&userName=")
		.append(userName).append("&passwd=").append(userPwd);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		BaseJsonBean resultJson = null;
		try {
			resultJson = gson.fromJson(jsonData, new TypeToken<BaseJsonBean>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法changePassWord转换" + jsonData + "为BaseJsonBean时出错！");
		}
		return resultJson;
	}
}