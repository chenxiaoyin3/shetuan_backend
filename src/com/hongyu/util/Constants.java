package com.hongyu.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

	/**
	 * 不可实例化
	 */
	private Constants() {
	}

	/**
	 * 审核状态
	 * 
	 * @author guoxinze
	 *
	 */
	public  enum AuditStatus {

		/** 未提交 */
		unsubmitted,

		/** 审核中 */
		auditing,

		/** 已通过 */
		pass,

		/** 已驳回 */
		notpass,
	}

	/**
	 * 线路扣点方式
	 * 
	 * @author guoxinze
	 *
	 */
	public enum DeductLine {

		/** 团队散客扣点 */
		tuanke,

		/** 人头扣点 */
		rentou,
	}
	
	/**
	 * 其他扣点方式
	 */
	public enum DeductQita {

		/** 流水 */
		liushui,
	}

	/**
	 * 票务扣点方式
	 * 
	 * @author guoxinze
	 *
	 */
	public enum DeductPiaowu {

		/** 流水扣点 */
		liushui,

		/** 人头扣点 */
		rentou,
	}

	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/***************** 拦截器参数 *****************/
	/** 登录表单路径 */
	public static String loginPath = "/user/login";

	/** 登录表单检验路径 */
	public static String loginCheckPath = "common/prege/logincivilheck";

	/** 静态资源路径 */
	public static String resourcesPath = "resources";

	/** 登陆提交路径 */
	public static String submitPath = "common/submit";
	
	/** 验证是否重复登陆路径*/

	public static String judgeRepeatLoginPath = "common/avoidRepeatLogin";
	
	/** 注销 */
	public static String logoff = "common/logout";

	/** 验证码 */
	public static String captchaPath = "common/captcha";

	/** 主页面 */
	public static String mainPath = "/home";

	/** 无权限界面 */
	public static String exceptionPath = "/common/privilege/authoritycheck";

	/***************** 监听器常量 start *****************/
	public static final String JiaoguanlifeiListenerURL = "admin/guanlifeiAudit";
	public static final String JiaoguanlifeiListenerFinance = "分公司财务部";
	public static final String PiaowubuGysShenhe = "admin/pinkong/newcontract";
	public static final String ZonggongsiCaiwushenhe = "admin/accountant/servicerDeposit";
	public static final String CaigoubushenheTuiyajin = "admin/gystuichu/shenhe";
	public static final String Biangengkoudian = "admin/pinkong/biankoudian";
	public static final String Xuqian = "admin/pinkong/xuqian";
	public static final String Tuibufen = "admin/pinkong/tuibufen";
	public static final String XianlushenheUrl = "admin/pinkong/xianlu";
	public static final String sceneticketprice = "admin/pinkong/sceneticketprice";
	public static final String hotelroomprice = "admin/pinkong/hotelroomprice";
	public static final String hotelandsceneroomprice = "admin/pinkong/hotelandsceneroomprice";
	public static final String GroupBiankoudian = "admin/caigou/biankoudian";
	public static final String Promotion = "admin/linepromotion/audit";
	public static final String storeFhynew = "admin/fhyStore/newReview";
	public static final String creditFhy = "admin/fhyCredit/review";
	public static final String XiaotuanAudit = "admin/storeLineOrder/provider_cancel_group";
	public static final String Regulate = "admin/regulate/shenhe";
	public static final String PinkongAuditStoreTuiTuan = "admin/storeLineOrderScg";

	public static final String CaiwuAuditStoreTuiTuan = "admin/storeLineOrderScg";
	public static final String PinkongAuditStoreShouHou = "admin/storeLineOrderScs";
	public static final String CaiwuAuditStoreShouHou = "admin/storeLineOrderScs";
	public static final String Shuaiweidan = "admin/swd/shenhe";
	public static final String branchGroupSettle = "admin/branchsettle/review";
	public static final String visaPrice="admin/pinkong/visaPrice";
    public static final String subscribePrice="admin/pinkong/subScribe";



	/** 部门Model: 总公司商贸部财务部 */
	public static final String Business_Finance = "总公司商贸部财务部";
	/** 部门Model: 总公司商贸部渠道销售部 */
	public static final String Business_Sale = "总公司商贸部渠道销售部";
	/** 部门Model: 总公司商贸部采购部 */
	public static final String Business_Purchase = "总公司商贸部采购部";
	/** 部门Model: 总公司商贸部库管部 */
	public static final String Business_InBound = "总公司商贸部采购部";

	/** 商贸部采购部员工 权限: 采购单管理 */
	public static final String Purchase_Manage = "admin/business/purchase/purchaseemployee";
	/** 商贸部采购部经理 权限: 采购审核 */
	public static final String Purchase_Audit = "admin/business/purchase/purchasemanager";
	/** 商贸部渠道销售部 权限: 提成比例管理 */
	public static final String Purchase_Divide = "admin/business/purchase/purchasequdao";
	/** 商贸部库管员 权限: 入库管理 */
	public static final String Purchase_InBound = "admin/business/purchase/storekeeper";
	/** 商贸部财务部 权限: 结算管理 */
	public static final String Purchase_Balance = "admin/business/purchase/financer";

	/** 采购入库流程 任务结点名称 : 采购部经理审核 */
	public static final String TaskName_Purchase_Audit = "采购部经理审核";

	/** 采购入库流程 任务结点名称 : 渠道销售部设置提成比例 */
	public static final String TaskName_Channel_Percent = "渠道销售部设置提成比例";

	/** 采购入库流程 任务结点名称 : 采购部填写物流信息 */
	public static final String TaskName_Logistics_Info = "采购部填写物流信息";

	/** 采购入库流程 任务结点名称 : 库管入库 */
	public static final String TaskName_InBound = "库管入库";

	/** 采购入库流程 任务结点名称 : 采购部结算确认 */
	public static final String TaskName_Balance_Confirm = "采购部结算确认";

	/** 采购入库流程 任务结点名称 : 采购部提请结算 */
	public static final String TaskName_Balance_Submit = "采购部提请结算";

	/** 采购入库流程 任务结点名称 : 商贸财务付款 */
	public static final String TaskName_Balance_Pay = "商贸财务付款";

	/** 采购入库流程 任务结点名称 : 商贸财务预付款 */
	public static final String TaskName_Balance_Pre = "商贸财务预付款";

	/** 采购入库流程 任务结点名称 : 商贸财务结算尾款 */
	public static final String TaskName_Balance_Due = "商贸财务结算尾款";

	/***************** 监听器常量 end *****************/
	
	
	/**************退货采购账号****************/
	public static final String REFUND_PURCHASE_ACCOUNT = "thcg";

	/***************** 部门常量 ******************/
	public static final String fengongsimendian = "分公司门店";
	//郭新泽修改2190116
	public static final String zhiyingmendian = "分公司直营门店";
	public static final String zonggongsi = "总公司";
	public static final String fengongsi = "分公司";
	public static final Integer hyStore = 0;//虹宇门店类型
	public static final Integer gkStore = 1;//挂靠门店
	public static final Integer zyStore  = 2;//直营门店
	public static final String hyActiviedStoreManagerRole="虹宇门店经理已激活";
	public static final String gkActiviedStoreManagerRole = "挂靠门店经理已激活";
	public static final String zyActiviedStoreManagerRole = "直营门店经理已激活";
	public static class WeBusinessType {
		public static final int Interior = 0;
		public static final int Exterior = 1;

	}
	/***************订单来源***********************/
	public static final int mendian = 0;//门店
	public static final int guanwangnonmendian = 1;//官网不选择门店
	public static final int guanwangmendian = 2;//官网选择门店
	

	/***************** 采购单常量 ****************/
	// 采购付款方式
	static final public Integer PURCHASE_TYPE_FULL_PAYMENT = 0;
	static final public Integer PURCHASE_TYPE_PARTIAL_PAYMENT = 1;
	static final public Integer PURCHASE_TYPE_PAY_ON_DELIVERY = 2;
	// 采购单状态
	/***************** 待提交 ******************/
	static final public Integer PURCHASE_STATUS_WAIT_FOR_SUBMIT = -1;
	/***************** 待审核 ******************/
	static final public Integer PURCHASE_STATUS_WAIT_FOR_AUDITED = 0;
	/***************** 待付款 ******************/
	static final public Integer PURCHASE_STATUS_WAIT_FOR_PAID = 1;
	/***************** 待设置提成比例 ******************/
	static final public Integer PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION = 2;
	/***************** 待填写物流信息 ******************/
	static final public Integer PURCHASE_STATUS_WAIT_FOR_SET_SHIP_INFO = 3;
