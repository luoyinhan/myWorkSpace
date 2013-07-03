package com.coship.ott.transport;

public class InterfaceUrls {
	/**
	 * 4.1 获取客户端启动画面
	 * */
	public static String GET_HOMEPAGE_CONTENT = "msis/getHomePageContent";
	/**
	 * 4.3 获取首页推荐资源
	 * */
	public static String GET_RECOMMEND_RESOURCE = "msis/getRecommendResource";
	/**
	 * 4.4 获取栏目列表
	 * */
	public static String GET_CATALOG = "msis/getCatalog";
	/**
	 * 4.5 获取系统参数
	 * */
	public static String GET_PRAM = "msis/getPram";
	/**
	 * 4.8 获取资源列表
	 * */
	public static String GET_ASSET_LIST = "msis/getAssetList";
	/**
	 * 4.9 根据资源包代码获取资源列表
	 * */
	public static String GET_ASSETLIST_BY_PACKAGECODE = "msis/getAssetListByPackageCode";
	/**
	 * 4.10 获取搜索热门字
	 * */
	public static String GET_KEYWORD = "msis/getKeyWord";
	/**
	 * 4.11 搜索资源列表
	 * */
	public static String QUERY_ASSET_LIST = "msis/queryAssetList";
	/**
	 * 4.12 影片资源详情
	 * */
	public static String GET_ASSETDETAIL = "msis/getAssetDetail";
	/**
	 * 4.13 获取频道列表
	 * */
	public static String GET_CHANNELS = "msis/getChannels";
	/**
	 * 4.13 获取关联数据列表
	 * */
	public static String GET_RELATEASSET = "msis/getRelateAsset";
	/**
	 * 4.14 获取频道下节目列表
	 * */
	public static String GET_CHANNEL_PROGRAM = "msis/getChannelProgram";
	/**
	 * 4.15 获取频道品牌列表
	 * */
	public static String GET_CHANNEL_BRAND = "msis/getChannelbrand";
	/**
	 * 4.17 获取推荐直播节目
	 * */
	public static String GET_RECOMMEND_PROGRAM = "msis/getRecommendPorgram";
	/**
	 * 4.20 获取推荐频道品牌
	 * */
	public static String GET_RECOMMEND_CHANNELBRAND = "msis/getRecommendChannelbrand";
	/**
	 * 4.19 获取直播节目详情
	 * */
	public static String GET_CHANNELBRAND_INFO = "msis/getChannelbrandInfo";
	/**
	 * 4.19 获取直播节目详情
	 * */
	public static String GET_PROGRAM_INFO = "msis/getPorgramInfo";
	/**
	 * 4.20 节目评论
	 * */
	public static String ADD_USER_COMMENT = "userCenter/userComment";
	/**
	 * 4.21 查看节目最近评论
	 * */
	public static String GET_COMMENT_BY_ASSETID = "userCenter/getCommentByAssetId";
	/**
	 * 4.21 查看节目最近评论
	 * */
	public static String GET_COMMENT_BY_USERCODE = "userCenter/getCommentByUserCode";
	/**
	 * 4.21 删除用户评论
	 * */
	public static String DEL_USER_COMMENTS = "userCenter/deleteComment";
	/**
	 * 4.23 收藏节目
	 * */
	public static String ADD_FAVORITE = "userCenter/addFavorite";
	/**
	 * 4.24 查看收藏
	 * */
	public static String GET_FAVOURITE = "userCenter/getFavorite";
	/**
	 * 4.24 查看收藏
	 * */
	public static String DEL_FAVOURITE = "userCenter/delFavorite";
	/**
	 * 4.26 预定节目
	 * */
	public static String ADD_BOOK = "userCenter/addBook";
	/**
	 * 4.27 查看预定
	 * */
	public static String QUERY_BOOK = "userCenter/queryBook";
	/**
	 * 4.28 删除预订
	 * */
	public static String DEL_BOOK = "userCenter/delBook";
	/**
	 * 4.30 记录书签
	 * */
	public static String ADD_BOOKMARK = "userCenter/addBookMark";
	/**
	 * 4.31 查询书签
	 * */
	public static String GET_BOOKMARK = "userCenter/getBookMark";
	/**
	 * 4.34 删除书签
	 * */
	public static String DEL_BOOKMARK = "userCenter/delBookMark";
	/**
	 * 4.35 用户登录
	 * */
	public static String LOGIN = "userCenter/login";
	/**
	 * 4.36 用户鉴权
	 * */
	public static String GET_AUTH_SUBID = "userCenter/auth";
	/**
	 * 4.38 获取播放串
	 * */
	public static String GET_PLAYURL = "msis/getPlayURL";
	/**
	 * 4.39 用户反馈
	 * */
	public static String ADD_USER_FEEDBACK = "msis/addUserFeedBack";
	/**
	 * 4.40 查询更新
	 * */
	public static String QUERY_CLIENT_VERSION = "msis/queryClientVersion";
	/**
	 * 4.41 记录用户分享
	 * */
	public static String ADD_USER_SHARE = "userCenter/addUserShare";
	/**
	 * 4.42 查询用户分享
	 * */
	public static String QUERY_USER_SHARE = "userCenter/queryUserShare";
	/**
	 * 4.44 删除用户分享
	 * */
	public static String DEL_USER_SHARE = "userCenter/deleteShare";
	/**
	 * 4.43 获取频道的当前节目单
	 * */
	public static String GET_CURRENT_PROGRAM = "msis/getChannelCurrentPrograms";
	/**
	 * 4.44 获取多個频道的当前节目单
	 * */
	public static String GET_CHANNELS_CURRENT_PROGRAMS = "msis/getChannelsCurrentPrograms";
	/**
	 * 
	 * 4.46 获取关联关键词
	 * */
	public static String GET_RELATED_KEYWORDS = "msis/getRelatedKeyWords";
	/**
	 * 4.51 获取系统时间
	 */
	public static String GET_SYSTEM_TIME = "msis/getSystemTime";
	/**
	 * 广东省网专用，根据assetID获取resourceCode;
	 * */
	public static String GET_ASSET_RESOURCECODE = "msis/getAssetResourceCode";

