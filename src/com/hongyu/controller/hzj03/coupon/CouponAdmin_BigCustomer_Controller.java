package com.hongyu.controller.hzj03.coupon;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CouponBigCustomer;
import com.hongyu.entity.CouponBigCustomerAccount;
import com.hongyu.entity.CouponMoney;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CouponBigCustomerAccountService;
import com.hongyu.service.CouponBigCustomerService;
import com.hongyu.service.CouponMoneyService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.Constants;
import com.hongyu.util.CouponSNGenerator;
import com.hongyu.util.RandomStr;
import com.hongyu.util.SendMessageEMY;

/** 电子券 - 后台管理 - 大客户购买电子券 */
@Controller
@RequestMapping("/admin/business/coupon")
public class CouponAdmin_BigCustomer_Controller {

	/** 电子券 类型: 大客户购买 */
	private static final String conpon_money_bigcustomer = "大客户购买";

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	@Resource(name = "couponBigCustomerServiceImpl")
	CouponBigCustomerService couponBigCustomerService;

	@Resource(name = "couponBigCustomerAccountServiceImpl")
	CouponBigCustomerAccountService couponBigCustomerAccountService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@Resource(name = "couponMoneyServiceImpl")
	CouponMoneyService couponMoneyService;

	/** 电子券-大客户购买-分发 */
	@RequestMapping(value = "/bigcustomer/distribute", method = RequestMethod.POST)
	@ResponseBody
	public Json couponBigCustomerDistribute(Integer validityPeriod, String receiverPhone, String receiver,
			Integer amount, String payType, Long couponMoneyId, HttpSession session) { // amount:发放数量

		CouponMoney couponMoney = couponMoneyService.find(couponMoneyId);

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowaday = sdf.format(new Date());
		Json json = new Json();

		// 生成激活码
		List<String> randomStrList = new ArrayList<>(amount);
		for (int i = 0; i < amount; i++) {
			String randomStr = RandomStr.getRandomStr();
			randomStrList.add(randomStr);
		}

		// 生成电子券编号
		List<String> codeList = new ArrayList<>(amount);
		for (int i = 0; i < amount; i++) {
			String code = Constants.COUPON_BIGCUSTOMER_PREFIX + nowaday + String.format("%04d", couponMoney.getMoney())
					+ CouponSNGenerator.getBigCustomerSN(); // SN至少为6位,不足补零
			codeList.add(code);
		}

		try {
			// 1.在hy_coupon_bigcustomer_account大客户购买对账表 增加数据
			CouponBigCustomerAccount cbca = new CouponBigCustomerAccount();
			cbca.setReceiverPhone(receiverPhone);
			cbca.setIssueTime(new Date());
			cbca.setSum(couponMoney.getMoney() + 0f);
			cbca.setNum(amount);
			cbca.setDiscount(couponMoney.getRebateRatio());
			cbca.setTotal(couponMoney.getMoney() * amount * couponMoney.getRebateRatio());
			cbca.setPayType(payType);
			cbca.setSaler(admin.getName());
			cbca.setBindNum(0); // 大客户购买 分发时默认绑定数量为0
			cbca.setConfirmState(0); // 大客户购买 未确认前初始化为0
			couponBigCustomerAccountService.save(cbca);

			// 大于4条使用网址的方式
			// 生成suffixurl
			String illegalCode = RandomStr.getRandomCharacterAndNumber(4);
			StringBuilder sb = new StringBuilder("/");
			sb.append(nowaday);
			sb.append("/");
			sb.append(receiverPhone);
			sb.append("/");
			sb.append(illegalCode);

			// 2.在hy_coupon_bigcustomer 大客户购买电子券表中 增加数据
			for (int i = 0; i < amount; i++) {
				CouponBigCustomer c = new CouponBigCustomer();

				c.setSum(Float.parseFloat(couponMoney.getMoney() + ""));
				c.setRatio(couponMoney.getRebateRatio());
				c.setValidityPeriod(validityPeriod);
				c.setReceiverPhone(receiverPhone);

				c.setSaler(admin.getName());
				c.setState(0); // 0:未绑定 1:已绑定
				c.setIssueTime(new Date());

				c.setActivationCode(randomStrList.get(i)); // 12位随机激活码

				if (receiver != null)
					c.setReceiver(receiver);

				c.setCouponCode(codeList.get(i));

				if (c.getValidityPeriod() > 0) {
					
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DATE, c.getValidityPeriod());
					c.setExpireTime(calendar.getTime());
				}

				if (amount >= 4) {
					c.setSuffixurl(sb.toString()); // 短信url后缀存入数据库
				}

				c.setCouponBigCustomerAccountId(cbca.getId());
				couponBigCustomerService.save(c);
			}

			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}