//	/***************** 待入库 ******************/
//	static final public Integer PURCHASE_STATUS_WAIT_FOR_INBOUND = 4;
	/***************** 入库中 ******************/
	static final public Integer PURCHASE_STATUS_INBOUNDING = 4;
	/***************** 已入库 ******************/
	static final public Integer PURCHASE_STATUS_FINISH_INBOUND = 5;
	/***************** 待结算 ******************/
	static final public Integer PURCHASE_STATUS_WAIT_FOR_BALANCE = 6;
	/***************** 已完成 ******************/
	static final public Integer PURCHASE_STATUS_FINISH_BALANCE = 7;
	/***************** 已驳回 ******************/
	static final public Integer PURCHASE_STATUS_REJECTED = 8;
	
	/*************微信公众号相关接口*****************/
	public static final String GET_OAUTH_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
	public static final String GET_METHOD = "GET";
	public static final String POST_METHOD = "POST";
	public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
//	public static final String Store_RentGuide_Template="rsQXAfGVrXo6XsGa58hwW07wmDzDy8B8CJEQfBun6lg";
	public static final String Store_RentGuide_Template="JnQvQrAfCRlYlGbqOLQe_nW2K-azOWS2sJF2ymsXuaE";
	public static final String Store_RentGuideCancel_Template="wRuPtjpxxUOt7dJ6y19XQqRUoI849mWtOw85j41QCcI";
	
	public static final String APP_ID="wx69b4b6538f5d4fba";
	public static final String APP_SECRET="14b66ee47141925c81a7782e94607789";
	public static final String TEST_OPENID="ox5AHj5FtXrKQKWQUpWIJ39zUzAw";
	public static final String Quxiao_Paiqian="wRuPtjpxxUOt7dJ6y19XQqRUoI849mWtOw85j41QCcI";
	
	public static final int AGENTID_STORE = 1000003;
	public static final int AGENTID_LIANSUO = 1000010;
	public static final int AGENTID_XINGZHENG = 1000008;
	public static final int AGENTID_FUZONG = 1000011;
	// 发送模板消息
	public static final String SEND_TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/template/send";

	/**************江泰预充值副总审核限额id*********************************/
	static final public String fzjiangtai="fzjiangtai";
	static final public String storeLogout="storeLogout";
	/******************其他服务费id*********************************/
	static final public String elseFee="elseFee";

    /*******法大大电子合同***********/
    public static String AUTO_SIGN_NOTIFY_URL;
    public static String CUSTOMER_RETURN_URL;
    public static String CUSTOMER_NOTIFY_URL;
    public static String FDD_APP_ID;
    public static String FDD_APP_SECRET;
    public static String FDD_VERSION;
    public static String FDD_URL;
    public static String FDD_HYNAME;
    public static String FDD_HYCUSTOMERID;
    @Value("${fdd.AUTO_SIGN_NOTIFY_URL}")
    public void setAutoSignNotifyUrl(String autoSignNotifyUrl) {AUTO_SIGN_NOTIFY_URL = autoSignNotifyUrl;}
    @Value("${fdd.CUSTOMER_RETURN_URL}")
    public  void setCustomerReturnUrl(String customerReturnUrl) {CUSTOMER_RETURN_URL = customerReturnUrl;}
    @Value("${fdd.CUSTOMER_NOTIFY_URL}")
    public  void setCustomerNotifyUrl(String customerNotifyUrl) {CUSTOMER_NOTIFY_URL = customerNotifyUrl;}
    @Value("${fdd.FDD_APP_ID}")
    public  void setFddAppId(String fddAppId) {FDD_APP_ID = fddAppId;}
    @Value("${fdd.FDD_APP_SECRET}")
    public  void setFddAppSecret(String fddAppSecret) {FDD_APP_SECRET = fddAppSecret;}
    @Value("${fdd.FDD_VERSION}")
    public  void setFddVersion(String fddVersion) {FDD_VERSION = fddVersion;}
    @Value("${fdd.FDD_URL}")
    public  void setFddUrl(String fddUrl) {FDD_URL = fddUrl;}
    @Value("${fdd.FDD_HYNAME}")
    public  void setFddHyname(String fddHyname) {FDD_HYNAME = fddHyname;}
    @Value("${fdd.FDD_HYCUSTOMERID}")
    public  void setFddHycustomerid(String fddHycustomerid) {FDD_HYCUSTOMERID = fddHycustomerid;}
    /*******end***********/

    /**************江泰保险******************/
	/**MD5KEY  江泰提供*/
	public  static String MD5KEY;
	/**渠道信息（自定义，双方同步）*/
	public  static String CHANNEL;
	/**渠道机构代码（江泰提供）*/
	public  static String CHANNEL_COM_CODE;
	/**旅行社代码（江泰提供）*/
	public  static String TRAVEL_AGENCY_CODE;
	/**旅行社营业许可证号（总社）（根据国家旅游总局公布）*/
	public  static String TRAVEL_AGENCY_LICENSE_CODE;
	/**渠道交易代码（公共字段说明) 投保代码*/
	public  static String CHANNEL_TRADE_CODE_ORDER;
	/**渠道交易代码（公共字段说明) 撤保代码*/
	public  static String CHANNEL_TRADE_CODE_CANCEL_ORDER;
	/**渠道业务代码（江泰提供）可以为空*/
	public  static String CHANNEL_BUSINESS_CODE;
	/**渠道操作人代码(可以为空)*/
	public  static String CHANNEL_OPERATE_CODE;
	/**江泰投保出单接口URL*/
	public  static String JT_ORDER_URL;
	/**江泰撤保接口URL*/
	public  static String JT_CANCEL_ORDER_URL;
	/**江泰下载电子保单接口URL*/
	public  static String JT_ORDER_DOWN_URL;
	/**江泰下载电子个人凭证接口URL*/
	public  static String JT_CERTIFICATE_DOWN_URL;
    @Value("${jiangtai.MD5KEY}")
    public  void setMD5KEY(String md5KEY) {MD5KEY = md5KEY;}
    @Value("${jiangtai.CHANNEL}")
    public  void setCHANNEL(String channel) {CHANNEL = channel;}
    @Value("${jiangtai.CHANNEL_COM_CODE}")
    public  void setChannelComCode(String channelComCode) {CHANNEL_COM_CODE = channelComCode;}
    @Value("${jiangtai.TRAVEL_AGENCY_CODE}")
    public  void setTravelAgencyCode(String travelAgencyCode) {TRAVEL_AGENCY_CODE = travelAgencyCode;}
    @Value("${jiangtai.TRAVEL_AGENCY_LICENSE_CODE}")
    public  void setTravelAgencyLicenseCode(String travelAgencyLicenseCode) {TRAVEL_AGENCY_LICENSE_CODE = travelAgencyLicenseCode;}
    @Value("${jiangtai.CHANNEL_TRADE_CODE_ORDER}")
    public  void setChannelTradeCodeOrder(String channelTradeCodeOrder) {CHANNEL_TRADE_CODE_ORDER = channelTradeCodeOrder;}
    @Value("${jiangtai.CHANNEL_TRADE_CODE_CANCEL_ORDER}")
    public  void setChannelTradeCodeCancelOrder(String channelTradeCodeCancelOrder) {CHANNEL_TRADE_CODE_CANCEL_ORDER = channelTradeCodeCancelOrder;}
    @Value("${jiangtai.CHANNEL_BUSINESS_CODE}")
    public  void setChannelBusinessCode(String channelBusinessCode) {CHANNEL_BUSINESS_CODE = channelBusinessCode;}
    @Value("${jiangtai.CHANNEL_OPERATE_CODE}")
    public  void setChannelOperateCode(String channelOperateCode) {CHANNEL_OPERATE_CODE = channelOperateCode;}
    @Value("${jiangtai.JT_ORDER_URL}")
    public  void setJtOrderUrl(String jtOrderUrl) {JT_ORDER_URL = jtOrderUrl;}
    @Value("${jiangtai.JT_CANCEL_ORDER_URL}")
    public  void setJtCancelOrderUrl(String jtCancelOrderUrl) {JT_CANCEL_ORDER_URL = jtCancelOrderUrl;}
    @Value("${jiangtai.JT_ORDER_DOWN_URL}")
    public  void setJtOrderDownUrl(String jtOrderDownUrl) {JT_ORDER_DOWN_URL = jtOrderDownUrl;}
    @Value("${jiangtai.JT_CERTIFICATE_DOWN_URL}")
    public  void setJtCertificateDownUrl(String jtCertificateDownUrl) {JT_CERTIFICATE_DOWN_URL = jtCertificateDownUrl;}
	/**********end***********/

	public final static String COUPON_LINE_PREFIX= "10";
	public final static String COUPON_AWARD_PREFIX= "20";
	public final static String COUPON_SALE_PREFIX= "30";
	public final static String COUPON_GIFT_PREFIX= "40";
	public final static String COUPON_BIGCUSTOMER_PREFIX= "50";
	/*************************编号静态变量   end*****************************/
	/************************损失产品***********************************/
	public static final Integer SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED = 0;
	public static final Integer SPECIALTY_LOST_STATUS_PASS_AUDITING = 1;
	public static final Integer SPECIALTY_LOST_STATUS_FAIL_AUDITING = 2;
	public static final Integer SPECIALTY_LOST_EXPIRED = 0;
	public static final Integer SPECIALTY_LOST_DAMAGED = 1;
	public static final Integer SPECIALTY_LOST_REASON_PROVIDER = 0;
	public static final Integer SPECIALTY_LOST_REASON_PLATFORM = 1;
	public static final Integer SPECIALTY_LOST_REASON_CONSUMER = 2;
	
	/************************商贸订单状态***********************************/
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_PAY = 0;	//待付款
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW = 1;	//待审核
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_INBOUND = 2;	//待出库
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY = 3;	//待发货
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE = 4;	//待收货
	public static final Integer BUSINESS_ORDER_STATUS_HAS_RECEIVED = 5;	//已收货
	public static final Integer BUSINESS_ORDER_STATUS_FINISH = 6;	//已完成
	public static final Integer BUSINESS_ORDER_STATUS_CANCELED = 7;	//已取消
	public static final Integer BUSINESS_ORDER_STATUS_APPLY_RETURN_GOODS_TO_CONFIRM = 8;	//申请退货待确认
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_RETURN_GOODS = 9;	//待退货
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_RETURN_GOODS_INBOUND = 10;	//待退货入库
	public static final Integer BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND = 11;	//待退款
	public static final Integer BUSINESS_ORDER_STATUS_FINISH_REFUND = 12;	//已退款
	
	/************************订单退款状态***********************************/
	public static final Integer BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_CONFIRM = 1;
	public static final Integer BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_RETURN_PRODUCT = 2;
	public static final Integer BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_RETURN_INBOUND = 3;
	public static final Integer BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY = 4;
	public static final Integer BUSINESS_ORDER_REFUND_STATUS_FINISH = 5;
	
	/************************系统参数类型***********************************/
	public static final String BUSINESS_SYSTEM_PARAMETER_TYPE_EXPIRATION_DAYS = "过期提醒期限";
	
	/************************微商类型***********************************/
	public static final Integer WEBUSINESS_TYPE_HONGYU = 0;
	public static final Integer WEBUSINESS_TYPE_NON_HONGYU = 1;
	public static final Integer WEBUSINESS_TYPE_PERSON = 2;
	
	/************************微商模型类型***********************************/
	public static final String WEBUSINESS_MODEL_HONGYU = "虹宇门店";
	public static final String WEBUSINESS_MODEL_NON_HONGYU = "非虹宇门店";
	public static final String WEBUSINESS_MODEL_PERSON = "个人商贸";
	
	
	/************************二次消费的标识位***********************************/
	public static final Integer DUMMY_TWICE_CONSUME = 0;
	public static final Integer REAL_TWICE_CONSUME= 1;
	
	/*************************PV点击类型**********************************/
	public static enum PVClickType{
		HOME_PAGE,	//首页
		SPECIALTY_DETAIL,	//特产详情页
		SIMPLE_PROMOTION_DETAIL,	//普通优惠详情页
		GROUP_PROMOTION_DETAIL	//组合优惠详情页
		
	};
	

	/*************************旅游订单状态**********************************/
	public static final Integer HY_ORDER_STATUS_WAIT_STORE_PAY = 0;	//待门店付款
	public static final Integer HY_ORDER_STATUS_WAIT_STORE_CONFIRM = 1;	//待门店确认
	public static final Integer HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM = 2;	//待供应商确认
	public static final Integer HY_ORDER_STATUS_PROVIDER_ACCEPT = 3;	//供应商通过
	public static final Integer HY_ORDER_STATUS_REJECT_WAIT_FINANCE = 4;	//驳回待财务确认
	public static final Integer HY_ORDER_STATUS_REJECTED = 5;	//已驳回
	public static final Integer HY_ORDER_STATUS_CANCELED = 6;	//已取消

	/*************************旅游订单支付状态**********************************/
	public static final Integer HY_ORDER_PAY_STATUS_WAIT = 0;	//待支付
	public static final Integer HY_ORDER_PAY_STATUS_PAID = 1;	//已支付
	
	
	/*************************旅游订单门店确认状态**********************************/
	public static final Integer HY_ORDER_CHECK_STATUS_WAIT = 0;	//待确认
	public static final Integer HY_ORDER_CHECK_STATUS_ACCEPT = 1;	//已确认
	public static final Integer HY_ORDER_CHECK_STATUS_REJECT = 2;	//已驳回


	/******************个人接入支付地址***********************/
	//测试
	public static final String PersonalPay="https://111.205.51.141/per/preEpayLogin.do?_locale=zh_CN";
	//生产
