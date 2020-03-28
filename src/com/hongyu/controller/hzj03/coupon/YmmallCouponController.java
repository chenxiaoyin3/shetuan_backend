package com.hongyu.controller.hzj03.coupon;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.CouponBalanceUse;
import com.hongyu.entity.CouponBigCustomer;
import com.hongyu.entity.CouponBigCustomerAccount;
import com.hongyu.entity.CouponGift;
import com.hongyu.entity.CouponLine;
import com.hongyu.entity.CouponMoney;
import com.hongyu.entity.CouponSale;
import com.hongyu.entity.CouponSaleAccount;
import com.hongyu.entity.CouponSaleOrder;
import com.hongyu.entity.SmsVerify;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.CouponAwardAccountService;
import com.hongyu.service.CouponAwardService;
import com.hongyu.service.CouponBalanceUseService;
import com.hongyu.service.CouponBigCustomerAccountService;
import com.hongyu.service.CouponBigCustomerService;
import com.hongyu.service.CouponGiftService;
import com.hongyu.service.CouponLineService;
import com.hongyu.service.CouponMoneyService;
import com.hongyu.service.CouponSaleAccountService;
import com.hongyu.service.CouponSaleOrderService;
import com.hongyu.service.CouponSaleService;
import com.hongyu.service.SmsVerifyService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.Constants;
import com.hongyu.util.CouponSNGenerator;
import com.hongyu.util.RandomStr;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.wechatpay.WechatPayMainOffcialAccount;
import com.hongyu.wechatpay.bean.PayBean;
import com.hongyu.wechatpay.bean.ReqOfficialBean;
import com.hongyu.wechatpay.util.XMLUtil;

/** 电子券 -微商城 */
@Controller
@RequestMapping("/ymmall/coupon")
public class YmmallCouponController {
	private class CouponBalanceCustom implements Comparable<CouponBalanceCustom> {
		private Long id;
		private String couponCode;
		private String activationCode;
		private Date issueTime;
		private Date expireTime;
		private Integer validityPeriod;
		private Float sum;

		/** 由CouponLine转为CouponBalanceCustom */
		public CouponBalanceCustom(CouponLine couponLine) {
			this.id = couponLine.getId();
			this.couponCode = couponLine.getCouponCode();
			this.activationCode = couponLine.getActivationCode();
			this.issueTime = couponLine.getIssueTime();
			this.expireTime = couponLine.getExpireTime();
			this.validityPeriod = couponLine.getValidityPeriod();
			this.sum = couponLine.getSum();
		}

		/** 由couponSale转为CouponBalanceCustom */
		public CouponBalanceCustom(CouponSale couponSale) {
			this.id = couponSale.getId();
			this.couponCode = couponSale.getCouponCode();
			this.activationCode = couponSale.getActivationCode();
			this.issueTime = couponSale.getIssueTime();
			this.expireTime = couponSale.getExpireTime();
			this.validityPeriod = couponSale.getValidityPeriod();
			this.sum = couponSale.getSum();
		}

		/** 由couponBigCustomer转为CouponBalanceCustom */
		public CouponBalanceCustom(CouponBigCustomer couponBigCustomer) {
			this.id = couponBigCustomer.getId();
			this.couponCode = couponBigCustomer.getCouponCode();
			this.activationCode = couponBigCustomer.getActivationCode();
			this.issueTime = couponBigCustomer.getIssueTime();
			this.expireTime = couponBigCustomer.getExpireTime();
			this.validityPeriod = couponBigCustomer.getValidityPeriod();
			this.sum = couponBigCustomer.getSum();
		}

		public Date getIssueTime() {
			return issueTime;
		}

		// public void setIssueTime(Date issueTime) {
		// this.issueTime = issueTime;
		// }

		@Override
		public int compareTo(CouponBalanceCustom another) {
			return this.getIssueTime().before(another.getIssueTime()) ? -1 : 1;
		}

	}

	/** 未绑定 0 */
	private static final int status_notbind = 0;
	/** 已绑定 1 */
	private static final int status_binded = 1;
	// /** 冻结 2 */
	// private static final int status_freeze = 2;

