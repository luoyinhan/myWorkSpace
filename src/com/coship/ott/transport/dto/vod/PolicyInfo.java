package com.coship.ott.transport.dto.vod;

/**
 * 优惠策略实体类
 * */
public class PolicyInfo {
	// 用户所属行政区域代码 字母、数字和下划线的组合
	private String regionCode;
	// 字母、数字和下划线的组合
	private String policyCode;
	// 优惠策略名称 任意字符
	private String policyName;
	// 优惠方式 枚举值：0：折扣1：减免 如：7折用0.3表示
	private int discountMode;
	// 优惠数额 优惠方式为0：折扣介于0到1之间，小数点后保存两位。优惠方式为1：以分为单位。
	private int discountAmount;
	// 优先级 枚举值：
	// 0：低
	// 1：中
	// 2：高
	private int policyPrior;
	// 排它性 枚举值：
	// 0：是
	// 1：否
	private int exclusivity;
	// 优惠策略生效时间 格式：YYYY-MM-DD HH:MM:SS
	private String effectiveTime;
	// 优惠策略过期时间 格式：YYYY-MM-DD HH:MM:SS
	private String expireTime;
	// 优惠时段开始时间 格式： HH:MM:SS
	private String periodBegin;
	// 优惠时段结束时间 格式： HH:MM:SS
	private String periodEnd;

	public PolicyInfo() {
	}

	public PolicyInfo(String regionCode, String policyCode, String policyName,
			int discountMode, int discountAmount, int policyPrior,
			int exclusivity, String effectiveTime, String expireTime,
			String periodBegin, String periodEnd) {
		super();
		this.regionCode = regionCode;
		this.policyCode = policyCode;
		this.policyName = policyName;
		this.discountMode = discountMode;
		this.discountAmount = discountAmount;
		this.policyPrior = policyPrior;
		this.exclusivity = exclusivity;
		this.effectiveTime = effectiveTime;
		this.expireTime = expireTime;
		this.periodBegin = periodBegin;
		this.periodEnd = periodEnd;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public int getDiscountMode() {
		return discountMode;
	}

	public void setDiscountMode(int discountMode) {
		this.discountMode = discountMode;
	}

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
		this.discountAmount = discountAmount;
	}

	public int getPolicyPrior() {
		return policyPrior;
	}

	public void setPolicyPrior(int policyPrior) {
		this.policyPrior = policyPrior;
	}

	public int getExclusivity() {
		return exclusivity;
	}

	public void setExclusivity(int exclusivity) {
		this.exclusivity = exclusivity;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public String getPeriodBegin() {
		return periodBegin;
	}

	public void setPeriodBegin(String periodBegin) {
		this.periodBegin = periodBegin;
	}

	public String getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(String periodEnd) {
		this.periodEnd = periodEnd;
	}
}