	/** 电子券-大客户购买-查询 */
	@RequestMapping(value = "/bigcustomer/query", method = RequestMethod.POST)
	@ResponseBody
	public Json couponBigCustomerQuery(Pageable pageable, CouponBigCustomer couponBigCustomer) {
		Json j = new Json();

		if (couponBigCustomer == null) {
			couponBigCustomer = new CouponBigCustomer();
		}

		List<Order> orders = new ArrayList<>();
		orders.add(new Order("id", Direction.desc)); // 倒序排序
		pageable.setOrders(orders);

		try {
			Page<CouponBigCustomer> page = couponBigCustomerService.findPage(pageable, couponBigCustomer);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponBigCustomer c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("activationCode", c.getActivationCode());
				map.put("receiver", c.getReceiver());
				map.put("receiverPhone", c.getReceiverPhone());
				WechatAccount w = wechatAccountService.find(c.getBindWechatAccountId());
				map.put("bindWechatAccountId", (w != null ? w.getWechatName() : ""));
				map.put("bindWechatTime", c.getBindWechatTime());
				map.put("issueTime", c.getIssueTime());
				map.put("saler", c.getSaler());
				map.put("expireTime", c.getExpireTime());

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("pageSize", pageable.getRows());
			obj.put("pageNumber", pageable.getPage());
			obj.put("total", page.getTotal());
			obj.put("rows", rows);

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

	/** 电子券-大客户购买-统计 */
	@RequestMapping(value = "/bigcustomer/statis", method = RequestMethod.POST)
	@ResponseBody
	public Json couponBigCustomerStatis(String saler, String startTime, String endTime) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			StringBuilder jpql = new StringBuilder(
					"SELECT sum,SUM(num), discount,SUM(total), SUM(bind_num) FROM hy_coupon_bigcustomer_account WHERE 1=1 ");
			if (saler != null && !saler.equals("")) {
				jpql.append("  AND saler LIKE " + "'%" + saler + "%' ");
			}
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum,discount ORDER BY sum,discount");

			List<Object[]> list = couponBigCustomerService.statis(jpql.toString());
			if (CollectionUtils.isEmpty(list)) {
				j.setMsg("未获取到符合条件的结果");
				j.setSuccess(false);
				return j;
			}

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (Object[] o : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("sum", o[0]);
				map.put("num", o[1]);
				if (o[1] != null && o[0] != null) {
					map.put("total", ((BigDecimal) o[1]).multiply(new BigDecimal((Float) o[0])));
				} else {
					map.put("total", "");
				}
				map.put("ratio", o[2]);
				map.put("income", o[3]);
				map.put("binded", o[4]);
				if (o[1] != null && o[4] != null) {
					map.put("notBind", ((BigDecimal) o[1]).subtract((BigDecimal) o[4]).intValue());
				} else {
					map.put("notBind", "");
				}

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<String, Object>();
			obj.put("rows", rows);
			obj.put("saler", saler == null ? "" : saler);
			obj.put("startTime", startTime == null ? "" : startTime);
			obj.put("endTime", endTime == null ? "" : endTime);

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

	/** 电子券-大客户购买-明细 */
	@RequestMapping(value = "/bigcustomer/detail", method = RequestMethod.POST)
	@ResponseBody
	public Json couponBigCustomerDetail(String saler, String startTime, String endTime, Float sum, Float ratio,
			Pageable pageable) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			List<Filter> filters = new ArrayList<>();
			if (saler != null && !saler.equals("")) {
				filters.add(new Filter("saler", Operator.like, saler));
			}
			if (startTime != null && !startTime.equals("")) {
				filters.add(
						new Filter("issueTime", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			}
			if (endTime != null && !endTime.equals("")) {
				filters.add(
						new Filter("issueTime", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			}
			if (sum != null) {
				filters.add(new Filter("sum", Operator.eq, sum));
			}
			if (ratio != null) {
				filters.add(new Filter("ratio", Operator.eq, ratio));
			}
			pageable.setFilters(filters);

			List<Order> orders = new ArrayList<>();
			orders.add(new Order("id", Direction.desc));// 倒序排序
			pageable.setOrders(orders);

			Page<CouponBigCustomer> page = couponBigCustomerService.findPage(pageable);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponBigCustomer c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("activationCode", c.getActivationCode());
				map.put("receiver", c.getReceiver());
				map.put("receiverPhone", c.getReceiverPhone());
				WechatAccount w = wechatAccountService.find(c.getBindWechatAccountId());
				map.put("bindWechatAccountId", (w != null ? w.getWechatName() : ""));
				map.put("bindWechatTime", c.getBindWechatTime());
				map.put("issueTime", c.getIssueTime());
				map.put("saler", c.getSaler());
				map.put("expireTime", c.getExpireTime());

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("pageSize", pageable.getRows());
			obj.put("pageNumber", pageable.getPage());
			obj.put("total", page.getTotal());
			obj.put("rows", rows);

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

	/** 电子券-大客户购买-下拉选项 */
	@RequestMapping(value = "/bigcustomer/options")
	@ResponseBody
	public Json couponBigCustomerOptions() {
		Json j = new Json();

		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("issueType", Operator.like, conpon_money_bigcustomer));
			List<CouponMoney> list = couponMoneyService.findList(null, filters, null);

			List<HashMap<String, Object>> obj = new ArrayList<>();
			for (CouponMoney c : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("item", c.getMoney() + "/" + c.getRebateRatio());
				map.put("couponMoneyId", c.getId());

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

	/** 电子券-大客户购买-对账单-列表 */
	@RequestMapping(value = "/bigcustomer/confirm/list", method = RequestMethod.POST)
	@ResponseBody
	public Json couponBigCustomerConfirmList(Pageable pageable, CouponBigCustomerAccount couponBigCustomerAccount) {
		Json j = new Json();

		List<Order> orders = new ArrayList<>();
		orders.add(new Order("id", Direction.desc)); // 倒序排序
		pageable.setOrders(orders);

		if (couponBigCustomerAccount == null) {
			couponBigCustomerAccount = new CouponBigCustomerAccount();
		}
		try {
			Page<CouponBigCustomerAccount> page = couponBigCustomerAccountService.findPage(pageable,
					couponBigCustomerAccount);

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

	/** 电子券-大客户购买-对账单-对账确认 */
	@RequestMapping(value = "/bigcustomer/confirm/verify", method = RequestMethod.POST)
	@ResponseBody
	public Json couponBigCustomerConfirVerify(CouponBigCustomerAccount couponBigCustomerAccount, HttpSession session) {
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		Json j = new Json();
		try {
			// 在对账确认后发送短信
			CouponBigCustomerAccount c = couponBigCustomerAccountService.find(couponBigCustomerAccount.getId());
			final int amount = c.getNum();

			// 根据CouponBigCustomerAccount的id获取对应的CouponBigCustomer
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("couponBigCustomerAccountId", couponBigCustomerAccount.getId()));
			final List<CouponBigCustomer> list = couponBigCustomerService.findList(null, filters, null);

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
//				StringBuilder sb = new StringBuilder(list.get(0).getSuffixurl());
//				sb.insert(0, Constants.URL_FOR_COUPONSMS);
////				String str = "【虹宇国际旅行社】您已成功购买了" + amount + "张" + list.get(0).getSum() + "元的电子券！您可以在 " + sb.toString()
////						+ " 中进行查看!";
//				
//				//write by wj
//				String sum = amount + "张" + list.get(0).getSum() + "元";
//				String code = sb.toString();
//				String message = "{\"sum\":\""+sum+"\",\"code\":\""+code+"\"}";
//				
//				SendMessageEMY.sendMessage(c.getReceiverPhone(), message,4);
//			}

			// 更新hy_coupon_bigcustomer_account表
			couponBigCustomerAccount.setConfirmer(admin.getName());
			couponBigCustomerAccount.setConfirmTime(new Date());
			couponBigCustomerAccount.setConfirmState(1); // 0 未确认 1 已确认
			couponBigCustomerAccountService.update(couponBigCustomerAccount, "receiverPhone", "issueTime", "sum", "num",
					"bindNum", "discount", "total", "payType", "saler");

			j.setMsg("确认成功");
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg("确认失败");
			j.setSuccess(false);
		}
		return j;
	}

	/** 电子券-大客户购买-统计-导出Excel */
	@RequestMapping("/bigcustomer/statis/excel")
//	@ResponseBody
	public void couponBigCustomerStatisExcel(String saler, String startTime, String endTime, HttpServletRequest request,
			HttpServletResponse response) {

//		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {

			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();

			if (startTime != null) {
				sb2.append(startTime.substring(0, 10));
			}
			sb2.append("——");
			if (endTime != null) {
				sb2.append(endTime.substring(0, 10));
			}

			sb2.append("大客户购买电子券统计");
			String fileName = "大客户购买电子券统计.xls"; // Excel文件名
			String tableTitle = sb2.toString(); // Excel表标题
			String configFile = "couponBigCustomerStatis.xml"; // 配置文件

			StringBuilder jpql = new StringBuilder(
					"SELECT sum,SUM(num), discount,SUM(total), SUM(bind_num) FROM hy_coupon_bigcustomer_account WHERE 1=1 ");
			if (saler != null && !saler.equals("")) {
				jpql.append("  AND saler LIKE " + "'%" + saler + "%' ");
			}
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum,discount ORDER BY sum,discount");

			List<Object[]> list = couponBigCustomerService.statis(jpql.toString());

			List<CouponBigCustomerStatisInfo> infos = new ArrayList<>();
			for (Object[] object : list) {
				CouponBigCustomerStatisInfo couponBigCustomerStatisInfo = new CouponBigCustomerStatisInfo();
				couponBigCustomerStatisInfo.setSum((Float) object[0]);
				couponBigCustomerStatisInfo.setNum((BigDecimal) object[1]);
				couponBigCustomerStatisInfo
						.setTotal(((BigDecimal) object[1]).multiply(new BigDecimal((Float) object[0])));
				couponBigCustomerStatisInfo.setRatio((Float) object[2]);
				couponBigCustomerStatisInfo.setIncome((Double) object[3]);
				couponBigCustomerStatisInfo.setBinded((BigDecimal) object[4]);
				couponBigCustomerStatisInfo.setNotBind(((BigDecimal) object[1]).subtract((BigDecimal) object[4]));
				infos.add(couponBigCustomerStatisInfo);
			}

			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
//		return null;
	}

	/** 电子券-大客户购买-统计-内部类 */
	public class CouponBigCustomerStatisInfo {
		private Float sum; // o[0]
		private BigDecimal num; // o[1]
		private BigDecimal total; /// (BigDecimal) o[1]).multiply(new
									/// BigDecimal((Float) o[0]))
		private Float ratio; // o[2]
		private Double income; // o[3]
		private BigDecimal binded; // o[4]
		private BigDecimal notBind; // (BigDecimal) o[1]).subtract((BigDecimal)
									// o[4]).intValue()

		public Float getSum() {
			return sum;
		}

		public void setSum(Float sum) {
			this.sum = sum;
		}

		public BigDecimal getNum() {
			return num;
		}

		public void setNum(BigDecimal num) {
			this.num = num;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public void setTotal(BigDecimal total) {
			this.total = total;
		}

		public Float getRatio() {
			return ratio;
		}

		public void setRatio(Float ratio) {
			this.ratio = ratio;
		}

		public Double getIncome() {
			return income;
		}

		public void setIncome(Double income) {
			this.income = income;
		}

		public BigDecimal getBinded() {
			return binded;
		}

		public void setBinded(BigDecimal binded) {
			this.binded = binded;
		}

		public BigDecimal getNotBind() {
			return notBind;
		}

		public void setNotBind(BigDecimal notBind) {
			this.notBind = notBind;
		}

	}

}