	/** 电子券 类型: 商城销售 */
	private static final String conpon_money_sale = "商城销售";

	/** 电子券 类型: 商城赠送 */
	private static final String conpon_money_gift = "商城赠送";

	/** 商城销售电子券 默认有效期 5年 */
	private static final int default_validity_period = 1825;

	@Resource(name = "couponBigCustomerAccountServiceImpl")
	CouponBigCustomerAccountService couponBigCustomerAccountService;

	@Resource(name = "couponAwardAccountServiceImpl")
	CouponAwardAccountService couponAwardAccountService;

	@Resource(name = "couponBigCustomerServiceImpl")
	CouponBigCustomerService couponBigCustomerService;

	@Resource(name = "couponAwardServiceImpl")
	CouponAwardService couponAwardService;

	@Resource(name = "couponLineServiceImpl")
	CouponLineService couponLineService;

	@Resource(name = "couponGiftServiceImpl")
	CouponGiftService couponGiftService;

	@Resource(name = "couponBalanceUseServiceImpl")
	CouponBalanceUseService couponBalanceUseService;

	@Resource(name = "couponSaleServiceImpl")
	CouponSaleService couponSaleService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@Resource(name = "smsVerifyServiceImpl")
	SmsVerifyService smsVerifyService;

	@Resource(name = "couponSaleAccountServiceImpl")
	CouponSaleAccountService couponSaleAccountService;

	@Resource(name = "couponMoneyServiceImpl")
	CouponMoneyService couponMoneyService;

	@Resource(name = "couponSaleOrderServiceImpl")
	CouponSaleOrderService couponSaleOrderService;

	@Resource(name = "specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryServiceImpl;
	
	@Resource(name="commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;

	/** 商城销售- 获取短信验证码 */
	@RequestMapping(value = "/sale/confirmcode")
	@ResponseBody
	public Json getSMSConfirmCode(String receiverPhone) {
		// send sms verify
		Random rand = new Random();
		int confirmCode = rand.nextInt(999999);
		if (confirmCode < 100000) {
			confirmCode += 100000;
		}
//		boolean isSuccess = SendMessageEMY.sendMessage(receiverPhone, "【虹宇国际旅行社】电子券购买验证码:" + confirmCode);
		//write by wj
		String str = "{\"code\":\""+confirmCode+"\"}";
		boolean isSuccess = SendMessageEMY.businessSendMessage(receiverPhone,str,1);
		if (isSuccess) {
			// add sms verify to database
			SmsVerify smsVerify = new SmsVerify();
			smsVerify.setConfirmCode(confirmCode + "");
			smsVerify.setPhone(receiverPhone);
			smsVerify.setCreateTime(new Date());
			// 验证码300s内有效   改为了600s
			long expire = System.currentTimeMillis() + 600 * 1000;
			Date d = new Date();
			d.setTime(expire);
			smsVerify.setExpiredTime(d);
			smsVerifyService.save(smsVerify);
		}

		Json j = new Json();
		j.setSuccess(true);
		return j;
	}

	/** 电子券-商城销售-用户提交订单 */
	@RequestMapping(value = "/sale/purchase", method = RequestMethod.POST)
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json couponSaleOrder(Integer amount, Long wechat_id, Long couponMoneyId, String phone, String confirmCode,
			HttpSession session) { // amount:发放数量

		Json j = new Json();
		try {
			// 结算之前对短信验证码进行校验
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("phone", phone));
			filters.add(Filter.eq("confirmCode", confirmCode));
			filters.add(new Filter("createTime", Operator.le, new Date()));
			filters.add(new Filter("expiredTime", Operator.ge, new Date()));

			List<SmsVerify> list = smsVerifyService.findList(null, filters, null);
			if (list == null || list.size() == 0) {
				j.setMsg("验证码错误或已过期,请重新获取");
				j.setSuccess(false);
				return j;
			}

			// 写商城销售电子券订单表
			CouponSaleOrder couponSaleOrder = new CouponSaleOrder();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			String orderId;
			synchronized (this) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.in("type", SequenceTypeEnum.couponSaleOrderSn));
				List<CommonSequence> ss = commonSequenceService.findList(null, fs, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				// 将第9位置为1 ，和商城订单区分
				orderId = sdf.format(new Date()) +"1" + String.format("%07d", value);
				couponSaleOrder.setOrderCode(orderId);
			}

//			WechatAccount wa = wechatAccountService.find(wechat_id);
			// couponSaleOrder.setOrderPhone(wa.getPhone());
			couponSaleOrder.setOrderWechatId(wechat_id);

			CouponMoney couponMoney = couponMoneyService.find(couponMoneyId);
			Float f = couponMoney.getMoney() * amount * couponMoney.getRebateRatio();

			BigDecimal bd = new BigDecimal(f).setScale(2, RoundingMode.HALF_UP);

			couponSaleOrder.setTotalMoney(bd.floatValue());
			couponSaleOrder.setShouldPayMoney(bd.floatValue());
			couponSaleOrder.setOrderState(0); // 0:待付款
			couponSaleOrder.setOrderTime(new Date());
			couponSaleOrder.setCouponMoneyId(couponMoneyId);
			couponSaleOrder.setOrderAmount(amount);
			couponSaleOrder.setOrderPhone(phone);

			couponSaleOrderService.save(couponSaleOrder);

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("orderId", orderId);
			obj.put("total_fee", bd.floatValue());

			j.setObj(obj);
			j.setSuccess(true);
			j.setMsg("提交订单成功");

			j.setObj(orderId);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("提交订单失败");
		}

		return j;
	}