//	public static final String PersonalPay="https://www.cebbank.com/per/preEpayLogin.do?_locale=zh_CN";
	
	/******************企业支付接入地址**********************/
	//测试
	public static final String EnterPrisePay="https://111.205.51.141:8443/cebent/preEpayLogin.do?_locale=zh_CN";
	//生产
//	public static final String EnterPrisePay="https://ebank.cebbank.com/cebent/preEpayLogin.do?_locale=zh_CN";
	
	/******************企业本行支付审核地址******************/
	//测试
	public static final String EnterPriseCheck="https://111.205.51.141:8443/cebent/prelogin.do";
			
	/******************单笔查询服务器地址*******************/
	//测试
	public static final String Query="https://111.205.51.141/per/QueryMerchantEpay.do";
	//生产
//	public static final String Query="https://www.cebbank.com/per/QueryMerchantEpay.do";
	
//	/******************分期支付接入地址*********************/
//	//测试
//	public static final String statePay="https://111.205.51.141/per/statePay.do?_locale=zh_CN";
//	//生成
////	public static final String statePay="https://www.cebbank.com/per/stagePay.do?_locale=zh_CN";
//	
//	/******************客服手续费试算服务器地址***************/
//	//测试
//	public static final String stagePayCost="https://111.205.51.141/per/stagePayCost.do";
//	//生产
////	public static final String stagePayCost="https://www.cebbank.com/per/stagePayCost.do";
	/******************个人跨行支付接入地址*********************/
	//测试
	public static final String interBankPersonal="https://111.205.51.141/per/preEpayLogin2.do?_locale=zh_CN";
	//生产
