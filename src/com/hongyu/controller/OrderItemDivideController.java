package com.hongyu.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.OrderItemDivide;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WeDivideReport;
import com.hongyu.service.OrderItemDivideService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WeDivideReportService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("admin/business/orderItemDivide/")
public class OrderItemDivideController {
	@Resource(name = "orderItemDivideServiceImpl")
	OrderItemDivideService orderItemDivideService;
	
	@Resource(name = "weDivideReportServiceImpl")
	WeDivideReportService weDivideReportServiceImpl;
	
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, @DateTimeFormat(iso = ISO.DATE_TIME) Date starttime,
			@DateTimeFormat(iso = ISO.DATE_TIME) Date endtime, OrderItemDivide orderItemDivide) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			if (starttime != null) {
				starttime = DateUtil.getStartOfDay(starttime);
				filters.add(Filter.ge("acceptTime", starttime));
			}
			if (endtime != null) {
				endtime = DateUtil.getEndOfDay(endtime);
				filters.add(Filter.le("acceptTime", endtime));
			}
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<OrderItemDivide> page = orderItemDivideService.findPage(pageable, orderItemDivide);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}
	
	
	@RequestMapping("/webusinessexcel")
	public Json weDivideReportExcel(@DateTimeFormat(iso=ISO.DATE_TIME)Date start,@DateTimeFormat(iso=ISO.DATE_TIME)Date end, String wechataccount,
			HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			
			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			
			if (start != null) {
				sb2.append(sdf.format(start));
			}
			sb2.append("——");
			if (end != null) {
				sb2.append(sdf.format(end));
			}
			
			sb2.append("微商分成统计");
			String fileName = "微商分成统计表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "divideReport.xml"; // 配置文件
			
			List<Filter> filters=new ArrayList<>();
			if(start!=null){
			start=DateUtil.getStartOfDay(start);
			filters.add(Filter.ge("balanceTime", start));
			}
			if(end!=null){
			end=DateUtil.getEndOfDay(end);
			filters.add(Filter.le("balanceTime", end));
			}
			if (StringUtils.isNoneBlank(wechataccount)) {
				List<Filter> webusinessFilters = new ArrayList<Filter>();
				webusinessFilters.add(Filter.eq("wechatAccount", wechataccount));
				List<WeBusiness> weBusinesses = weBusinessService.findList(null, webusinessFilters, null);
				//如果没有指定微商的微信账户
				if (weBusinesses.isEmpty()) {
					com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
					excelCon.export2Excel(request, response, new ArrayList<WeDivideInfo>(), fileName, tableTitle, configFile);
					return null;
				} else {
					filters.add(Filter.in("weBusiness", weBusinesses));
				}
			}
			
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("id"));
			List<WeDivideReport> reports = weDivideReportServiceImpl.findList(null, filters, orders);
			
			List<WeDivideInfo> infos = new ArrayList<WeDivideInfo>();
			for (WeDivideReport report : reports) {
				WeDivideInfo info = new WeDivideInfo();
				info.setWeBusinessName(report.getWeBusiness().getName());
				info.setWeChatAccount(report.getWeBusiness().getWechatAccount());
				info.setSalesAmount(report.getSalesAmount().doubleValue());
				info.setSalesTime(sdf.format(report.getSalesTime()));
				info.setBalanceTime(sdf.format(report.getBalanceTime()));
				infos.add(info);
			}
			
			
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
		return null;
	}
	
	public class WeDivideInfo {
		private String weBusinessName;
		private String weChatAccount;
		private Double salesAmount;
		private String salesTime;
		private String balanceTime;
		public String getWeBusinessName() {
			return weBusinessName;
		}
		public void setWeBusinessName(String weBusinessName) {
			this.weBusinessName = weBusinessName;
		}
		public String getWeChatAccount() {
			return weChatAccount;
		}
		public void setWeChatAccount(String weChatAccount) {
			this.weChatAccount = weChatAccount;
		}
		public Double getSalesAmount() {
			return salesAmount;
		}
		public void setSalesAmount(Double salesAmount) {
			this.salesAmount = salesAmount;
		}
		public String getSalesTime() {
			return salesTime;
		}
		public void setSalesTime(String salesTime) {
			this.salesTime = salesTime;
		}
		public String getBalanceTime() {
			return balanceTime;
		}
		public void setBalanceTime(String balanceTime) {
			this.balanceTime = balanceTime;
		}
		
		
		
	}
}
