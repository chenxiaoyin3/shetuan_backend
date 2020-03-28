package com.hongyu.controller.hzj03.coupon;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.hongyu.entity.CouponLine;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CouponGiftService;
import com.hongyu.service.CouponLineService;
import com.hongyu.service.WechatAccountService;

/** 电子券 - 后台管理 - 线路 */
@Controller
@RequestMapping("/admin/business/coupon")
public class CouponAdmin_Line_Controller {
	/** 未绑定 0 */
	private static final int status_notbind = 0;
	/** 已绑定 1 */
	private static final int status_binded = 1;
	/** 冻结 2 */
	private static final int status_freeze = 2;


	@Resource(name = "couponGiftServiceImpl")
	CouponGiftService couponGiftService;

	@Resource(name = "couponLineServiceImpl")
	CouponLineService couponLineService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	// ******************************************************************************************************

	/** 电子券-线路-查询 */
	@RequestMapping(value = "/line/query", method = RequestMethod.POST)
	@ResponseBody
	public Json couponLineQuery(Pageable pageable, CouponLine couponLine) {
		Json j = new Json();

		if (couponLine == null) {
			couponLine = new CouponLine();
		}

		try {
			
			List<Order> orders = new ArrayList<>();
			orders.add(new Order("id",Direction.desc));//倒序排序
			pageable.setOrders(orders);
			
			Page<CouponLine> page = couponLineService.findPage(pageable, couponLine);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponLine c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("receiver", c.getReceiver());
				map.put("receiverPhone", c.getReceiverPhone());
				map.put("bindPhone", c.getBindPhone());
				WechatAccount w = wechatAccountService.find(c.getBindWechatAccountId());
				map.put("bindWechatAccountId", (w != null ? w.getWechatName() : ""));
				map.put("bindWechatTime", c.getBindWechatTime());
				map.put("issueTime", c.getIssueTime());
				map.put("expireTime", c.getExpireTime());
				map.put("lineName", c.getLineName());
				map.put("startDate", c.getStartDate());
				map.put("issuer", c.getIssuer());

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

	/** 电子券-线路-统计 */
	@RequestMapping(value = "/line/statis", method = RequestMethod.POST)
	@ResponseBody
	public Json couponLineStatis(String startTime, String endTime, String lineName, String startDate, String issuer) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			StringBuilder jpql = new StringBuilder("SELECT sum, COUNT(sum),SUM(CASE WHEN state = " + status_notbind
					+ " THEN 1 ELSE 0 END),SUM(CASE WHEN state = " + status_binded
					+ " THEN 1 ELSE 0 END),SUM(CASE WHEN state = " + status_freeze
					+ " THEN 1 ELSE 0 END) FROM hy_coupon_line WHERE 1=1 ");
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			if (lineName != null && !lineName.equals("")) {
				jpql.append("  AND line_name LIKE " + "'%" + lineName + "%' ");
			}
			if (startDate != null && !startDate.equals("")) {
				jpql.append("  AND start_date > " + "'" + startDate.substring(0, 10) + " " + "00:00:00" + "'");
				jpql.append("  AND start_date < " + "'" + startDate.substring(0, 10) + " " + "23:59:59" + "'");
			}
			if (issuer != null && !issuer.equals("")) {
				jpql.append("  AND issuer LIKE " + "'%" + issuer + "%' ");
			}
			jpql.append(" GROUP BY sum Order BY sum");

			List<Object[]> list = couponGiftService.statis(jpql.toString());
			if (list == null || list.size() == 0) {
				j.setMsg("未获取到符合条件的结果");
				j.setSuccess(false);
				j.setObj(list);
				return j;
			}

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (Object[] o : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("sum", o[0]); // 电子券金额
				map.put("num", o[1]); // 赠送数量

				if (o[0] != null && o[1] != null) {
					map.put("total", (Float) o[0] * ((BigInteger) o[1]).floatValue()); // 发放金额
				} else {
					map.put("total", ""); // 发放金额
				}

				map.put("notBind", o[2]); // 未绑定
				map.put("binded", o[3]); // 已绑定
				map.put("freeze", o[4]); // 冻结

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<String, Object>();
			obj.put("rows", rows);
			obj.put("startTime", startTime == null ? "" : startTime);
			obj.put("endTime", endTime == null ? "" : endTime);
			obj.put("lineName", lineName == null ? "" : lineName);
			obj.put("startDate", startDate == null ? "" : startDate);
			obj.put("issuer", issuer == null ? "" : issuer);

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

	
	
	
	/** 电子券-线路-明细 */
	@RequestMapping(value = "/line/detail", method = RequestMethod.POST)
	@ResponseBody
	public Json couponLineDetail(String startTime, String endTime, String lineName, String startDate, String issuer,
			Float sum, Pageable pageable) {
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
			if (issuer != null && !issuer.equals("")) {
				filters.add(new Filter("issuer", Operator.like, issuer));
			}
			if (lineName != null && !lineName.equals("")) {
				filters.add(new Filter("lineName", Operator.like, lineName));
			}
			if (startDate != null && !startDate.equals("")) {
				filters.add(
						new Filter("startDate", Operator.ge, sdf.parse(startDate.substring(0, 10) + " " + "00:00:00")));
				filters.add(
						new Filter("startDate", Operator.le, sdf.parse(startDate.substring(0, 10) + " " + "23:59:59")));
			}
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<>();
			orders.add(new Order("id",Direction.desc));//倒序排序
			pageable.setOrders(orders);
			
			Page<CouponLine> page = couponLineService.findPage(pageable);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponLine c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("receiver", c.getReceiver());
				map.put("receiverPhone", c.getReceiverPhone());
				map.put("bindPhone", c.getBindPhone());
				WechatAccount w = wechatAccountService.find(c.getBindWechatAccountId());
				map.put("bindWechatAccountId", (w != null ? w.getWechatName() : ""));
				map.put("bindWechatTime", c.getBindWechatTime());
				map.put("issueTime", c.getIssueTime());
				map.put("expireTime", c.getExpireTime());
				map.put("lineName", c.getLineName());
				map.put("startDate", c.getStartDate());
				map.put("issuer", c.getIssuer());

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

	
	/** 电子券-线路-统计-导出Excel */
	@RequestMapping(value = "/line/statis/excel")
//	@ResponseBody
	public void couponLineStatisExcel(String startTime, String endTime, String lineName, String startDate, String issuer, HttpServletRequest request,
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
			
			sb2.append("线路赠送电子券统计");
			String fileName = "线路赠送电子券统计.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "couponLineStatis.xml"; // 配置文件
			
			
			
			
			StringBuilder jpql = new StringBuilder("SELECT sum, COUNT(sum),SUM(CASE WHEN state = " + status_notbind
					+ " THEN 1 ELSE 0 END),SUM(CASE WHEN state = " + status_binded
					+ " THEN 1 ELSE 0 END),SUM(CASE WHEN state = " + status_freeze
					+ " THEN 1 ELSE 0 END) FROM hy_coupon_line WHERE 1=1 ");
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			if (lineName != null && !lineName.equals("")) {
				jpql.append("  AND line_name LIKE " + "'%" + lineName + "%' ");
			}
			if (startDate != null && !startDate.equals("")) {
				jpql.append("  AND start_date > " + "'" + startDate.substring(0, 10) + " " + "00:00:00" + "'");
				jpql.append("  AND start_date < " + "'" + startDate.substring(0, 10) + " " + "23:59:59" + "'");
			}
			if (issuer != null && !issuer.equals("")) {
				jpql.append("  AND issuer LIKE " + "'%" + issuer + "%' ");
			}
			jpql.append(" GROUP BY sum Order BY sum");

			List<Object[]> list = couponGiftService.statis(jpql.toString());
//			if (list == null || list.size() == 0) {
//				j.setMsg("未获取到符合条件的结果");
//				j.setSuccess(false);
//				j.setObj(list);
//				return j;
//			}

			List<CouponLineStatisInfo> infos = new ArrayList<>();
			for(Object[] object : list){
				CouponLineStatisInfo couponLineStatisInfo = new CouponLineStatisInfo();
				couponLineStatisInfo.setSum((Float)object[0]);
				couponLineStatisInfo.setNum((BigInteger)object[1]);
				couponLineStatisInfo.setTotal( (Float)object[0] *  ((BigInteger) object[1]).floatValue());
				couponLineStatisInfo.setNotBind(((BigDecimal) object[2]));
				couponLineStatisInfo.setBinded((BigDecimal) object[3]);
				couponLineStatisInfo.setFreeze((BigDecimal)object[4]);
				
				infos.add(couponLineStatisInfo);
			}
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
//		return null;
	}
	
	/** 电子券-线路-统计-内部类*/
	public class CouponLineStatisInfo{
		private  Float sum; //o[0]
		private BigInteger num;  //o[1]
		private Float total;  //(Float) o[0] * ((BigInteger) o[1]).floatValue()
		private BigDecimal notBind; //o[2]
		private BigDecimal binded;   //o[3]
		private BigDecimal freeze;   //o[4]
		public Float getSum() {
			return sum;
		}
		public void setSum(Float sum) {
			this.sum = sum;
		}
		public BigInteger getNum() {
			return num;
		}
		public void setNum(BigInteger num) {
			this.num = num;
		}
		public Float getTotal() {
			return total;
		}
		public void setTotal(Float total) {
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
		public BigDecimal getFreeze() {
			return freeze;
		}
		public void setFreeze(BigDecimal freeze) {
			this.freeze = freeze;
		}
	}
	
}