	/**
	 * 用户信息查询接口
	 * */
	public static String GET_USERINFO = "userCenter/queryUserInfo";

	/**
	 * 修改用户信息
	 */
	public static String MODIFY_USER_INFO = "userCenter/modUser";

	/**
	 * 效验绑定设备
	 * */
	public static String VALIDATE_COMBINE_DEVICE = "userCenter/verifyBindDevicenoInfo";

	/**
	 * 智能卡绑定
	 */
	public static String ACCOUNT_BIND = "userCenter/accountBind";

	/**
	 * 修改用户密码
	 */
	public static String MODACCOUNT_OLD_PASSWORD = "userCenter/modAccountOldPassword";
	/**
	 * 2.4.9 签约帐号密码修改接口
	 */
	public static String MODACCOUNT_CARD_PASSWORD = "userCenter/modDevicenoPassword";
	/**
	 * 注册
	 * */
	public static String GET_REGISTER = "userCenter/registerUser";
	/**
	 * 2.4.5 效验用户信息
	 * */
	public static String VALIDATE_USER_INFO = "userCenter/verifyUserInfo";
	/**
	 * 已定套餐
	 */

	public static String QUERY_ORDERED_PRODUCT = "userCenter/queryOrderedProductInfos";
	/**
	 * 未定套餐
	 */
	public static String QUERY_UNORDERED_PRODUCT = "userCenter/queryUnorderedProductInfos";
	/**
	 * 订购套餐
	 */
	public static String CREATE_ORDER_PRODUCT = "userCenter/createProductOrder";
	/**
	 * 退定套餐
	 */
	public static String CANCEL_ORDERED_PRODUCT = "userCenter/cancelProductOrder";
	/**
	 * 密码重置
	 */
	public static String MODACCOUNT_PASSWORD = "userCenter/modAccountPassword";
	/**
	 * 校验用户名
	 */
	public static String VERIFY_ACCOUNTNAME = "userCenter/verifyAccountName";
	/**
	 * 校验用户同时登录情况
	 */
	public static String GET_HEARTBEAT = "userCenter/heartbeat";
	/**
	 * 获取公告数量
	 */
	public static String QUERY_NEWNOTICE = "msis/queryNewNotice";
	/**
	 * 获取公告内容
	 */
	public static String GET_NOTICES = "msis/getNotices";
	/**
	 * 获取专题列表
	 */
	public static String GET_SPECIALACT = "msis/getSpecialAct";
	/**
	 * 获取专题详情
	 */
	public static String GET_SPECIALACTINFO = "msis/getSpecialActInfo";
	/**
	 * 重绑智能卡号
	 */
	public static String CHANGE_CARD_NUM = "userCenter/replaceDevice";

}