package com.hongyu.controller.hzj03.coupon;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.TwiceConsumeRecord;
import com.hongyu.entity.TwiceConsumeStatis;
import com.hongyu.service.CouponBigCustomerService;
import com.hongyu.service.TwiceConsumeRecordService;
import com.hongyu.service.TwiceConsumeStatisService;

/** 电子券 - 后台管理 - 二次消费 */
@Controller
@RequestMapping("/admin/business/coupon")
public class CouponAdmin_Twice_Controller {

	@Resource(name = "couponBigCustomerServiceImpl")
	CouponBigCustomerService couponBigCustomerService;

	@Resource(name = "twiceConsumeRecordServiceImpl")
	TwiceConsumeRecordService twiceConsumeRecordService;

	@Resource(name = "twiceConsumeStatisServiceImpl")
	TwiceConsumeStatisService twiceConsumeStatisService;

	/** 二次消费-统计 */
	@RequestMapping(value = "/twice/statis")
	@ResponseBody
	public Json getTwiceStatis(String consumer, String startTime, String endTime) {
		Json json = new Json();

		try {

			if (consumer == null && startTime == null && endTime == null) { // 没有任何查询条件

				List<TwiceConsumeStatis> list = twiceConsumeStatisService.findAll();
				json.setObj(list);
				json.setMsg("查询成功！");
				json.setSuccess(true);
			}

			else if (consumer != null && startTime == null && endTime == null) { // 只有姓名
				List<Filter> filters = new ArrayList<>();
				filters.add(new Filter("consumer", Operator.like, consumer));
				Pageable pageable = new Pageable(1, Pageable.MAX_PAGE_SIZE); // 一页的最大值
				pageable.setFilters(filters);
				Page<TwiceConsumeStatis> page = twiceConsumeStatisService.findPage(pageable);
				if (page == null || page.getPageSize() == 0) {
					json.setObj(null);
					json.setMsg("没有所需的查询结果！");
					json.setSuccess(true);
				} else {
					json.setObj(page.getRows());
					json.setMsg("查询成功！");
					json.setSuccess(true);
				}
			}

			else {// 包含了时间 形如 2018-04-24T23:59:59.123Z
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				// 注意需要令state为1筛选真·二次消费记录
				StringBuilder jpql = new StringBuilder(
						"SELECT consumer AS c,(select phone from hy_twice_consume_record where consumer = c LIMIT 1) AS phonenumber,COUNT(id),SUM(payment) FROM hy_twice_consume_record WHERE state = 1 ");
				if (consumer != null && !consumer.equals("")) {
					jpql.append("  AND consumer LIKE " + "'%" + consumer + "%' ");
				}
				if (startTime != null && !startTime.equals("")) {
					Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
					jpql.append("  AND consume_time >=  " + "'" + sdf.format(sTime) + "'");
				}
				if (endTime != null && !endTime.equals("")) {
					Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
					jpql.append("  AND consume_time <=  " + "'" + sdf.format(eTime) + "'");
				}
				
				jpql.append(" GROUP BY consumer ORDER BY consumer");

				List<Object[]> list = couponBigCustomerService.statis(jpql.toString());
				if (list == null || list.size() == 0) {
					json.setMsg("未获取到符合条件的结果");
					json.setSuccess(false);
					json.setObj(list);
					return json;
				}

				List<HashMap<String, Object>> rows = new ArrayList<>();
				for (Object[] o : list) {
					HashMap<String, Object> map = new HashMap<>();

					map.put("consumer", o[0]);
					map.put("phone", o[1]);
					map.put("consumeCount", o[2]);
					map.put("totalAmount", o[3]);

					rows.add(map);
				}

				// HashMap<String, Object> obj = new HashMap<String, Object>();
				// obj.put("rows", rows);
				// obj.put("consumer", consumer == null ? "" : consumer);
				// obj.put("startTime", startTime == null ? "" : startTime);
				// obj.put("endTime", endTime == null ? "" : endTime);

				json.setMsg("查询成功");
				json.setSuccess(true);
				json.setObj(rows);

				return json;
			}

		} catch (Exception e) {
			json.setObj(null);
			json.setMsg("查询失败！");
			json.setSuccess(false);
		}

		return json;
	}

	/** 二次消费-记录 */
	@RequestMapping(value = "/twice/view")
	@ResponseBody
	public Json getTwiceConsume(Pageable pageable, String startTime, String endTime,
			TwiceConsumeRecord twiceConsumeRecord) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (twiceConsumeRecord == null) {
			twiceConsumeRecord = new TwiceConsumeRecord();
		}

		twiceConsumeRecord.setState(1); // 注意需要令state为1筛选真·二次消费记录
		try {
			List<Filter> filters = new ArrayList<>();
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("consumeTime", Operator.ge,
						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("consumeTime", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));

			pageable.setFilters(filters);

			List<Order> orders = new ArrayList<>();
			orders.add(new Order("id", Direction.desc));// 倒序排序
			pageable.setOrders(orders);

			Page<TwiceConsumeRecord> page = twiceConsumeRecordService.findPage(pageable, twiceConsumeRecord);

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(page);
		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(true);
			j.setObj(null);
		}
		return j;
	}

	/** 二次消费-统计-导出Excel */
	@RequestMapping(value = "/twice/statis/excel")
