package com.coship.ott.transport.action;

import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.constant.Constant;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.special.SpecialActInfoJson;
import com.coship.ott.transport.dto.special.SpecialActsJson;
import com.coship.ott.transport.dto.vod.ADJson;
import com.coship.ott.transport.dto.vod.AssetDetailJson;
import com.coship.ott.transport.dto.vod.AssetListJson;
import com.coship.ott.transport.dto.vod.CatalogJson;
import com.coship.ott.transport.dto.vod.KeyWordJson;
import com.coship.ott.transport.dto.vod.PlayURLJson;
import com.coship.ott.transport.dto.vod.PramJson;
import com.coship.ott.transport.dto.vod.RecommendResourceJson;
import com.coship.ott.transport.dto.vod.ResourceCodeJson;
import com.coship.ott.transport.dto.vod.ResourceJson;
import com.coship.ott.transport.util.NetTransportUtil;
import com.coship.ott.utils.Session;
import com.google.gson.reflect.TypeToken;

public class VodAction extends BaseAction {

	private static final String LOGTAG = "VodAction";

	/**
	 * 4.29 获取广告
	 * 
	 * @param adPosId
	 *            广告位置 Int 必选
	 * @return {@link ADJson}
	 * 
	 * */
	public ADJson getAD(String url, int adPosId) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&adPosId=").append(adPosId);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ADJson aDJson = null;
		try {
			aDJson = gson.fromJson(jsonData, new TypeToken<ADJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getAD转换" + jsonData + "为ADJson时出错！");
		}
		return aDJson;
	}

	/**
	 * 4.3 获取首页推荐资源
	 * 
	 * @param version
	 *            协议版本 String 20 必选
	 * @param resolution
	 *            终端分辨率 String 10 必选 800*600
	 * 
	 * @return {@link RecommendResourceJson}
	 * */
	public RecommendResourceJson getRecommendResource(String url) {
		StringBuffer urlBuffer = urlbuf;
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		RecommendResourceJson recommendResource = null;
		try {
			recommendResource = gson.fromJson(jsonData,
					new TypeToken<RecommendResourceJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getRecommendResource转换" + jsonData
					+ "为RecommendResourceJson时出错！");
		}
		return recommendResource;
	}

	/**
	 * 4.4 获取栏目列表
	 * 
	 * @param catalogType
	 *            栏目类型 int 必选 1:首页推荐栏目（该类栏目没有子栏目） 2：标准栏目
	 * @param userCode
	 *            用户ID String 16 可选 用户ID,用户注册后必填
	 * @param parentId
	 *            父分类 Int 可选 父栏目ID，根栏目为空 catalogType=2时必须输入
	 * @param accessSource
	 *            接口源 Int 必选 1：PC 网站 2：PAD 网站 3：手机网站 4：PC 客户端 5：PAD客户端
	 *            6：OTT.CLIENT
	 * @return CatalogJson
	 * */
	public CatalogJson getCatalog(String url, int catalogType, String userCode,
			int parentId, int accessSource) {
		// StringBuffer urlBuffer = urlbuf;
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append("V003").append("&terminalType=")
				.append(Constant.TERMINAL_TYPE).append("&resolution=")
				.append(Constant.RESOLUTION).append("&userName=")
				.append(Session.getInstance().getUserName());
		urlBuffer.append("&catalogType=").append(catalogType)
				.append("&userCode=").append(userCode).append("&parentId=")
				.append(parentId).append("&accessSource=").append(accessSource);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());

		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		CatalogJson catalogJson = null;
		try {
			catalogJson = gson.fromJson(jsonData, new TypeToken<CatalogJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getCatalog转换" + jsonData + "为CatalogJson时出错！");
		}
		return catalogJson;
	}

	/**
	 * 4.5 获取系统参数
	 * 
	 * @param PramName
	 *            参数组名 String 10 必选 assetType: 影片类型 PublishDate: 影片发行时间
	 *            OriginName影片产地 CityCode频道地域 ChannelType频道类型
	 * 
	 * @return PramJson
	 * */
	public PramJson getPram(String url, String PramName) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&PramName=").append(PramName);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		PramJson pramJson = null;
		try {
			pramJson = gson.fromJson(jsonData, new TypeToken<PramJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getPram转换" + jsonData + "为PramJson时出错！");
		}
		return pramJson;
	}

