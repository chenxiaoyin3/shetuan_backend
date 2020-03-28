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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CouponSale;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CouponSaleService;
import com.hongyu.service.WechatAccountService;

/** 电子券 - 后台管理 - 商城销售 */
@Controller
@RequestMapping("/admin/business/coupon")
public class CouponAdmin_Sale_Controller {
	@Resource(name = "couponSaleServiceImpl")
	CouponSaleService couponSaleService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	/** 电子券-商城销售-查询 */
	@RequestMapping(value = "/sale/query", method = RequestMethod.POST)
	@ResponseBody
	public Json couponSaleQuery(Pageable pageable, CouponSale couponSale) {
		Json j = new Json();

		if (couponSale == null) {
			couponSale = new CouponSale();
		}

		List<Order> orders = new ArrayList<>();
		orders.add(new Order("id", Direction.desc));// 倒序排序
		pageable.setOrders(orders);

		try {
			Page<CouponSale> page = couponSaleService.findPage(pageable, couponSale);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponSale c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("activationCode", c.getActivationCode());
				// map.put("receiver", c.getReceiver());
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

	/** 电子券-商城销售-统计 */
	@RequestMapping(value = "/sale/statis", method = RequestMethod.POST)
	@ResponseBody
	public Json couponSaleStatis(String startTime, String endTime) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			StringBuilder jpql = new StringBuilder(
					"SELECT sum,SUM(num), discount,SUM(total), SUM(bind_num) FROM hy_coupon_sale_account WHERE 1=1 ");
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum,discount ORDER BY sum,discount");

			List<Object[]> list = couponSaleService.statis(jpql.toString());
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

	/** 电子券-商城销售-明细 */
	@RequestMapping(value = "/sale/detail", method = RequestMethod.POST)
	@ResponseBody
	public Json couponSaleDetail(String startTime, String endTime, Float sum, Float ratio, Pageable pageable) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			List<Filter> filters = new ArrayList<>();
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

			Page<CouponSale> page = couponSaleService.findPage(pageable);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponSale c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("activationCode", c.getActivationCode());
				// map.put("receiver", c.getReceiver());
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

	/** 电子券-商城销售-统计-导出Excel */
	@RequestMapping(value = "/sale/statis/excel")
//	@ResponseBody
	public void couponSaleStatisExcel(String startTime, String endTime, HttpServletRequest request,
			HttpServletResponse response) {
//		Json j = new Json();
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

			sb2.append("商城销售电子券统计");
			String fileName = "商城销售电子券统计.xls"; // Excel文件名
			String tableTitle = sb2.toString(); // Excel表标题
			String configFile = "couponSaleStatis.xml"; // 配置文件

			StringBuilder jpql = new StringBuilder(
					"SELECT sum,SUM(num), discount,SUM(total), SUM(bind_num) FROM hy_coupon_sale_account WHERE 1=1 ");
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum,discount ORDER BY sum,discount");

			List<Object[]> list = couponSaleService.statis(jpql.toString());
//			if (list == null || list.size() == 0) {
//				j.setMsg("未获取到符合条件的结果");
//				j.setSuccess(false);
//				j.setObj(list);
//				return j;
//			}

			List<CouponSaleStaticInfo> infos = new ArrayList<>();
			for (Object[] object : list) {
				CouponSaleStaticInfo couponSaleStaticInfo = new CouponSaleStaticInfo();
				couponSaleStaticInfo.setSum((Float) object[0]);
				couponSaleStaticInfo.setNum((BigDecimal) object[1]);
				couponSaleStaticInfo.setTotal(((BigDecimal) object[1]).multiply(new BigDecimal((Float) object[0])));
				couponSaleStaticInfo.setRatio((Float) object[2]);
				couponSaleStaticInfo.setIncome((Double) object[3]);
				couponSaleStaticInfo.setBinded((BigDecimal) object[4]);
				couponSaleStaticInfo.setNotBind(((BigDecimal) object[1]).subtract((BigDecimal) object[4]));
				infos.add(couponSaleStaticInfo);
			}

			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
//		return null;
	}

	/** 电子券-商城销售-统计-内部类 */
	public class CouponSaleStaticInfo {

		private Float sum; // o[0]
		private BigDecimal num; // o[1]
		private BigDecimal total; // (BigDecimal) o[1]).multiply(new
									// BigDecimal((Float) o[0]))
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
