package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;

/**
 * 产品实体类
 * */
public class ProductInfo {
	// 产品编码 字母、数字和下划线的组合
	private String productCode;
	// 产品名称 任意字符
	private String productName;
	// 当前产品价格，便于查询（以分为单位） 正整数
	private int productPrice;
	// 计费类型 枚举值：
	// 0:按次
	// 1:立即生效当月收费(包月)
	// 2:立即生效下月收费(包月)
	// 3:免费
	// 4:包时段
	// 5:单片按次
	// 6:整包接次
	private int chargeMode;
	// 计费周期，本次计费时效长度，如果超过时效则再次计费
	// 比如1天，1个月等 正整数
	// 如果计费类型为一次性、周期性必选。
	private int chargeTerm;
	// 计费周期单位 枚举值：
	// 0：分
	// 1：小时
	// 2：天
	// 3：月
	// 4：年
	// 5：免费商品默认值
	// 6：部（一部影片）
	// 7：次（允许使用的次数）
	// 8：秒（使用时长）。
	// 9：兆（使用流量）
	private int chargeTermUnit;
	// 包括定价信息+优惠信息（资费详情,由iEPG展示给用户）例如：点播一次2元，24内不重复收费，21：00到23：00点播一次1元 任意字符
	private String priceDesc;
	// 价格编码 字母、数字和下划线的组合
	private String PPVID;
	// 价格名称 任意字符
	private String PPVName;
	// 备注 任意字符
	private String remark;
	// 字母、数字和下划线的组合 20
	private String serviceCode;
	// 套餐号 字母、数字和下划线的组合
	private String packageId;
	// 是否允许重复订购 0 是 1 否 默认为1
	private int isRepeatOrder;
	// 是否允许基本包鉴权 0 是 1 否 默认为0
	private int isBaseAuth;
	// 按次产品订购和鉴权方式 0 按资源 1 按产品
	private int PPV_OrderType;
	// 优惠策略 对应关系0－N
	private ArrayList<PolicyInfo> PolicyInfoList;

	public ProductInfo() {
	}

	public ProductInfo(String productCode, String productName,
			int productPrice, int chargeMode, int chargeTerm,
			int chargeTermUnit, String priceDesc, String pPVID, String pPVName,
			String remark, String serviceCode, String packageId,
			int isRepeatOrder, int isBaseAuth, int pPV_OrderType,
			ArrayList<PolicyInfo> policyInfoList) {
		super();
		this.productCode = productCode;
		this.productName = productName;
		this.productPrice = productPrice;
		this.chargeMode = chargeMode;
		this.chargeTerm = chargeTerm;
		this.chargeTermUnit = chargeTermUnit;
		this.priceDesc = priceDesc;
		PPVID = pPVID;
		PPVName = pPVName;
		this.remark = remark;
		this.serviceCode = serviceCode;
		this.packageId = packageId;
		this.isRepeatOrder = isRepeatOrder;
		this.isBaseAuth = isBaseAuth;
		PPV_OrderType = pPV_OrderType;
		PolicyInfoList = policyInfoList;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(int productPrice) {
		this.productPrice = productPrice;
	}

	public int getChargeMode() {
		return chargeMode;
	}

	public void setChargeMode(int chargeMode) {
		this.chargeMode = chargeMode;
	}

	public int getChargeTerm() {
		return chargeTerm;
	}

	public void setChargeTerm(int chargeTerm) {
		this.chargeTerm = chargeTerm;
	}

	public int getChargeTermUnit() {
		return chargeTermUnit;
	}

	public void setChargeTermUnit(int chargeTermUnit) {
		this.chargeTermUnit = chargeTermUnit;
	}

	public String getPriceDesc() {
		return priceDesc;
	}

	public void setPriceDesc(String priceDesc) {
		this.priceDesc = priceDesc;
	}

	public String getPPVID() {
		return PPVID;
	}

	public void setPPVID(String pPVID) {
		PPVID = pPVID;
	}

	public String getPPVName() {
		return PPVName;
	}

	public void setPPVName(String pPVName) {
		PPVName = pPVName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public int getIsRepeatOrder() {
		return isRepeatOrder;
	}

	public void setIsRepeatOrder(int isRepeatOrder) {
		this.isRepeatOrder = isRepeatOrder;
	}

	public int getIsBaseAuth() {
		return isBaseAuth;
	}

	public void setIsBaseAuth(int isBaseAuth) {
		this.isBaseAuth = isBaseAuth;
	}

	public int getPPV_OrderType() {
		return PPV_OrderType;
	}

	public void setPPV_OrderType(int pPV_OrderType) {
		PPV_OrderType = pPV_OrderType;
	}

	public ArrayList<PolicyInfo> getPolicyInfoList() {
		return PolicyInfoList;
	}

	public void setPolicyInfoList(ArrayList<PolicyInfo> policyInfoList) {
		PolicyInfoList = policyInfoList;
	}
}