	/** 电子券-商城销售-确认支付 */
	@RequestMapping(value = { "/mp/{orderId}" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> detailWithWap(@PathVariable String orderId, @RequestParam Map<String, Object> params,
			@RequestBody Map<String, Object> models, HttpServletResponse servletResponse) throws Exception {
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		PayBean payBean = new PayBean();
		payBean.setOrder(orderId);
		if (params.containsKey("total_fee")) {
			payBean.setAmount((String) params.get("total_fee"));
		}
		if (params.containsKey("body")) {
			payBean.setBody((String) params.get("body"));
		}
		if (params.containsKey("notify_url")) {
			payBean.setCallbackUrl((String) params.get("notify_url"));
		}
		if (params.containsKey("openid")) {
			payBean.setOpenId((String) params.get("openid"));
		}

		if (models.containsKey("total_fee")) {
			payBean.setAmount((String) models.get("total_fee"));
		}
		if (models.containsKey("body")) {
			payBean.setBody((String) models.get("body"));
		}
		if (models.containsKey("notify_url")) {
			payBean.setCallbackUrl((String) models.get("notify_url"));
		}
		if (models.containsKey("openid")) {
			payBean.setOpenId((String) models.get("openid"));
		}

		// 回调地址，将来要修改
		// payBean.setCallbackUrl("http://www.tobyli16.com:8080/pay/wechat/notify/"+orderId);

		ReqOfficialBean reqBean = WechatPayMainOffcialAccount.getReqOfficial(payBean, 1);

		result.put("appId", reqBean.appId);
		result.put("timestamp", reqBean.timeStamp);
		result.put("nonceStr", reqBean.nonceStr);
		result.put("package", reqBean.packageValue);
		result.put("signType", "MD5");
		result.put("paySign", reqBean.paySign);
		response.put("code", "1");
		response.put("result", result);
		return response;
	}

	/** 电子券-商城销售-支付处理 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/couponSale/notify/{orderId}" }, method = RequestMethod.POST)
	public void notify(@PathVariable String orderId, @RequestParam Map<String, Object> params,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 读取参数
		InputStream inputStream;
		StringBuffer sb = new StringBuffer();
		inputStream = request.getInputStream();
		String s;
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		while ((s = in.readLine()) != null) {
			sb.append(s);
		}
		in.close();
		inputStream.close();

		// =======================================================================================


		try {

			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("orderCode", Operator.eq, orderId));
			CouponSaleOrder couponSaleOrder = couponSaleOrderService.findList(null, filters, null).get(0);
			CouponMoney couponMoney = couponMoneyService.find(couponSaleOrder.getCouponMoneyId());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String nowaday = sdf.format(new Date());

			// 1.在hy_coupon_sale_account表 增加数据
			CouponSaleAccount cbca = new CouponSaleAccount();
			cbca.setReceiverPhone(couponSaleOrder.getOrderPhone());
			cbca.setIssueTime(new Date());
			cbca.setSum(couponMoney.getMoney() + 0f);
			cbca.setNum(couponSaleOrder.getOrderAmount());
			cbca.setBindNum(0); // 没有绑定
			cbca.setDiscount(couponMoney.getRebateRatio());
			cbca.setTotal(couponSaleOrder.getTotalMoney());

			if (couponSaleOrder.getOrderAmount() > 3) {// 超过三条，考虑使用短信内嵌url的方式
				// 生成suffixurl
				String illegalCode = RandomStr.getRandomCharacterAndNumber(4);
				StringBuilder sbd = new StringBuilder("/");
				sbd.append(nowaday);
				sbd.append("/");
				sbd.append(couponSaleOrder.getOrderPhone());
				sbd.append("/");
				sbd.append(illegalCode);

				cbca.setSuffixUrl(sbd.toString());
			}
			couponSaleAccountService.save(cbca);


			// 2.在hy_coupon_sale 表中 增加数据
			for (int i = 0; i < couponSaleOrder.getOrderAmount(); i++) {
				CouponSale c = new CouponSale();

				String code = Constants.COUPON_SALE_PREFIX + nowaday + String.format("%04d", couponMoney.getMoney())
						+ CouponSNGenerator.getSaleSN();
				c.setCouponCode(code);
				c.setIssueTime(new Date());
				c.setValidityPeriod(default_validity_period);
				c.setSum(Float.parseFloat(couponMoney.getMoney() + ""));
				c.setRatio(couponMoney.getRebateRatio());
				c.setState(0); // 0:未绑定 1:已绑定
				c.setReceiverPhone(couponSaleOrder.getOrderPhone());
				c.setActivationCode(RandomStr.getRandomStr()); // 12位随机激活码

				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, default_validity_period);
				c.setExpireTime(calendar.getTime());

				c.setCouponSaleAccountId(cbca.getId());
				couponSaleService.save(c);

			}

			// 3.发送短信
			final int amount = couponSaleOrder.getOrderAmount();
			// 根据CouponSaleAccount的id获取对应的CouponSale
			filters.clear();
			filters.add(Filter.eq("couponSaleAccountId", cbca.getId()));
			final List<CouponSale> list = couponSaleService.findList(null, filters, null);

//			if (amount < 4) { // 不超过4条，直接一条短信一个激活码
				new Thread(new Runnable() {
					@Override
					public void run() {
//						String str = "【虹宇国际旅行社】您已成功购买了" + list.get(0).getSum() + "元的电子券!电子券编号:";
						
						String sum = list.get(0).getSum() + "元";
						for (int i = 0; i < amount; i++) {
//							SendMessageEMY.sendMessage(list.get(i).getReceiverPhone(),
//									str + list.get(i).getCouponCode() + "  电子券激活码:" + list.get(i).getActivationCode());
							//write by wj
							String num = list.get(i).getCouponCode();
							String code = list.get(i).getActivationCode();
							String phone = list.get(i).getReceiverPhone().toString();
							String message = "{\"sum\":\""+sum+"\",\"num\":\""+num+"\",\"code\":\""+code+"\"}";
							SendMessageEMY.businessSendMessage(phone,message,3);
							try {
								Thread.sleep(15 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
//			} else { // 大于等于4条
//				StringBuilder stringBuilder = new StringBuilder(cbca.getSuffixUrl()); // 获取短信后缀
//				stringBuilder.insert(0, Constants.URL_FOR_COUPONSMS);
////				String str = "【虹宇国际旅行社】您已成功购买了" + amount + "张" + list.get(0).getSum() + "元的电子券！您可以在 " + sb.toString()
////						+ " 中进行查看!";
//				
//				//write by wj
//				String sum = amount + "张" + list.get(0).getSum() + "元";
//				String code = sb.toString();
//				String message = "{\"sum\":\""+sum+"\",\"code\":\""+code+"\"}";			
//				SendMessageEMY.businessSendMessage(couponSaleOrder.getOrderPhone(), message,4);
//			}

			// 4.修改订单状态
			couponSaleOrder.setOrderState(1); // 1:已支付
			couponSaleOrder.setPayTime(new Date());
			couponSaleOrderService.update(couponSaleOrder);

		} catch (Exception e) {
			e.printStackTrace();
		}


		// =======================================================================================
		// 解析xml成map
		Map<String, String> m = new HashMap<String, String>();
		m = XMLUtil.doXMLParse(sb.toString());

		String resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
				+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(resXml.getBytes());
		out.flush();
		out.close();

	}

	/** 电子券-商城销售-下拉选项 */
	@RequestMapping(value = "/balance_used_coupon/sale_list")
	@ResponseBody
	public Json couponSaleOptions() {
		Json j = new Json();

		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("issueType", Operator.like, conpon_money_sale));
			filters.add(new Filter("endTime",Operator.gt, new Date()));
			List<CouponMoney> list = couponMoneyService.findList(null, filters, null);

			List<HashMap<String, Object>> obj = new ArrayList<>();
			for (CouponMoney c : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("price", c.getMoney());
				map.put("discountPrice",
						new BigDecimal(c.getRebateRatio() * c.getMoney()).setScale(2, RoundingMode.HALF_UP)); // 保留一位小数点
				map.put("couponMoneyId", c.getId());
				map.put("rebateRatio", c.getRebateRatio());
				map.put("endTime", c.getEndTime());

				obj.add(map);
			}

			j.setMsg(null);
			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setMsg(null);
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
	}

	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************

	/** 获取用户余额 */
	@RequestMapping(value = "/balance_used_coupon/total")
	@ResponseBody
	public Json getBalanceUsedCouponTotal(Long wechat_id, String phone) {
		Json j = new Json();
		try {
			WechatAccount wechatAccount = wechatAccountService.find(wechat_id);
			j.setObj(wechatAccount.getTotalbalance());
			j.setSuccess(true);
			j.setMsg("");
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("");
		}
		return j;
	}

	/** 余额使用历史 */
	@RequestMapping(value = "/balance_used_coupon/use_history")
	@ResponseBody
	public Json getBalanceUsedCouponUseHistory(Pageable pageable, Long wechat_id, String phone) {
		Json j = new Json();
		try {
			CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
			couponBalanceUse.setWechatId(wechat_id);
			Page<CouponBalanceUse> page = couponBalanceUseService.findPage(pageable, couponBalanceUse);
			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(page);
		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
	}

	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************

	/** 电子券-商城赠送-下拉列表 */
	@RequestMapping("/once_used_coupon/receive_list")
	@ResponseBody
	public Json getOnceUsedCouponReceiveList(long wechat_id) {
		Json j = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("endTime",Operator.ge, new Date()));
			filters.add(Filter.like("issueType", conpon_money_gift));
			List<CouponMoney> res = couponMoneyService.findList(null, filters, null);

			List<Filter> filters2 = new ArrayList<>();
			filters2.add(Filter.eq("bindWechatAccountId", wechat_id)); // 根据微信账户表id判断
			filters2.add(new Filter("expireTime",Operator.ge, new Date()));
			List<CouponGift> couponGiftList = couponGiftService.findList(null, filters2, null);
			HashSet<Long> couponMoneyIdSet = new HashSet<>();// 存放wechat_id的该用户领过的所有的商城赠送券的id
			for (CouponGift couponGift : couponGiftList) {
				couponMoneyIdSet.add(couponGift.getCouponMoneyId());
			}

			// 判断该用户是否已经领取过该电子券并拼装数据
			List<HashMap<String, Object>> obj = new ArrayList<>();
			for (CouponMoney c : res) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("money", c.getMoney());
				map.put("id", c.getId()); // couponMoney表的id
				map.put("canOverlay", c.getCanOverlay());
				map.put("endTime", c.getEndTime());
				map.put("condition", c.getCouponCondition());
				// if (c.getSpecialtyCategoryId() == -1) {
				// map.put("specialtyCategory", "全部品类");
				// } else {
				// SpecialtyCategory specialtyCategory =
				// specialtyCategoryServiceImpl.find(c.getSpecialtyCategoryId());
				// map.put("specialtyCategory", specialtyCategory.getName());
				// }
				boolean bool = couponMoneyIdSet.contains(c.getId()) ? true : false;
				map.put("hasAcquired", bool);

				obj.add(map);
			}

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
	}

