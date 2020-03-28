package com.hongyu.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyBusinessPV;
import com.hongyu.entity.SubmitConfirm;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.HyBusinessPVService;

@Controller
@RequestMapping("/admin/business/website_statistics")
public class WebsiteStatisticsController {
	@Resource(name = "hyBusinessPVServiceImpl")
	HyBusinessPVService hyBusinessPVService;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;
	
	@RequestMapping("/list/view")
	@ResponseBody
	public Json StatisticListByDay(Pageable pageable,@DateTimeFormat(pattern="yyyy-MM-dd") Date startDay, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endDay){
		Json json = new Json();
		// Date start = new Date();
		long start = new Date().getTime();
		try {
			List<Date> lDate = new ArrayList<Date>();
			lDate.add(startDay);
			Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(startDay);
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(endDay);
			while (endDay.after(calBegin.getTime())) {
				// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
				lDate.add(calBegin.getTime());
			}
			List<Map<String, Object>> obj = new ArrayList<Map<String, Object>>();
			for (Date l : lDate) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("startTime", l);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String day = sdf.format(l);

				// 访问量
				String jpql = "SELECT COUNT(id) from hy_business_pv  where DATE_FORMAT(click_time,'%Y-%m-%d')='"
						+ day + "' group by DATE_FORMAT(click_time,'%Y-%m-%d')";
				List<Object[]> list = hyBusinessPVService.statis(jpql);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("openView", times.intValue());
				} else {
					map.put("openView", 0);
				}

				// 浏览量
				String jpql2 = "SELECT COUNT(id) from hy_business_pv  where DATE_FORMAT(click_time,'%Y-%m-%d')='"
						+ day + "' and click_type>0 group by DATE_FORMAT(click_time,'%Y-%m-%d')";
				list = hyBusinessPVService.statis(jpql2);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("detailView", times.intValue());
				} else {
					map.put("detailView", 0);
				}

				// 下单量
				String jpql3 = "SELECT COUNT(ID) FROM hy_business_order WHERE DATE_FORMAT(order_time,'%Y-%m-%d')= '"
						+ day + "' and is_show=0";
				list = businessOrderService.statis(jpql3);
				// System.out.println(list.get(0));
				// System.out.println(list.get(0)[0].getClass().toString());
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("orderCount", times.intValue());
				} else {
					map.put("orderCount", 0);
				}

				// 支付量
				String jpql4 = "SELECT COUNT(ID) FROM hy_business_order WHERE DATE_FORMAT(order_time,'%Y-%m-%d')= '"
						+ day + "' and order_state>0 and is_show=0";
				list = businessOrderService.statis(jpql4);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("payCount", times.intValue());
				} else {
					map.put("payCount", 0);
				}
				obj.add(map);
				System.out.println(day);
			}

			Map<String, Object> answer = new HashMap<>();
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", obj.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", obj.subList((page - 1) * rows, page * rows > obj.size() ? obj.size() : page * rows)); // 手动分页？
			json.setObj(answer);

			json.setMsg("获取成功");
			json.setSuccess(true);

			long end = new Date().getTime();
			System.out.println("开始时间是： " + start);
			System.out.println("结束时间是： " + end);
			System.out.println("运行时间为：" + (end - start) / 1000);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
	}

	@RequestMapping("/list_by_hour/view")
	@ResponseBody
	public Json StatisticListByHour(Pageable pageable, String day) {
		Json json = new Json();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startTime = sdf.parse(day + " " + "00:00:00");
			Calendar c = Calendar.getInstance();
			c.setTime(startTime);
			int nextDay = c.get(Calendar.DATE);
			c.set(Calendar.DATE, nextDay + 1);
			Date endTime = c.getTime();

			List<Date> lDate = new ArrayList<Date>();
			lDate.add(startTime);
			Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(startTime);
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(endTime);
			while (endTime.after(calBegin.getTime())) {
				// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
				calBegin.add(Calendar.HOUR_OF_DAY, 1);
				lDate.add(calBegin.getTime());
			}
			System.out.println("***********");
			List<Map<String, Object>> obj = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < lDate.size() - 1; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				Date d = lDate.get(i);
				String sd = sdf.format(d);
				System.out.println("sd = " + sd);
				Date dnext = lDate.get(i + 1);
				String sdnext = sdf.format(dnext);
//				System.out.println(d);

				map.put("statTime", d);
				// 访问量
				String jpql = "SELECT COUNT(id) from hy_business_pv  where click_time>='" + sd + "' and click_time<'"
						+ sdnext + "'";
				List<Object[]> list = hyBusinessPVService.statis(jpql);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("openView", times.intValue());
				} else {
					map.put("openView", 0);
				}

				// 浏览量
				String jpql2 = "SELECT COUNT(id) from hy_business_pv  where click_time>='" + sd + "' and click_time<'"
						+ sdnext + "' and click_type>0";
				list = hyBusinessPVService.statis(jpql2);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("detailView", times.intValue());
				} else {
					map.put("detailView", 0);
				}

				// 下单量
				String jpql3 = "SELECT COUNT(ID) FROM hy_business_order WHERE order_time>='" + sd + "' and order_time<'"
						+ sdnext + "'";
				list = businessOrderService.statis(jpql3);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("orderCount", times.intValue());
				} else {
					map.put("orderCount", 0);
				}

				// 支付量
				String jpql4 = "SELECT COUNT(ID) FROM hy_business_order WHERE order_time>='" + sd + "' and order_time<'"
						+ sdnext + "' and order_state>0";
				list = businessOrderService.statis(jpql4);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					map.put("payCount", times.intValue());
				} else {
					map.put("payCount", 0);
				}
				obj.add(map);

			}