//	@ResponseBody
	public void getTwiceStatisExcel(String consumer, String startTime, String endTime, HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
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

			sb2.append("二次消费统计");
			String fileName = "二次消费统计.xls"; // Excel文件名
			String tableTitle = sb2.toString(); // Excel表标题
			String configFile = "twiceStatis.xml"; // 配置文件

			List<TwiceStatisInfo> infos = new ArrayList<>();

			if (consumer == null && startTime == null && endTime == null) { // 没有任何查询条件

				List<TwiceConsumeStatis> list = twiceConsumeStatisService.findAll();
//				if (list == null || list.isEmpty()) {
//					json.setObj(null);
//					json.setMsg("未获取到符合条件的结果");
//					json.setSuccess(true);
//					return json;
//				}

				for (TwiceConsumeStatis t : list) {
					TwiceStatisInfo twiceStatisInfo = new TwiceStatisInfo();
					twiceStatisInfo.setConsumer(t.getConsumer());
					twiceStatisInfo.setPhone(t.getPhone());
					twiceStatisInfo.setConsumeCount(new BigInteger(t.getConsumeCount() + ""));
					twiceStatisInfo.setTotalAmount(t.getTotalAmount() + 0.0);
					infos.add(twiceStatisInfo);
				}
			}

			else if (consumer != null && startTime == null && endTime == null) { // 只有姓名
				List<Filter> filters = new ArrayList<>();
				filters.add(new Filter("consumer", Operator.like, consumer));
				Pageable pageable = new Pageable(1, Pageable.MAX_PAGE_SIZE); // 一页的最大值
				pageable.setFilters(filters);
				Page<TwiceConsumeStatis> page = twiceConsumeStatisService.findPage(pageable);
				if (page == null || page.getPageSize() == 0) {
					json.setObj(null);
					json.setMsg("未获取到符合条件的结果");
					json.setSuccess(true);
				} else {
					for (TwiceConsumeStatis t : page.getRows()) {
						TwiceStatisInfo twiceStatisInfo = new TwiceStatisInfo();
						twiceStatisInfo.setConsumer(t.getConsumer());
						twiceStatisInfo.setPhone(t.getPhone());
						twiceStatisInfo.setConsumeCount(new BigInteger(t.getConsumeCount() + ""));
						twiceStatisInfo.setTotalAmount(t.getTotalAmount() + 0.0);
						infos.add(twiceStatisInfo);
					}
				}
			}

			else {// 包含了时间
				StringBuilder jpql = new StringBuilder(
						"SELECT consumer AS c,(select phone from hy_twice_consume_record where consumer = c LIMIT 1) AS phonenumber,COUNT(id),SUM(payment) FROM hy_twice_consume_record WHERE state = 1 ");
				if (consumer != null && !consumer.equals("")) {
					jpql.append("  AND consumer LIKE " + "'%" + consumer + "%' ");
				}
				if (startTime != null && !startTime.equals("")) {
					Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
					jpql.append("  AND consume_time >=  " + "'" + sdf.format(sTime) + "'");
				}
				if (endTime != null && !endTime.equals("")) {
					Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
					jpql.append("  AND consume_time <=  " + "'" + sdf.format(eTime) + "'");
				}
				jpql.append(" GROUP BY consumer ORDER BY consumer");

				List<Object[]> list = couponBigCustomerService.statis(jpql.toString());
//				if (list == null || list.size() == 0) {
//					json.setMsg("未获取到符合条件的结果");
//					json.setSuccess(false);
//					json.setObj(list);
//					return json;
//				}

				for (Object[] o : list) {
					TwiceStatisInfo twiceStatisInfo = new TwiceStatisInfo();
					twiceStatisInfo.setConsumer((String) o[0]);
					twiceStatisInfo.setPhone((String) o[1]);
					twiceStatisInfo.setConsumeCount((BigInteger) o[2]);
					twiceStatisInfo.setTotalAmount((Double) o[3]);
					infos.add(twiceStatisInfo);
				}

				// HashMap<String, Object> obj = new HashMap<String, Object>();
				// obj.put("rows", rows);
				// obj.put("consumer", consumer == null ? "" : consumer);
				// obj.put("startTime", startTime == null ? "" : startTime);
				// obj.put("endTime", endTime == null ? "" : endTime);
			}

			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);

		} catch (Exception e) {
			e.printStackTrace();
		}

//		return null;
	}

	public class TwiceStatisInfo {
		private String consumer; // o[0]
		private String phone; // o[1]
		private BigInteger consumeCount; // o[2]
		private Double totalAmount; // o[3]

		public String getConsumer() {
			return consumer;
		}

		public void setConsumer(String consumer) {
			this.consumer = consumer;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public BigInteger getConsumeCount() {
			return consumeCount;
		}

		public void setConsumeCount(BigInteger consumeCount) {
			this.consumeCount = consumeCount;
		}

		public Double getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(Double totalAmount) {
			this.totalAmount = totalAmount;
		}
	}

}
