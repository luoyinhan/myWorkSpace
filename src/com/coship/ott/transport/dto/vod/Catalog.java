package com.coship.ott.transport.dto.vod;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏目实体类
 * */
public class Catalog implements Comparable<Catalog> {
	// 栏目ID 正整数
	private String columnID;
	// 栏目类别码
	// 根据不同的类型获取媒资或字栏目的方式不同
	// 0：标准栏目（从MSCP同步的栏目，可以按照栏目ID查询媒资或子栏目）
	// 1:首页推荐栏目（栏目，客户端或网站的首页上的栏目）
	private int columnTypeCode;
	// 栏目名称
	private String columnName;
	// 栏目别名
	private String alias;
	// 栏目描述
	private String describe;
	// 顺序
	private int rank;
	// 栏目对应的链接地址
	private String url;
	// URL类型
	// 1: 直接使用URL地址（热门专题对应）
	// 2: 本地URL地址
	// 3: 需要动态获取参数信息
	private String urlType;
	// 服务编码字段“服务编码”是天威项目特定字段，非必填，数字、字母、下划线组成
	private String serviceCode;
	// 栏目关联的产品类型
	// 枚举值：
	// -1:全部
	// 1:包月
	// 3:免费
	// 5:单片按次
	// 6:整包按次
	private String relevantChargeMode;
	// 栏目关联的产品信息 关系 1
	private ArrayList<ProductInfo> product;

	private List<Catalog> subList;
	private String parentID;

	public Catalog() {
	}

	public Catalog(String columnID, int columnTypeCode, String columnName,
			String alias, String describe, int rank, String url,
			String urlType, String serviceCode, String relevantChargeMode,
			ArrayList<ProductInfo> product, List<Catalog> subList,
			String parentID) {
		super();
		this.columnID = columnID;
		this.columnTypeCode = columnTypeCode;
		this.columnName = columnName;
		this.alias = alias;
		this.describe = describe;
		this.rank = rank;
		this.url = url;
		this.urlType = urlType;
		this.serviceCode = serviceCode;
		this.relevantChargeMode = relevantChargeMode;
		this.product = product;
		this.subList = subList;
		this.parentID = parentID;
	}

	public String getColumnID() {
		return columnID;
	}

	public void setColumnID(String columnID) {
		this.columnID = columnID;
	}

	public int getColumnTypeCode() {
		return columnTypeCode;
	}

	public void setColumnTypeCode(int columnTypeCode) {
		this.columnTypeCode = columnTypeCode;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlType() {
		return urlType;
	}

	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getRelevantChargeMode() {
		return relevantChargeMode;
	}

	public void setRelevantChargeMode(String relevantChargeMode) {
		this.relevantChargeMode = relevantChargeMode;
	}

	public ArrayList<ProductInfo> getProduct() {
		return product;
	}

	public void setProduct(ArrayList<ProductInfo> product) {
		this.product = product;
	}

	public List<Catalog> getSubList() {
		return subList;
	}

	public void setSubList(List<Catalog> subList) {
		this.subList = subList;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	@Override
	public int compareTo(Catalog another) {
		if (this.rank < another.rank) {
			return -1;
		} else if (this.rank == another.rank) {
			return 0;
		} else {
			return 1;
		}
	}
}