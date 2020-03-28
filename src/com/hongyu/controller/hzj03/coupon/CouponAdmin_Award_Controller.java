package com.hongyu.controller.hzj03.coupon;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import com.hongyu.entity.CouponAward;
import com.hongyu.entity.CouponAwardAccount;
import com.hongyu.entity.CouponBalanceUse;
import com.hongyu.entity.CouponMoney;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CouponAwardAccountService;
import com.hongyu.service.CouponAwardService;
import com.hongyu.service.CouponBalanceUseService;
import com.hongyu.service.CouponMoneyService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.Constants;
import com.hongyu.util.CouponSNGenerator;
import com.hongyu.util.SendMessageEMY;

/** 电子券 - 后台管理 - 销售奖励*/
@Controller
@RequestMapping("/admin/business/coupon")
public class CouponAdmin_Award_Controller {

	/** 电子券类型: 销售奖励*/
	private static final String coupon_money_award = "销售奖励";
	
	@Resource(name = "couponBalanceUseServiceImpl")
	CouponBalanceUseService couponBalanceUseService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "couponAwardServiceImpl")
	CouponAwardService couponAwardService;

	@Resource(name = "couponAwardAccountServiceImpl")
	CouponAwardAccountService couponAwardAccountService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@Resource(name = "couponMoneyServiceImpl")
	CouponMoneyService couponMoneyService;


	/**电子券-奖励-下拉选项*/
	@RequestMapping(value = "/award/options")
	@ResponseBody
	public Json couponAwardOptions() {
		Json j = new Json();

		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("issueType", Operator.like, coupon_money_award));
			List<CouponMoney> list = couponMoneyService.findList(null, filters, null);

			List<HashMap<String, Object>> obj = new ArrayList<>();
			for (CouponMoney c : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("item", c.getMoney()); //销售奖励只有金额没有折扣
//				map.put("couponMoneyId", c.getId());

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
	
	/** 电子券-奖励-分发 */
	@RequestMapping(value = "/award/distribute", method = RequestMethod.POST)
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json couponAwardDistribute(Integer validityPeriod, String receiverPhone, String receiver, Integer amount,
			Integer sum, HttpSession session) { // amount:发放数量

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowaday = sdf.format(new Date());
		Json json = new Json();

		try {
			// 判断hy_wechat_account表中是否有该手机号
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("phone", Operator.eq, receiverPhone));
			List<WechatAccount> list = wechatAccountService.findList(null, filters, null);
			if (list == null || list.size() == 0) { // 若没有该手机号
				// 1.在hy_coupon_award_account奖励电子券统计表 增加数据 设置为未绑定
				CouponAwardAccount caa = new CouponAwardAccount();
				caa.setReceiver(receiver);
				caa.setReceiverPhone(receiverPhone);
				caa.setIssueTime(new Date());
				caa.setSum((float) sum);
				caa.setNum(amount);
				caa.setBindNum(0); // 绑定数量为0
				caa.setTotal((float) (sum * amount));
				caa.setSaler(admin.getName());
				couponAwardAccountService.save(caa);

				for (int i = 0; i < amount; i++) {
					// 2.在hy_oupon_award 奖励电子券表中 增加数据
					CouponAward c = new CouponAward();
					String code = Constants.COUPON_AWARD_PREFIX + nowaday + String.format("%04d", sum)
							+ CouponSNGenerator.getAwardSN();
					c.setCouponCode(code);
					c.setIssueTime(new Date());
					c.setValidityPeriod(validityPeriod);
					c.setSum((float) sum);
					c.setState(0); // 0:未绑定 1:已绑定
					if (receiver != null)
						c.setReceiver(receiver);
					c.setReceiverPhone(receiverPhone);
					// c.setActivationCode(getRandomStr()); // 不需要激活码
					// if (c.getValidityPeriod() > 0) { //已经绑定，不需要设置过期时间
					// long expire = new Date().getTime() +
					// c.getValidityPeriod() *
					// 24 * 3600 * 1000;
					// Date d = new Date();
					// d.setTime(expire);
					// c.setExpireTime(d);
					// }

					c.setCouponAwardAccountId(caa.getId());
					couponAwardService.save(c);
				}
				
				//发短信进行提示
//				String str = "【虹宇国际旅行社】您已被奖励" + amount + "张" + sum +"元的电子券！您可以在 游买有卖商城-个人中心 绑定手机号" + receiverPhone + "后进行查看!";
//				SendMessageEMY.sendMessage(receiverPhone, str);
				//write by wj
				String message = amount + "张" + sum +"元";
				String phone = receiverPhone.toString();
				String str = "{\"sum\":\""+message+"\",\"phone\":\""+phone+"\"}";
				SendMessageEMY.businessSendMessage(receiverPhone,str,2);
				
			} else { // 若存在该手机号
				WechatAccount wechatAccount = list.get(0);

				// 1.修改微信账户的余额并更新
				if(wechatAccount.getTotalbalance() == null){
					wechatAccount.setTotalbalance(new BigDecimal(amount * sum));
				}
				else{
					wechatAccount.setTotalbalance(wechatAccount.getTotalbalance().add(new BigDecimal(amount * sum)));
				}
				
				wechatAccountService.update(wechatAccount);

				// 2.在hy_coupon_award_account奖励统计表 增加数据
				CouponAwardAccount caa = new CouponAwardAccount();
				caa.setReceiver(receiver);
				caa.setReceiverPhone(receiverPhone);
				caa.setIssueTime(new Date());
				caa.setSum((float) sum);
				caa.setNum(amount);
				caa.setBindNum(amount); // 分发时 奖励电子券全部绑定
				caa.setTotal((float) (sum * amount));
				caa.setSaler(admin.getName());
				couponAwardAccountService.save(caa);

				for (int i = 0; i < amount; i++) {
					// 3.在hy_oupon_award 奖励电子券表中 增加数据
					CouponAward c = new CouponAward();

					String code = Constants.COUPON_AWARD_PREFIX + nowaday + String.format("%04d", sum)
							+ CouponSNGenerator.getAwardSN();
					c.setCouponCode(code);

					c.setIssueTime(new Date());
					c.setValidityPeriod(validityPeriod);
					c.setSum((float) sum);
					c.setState(1); // 0:未绑定 1:已绑定
					if (receiver != null)
						c.setReceiver(receiver);
					c.setReceiverPhone(receiverPhone);
					// c.setActivationCode(getRandomStr()); // 不需要激活码
					c.setBindPhone(receiverPhone);
					c.setBindPhoneTime(new Date());
					c.setBindWechatAccountId(wechatAccount.getId());
					c.setBindWechatTime(new Date());
					// if (c.getValidityPeriod() > 0) { //已经绑定，不需要设置过期时间
					// long expire = new Date().getTime() +
					// c.getValidityPeriod() *
					// 24 * 3600 * 1000;
					// Date d = new Date();
					// d.setTime(expire);
					// c.setExpireTime(d);
					// }
					c.setCouponAwardAccountId(caa.getId());
					couponAwardService.save(c);

					// 4.在余额电子券使用记录表中增加数据
					CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
					couponBalanceUse.setCouponId(c.getId());
					couponBalanceUse.setWechatId(wechatAccount.getId());
					couponBalanceUse.setUseAmount(c.getSum());
					couponBalanceUse.setUseTime(new Date());
					couponBalanceUse.setPhone(receiverPhone);
					couponBalanceUse.setCouponCode(code);
					couponBalanceUse.setType(2);
					couponBalanceUseService.save(couponBalanceUse);
				}
			}

			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}

	/** 电子券-奖励-查询 */
	@RequestMapping(value = "/award/query", method = RequestMethod.POST)
	@ResponseBody
	public Json couponAwardQuery(Pageable pageable, CouponAward couponAward) {
		Json j = new Json();

		if (couponAward == null) {
			couponAward = new CouponAward();
		}
		
		List<Order> orders = new ArrayList<>();
		orders.add(new Order("id",Direction.desc));//倒序排序
		pageable.setOrders(orders);
		

		try {
			Page<CouponAward> page = couponAwardService.findPage(pageable, couponAward);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponAward c : page.getRows()) {
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

	/** 电子券-奖励-统计 */
	@RequestMapping(value = "/award/statis")
	@ResponseBody
	public Json couponAwardStatis(String startTime, String endTime, String receiver) {
		Json j = new Json();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			StringBuilder jpql = new StringBuilder(
					"SELECT sum,receiver,SUM(num),SUM(total), SUM(bind_num) FROM hy_coupon_award_account WHERE 1=1 ");
			if (receiver != null && !receiver.equals("")) {
				jpql.append("  AND receiver LIKE " + "'%" + receiver + "%' ");
			}
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum,receiver ORDER BY sum,receiver");

			List<Object[]> list = couponAwardService.statis(jpql.toString());
			if (list == null || list.size() == 0) {
				j.setMsg("未获取到符合条件的结果");
				j.setSuccess(false);
				j.setObj(list);
				return j;
			}

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (Object[] o : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("sum", o[0]);
				map.put("receiver", o[1]);
				map.put("num", o[2]);
				map.put("total", o[3]);
				map.put("binded", o[4]);

				if (o[2] != null && o[4] != null) {
					map.put("notBind", ((BigDecimal) o[2]).subtract((BigDecimal) o[4]).intValue());
				} else {
					map.put("notBind", "");
				}

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<String, Object>();
			obj.put("rows", rows);
			obj.put("receiver", receiver == null ? "" : receiver);
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

	/** 电子券-奖励-明细 */
	@RequestMapping(value = "/award/detail", method = RequestMethod.POST)
	@ResponseBody
	public Json couponAwardDetail(String receiver, String startTime, String endTime, Float sum, Pageable pageable) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			List<Filter> filters = new ArrayList<>();
			if (receiver != null && !receiver.equals("")) {
				filters.add(new Filter("receiver", Operator.like, receiver));
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
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<>();
			orders.add(new Order("id",Direction.desc));//倒序排序
			pageable.setOrders(orders);
			
			Page<CouponAward> page = couponAwardService.findPage(pageable);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponAward c : page.getRows()) {
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


	
	/**电子券-奖励-统计-导出Excel*/
	@RequestMapping(value = "/award/statis/excel")
//	@ResponseBody
	public void couponAwardStatisExcel(String startTime, String endTime, String receiver, HttpServletRequest request,
			HttpServletResponse response) {
		Json j = new Json();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
			
			sb2.append("销售奖励电子券统计");
			String fileName = "销售奖励电子券统计.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "couponAwardStatis.xml"; // 配置文件
			
			
			
			StringBuilder jpql = new StringBuilder(
					"SELECT sum,receiver,SUM(num),SUM(total), SUM(bind_num) FROM hy_coupon_award_account WHERE 1=1 ");
			if (receiver != null && !receiver.equals("")) {
				jpql.append("  AND receiver LIKE " + "'%" + receiver + "%' ");
			}
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum,receiver ORDER BY sum,receiver");

			List<Object[]> list = couponAwardService.statis(jpql.toString());
			if (list == null || list.size() == 0) {
				j.setMsg("未获取到符合条件的结果");
				j.setSuccess(false);
//				return j;
			}

			List<CouponAwardStatisInfo> infos = new ArrayList<>();
			for(Object[] object : list){
				CouponAwardStatisInfo couponAwardStatisInfo = new CouponAwardStatisInfo();
				couponAwardStatisInfo.setSum((Float)object[0]);
				couponAwardStatisInfo.setReceiver((String)object[1]);
				couponAwardStatisInfo.setNum((BigDecimal)object[2]);
				couponAwardStatisInfo.setTotal((Double)object[3]);
				couponAwardStatisInfo.setNotBind(((BigDecimal) object[2]).subtract((BigDecimal) object[4]));
				couponAwardStatisInfo.setBinded((BigDecimal) object[4]);
				
				infos.add(couponAwardStatisInfo);
			}
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);
			
			
			j.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();

		}

//		return j;
	}
	
	/**电子券-奖励-统计-内部类*/
	public class CouponAwardStatisInfo{
		
		private Float sum; //o[0]
		private String receiver; //o[1]
		private BigDecimal num;  //o[2]
		private Double total;  //o[3]
		private BigDecimal notBind;  // (BigDecimal) o[2]).subtract((BigDecimal) o[4])
		private BigDecimal binded;  //o[4]
		public Float getSum() {
			return sum;
		}
		public void setSum(Float sum) {
			this.sum = sum;
		}
		public String getReceiver() {
			return receiver;
		}
		public void setReceiver(String receiver) {
			this.receiver = receiver;
		}
		public BigDecimal getNum() {
			return num;
		}
		public void setNum(BigDecimal num) {
			this.num = num;
		}
		public Double getTotal() {
			return total;
		}
		public void setTotal(Double total) {
			this.total = total;
		}
		public BigDecimal getNotBind() {
			return notBind;
		}
		public void setNotBind(BigDecimal notBind) {
			this.notBind = notBind;
		}
		public BigDecimal getBinded() {
			return binded;
		}
		public void setBinded(BigDecimal binded) {
			this.binded = binded;
		}
	}
	
	
	
}