//	public static final String interBankPersonal="https://www.cebbank.com/per/preEpayLogin2.do?_locale=zh_CN";
	 /****************企业跨行支付接入地址*********************/
	//测试
	public static final String interBankEnterprise="https://111.205.51.141/cebent/preEpayLogin2.do?_locale=zh_CN";
	//生产
//	public static final String interBankEnterprise="https://www.cebbank.com/cebent/preEpayLogin2.do?_locale=zh_CN";
	/******************交易代码表**************************/
	public static final String PPayCode="IPER";//B2C支付/B2C跨行支付
	public static final String EPayCode="EPER";//企业支付/企业跨行支付
	public static final String QueryCode="IQSR";//单笔订单查询
	public static final String StagePayCode="SPER";//分期支付
	public static final String StagePayCostCode="SPFC";//客户手续费试算
	
	/******************线路促销状态**************************/
	public static final Integer LINE_PROMOTION_STATUS_AUDITING = 0;
	public static final Integer LINE_PROMOTION_STATUS_PASS = 1;
	public static final Integer LINE_PROMOTION_STATUS_FAIL = 2;
	public static final Integer LINE_PROMOTION_STATUS_EXPIRED = 3;
	public static final Integer LINE_PROMOTION_STATUS_CANCLED = 4;
	
	/******************产品促销状态**************************/
	public static final Integer PROMOTION_ACTIVITY_STATUS_AUDITING = 0;
	public static final Integer PROMOTION_ACTIVITY_STATUS_PASS = 1;
	public static final Integer PROMOTION_ACTIVITY_STATUS_FAIL = 2;
	public static final Integer PROMOTION_ACTIVITY_STATUS_EXPIRED = 3;
	public static final Integer PROMOTION_ACTIVITY_STATUS_CANCLED = 4;
	
	/******************产品促销类型**************************/
	public static final Integer PROMOTION_ACTIVITY_TYPE_TICKET = 0;
	public static final Integer PROMOTION_ACTIVITY_TYPE_HOTEL = 1;
	public static final Integer PROMOTION_ACTIVITY_TYPE_HOTEL_SCENE = 2;
	public static final Integer PROMOTION_ACTIVITY_TYPE_RESERVED_TICKET = 3;
	public static final Integer PROMOTION_ACTIVITY_TYPE_VISA = 4;
	
	public static final Integer PROFIT_SHARE_CONFIRM_STATUS_WAIT_FOR_CONFIRMATION =1;
	public static final Integer PROFIT_SHARE_CONFIRM_STATUS_CONFIRMED =2;
	public static final Integer PROFIT_SHARE_CONFIRM_STATUS_PAID = 3;
	public static final Integer PROFIT_SHARE_CONFIRM_STATUS_RETURNED = 4;
	
	public static final String WE_BUSINESS_DEF_LOGO="/resources/upload/image/201810/youmaiyoumai.jpg";
	public static final String WE_BUSINESS_DEF_STORE_NAME="游买有卖微商城";

	public static final String SQL_MIN_PRICE_SPEC_TOTAL = "select count(distinct p1.id)";
	public static final String SQL_MIN_PRICE_SPEC_PARAMS = "select s1.id sid,s1.name sname,sp1.id spid,sp1.specification spname,p1.platform_price pPrice,sum(sp2.sale_number*sp2.has_sold)+s1.base_sale_number hasSold,min(im1.medium_path) mediumPath";
	public static final String SQL_MIN_PRICE_SPEC = " from hy_specialty_price p1,hy_specialty_specification sp1,hy_specialty s1,hy_specialty_image im1,hy_specialty_specification sp2"
			+ " where p1.is_active=1 and p1.specification_id=sp1.id and sp1.specialty_id=s1.id and sp1.is_active=1 and s1.is_active=1 and s1.sale_state=1 and s1.id=im1.specialty_id and im1.is_logo=1 and sp2.specialty_id=s1.id"
			+ " and sp1.id=(select min(p2.specification_id) from hy_specialty_price p2, hy_specialty_specification sp5 where p2.specialty_id=s1.id and p2.specification_id=sp5.id and sp5.is_active=1 and p2.platform_price=(select min(p3.platform_price) from hy_specialty_price p3,hy_specialty_specification sp4 where p3.specification_id=sp4.id and sp4.is_active=1 and p3.is_active=1 and p3.specialty_id=s1.id))"//对应的平台价格有效且最低的，规格有效的，id最小的规格
			+ " and exists(select *from hy_specialty_specification sp3 where sp3.id in (sp1.id,sp1.pid) and sp3.base_inbound>0)";

	public static final String SQL_MIN_PRICE_SPEC_BY_LABEL = " from hy_specialty_price p1,hy_specialty_specification sp1,hy_specialty s1,hy_specialty_image im1,hy_specialty_specification sp2,hy_specialty_label sl1" + 
			" where p1.is_active=1 and p1.specification_id=sp1.id and sp1.specialty_id=s1.id and sp1.is_active=1 and s1.is_active=1 and s1.sale_state=1 and s1.id=im1.specialty_id and im1.is_logo=1 and sp2.specialty_id=s1.id" + 
			" and sp1.id=(select min(p2.specification_id) from hy_specialty_price p2, hy_specialty_specification sp5 where p2.specialty_id=s1.id and p2.specification_id=sp5.id and sp5.is_active=1 and p2.platform_price=(select min(p3.platform_price) from hy_specialty_price p3,hy_specialty_specification sp4 where p3.specification_id=sp4.id and sp4.is_active=1 and p3.is_active=1 and p3.specialty_id=s1.id))" + 
			" and exists(select *from hy_specialty_specification sp3 where sp3.id in (sp1.id,sp1.pid) and sp3.base_inbound>0)" +
			" and sl1.is_marked=1 and sl1.specialty_id=s1.id";
	
	/******************门店状态********************************/
	public static final Integer STORE_DAI_SHEN_HE = 0;//待审核
	public static final Integer STORE_SHEN_HE_WEI_TONG_GUO = 1;//审核未通过
	public static final Integer STORE_SHEN_HE_TONG_GUO_DONG_JIE_ZHONG = 2;//审核通过冻结中
	public static final Integer STORE_TUI_CHU_DAI_SHEN_HE = 3;//退出待审核
	public static final Integer STORE_TUI_CHU = 4;//退出
	public static final Integer STORE_JI_HUO = 5;//激活
	public static final Integer STORE_QIANG_ZHI_JI_HUO = 6;//强制激活

	/*********************待付款批量转账常量*****************************/
	/** 付款帐号*/
	public static final String PAY_ACCOUNT = "50660188000004151";
	/** 客户号*/
	public static final String CLIENT_NUM = "2006468068";
	/** 付款帐号名称*/
	public static final String PAY_ACCOUNT_NAME = "河北虹宇国际旅行社有限公司";
	/** 币种 人民币 01*/
	public static final String CURRENCY_RMB = "01";
	/** 虹宇默认银行关键字 "光大银行"*/
	public static final String DEFAULT_BANK_KEY_WORD = "光大银行";
	/** 转账类型-行内 0*/
	public static final String INNER_BANK_TRANSFER = "0";
	/** 转账类型-行外 1*/
	public static final String INTER_BANK_TRANSFER = "1";
	/** 支付类型  0:跨行汇款*/
	public static final String PAY_TYPE_INTER_BANK = "0";
	/** 支付类型  1:同城票交*/
	public static final String PAY_TYPE_LOCAL_CLEARINGS = "1";
	/** 是否加急 0:否*/
	public static final String URGENT = "0";
	/** 付款用途*/
	public static final String PAY_USAGE = "还款";
	/** 导出的txt的前缀*/
	public static final String PRE_FIX = "transfer-";
	/******************REDIS相关常量参数********************************/
    // table名
	public static final String TABLE_HY_ADMIN = "hyAdmin";
	public static final String TABLE_HY_ROLE = "hyRole";
	public static final String TABLE_HY_ROLE_AUTHORITY = "hyRoleAuthority";
	public static final String TABLE_HY_DEPARTMENT_AUTHORITY = "hyDepartmentAuthority";
    // opsForHash中的HV
	public static final String ROLE_NAME = "-1000";
	public static final String CHECKED_OPERATION = "checkedOperation";
	public static final String CHECKED_RANGE = "checkedRange";
	public static final String DEPARTMENT = "department";
    public static final String HYADMINS = "admins";

}