	/**
	 * 4.6 获取专题列表
	 * 
	 * @param userCode
	 *            用户ID String 16 可选
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @return SpecialActsJson
	 * */
	public SpecialActsJson getSpecialAct(String url, String userCode,
			int pageSize, int curPage) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&pageSize=")
				.append(pageSize).append("&curPage=").append(curPage);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		SpecialActsJson specialActJson = null;
		try {
			specialActJson = gson.fromJson(jsonData,
					new TypeToken<SpecialActsJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getSpecialAct转换" + jsonData
					+ "为SpecialActsJson时出错！");
		}
		return specialActJson;
	}

	/**
	 * 4.7 获取专题详情
	 * 
	 * @param userCode
	 *            用户ID String 16 可选
	 * @param specialActId
	 *            专题ID Int 必选
	 * @return SpecialActInfoJson
	 * */
	public SpecialActInfoJson getSpecialActInfo(String url, String userCode,
			int specialActId) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode)
				.append("&specialActId=").append(specialActId);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		SpecialActInfoJson specialActInfoJson = null;
		try {
			specialActInfoJson = gson.fromJson(jsonData,
					new TypeToken<SpecialActInfoJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getSpecialActInfo转换" + jsonData
					+ "为SpecialActInfoJson时出错！");
		}
		return specialActInfoJson;
	}

	/**
	 * 4.8 获取资源列表
	 * 
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @param userCode
	 *            用户ID String 可选
	 * @param queryType
	 *            查询类型 Int 必选 0：按照栏目获取 1：获取影片排行 2：获取直播排行 3：获取最新更新 4：按照检索条件
	 * @param catalogId
	 *            栏目ID String 可选 queryType＝0 必选 在queryType＝1or＝4时有用
	 * @param isRecommend
	 *            是否推荐 Int 可选 0不推荐 1推荐 在queryType＝1or＝4时有用
	 * @param assetType
	 *            资源类型 Int 可选 queryType＝4 必选 获取资源分类返回 在queryType＝4时有用
	 * @param originName
	 *            发行地 String 可选 中国、美国、欧洲 在queryType＝4时有用
	 * @param publishDate
	 *            发行年 String 可选 只需年（如2010、2011等） 在queryType＝4时有用
	 * @param orderTag
	 *            排序标志 Int 可选 1：按照时间倒叙（最新） 2：按照点播次数倒叙（最热） 在queryType＝0 or 4时有用
	 * @return {@link AssetListJson}
	 * */
	public AssetListJson getAssetList(String url, int pageSize, int curPage,
			String userCode, String queryType, String catalogId,
			String isRecommend, String assetType, String originName,
			String publishDate, String orderTag) {
		StringBuffer urlBuffer = urlbuf;
		queryType = TextUtils.isEmpty(queryType) ? "-1" : queryType;
		isRecommend = TextUtils.isEmpty(isRecommend) ? "-1" : isRecommend;
		assetType = TextUtils.isEmpty(assetType) ? "-1" : assetType;
		orderTag = TextUtils.isEmpty(orderTag) ? "-1" : orderTag;
		urlBuffer.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage).append("&userCode=").append(userCode)
				.append("&queryType=").append(queryType).append("&catalogId=")
				.append(catalogId).append("&isRecommend=").append(isRecommend)
				.append("&assetType=").append(assetType).append("&originName=")
				.append(originName).append("&publishDate=").append(publishDate)
				.append("&orderTag=").append(orderTag);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AssetListJson assetListJson = null;
		try {
			assetListJson = gson.fromJson(jsonData,
					new TypeToken<AssetListJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getAssetList转换" + jsonData + "为AssetListJson时出错！");
		}
		return assetListJson;
	}

	/**
	 * 4.9 根据资源包代码获取资源列表
	 * 
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @param userCode
	 *            用户ID String 可选
	 * @param resourceCode
	 *            资源包代码 Int 必选
	 * @return AssetListJson
	 * */
	public AssetListJson getAssetListByPackageCode(String url, int pageSize,
			int curPage, String resourceCode, String userCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage).append("&resourceCode=").append(resourceCode)
				.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AssetListJson assetListJson = null;
		try {
			assetListJson = gson.fromJson(jsonData,
					new TypeToken<AssetListJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getAssetListByPackageCode转换" + jsonData
					+ "为AssetListJson时出错！");
		}
		return assetListJson;
	}

	/**
	 * 4.10 获取搜索热门字
	 * 
	 * @return KeyWordJson
	 * */
	public KeyWordJson getKeyWord(String url) {
		StringBuffer urlBuffer = urlbuf;
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		KeyWordJson keyWordJson = null;
		try {
			keyWordJson = gson.fromJson(jsonData, new TypeToken<KeyWordJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getKeyWord转换" + jsonData + "为KeyWordJson时出错！");
		}
		return keyWordJson;
	}

	/**
	 * 4.46 获取关联关键词
	 * 
	 * @param keyWord
	 *            用户输入关键词 String 必选
	 * @return KeyWordJson
	 * */
	public KeyWordJson getRelatedKeyWords(String url, String keyWord) {
		StringBuffer urlBuffer = urlbuf;
		urlbuf.append("&keyWord=").append(keyWord);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		KeyWordJson keyWordJson = null;
		try {
			keyWordJson = gson.fromJson(jsonData, new TypeToken<KeyWordJson>() {
			}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getKeyWord转换" + jsonData + "为KeyWordJson时出错！");
		}
		return keyWordJson;
	}

	/**
	 * 4.11 搜索资源列表
	 * 
	 * @param pageSize
	 *            每页显示记录数 Int 可选
	 * @param curPage
	 *            当前页 Int 可选
	 * @param keyWord
	 *            关键字 String 必选 对应点播的keyword字段，对应直播的eventName字段 模糊搜索
	 * @param userCode
	 *            用户ID String 可选
	 * @param queryType
	 *            查询类型 Int 必选 0：all 1：直播节目 2：点播
	 * @param orderType
	 *            排序类型 Int 可选 0：升序 1：降序 默认 0：升序 搜索直播节目或搜索all时有用到
	 * @return ResourcesJson
	 * */
	public ResourceJson queryAssetList(String url, int pageSize, int curPage,
			String keyWord, String userCode, int queryType, int orderType) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&pageSize=").append(pageSize).append("&curPage=")
				.append(curPage).append("&keyWord=").append(keyWord)
				.append("&userCode=").append(userCode).append("&queryType=")
				.append(queryType).append("&orderType=").append(orderType);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ResourceJson resourcesJson = null;
		try {
			resourcesJson = gson.fromJson(jsonData,
					new TypeToken<ResourceJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法queryAssetList转换" + jsonData + "为ResourceJson时出错！");
		}
		return resourcesJson;
	}

	/**
	 * 4.12 影片资源详情
	 * 
	 * @param resourceCode
	 *            媒资编号 String 必选
	 * @param userCode
	 *            用户ID String 可选
	 * @return AssetDetailJson
	 * */
	public AssetDetailJson getAssetDetail(String url, String resourceCode,
			String userCode) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&resourceCode=").append(resourceCode)
				.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AssetDetailJson assetDetailJson = null;
		try {
			assetDetailJson = gson.fromJson(jsonData,
					new TypeToken<AssetDetailJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getAssetDetail转换" + jsonData
					+ "为AssetDetailJson时出错！");
		}
		return assetDetailJson;
	}

	/**
	 * 4.13获取关联 推荐数据
	 * 
	 * @param
	 * @return
	 * */
	public AssetListJson getRelateAsset(String url, String resourceCode,
			String userCode) {
		StringBuffer urlBuffer = urlbuf;
		urlbuf.append("&resourceCode=").append(resourceCode)
				.append("&userCode=").append(userCode);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		AssetListJson assetListJson = null;
		try {
			assetListJson = gson.fromJson(jsonData,
					new TypeToken<AssetListJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getKeyWord转换" + jsonData + "为KeyWordJson时出错！");
		}
		return assetListJson;
	}

	/**
	 * 4.35 记录直播节目点播次数
	 * 
	 * @param programId
	 *            节目ID Int 必选
	 * @return {@link BaseJsonBean}
	 * */
	public BaseJsonBean addProgramCount(String url, int programId) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&programId=").append(programId);
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
			Log.d(LOGTAG, "方法addProgramCount转换" + jsonData
					+ "为BaseJsonBean时出错！");
		}
		return baseJsonBean;
	}

	/**
	 * 4.36 获取播放串
	 * 
	 * @param resourceCode
	 *            媒资编号 String 20 必选
	 * @param productCode
	 *            产品编码 String 20 可选
	 * @param userCode
	 *            用户ID String 32 可选
	 * @param subID
	 *            订购ID String 13 可选(不写，后台自己处理，节省一次网络请求)
	 * @param playType
	 *            播放类型 枚举 2 必选
	 * @param delay
	 *            相对秒数时移，参数值为从当期时间开始回退的毫秒数 int 可选
	 * @param shifttime
	 *            时移绝对开始时间 int 可选
	 * @param shiftend
	 *            时移绝对时间停止 int 可选
	 * @param timecode
	 *            书签时间点 int 可选
	 * @param fmt
	 *            影片格式 int 可选 不为空： 1=400K (极速) 2=800K (标清) 3=1200K
	 *            (高清)4=2M或以上(超清)为空则是自适应(x264_0k_mpegts&sora=1)
	 * @param terminalType
	 *            2 Ipad
	 * 
	 * @return {@link PlayURLJson}
	 * */
	public PlayURLJson getPlayURL(String url, String resourceCode,
			String productCode, String subID, String userCode, int playType,
			long delay, long shifttime, long shiftend, long timecode, int fmt) {
		StringBuffer urlBuffer = new StringBuffer().append("?version=")
				.append(Constant.DATA_INTERFACE_VERSION)
				.append("&terminalType=").append(2).append("&resolution=")
				.append(Constant.RESOLUTION);
		if (!TextUtils.isEmpty(subID)) {
			urlBuffer.append("&resourceCode=").append(resourceCode)
					.append("&productCode=").append(productCode)
					.append("&subID=").append(subID).append("&userName=")
					.append(Session.getInstance().getUserName())
					.append("&userCode=").append(userCode).append("&playType=")
					.append(playType).append("&delay=").append(delay)
					.append("&shifttime=").append(shifttime)
					.append("&shiftend=").append(shiftend).append("&timecode=")
					.append(timecode);
		} else {
			urlBuffer.append("&resourceCode=").append(resourceCode)
					.append("&productCode=").append(productCode)
					.append("&userName=")
					.append(Session.getInstance().getUserName())
					.append("&userCode=").append(userCode).append("&playType=")
					.append(playType).append("&delay=").append(delay)
					.append("&shifttime=").append(shifttime)
					.append("&shiftend=").append(shiftend).append("&timecode=")
					.append(timecode);
		}
		if (0 != fmt) {
			urlBuffer.append("&fmt=").append(fmt);
		}
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		PlayURLJson playURLJson = null;
		try {
			playURLJson = gson.fromJson(jsonData, new TypeToken<PlayURLJson>() {
			}.getType());
			System.out.println(playURLJson.getPalyURL());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getPlayURL转换" + jsonData + "为PlayURLJson时出错！");
		}
		return playURLJson;
	}

	/**
	 * 广东省网专用，根据assetID获取resourceCode;
	 * */
	public ResourceCodeJson getAssetResourceCode(String url, String assetID) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&assetID=").append(assetID);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		ResourceCodeJson resourceCodeJson = null;
		try {
			resourceCodeJson = gson.fromJson(jsonData,
					new TypeToken<ResourceCodeJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法queryClientVersion转换" + jsonData
					+ "为ClientVersionJson时出错！");
		}
		return resourceCodeJson;
	}

	/**
	 * 获取专题列表
	 * */
	public SpecialActsJson getSpecialAct(String url, String userCode,
			String userName, int pageSize, int curPage) {
		StringBuffer urlBuffer = urlbuf;
		urlBuffer.append("&userCode=").append(userCode).append("&userName=")
				.append(userName).append("&pageSize=").append(pageSize)
				.append("&curPage").append(curPage);
		jsonData = NetTransportUtil.getContent(url, urlBuffer.toString());
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		SpecialActsJson specialactsJson = null;
		try {
			specialactsJson = gson.fromJson(jsonData,
					new TypeToken<SpecialActsJson>() {
					}.getType());
		} catch (Exception e) {
			Log.d(LOGTAG, "方法getSpecialAct转换" + jsonData
					+ "为SpecialActsJson时出错！");
		}
		return specialactsJson;
	}
}