	/** 电子券-商城赠送-用户领取 */
	@RequestMapping("/once_used_coupon/create_coupon")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json acquireOnceUsedCoupon(Long wechat_id, Long couponMoneyId) {
		Json j = new Json();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowaday = sdf.format(new Date());
		try {
			WechatAccount wa = wechatAccountService.find(wechat_id);
			CouponMoney couponMoney = couponMoneyService.find(couponMoneyId);
			CouponGift couponGift = new CouponGift();

			String code = Constants.COUPON_GIFT_PREFIX + nowaday + String.format("%04d", couponMoney.getMoney())
					+ CouponSNGenerator.getGiftSN(); // SN至少为6位,不足补零
			couponGift.setCouponCode(code);
			couponGift.setIssueTime(new Date());
			couponGift.setValidityPeriod(default_validity_period);
			couponGift.setSum(couponMoney.getMoney() + 0f);
			couponGift.setState(0); // 默认为 0:未使用
			couponGift.setReceiver(wa.getWechatName());
			couponGift.setReceiverPhone(wa.getPhone());
			couponGift.setActivationCode(RandomStr.getRandomStr()); // 激活码
																	// 商城赠送需要激活码？
			couponGift.setBindWechatAccountId(wechat_id);
			couponGift.setBindWechatTime(new Date());

			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, default_validity_period);

			couponGift.setExpireTime(c.getTime());
			couponGift.setCouponCondition(couponMoney.getCouponCondition());

			couponGift.setCanOverlay(couponMoney.getCanOverlay()); // 是否可叠加使用
			couponGift.setCouponMoneyId(couponMoneyId);

			couponGiftService.save(couponGift);

			j.setMsg("领取成功");
			j.setSuccess(true);

		} catch (Exception e) {
			j.setMsg("领取失败");
			j.setSuccess(false);
		}