//			System.out.println("***********");

			Map<String, Object> answer = new HashMap<>();
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", obj.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", obj.subList((page - 1) * rows, page * rows > obj.size() ? obj.size() : page * rows)); // 手动分页？
			json.setObj(answer);

			json.setMsg("获取成功");
			json.setSuccess(true);

			System.out.println(startTime);
			System.out.println(endTime);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;

	}

	
	@RequestMapping(value = "/downloadexcel/byday")
	@ResponseBody
	public String websiteStatisticsListByDay(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDay, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endDay,
			HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		long start = new Date().getTime();
		try {
			
			List<Date> lDate = new ArrayList<Date>();
			lDate.add(startDay);
			Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(startDay);
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(endDay);
			while (endDay.after(calBegin.getTime())) {
				// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
				lDate.add(calBegin.getTime());
			}
			List<WebsiteStatistics> obj = new ArrayList<WebsiteStatistics>();
			for (Date l : lDate) {
				WebsiteStatistics websiteStatistics = new WebsiteStatistics();
				
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String day = sdf.format(l);
				websiteStatistics.setStartTime(day);

				// 访问量
				String jpql = "SELECT SUM(is_valid),DATE_FORMAT(click_time,'%Y-%m-%d') from hy_business_pv  where DATE_FORMAT(click_time,'%Y-%m-%d')='"
						+ day + "' group by DATE_FORMAT(click_time,'%Y-%m-%d')";
				List<Object[]> list = hyBusinessPVService.statis(jpql);
				if (list.size() != 0) {
					BigDecimal times = (BigDecimal) list.get(0)[0];
					websiteStatistics.setOpenView(times.intValue());
				} else {
					websiteStatistics.setOpenView(0);
				}

				// 浏览量
				String jpql2 = "SELECT SUM(is_valid),DATE_FORMAT(click_time,'%Y-%m-%d') from hy_business_pv  where DATE_FORMAT(click_time,'%Y-%m-%d')='"
						+ day + "' and click_type>0 group by DATE_FORMAT(click_time,'%Y-%m-%d')";
				list = hyBusinessPVService.statis(jpql2);
				if (list.size() != 0) {
					BigDecimal times = (BigDecimal) list.get(0)[0];
					websiteStatistics.setDetailView(times.intValue());
				} else {
					websiteStatistics.setDetailView(0);
				}

				// 下单量
				String jpql3 = "SELECT COUNT(ID) FROM hy_business_order WHERE DATE_FORMAT(order_time,'%Y-%m-%d')= '"
						+ day + "' and is_show=0";
				list = businessOrderService.statis(jpql3);
				// System.out.println(list.get(0));
				// System.out.println(list.get(0)[0].getClass().toString());
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					websiteStatistics.setOrderCount(times.intValue());
				} else {
					websiteStatistics.setOrderCount(0);
				}

				// 支付量
				String jpql4 = "SELECT COUNT(ID) FROM hy_business_order WHERE DATE_FORMAT(order_time,'%Y-%m-%d')= '"
						+ day + "' and order_state>0 and is_show=0";
				list = businessOrderService.statis(jpql4);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					websiteStatistics.setPayCount(times.intValue());
				} else {
					websiteStatistics.setPayCount(0);
				}
				obj.add(websiteStatistics);
			}
				
			StringBuffer sb2 = new StringBuffer();
			sb2.append("运营数据报表");
			String fileName = "运营数据报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "websiteStatistics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, obj, fileName, tableTitle, configFile);
			return null;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("下载失败");
			json.setSuccess(false);
			return null;
		}
		

	}
	
	@RequestMapping("/downloadexcel/byhour")
	@ResponseBody
	public Json websiteStatisticsListByHour(String day,HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startTime = sdf.parse(day + " " + "00:00:00");
			Calendar c = Calendar.getInstance();
			c.setTime(startTime);
			int nextDay = c.get(Calendar.DATE);
			c.set(Calendar.DATE, nextDay + 1);
			Date endTime = c.getTime();

			List<Date> lDate = new ArrayList<Date>();
			lDate.add(startTime);
			Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(startTime);
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(endTime);
			while (endTime.after(calBegin.getTime())) {
				// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
				calBegin.add(Calendar.HOUR_OF_DAY, 1);
				lDate.add(calBegin.getTime());
			}
//			System.out.println("***********");
			List<WebsiteStatistics> obj = new ArrayList<WebsiteStatistics>();
			for (int i = 0; i < lDate.size() - 1; i++) {
				WebsiteStatistics websiteStatistics = new WebsiteStatistics();
				Date d = lDate.get(i);
				String sd = sdf.format(d);
				System.out.println("sd = " + sd);
				Date dnext = lDate.get(i + 1);
				String sdnext = sdf.format(dnext);
				System.out.println(d);
				websiteStatistics.setStartTime(sd);
				// 访问量
				String jpql = "SELECT COUNT(id) from hy_business_pv  where click_time>='" + sd + "' and click_time<'"
						+ sdnext + "'";
				List<Object[]> list = hyBusinessPVService.statis(jpql);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					websiteStatistics.setOpenView(times.intValue());
				} else {
					websiteStatistics.setOpenView(0);
				}

				// 浏览量
				String jpql2 = "SELECT COUNT(id) from hy_business_pv  where click_time>='" + sd + "' and click_time<'"
						+ sdnext + "' and click_type>0";
				list = hyBusinessPVService.statis(jpql2);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					websiteStatistics.setDetailView(times.intValue());
				} else {
					websiteStatistics.setDetailView(0);
				}

				// 下单量
				String jpql3 = "SELECT COUNT(ID) FROM hy_business_order WHERE order_time>='" + sd + "' and order_time<'"
						+ sdnext + "'";
				list = businessOrderService.statis(jpql3);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					websiteStatistics.setOrderCount(times.intValue());
				} else {
					websiteStatistics.setOrderCount(0);
				}

				// 支付量
				String jpql4 = "SELECT COUNT(ID) FROM hy_business_order WHERE order_time>='" + sd + "' and order_time<'"
						+ sdnext + "' and order_state>0";
				list = businessOrderService.statis(jpql4);
				if (list.size() != 0) {
					Object iObject = list.get(0);
					BigInteger times = new BigInteger(iObject.toString());
					websiteStatistics.setPayCount(times.intValue());
				} else {
					websiteStatistics.setPayCount(0);
				}
				obj.add(websiteStatistics);

			}
//			System.out.println("***********");

			StringBuffer sb2 = new StringBuffer();
			sb2.append("分小时运营数据报表");
			String fileName = "分小时运营数据报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "websiteStatistics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, obj, fileName, tableTitle, configFile);
			return null;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;

	}
	
	public class WebsiteStatistics{
		private String startTime;
		private int openView;
		private int detailView;
		private int orderCount;
		private int payCount;

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public int getOpenView() {
			return openView;
		}

		public void setOpenView(int openView) {
			this.openView = openView;
		}

		public int getDetailView() {
			return detailView;
		}

		public void setDetailView(int detailView) {
			this.detailView = detailView;
		}

		public int getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(int orderCount) {
			this.orderCount = orderCount;
		}

		public int getPayCount() {
			return payCount;
		}

		public void setPayCount(int payCount) {
			this.payCount = payCount;
		}
	}
	
}