		return j;
	}

	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************

	/** 获取用户未充值的充值卡列表 */
	@RequestMapping(value = "/balance_used_coupon/buy_list")
	@ResponseBody
	public Json getBalanceUsedCouponUseBuyList(int page, int rows, Long wechat_id, String phone) {
		Json j = new Json();
		try {

			// 判断该微信id是否绑定了手机号
			WechatAccount wa = wechatAccountService.find(wechat_id);
			String phoneNum = wa.getPhone();
			if (phoneNum == null || phoneNum.equals("")) {
				j.setSuccess(false);
				j.setMsg("请先在\"我的\"中绑定手机号");
				return j;
			}

			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("state", Operator.eq, status_notbind));// 0:未绑定
			filters.add(new Filter("expireTime", Operator.ge, new Date()));// 未过有效期
			filters.add(new Filter("receiverPhone", Operator.eq, phoneNum));
			// 获取用户未充值的充值卡-线路赠送
			List<CouponLine> listLine = couponLineService.findList(null, filters, null);
			// 获取用户未充值的充值卡-商城销售
			List<CouponSale> listSale = couponSaleService.findList(null, filters, null);
			// 获取用户未充值的充值卡-大客户购买
			List<CouponBigCustomer> listBigCustomer = couponBigCustomerService.findList(null, filters, null);

			List<CouponBalanceCustom> list = new ArrayList<>();

			if (listLine != null && listLine.size() > 0) {
				for (CouponLine l : listLine) {
					CouponBalanceCustom c = new CouponBalanceCustom(l);
					list.add(c);
				}
			}

			if (listSale != null && listSale.size() > 0) {
				for (CouponSale l : listSale) {
					CouponBalanceCustom c = new CouponBalanceCustom(l);
					list.add(c);
				}
			}

			if (listBigCustomer != null && listBigCustomer.size() > 0) {
				for (CouponBigCustomer l : listBigCustomer) {
					CouponBalanceCustom c = new CouponBalanceCustom(l);
					list.add(c);
				}
			}

			Collections.sort(list); // 按issueTime升序排序

			// 需要增加分页的控制

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(list);
		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
	}

	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************

	/** 获取用户一次电子券列表 */
	@RequestMapping(value = "/once_used_coupon/list")
	@ResponseBody
	public Json getOnceUsedCouponList(Long wechat_id) {
		Json j = new Json();

		try {
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("expireTime")); // 按过期时间降序排序

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("bindWechatAccountId", wechat_id));
			List<CouponGift> list = couponGiftService.findList(null, filters, orders);

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(list);

		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
		}

		return j;
	}

	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************

	/** 电子券-绑定激活 */
	@RequestMapping(value = "/balance_used_coupon/bind")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json couponBind(Long wechat_id, String activation_code) {
		Json j = new Json();

		WechatAccount wa = wechatAccountService.find(wechat_id);

		if (activation_code == null) {
			j.setMsg("请输入激活码");
			j.setSuccess(false);
			return j;
		}

		try {

			// 直接使用激活码进行绑定激活
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("activationCode", activation_code));
			filters.add(Filter.eq("state", 0)); // 0:未绑定 1:已绑定

			// 查询 线路赠送电子券表
			List<CouponLine> couponLineList = couponLineService.findList(null, filters, null);
			// 查询 商城销售电子券表
			List<CouponSale> couponSaleList = couponSaleService.findList(null, filters, null);
			// 查询 大客户购买电子券表
			List<CouponBigCustomer> couponBigCustomerList = couponBigCustomerService.findList(null, filters, null);

			if ((couponLineList == null || couponLineList.isEmpty())
					&& (couponSaleList == null || couponSaleList.isEmpty())
					&& (couponBigCustomerList == null || couponBigCustomerList.isEmpty())) {
				j.setMsg("激活码错误或电子券已激活");
				j.setSuccess(false);
				return j;
			}

			if (couponLineList != null && !couponLineList.isEmpty()) {

				// 更新线路赠送电子券表的状态
				CouponLine c = couponLineList.get(0);
				// c.setBindPhone(phone);
				c.setBindPhoneTime(new Date());
				c.setBindWechatAccountId(wechat_id);
				c.setBindWechatTime(new Date());
				c.setState(status_binded);
				couponLineService.update(c);

				// 在余额电子券使用记录表中增加数据
				CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
				couponBalanceUse.setWechatId(wechat_id);
				couponBalanceUse.setUseAmount(c.getSum());
				couponBalanceUse.setUseTime(new Date());
				// couponBalanceUse.setPhone(phone);
				couponBalanceUse.setCouponCode(c.getCouponCode());
				couponBalanceUse.setType(1);
				couponBalanceUseService.save(couponBalanceUse);

				// 更新微信账户表的余额
				if (wa.getTotalbalance() == null) {
					wa.setTotalbalance(new BigDecimal(c.getSum()));
				} else {
					wa.setTotalbalance(new BigDecimal(wa.getTotalbalance().floatValue() + c.getSum()));
				}

				wechatAccountService.update(wa);

			} else if (couponSaleList != null && !couponSaleList.isEmpty()) {

				CouponSale c = couponSaleList.get(0);
				// c.setBindPhone(phone);
				c.setBindPhoneTime(new Date());
				c.setBindWechatAccountId(wechat_id);
				c.setBindWechatTime(new Date());
				c.setState(status_binded);
				couponSaleService.update(c);

				// 在余额电子券使用记录表中增加数据
				CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
				couponBalanceUse.setWechatId(wechat_id);
				couponBalanceUse.setUseAmount(c.getSum());
				couponBalanceUse.setUseTime(new Date());
				// couponBalanceUse.setPhone(phone);
				couponBalanceUse.setCouponCode(c.getCouponCode());
				couponBalanceUse.setType(3);
				couponBalanceUseService.save(couponBalanceUse);

				// 更新微信账户表的余额
				// 更新微信账户表的余额
				if (wa.getTotalbalance() == null) {
					wa.setTotalbalance(new BigDecimal(c.getSum()));
				} else {
					wa.setTotalbalance(new BigDecimal(wa.getTotalbalance().floatValue() + c.getSum()));
				}
				wechatAccountService.update(wa);

			} else if (couponBigCustomerList != null && !couponBigCustomerList.isEmpty()) {
				// 更新coupon_bigcustomer表
				CouponBigCustomer c = couponBigCustomerList.get(0);
				// c.setBindPhone(phone);
				c.setBindPhoneTime(new Date());
				c.setBindWechatAccountId(wechat_id);
				c.setBindWechatTime(new Date());
				c.setState(status_binded);
				couponBigCustomerService.update(c);

				// 更新coupon_bigcustomer_account表
				Long couponBigCustomerAccountId = c.getCouponBigCustomerAccountId();
				CouponBigCustomerAccount couponBigCustomerAccount = couponBigCustomerAccountService
						.find(couponBigCustomerAccountId);
				Integer bindNum = couponBigCustomerAccount.getBindNum();
				couponBigCustomerAccount.setBindNum(bindNum + 1);
				couponBigCustomerAccountService.update(couponBigCustomerAccount);

				// 在余额电子券使用记录表中增加数据
				CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
				couponBalanceUse.setWechatId(wechat_id);
				couponBalanceUse.setUseAmount(c.getSum());
				couponBalanceUse.setUseTime(new Date());
				// couponBalanceUse.setPhone(phone);
				couponBalanceUse.setCouponCode(c.getCouponCode());
				couponBalanceUse.setType(4);
				couponBalanceUseService.save(couponBalanceUse);

				// 更新微信账户表的余额
				// 更新微信账户表的余额
				if (wa.getTotalbalance() == null) {
					wa.setTotalbalance(new BigDecimal(c.getSum()));
				} else {
					wa.setTotalbalance(new BigDecimal(wa.getTotalbalance().floatValue() + c.getSum()));
				}
				wechatAccountService.update(wa);

			}

			j.setMsg("操作成功");
			j.setSuccess(true);

		} catch (Exception e) {
			j.setMsg("操作失败");
			j.setSuccess(false);
		}

		return j;
	}

	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************
	/** 用户提交订单时，获取可用的一次型电子券(商城赠送) */
	@RequestMapping("/availableCoupon")
	@ResponseBody
	public Json getAvailableGiftCoupon(Long wechat_id, BigDecimal price, HttpSession session) {
		Json json = new Json();

		try {
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("sum"));

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("state", 0)); // 0 未绑定
			filters.add(Filter.eq("bindWechatAccountId", wechat_id));
			filters.add(new Filter("expireTime", Operator.ge, new Date())); // 未过期
			filters.add(new Filter("couponCondition", Operator.le, price));
			List<CouponGift> list = couponGiftService.findList(null, filters, orders);

			json.setObj(list);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			e.printStackTrace();
		}

		return json;
	}

	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************

	@RequestMapping("/orderlist")
	@ResponseBody
	public Json getOrderList(Long wechat_id) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderWechatId", wechat_id));
			// 1:已支付
			filters.add(Filter.eq("orderState", 1));
			List<CouponSaleOrder> list = couponSaleOrderService.findList(null, filters, null);

			List<HashMap<String, Object>> res = new ArrayList<>();
			for (CouponSaleOrder cs : list) {
				HashMap<String, Object> map = new HashMap<>();

				CouponMoney couponMoney = couponMoneyService.find(cs.getCouponMoneyId());

				map.put("sum", couponMoney.getMoney());
				map.put("ratio", couponMoney.getRebateRatio());
				map.put("orderTime", cs.getOrderTime());
				map.put("total", cs.getTotalMoney());
				map.put("orderPhone", cs.getOrderPhone());
				map.put("orderAmount", cs.getOrderAmount());

				res.add(map);
			}

			json.setObj(res);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("操作失败");
			json.setSuccess(false);
		}

		return json;
	}
}
