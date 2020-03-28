package com.hongyu.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.OrderItemDivide;
import com.hongyu.entity.OrderTransaction;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WeDivideReport;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.OrderItemDivideService;
import com.hongyu.service.OrderTransactionService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WeDivideReportService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/business/webusinessdivide/")
public class WeBusinessDivideController {
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	
	@Resource(name = "weDivideReportServiceImpl")
	WeDivideReportService weDivideReportServiceImpl;
	
	@Resource(name = "orderItemDivideServiceImpl")
	OrderItemDivideService orderItemDivideService;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	
	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemServiceImpl;
	
	@Resource(name = "orderTransactionServiceImpl")
	OrderTransactionService orderTransactionServiceImpl;
	
	@RequestMapping("/webusinessexcel/view")
	public Json weDivideReportExcel(@DateTimeFormat(iso=ISO.DATE_TIME)Date start,@DateTimeFormat(iso=ISO.DATE_TIME)Date end,
			HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			List<Filter> filters=new ArrayList<>();
			if(start!=null){
			start=DateUtil.getStartOfDay(start);
			filters.add(Filter.ge("balanceTime", start));
			}
			if(end!=null){
			end=DateUtil.getEndOfDay(end);
			filters.add(Filter.le("balanceTime", end));
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
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);
			
		} catch (Exception e) {
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
	
	public class PlatformDivide {
		private String type;
		private String salesAmount;
		private String divideAmount;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getSalesAmount() {
			return salesAmount;
		}
		public void setSalesAmount(String salesAmount) {
			this.salesAmount = salesAmount;
		}
		public String getDivideAmount() {
			return divideAmount;
		}
		public void setDivideAmount(String divideAmount) {
			this.divideAmount = divideAmount;
		}
	}
	
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/platformdivide/view")
	@ResponseBody
	public Json platformWeDivide(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		try {
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			
			List<Filter> filters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				filters.add(Filter.ge("salesTime", yesterDayStart));
				filters.add(Filter.le("salesTime", yesterDayEnd));
				
			}
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("salesTime", start));
			}
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("salesTime", end));
			}
			
			List<Filter> webusinessFilters = new ArrayList<Filter>();
			//先统计虹宇门店微商
			webusinessFilters.add(Filter.eq("type", 0));
			List<WeBusiness> businesses = weBusinessService.findList(null, webusinessFilters, null);
			
			if (!businesses.isEmpty()) {
				filters.add(Filter.in("weBusiness", businesses));
				List<WeDivideReport> reports = weDivideReportServiceImpl.findList(null, filters, null);
				if (!reports.isEmpty()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("type", Integer.valueOf(0));
					BigDecimal divideAmount = new BigDecimal(0);
					BigDecimal saleMoney = new BigDecimal(0.0);
					for (WeDivideReport report : reports) {
						saleMoney = saleMoney.add(report.getSalesAmount());
						divideAmount = divideAmount.add(report.getDivideAmount());
					}
					map.put("salesAmount", saleMoney);
					map.put("divideAmount", divideAmount);
					result.add(map);
				}
				filters.remove(filters.size()-1);
			}
			
			//考虑非虹宇门店微商
			webusinessFilters.remove(webusinessFilters.size()-1);
			webusinessFilters.add(Filter.eq("type", 1));
			List<WeBusiness> nonHongyuBusinesses = weBusinessService.findList(null, webusinessFilters, null);
			if (!nonHongyuBusinesses.isEmpty()) {
				filters.add(Filter.in("weBusiness", nonHongyuBusinesses));
				List<WeDivideReport> reports = weDivideReportServiceImpl.findList(null, filters, null);
				if (!reports.isEmpty()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("type", Integer.valueOf(1));
					BigDecimal divideAmount = new BigDecimal(0);
					BigDecimal saleMoney = new BigDecimal(0.0);
					for (WeDivideReport report : reports) {
						saleMoney = saleMoney.add(report.getSalesAmount());
						divideAmount = divideAmount.add(report.getDivideAmount());
					}
					map.put("salesAmount", saleMoney);
					map.put("divideAmount", divideAmount);
					result.add(map);
				}
				filters.remove(filters.size()-1);
			}
			
			
			//考虑个人微商
			webusinessFilters.remove(webusinessFilters.size()-1);
			webusinessFilters.add(Filter.eq("type", 2));
			List<WeBusiness> personBusinesses = weBusinessService.findList(null, webusinessFilters, null);
			if (!personBusinesses.isEmpty()) {
				filters.add(Filter.in("weBusiness", personBusinesses));
				List<WeDivideReport> reports = weDivideReportServiceImpl.findList(null, filters, null);
				if (!reports.isEmpty()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("type", Integer.valueOf(2));
					BigDecimal divideAmount = new BigDecimal(0);
					BigDecimal saleMoney = new BigDecimal(0.0);
					for (WeDivideReport report : reports) {
						saleMoney = saleMoney.add(report.getSalesAmount());
						divideAmount = divideAmount.add(report.getDivideAmount());
					}
					map.put("salesAmount", saleMoney);
					map.put("divideAmount", divideAmount);
					result.add(map);
				}
				filters.remove(filters.size()-1);
			}
					
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
			
		} catch (Exception e) {
			json.setMsg("查询失败");
			json.setSuccess(false);
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/platformdivideexcel/view")
	public String platformWeDivideExcel(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			List<PlatformDivide> result = new ArrayList<PlatformDivide>();
			
			List<Filter> filters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				filters.add(Filter.ge("salesTime", yesterDayStart));
				filters.add(Filter.le("salesTime", yesterDayEnd));
				
			}
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("salesTime", start));
			}
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("salesTime", end));
			}
			
			List<Filter> webusinessFilters = new ArrayList<Filter>();
			//先统计虹宇门店微商
			webusinessFilters.add(Filter.eq("type", 0));
			List<WeBusiness> businesses = weBusinessService.findList(null, webusinessFilters, null);
			
			if (!businesses.isEmpty()) {
				filters.add(Filter.in("weBusiness", businesses));
				List<WeDivideReport> reports = weDivideReportServiceImpl.findList(null, filters, null);
				if (!reports.isEmpty()) {
					PlatformDivide map = new PlatformDivide();
					map.setType("虹宇门店微商");
					BigDecimal divideAmount = new BigDecimal(0);
					BigDecimal saleMoney = new BigDecimal(0.0);
					for (WeDivideReport report : reports) {
						saleMoney = saleMoney.add(report.getSalesAmount());
						divideAmount = divideAmount.add(report.getDivideAmount());
					}
					map.setSalesAmount(saleMoney.toString());
					map.setDivideAmount(divideAmount.toString());
					result.add(map);
				}
				filters.remove(filters.size()-1);
			}
			
			//考虑非虹宇门店微商
			webusinessFilters.remove(webusinessFilters.size()-1);
			webusinessFilters.add(Filter.eq("type", 1));
			List<WeBusiness> nonHongyuBusinesses = weBusinessService.findList(null, webusinessFilters, null);
			if (!nonHongyuBusinesses.isEmpty()) {
				filters.add(Filter.in("weBusiness", nonHongyuBusinesses));
				List<WeDivideReport> reports = weDivideReportServiceImpl.findList(null, filters, null);
				if (!reports.isEmpty()) {
					PlatformDivide map = new PlatformDivide();
					map.setType("非虹宇门店微商");
					BigDecimal divideAmount = new BigDecimal(0);
					BigDecimal saleMoney = new BigDecimal(0.0);
					for (WeDivideReport report : reports) {
						saleMoney = saleMoney.add(report.getSalesAmount());
						divideAmount = divideAmount.add(report.getDivideAmount());
					}
					map.setSalesAmount(saleMoney.toString());
					map.setDivideAmount(divideAmount.toString());
					result.add(map);
				}
				filters.remove(filters.size()-1);
			}
			
			
			//考虑个人微商
			webusinessFilters.remove(webusinessFilters.size()-1);
			webusinessFilters.add(Filter.eq("type", 2));
			List<WeBusiness> personBusinesses = weBusinessService.findList(null, webusinessFilters, null);
			if (!personBusinesses.isEmpty()) {
				filters.add(Filter.in("weBusiness", personBusinesses));
				List<WeDivideReport> reports = weDivideReportServiceImpl.findList(null, filters, null);
				if (!reports.isEmpty()) {
					PlatformDivide map = new PlatformDivide();
					map.setType("个人微商");
					BigDecimal divideAmount = new BigDecimal(0);
					BigDecimal saleMoney = new BigDecimal(0.0);
					for (WeDivideReport report : reports) {
						saleMoney = saleMoney.add(report.getSalesAmount());
						divideAmount = divideAmount.add(report.getDivideAmount());
					}
					map.setSalesAmount(saleMoney.toString());
					map.setDivideAmount(divideAmount.toString());
					result.add(map);
				}
				filters.remove(filters.size()-1);
			}
			
			// 生成Excel表标题
			StringBuilder sb2 = new StringBuilder();
			
			sb2.append("平台微商分成汇总");
			String fileName = "平台微商分成汇总.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "platformDivide.xml"; // 配置文件
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, result, fileName, tableTitle, configFile);

		} catch (Exception e) {
			return null;
		}
		
		return null;
	}
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/storedividelist/view")
	@ResponseBody
	public Json storeWeDivide(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			Integer type, String storename, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		
		try {
			String start = null;
			String end = null;
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				start = sdf.format(DateUtil.getStartOfDay(yesterDay));
				end= sdf.format(DateUtil.getEndOfDay(yesterDay));			
			}
			if (startdate != null) {
				start = sdf.format(DateUtil.getStartOfDay(startdate));
			}
			if (enddate != null) {
				end = sdf.format(DateUtil.getEndOfDay(enddate));
			}
			
			if (type == null) {
				json.setSuccess(false);
				json.setMsg("缺少type参数");
				json.setObj(null);
				return json;
			}
			
			WrapStoreDividePage list = weDivideReportServiceImpl.findStoreDividePage(pageable, start, end, type, storename, true);
			Page<WrapStoreDivide> page = new Page<>(list.getList(), list.getTotal(), pageable);
			json.setSuccess(true);
			json.setObj(page);
			json.setMsg("查询成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setObj(e);
			json.setMsg("查询失败");
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/storedivideexcel/view")
	public Json storeWeDivideExcel(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			Integer type, String storename, HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		
		try {
			String start = null;
			String end = null;
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				start = sdf.format(DateUtil.getStartOfDay(yesterDay));
				end= sdf.format(DateUtil.getEndOfDay(yesterDay));			
			}
			if (startdate != null) {
				start = sdf.format(DateUtil.getStartOfDay(startdate));
			}
			if (enddate != null) {
				end = sdf.format(DateUtil.getEndOfDay(enddate));
			}
			
			if (type == null) {
				json.setSuccess(false);
				json.setMsg("缺少type参数");
				json.setObj(null);
				return json;
			}
			
			WrapStoreDividePage list = weDivideReportServiceImpl.findStoreDividePage(null, start, end, type, storename, false);
			List<WrapStoreDivide> divides = list.getList();
			
			// 生成Excel表标题
			StringBuilder sb2 = new StringBuilder();
			
			sb2.append("门店分成汇总");
			String fileName = "门店分成汇总.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "storeDivide.xml"; // 配置文件
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, divides, fileName, tableTitle, configFile);
		} catch (Exception e) {
			return null;
		}
		
		
		return null;
	}
	
	
	
	
	public static class WrapStoreDivide implements Serializable {
		public Long storeId;
		public String storeName;
		//0:虹宇微商 1:非虹宇微商
		public Integer type;
		public BigDecimal salesAmount;
		public BigDecimal divideAmount;
		
		
		public Long getStoreId() {
			return storeId;
		}
		public void setStoreId(Long storeId) {
			this.storeId = storeId;
		}
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
		public BigDecimal getSalesAmount() {
			return salesAmount;
		}
		public void setSalesAmount(BigDecimal salesAmount) {
			this.salesAmount = salesAmount;
		}
		public BigDecimal getDivideAmount() {
			return divideAmount;
		}
		public void setDivideAmount(BigDecimal divideAmount) {
			this.divideAmount = divideAmount;
		}
		
		
	}
	
	public static class WrapStoreDividePage {
		public List<WrapStoreDivide> list;
		public Long total;
		public List<WrapStoreDivide> getList() {
			return list;
		}
		public void setList(List<WrapStoreDivide> list) {
			this.list = list;
		}
		public Long getTotal() {
			return total;
		}
		public void setTotal(Long total) {
			this.total = total;
		}
	}
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/webusinessdividelist/view")
	@ResponseBody
	public Json weBusinessDivide(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			Integer type, Long storeId, String businessName, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		
		try {
			String start = null;
			String end = null;
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				start = sdf.format(DateUtil.getStartOfDay(yesterDay));
				end= sdf.format(DateUtil.getEndOfDay(yesterDay));			
			} 
			if (startdate != null) {
				start = sdf.format(DateUtil.getStartOfDay(startdate));
			} 
			if (enddate != null) {
				end = sdf.format(DateUtil.getEndOfDay(enddate));
			}
			
			
			WrapBusinessDividePage list = weDivideReportServiceImpl.findBusinessDividePage(pageable, start, end, type, storeId, businessName, true);
			Page<WrapBusinessDivide> page = new Page<>(list.getList(), list.getTotal(), pageable);
			json.setSuccess(true);
			json.setObj(page);
			json.setMsg("查询成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setObj(e);
			json.setMsg("查询失败");
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/webusinessdivideexcel/view")
	public String weBusinessDivideExcel(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			Integer type, Long storeId, String businessName, HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		
		try {
			String start = null;
			String end = null;
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				start = sdf.format(DateUtil.getStartOfDay(yesterDay));
				end= sdf.format(DateUtil.getEndOfDay(yesterDay));			
			}
			if (startdate != null) {
				start = sdf.format(DateUtil.getStartOfDay(startdate));
			} 
			if (enddate != null) {
				end = sdf.format(DateUtil.getEndOfDay(enddate));
			}
			
			
			WrapBusinessDividePage list = weDivideReportServiceImpl.findBusinessDividePage(null, start, end, type, storeId, businessName, false);
			List<WrapBusinessDivide> divides = list.getList();
			
			// 生成Excel表标题
			StringBuilder sb2 = new StringBuilder();
			
			sb2.append("微商分成汇总");
			String fileName = "微商分成汇总.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "businessDivide.xml"; // 配置文件
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, divides, fileName, tableTitle, configFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public static class WrapBusinessDivide implements Serializable {
		public Long businessId;
		public String businessName;
		//0:虹宇微商 1:非虹宇微商 2:个人微商
		public Integer type;
		public BigDecimal salesAmount;
		public BigDecimal divideAmount;
		public Long getBusinessId() {
			return businessId;
		}
		public void setBusinessId(Long businessId) {
			this.businessId = businessId;
		}
		public String getBusinessName() {
			return businessName;
		}
		public void setBusinessName(String businessName) {
			this.businessName = businessName;
		}
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
		public BigDecimal getSalesAmount() {
			return salesAmount;
		}
		public void setSalesAmount(BigDecimal salesAmount) {
			this.salesAmount = salesAmount;
		}
		public BigDecimal getDivideAmount() {
			return divideAmount;
		}
		public void setDivideAmount(BigDecimal divideAmount) {
			this.divideAmount = divideAmount;
		}
		
		
	}
	
	public static class WrapBusinessDividePage {
		public List<WrapBusinessDivide> list;
		public Long total;
		public List<WrapBusinessDivide> getList() {
			return list;
		}
		public void setList(List<WrapBusinessDivide> list) {
			this.list = list;
		}
		public Long getTotal() {
			return total;
		}
		public void setTotal(Long total) {
			this.total = total;
		}
	}
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/businessdivideitemlist/view")
	@ResponseBody
	public Json weBusinessDivideItemList(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			Integer type, Long storeId, Long businessId, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		Date start = null;
		Date end = null;
		List<Filter> filters = new ArrayList<>();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				start = DateUtil.getStartOfDay(yesterDay);
				end = DateUtil.getEndOfDay(yesterDay);
			}
			if (startdate != null) {
				start = DateUtil.getStartOfDay(startdate);
			}
			if (enddate != null) {
				end = DateUtil.getEndOfDay(enddate);
			}
			
			if (start != null) {
				filters.add(Filter.ge("acceptTime", start));
			}
			
			if (end != null) {
				filters.add(Filter.le("acceptTime", end));
			}
			
			List<WrapBusinessDivideDetail> result = new ArrayList<>();
			List<WeBusiness> businesses = null;
			WeBusiness business = null;
			if (storeId != null) {
				List<Filter> businessFilter = new ArrayList<>();
				businessFilter.add(Filter.eq("storeId", storeId));
				businessFilter.add(Filter.eq("type", type));
				businesses = weBusinessService.findList(null, businessFilter, null);
				if (businesses.isEmpty()) {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<>(new ArrayList<>(), 0, pageable));
					return json;
				}
				filters.add(Filter.in("weBusiness", businesses));
			} else if (businessId != null) {
				business = weBusinessService.find(businessId);
				if (business == null) {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<>(new ArrayList<>(), 0, pageable));
					return json;
				}
				filters.add(Filter.eq("weBusiness", business));
			}
			
			List<OrderItemDivide> list1 = orderItemDivideService.findList(null, filters, null);
			for (OrderItemDivide item : list1) {
				WrapBusinessDivideDetail detail = new WrapBusinessDivideDetail();
				detail.setType(item.getWeBusiness().getType());
				detail.setBusinessId(item.getWeBusiness().getId());
				detail.setBusinessName(item.getWeBusiness().getName());
				detail.setDivideAmount(item.getWeBusinessAmount());
				detail.setOrderCode(item.getBusinessOrder().getOrderCode());
				detail.setSpecialtyName(businessOrderItemServiceImpl.getSpecialtyName(item.getBusinessOrderItem()));
				String specification = businessOrderItemServiceImpl.getSpecificationName(item.getBusinessOrderItem());
				if (specification == null) {
					detail.setSpecification("无");
				} else {
					detail.setSpecification(specification);
				}
				detail.setSpecialtyCode(businessOrderItemServiceImpl.getSpecialtyCode(item.getBusinessOrderItem()));
				detail.setUnitPrice(item.getBusinessOrderItem().getOriginalPrice());
				detail.setQuantity(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity());
				detail.setSalesAmount(item.getBusinessOrderItem().getSalePrice().multiply(
						BigDecimal.valueOf(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity())));
				detail.setOrderTime(sdf.format(item.getBusinessOrderItem().getCreateTime()));
				detail.setAcceptTime(sdf.format(item.getAcceptTime()));
				List<Filter> fs = new ArrayList<>();
				fs.add(Filter.eq("businessOrder", item.getBusinessOrder()));
				List<OrderTransaction> transactions = orderTransactionServiceImpl.findList(null, fs, null);
				if (transactions.size() > 0) {
					if (transactions.get(0).getPayType().equals(Integer.valueOf(1))) {
						detail.setPayType("微信支付");
					} else if (transactions.get(0).getPayType().equals(Integer.valueOf(2))) {
						detail.setPayType("支付宝支付");
					}
				}
				result.add(detail);
			}
			
			filters.clear();
			if (start != null) {
				filters.add(Filter.ge("acceptTime", start));
			}
			if (end != null) {
				filters.add(Filter.le("acceptTime", end));
			}
			if (businesses!=null) {
				filters.add(Filter.in("mWeBusiness", businesses));
			} else if (business != null) {
				filters.add(Filter.eq("mWeBusiness", business));
			}
			
			List<OrderItemDivide> list2 = orderItemDivideService.findList(null, filters, null);
			for (OrderItemDivide item : list2) {
				WrapBusinessDivideDetail detail = new WrapBusinessDivideDetail();
				detail.setType(item.getmWeBusiness().getType());
				detail.setBusinessId(item.getmWeBusiness().getId());
				detail.setBusinessName(item.getmWeBusiness().getName());
				detail.setDivideAmount(item.getmWeBusinessAmount());
				detail.setOrderCode(item.getBusinessOrder().getOrderCode());
				detail.setSpecialtyName(businessOrderItemServiceImpl.getSpecialtyName(item.getBusinessOrderItem()));
				String specification = businessOrderItemServiceImpl.getSpecificationName(item.getBusinessOrderItem());
				if (specification == null) {
					detail.setSpecification("无");
				} else {
					detail.setSpecification(specification);
				}
				detail.setSpecialtyCode(businessOrderItemServiceImpl.getSpecialtyCode(item.getBusinessOrderItem()));
				detail.setUnitPrice(item.getBusinessOrderItem().getOriginalPrice());
				detail.setQuantity(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity());
				detail.setSalesAmount(item.getBusinessOrderItem().getSalePrice().multiply(
						BigDecimal.valueOf(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity())));
				detail.setOrderTime(sdf.format(item.getBusinessOrderItem().getCreateTime()));
				detail.setAcceptTime(sdf.format(item.getAcceptTime()));
				List<Filter> fs = new ArrayList<>();
				fs.add(Filter.eq("businessOrder", item.getBusinessOrder()));
				List<OrderTransaction> transactions = orderTransactionServiceImpl.findList(null, fs, null);
				if (transactions.size() > 0) {
					if (transactions.get(0).getPayType().equals(Integer.valueOf(1))) {
						detail.setPayType("微信支付");
					} else if (transactions.get(0).getPayType().equals(Integer.valueOf(2))) {
						detail.setPayType("支付宝支付");
					}
				}
				result.add(detail);
			}
			
			
			filters.clear();
			if (start != null) {
				filters.add(Filter.ge("acceptTime", start));
			}
			if (end != null) {
				filters.add(Filter.le("acceptTime", end));
			}
			if (businesses!=null) {
				filters.add(Filter.in("rWeBusiness", businesses));
			} else if (business != null) {
				filters.add(Filter.eq("rWeBusiness", business));
			}
			List<OrderItemDivide> list3 = orderItemDivideService.findList(null, filters, null);
			for (OrderItemDivide item : list3) {
				WrapBusinessDivideDetail detail = new WrapBusinessDivideDetail();
				detail.setType(item.getrWeBusiness().getType());
				detail.setBusinessId(item.getrWeBusiness().getId());
				detail.setBusinessName(item.getrWeBusiness().getName());
				detail.setDivideAmount(item.getrWeBusinessAmount());
				detail.setOrderCode(item.getBusinessOrder().getOrderCode());
				detail.setSpecialtyName(businessOrderItemServiceImpl.getSpecialtyName(item.getBusinessOrderItem()));
				String specification = businessOrderItemServiceImpl.getSpecificationName(item.getBusinessOrderItem());
				if (specification == null) {
					detail.setSpecification("无");
				} else {
					detail.setSpecification(specification);
				}
				detail.setSpecialtyCode(businessOrderItemServiceImpl.getSpecialtyCode(item.getBusinessOrderItem()));
				detail.setUnitPrice(item.getBusinessOrderItem().getOriginalPrice());
				detail.setQuantity(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity());
				detail.setSalesAmount(item.getBusinessOrderItem().getSalePrice().multiply(
						BigDecimal.valueOf(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity())));
				detail.setOrderTime(sdf.format(item.getBusinessOrderItem().getCreateTime()));
				detail.setAcceptTime(sdf.format(item.getAcceptTime()));
				List<Filter> fs = new ArrayList<>();
				fs.add(Filter.eq("businessOrder", item.getBusinessOrder()));
				fs.add(Filter.eq("payFlow", Integer.valueOf(1)));
				List<OrderTransaction> transactions = orderTransactionServiceImpl.findList(null, fs, null);
				if (transactions.size() > 0) {
					if (transactions.get(0).getPayType().equals(Integer.valueOf(1))) {
						detail.setPayType("微信支付");
					} else if (transactions.get(0).getPayType().equals(Integer.valueOf(2))) {
						detail.setPayType("支付宝支付");
					}
				}
				result.add(detail);
			}
			
			Collections.sort(result, new Comparator<WrapBusinessDivideDetail>() {

				@Override
				public int compare(WrapBusinessDivideDetail o1, WrapBusinessDivideDetail o2) {
					// TODO Auto-generated method stub
					return o2.getOrderTime().compareTo(o1.getOrderTime());
				}
			});
			
			int pageNumber=pageable.getPage();
			int pagesize=pageable.getRows();
			List<WrapBusinessDivideDetail> ans = result.subList((pageNumber-1)*pagesize, 
					pageNumber*pagesize>result.size()?result.size():pageNumber*pagesize);
			
			Map<String, Object> map = new HashMap<>();
			map.put("total", ans.size());
			map.put("pageNumber", pageNumber);
			map.put("pageSize", pagesize);
			map.put("rows", ans);
		
			json.setObj(map);
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setObj(e);
			json.setMsg("查询失败");
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	/**
	 * 若start和end均为空，则默认显示前一天的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/businessdivideitemexcel/view")
	public String weBusinessDivideItemExcel(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate,
			Integer type, Long storeId, Long businessId, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		Date start = null;
		Date end = null;
		List<Filter> filters = new ArrayList<>();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		// 生成Excel表标题
		StringBuilder sb2 = new StringBuilder();
		
		sb2.append("微商提成明细汇总");
		String fileName = "微商提成明细汇总.xls";  // Excel文件名
		String tableTitle = sb2.toString();   // Excel表标题
		String configFile = "businessDivideItem.xml"; // 配置文件
		try {
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				start = DateUtil.getStartOfDay(yesterDay);
				end = DateUtil.getEndOfDay(yesterDay);
			}
			if (startdate != null) {
				start = DateUtil.getStartOfDay(startdate);
			}
			if (enddate != null) {
				end = DateUtil.getEndOfDay(enddate);
			}
			
			if (start != null) {
				filters.add(Filter.ge("acceptTime", start));
			}
			
			if (end != null) {
				filters.add(Filter.le("acceptTime", end));
			}
			
			List<WrapBusinessDivideDetail> result = new ArrayList<>();
			List<WeBusiness> businesses = null;
			WeBusiness business = null;
			if (storeId != null) {
				List<Filter> businessFilter = new ArrayList<>();
				businessFilter.add(Filter.eq("storeId", storeId));
				businessFilter.add(Filter.eq("type", type));
				businesses = weBusinessService.findList(null, businessFilter, null);
				if (businesses.isEmpty()) {
					com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
					excelCon.export2Excel(request, response, result, fileName, tableTitle, configFile);
					return null;
				}
				filters.add(Filter.in("weBusiness", businesses));
			} else if (businessId != null) {
				business = weBusinessService.find(businessId);
				if (business == null) {
					com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
					excelCon.export2Excel(request, response, result, fileName, tableTitle, configFile);
					return null;
				}
				filters.add(Filter.eq("weBusiness", business));
			}
			
			List<OrderItemDivide> list1 = orderItemDivideService.findList(null, filters, null);
			for (OrderItemDivide item : list1) {
				WrapBusinessDivideDetail detail = new WrapBusinessDivideDetail();
				detail.setType(item.getWeBusiness().getType());
				detail.setBusinessId(item.getWeBusiness().getId());
				detail.setBusinessName(item.getWeBusiness().getName());
				detail.setDivideAmount(item.getWeBusinessAmount());
				detail.setOrderCode(item.getBusinessOrder().getOrderCode());
				detail.setSpecialtyName(businessOrderItemServiceImpl.getSpecialtyName(item.getBusinessOrderItem()));
				String specification = businessOrderItemServiceImpl.getSpecificationName(item.getBusinessOrderItem());
				if (specification == null) {
					detail.setSpecification("无");
				} else {
					detail.setSpecification(specification);
				}
				detail.setSpecialtyCode(businessOrderItemServiceImpl.getSpecialtyCode(item.getBusinessOrderItem()));
				detail.setUnitPrice(item.getBusinessOrderItem().getOriginalPrice());
				detail.setQuantity(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity());
				detail.setSalesAmount(item.getTotalAmount());
				detail.setOrderTime(sdf.format(item.getBusinessOrderItem().getCreateTime()));
				List<Filter> fs = new ArrayList<>();
				fs.add(Filter.eq("businessOrder", item.getBusinessOrder()));
				List<OrderTransaction> transactions = orderTransactionServiceImpl.findList(null, fs, null);
				if (transactions.size() > 0) {
					if (transactions.get(0).getPayType().equals(Integer.valueOf(1))) {
						detail.setPayType("微信支付");
					} else if (transactions.get(0).getPayType().equals(Integer.valueOf(2))) {
						detail.setPayType("支付宝支付");
					}
				}
				result.add(detail);
			}
			
			filters.clear();
			if (start != null) {
				filters.add(Filter.ge("acceptTime", start));
			}
			if (end != null) {
				filters.add(Filter.le("acceptTime", end));
			}
			if (businesses!=null) {
				filters.add(Filter.in("mWeBusiness", businesses));
			} else if (business != null) {
				filters.add(Filter.eq("mWeBusiness", business));
			}
			
			List<OrderItemDivide> list2 = orderItemDivideService.findList(null, filters, null);
			for (OrderItemDivide item : list2) {
				WrapBusinessDivideDetail detail = new WrapBusinessDivideDetail();
				detail.setType(item.getmWeBusiness().getType());
				detail.setBusinessId(item.getmWeBusiness().getId());
				detail.setBusinessName(item.getmWeBusiness().getName());
				detail.setDivideAmount(item.getmWeBusinessAmount());
				detail.setOrderCode(item.getBusinessOrder().getOrderCode());
				detail.setSpecialtyName(businessOrderItemServiceImpl.getSpecialtyName(item.getBusinessOrderItem()));
				String specification = businessOrderItemServiceImpl.getSpecificationName(item.getBusinessOrderItem());
				if (specification == null) {
					detail.setSpecification("无");
				} else {
					detail.setSpecification(specification);
				}
				detail.setSpecialtyCode(businessOrderItemServiceImpl.getSpecialtyCode(item.getBusinessOrderItem()));
				detail.setUnitPrice(item.getBusinessOrderItem().getOriginalPrice());
				detail.setQuantity(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity());
				detail.setSalesAmount(item.getTotalAmount());
				detail.setOrderTime(sdf.format(item.getBusinessOrderItem().getCreateTime()));
				List<Filter> fs = new ArrayList<>();
				fs.add(Filter.eq("businessOrder", item.getBusinessOrder()));
				List<OrderTransaction> transactions = orderTransactionServiceImpl.findList(null, fs, null);
				if (transactions.size() > 0) {
					if (transactions.get(0).getPayType().equals(Integer.valueOf(1))) {
						detail.setPayType("微信支付");
					} else if (transactions.get(0).getPayType().equals(Integer.valueOf(2))) {
						detail.setPayType("支付宝支付");
					}
				}
				result.add(detail);
			}
			
			
			filters.clear();
			if (start != null) {
				filters.add(Filter.ge("acceptTime", start));
			}
			if (end != null) {
				filters.add(Filter.le("acceptTime", end));
			}
			if (businesses!=null) {
				filters.add(Filter.in("rWeBusiness", businesses));
			} else if (business != null) {
				filters.add(Filter.eq("rWeBusiness", business));
			}
			List<OrderItemDivide> list3 = orderItemDivideService.findList(null, filters, null);
			for (OrderItemDivide item : list3) {
				WrapBusinessDivideDetail detail = new WrapBusinessDivideDetail();
				detail.setType(item.getrWeBusiness().getType());
				detail.setBusinessId(item.getrWeBusiness().getId());
				detail.setBusinessName(item.getrWeBusiness().getName());
				detail.setDivideAmount(item.getrWeBusinessAmount());
				detail.setOrderCode(item.getBusinessOrder().getOrderCode());
				detail.setSpecialtyName(businessOrderItemServiceImpl.getSpecialtyName(item.getBusinessOrderItem()));
				String specification = businessOrderItemServiceImpl.getSpecificationName(item.getBusinessOrderItem());
				if (specification == null) {
					detail.setSpecification("无");
				} else {
					detail.setSpecification(specification);
				}
				detail.setSpecialtyCode(businessOrderItemServiceImpl.getSpecialtyCode(item.getBusinessOrderItem()));
				detail.setUnitPrice(item.getBusinessOrderItem().getOriginalPrice());
				detail.setQuantity(item.getBusinessOrderItem().getQuantity()-item.getBusinessOrderItem().getReturnQuantity());
				detail.setSalesAmount(item.getTotalAmount());
				detail.setOrderTime(sdf.format(item.getBusinessOrderItem().getCreateTime()));
				List<Filter> fs = new ArrayList<>();
				fs.add(Filter.eq("businessOrder", item.getBusinessOrder()));
				fs.add(Filter.eq("payFlow", Integer.valueOf(1)));
				List<OrderTransaction> transactions = orderTransactionServiceImpl.findList(null, fs, null);
				if (transactions.size() > 0) {
					if (transactions.get(0).getPayType().equals(Integer.valueOf(1))) {
						detail.setPayType("微信支付");
					} else if (transactions.get(0).getPayType().equals(Integer.valueOf(2))) {
						detail.setPayType("支付宝支付");
					}
				}
				result.add(detail);
			}
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, result, fileName, tableTitle, configFile);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public static class WrapBusinessDivideDetail implements Serializable {
		public Long businessId;
		public String businessName;
		//0:虹宇微商 1:非虹宇微商 2:个人微商
		public Integer type;
		public BigDecimal salesAmount;
		public BigDecimal divideAmount;
		public String orderCode;
		public String specialtyCode;
		public String specialtyName;
		public String specification;
		public BigDecimal unitPrice;
		public Integer quantity;
//		public BigDecimal orderMoney;
		public String orderTime;
		public String payType;

		public String getAcceptTime() {
			return acceptTime;
		}

		public void setAcceptTime(String acceptTime) {
			this.acceptTime = acceptTime;
		}

		public String acceptTime;
		
		public Long getBusinessId() {
			return businessId;
		}
		public void setBusinessId(Long businessId) {
			this.businessId = businessId;
		}
		public String getBusinessName() {
			return businessName;
		}
		public void setBusinessName(String businessName) {
			this.businessName = businessName;
		}
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
		public BigDecimal getSalesAmount() {
			return salesAmount;
		}
		public void setSalesAmount(BigDecimal salesAmount) {
			this.salesAmount = salesAmount;
		}
		public BigDecimal getDivideAmount() {
			return divideAmount;
		}
		public void setDivideAmount(BigDecimal divideAmount) {
			this.divideAmount = divideAmount;
		}
		public String getOrderCode() {
			return orderCode;
		}
		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}
		public String getSpecialtyCode() {
			return specialtyCode;
		}
		public void setSpecialtyCode(String specialtyCode) {
			this.specialtyCode = specialtyCode;
		}
		public String getSpecialtyName() {
			return specialtyName;
		}
		public void setSpecialtyName(String specialtyName) {
			this.specialtyName = specialtyName;
		}
		public String getSpecification() {
			return specification;
		}
		public void setSpecification(String specification) {
			this.specification = specification;
		}
		public BigDecimal getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
//		public BigDecimal getOrderMoney() {
//			return orderMoney;
//		}
//		public void setOrderMoney(BigDecimal orderMoney) {
//			this.orderMoney = orderMoney;
//		}
		public String getOrderTime() {
			return orderTime;
		}
		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}
		public String getPayType() {
			return payType;
		}
		public void setPayType(String payType) {
			this.payType = payType;
		}
		
		
		
		
	}
	
	public static class WrapBusinessDivideDetailPage {
		public List<WrapBusinessDivideDetail> list;
		public Long total;
		public List<WrapBusinessDivideDetail> getList() {
			return list;
		}
		public void setList(List<WrapBusinessDivideDetail> list) {
			this.list = list;
		}
		public Long getTotal() {
			return total;
		}
		public void setTotal(Long total) {
			this.total = total;
		}
	}
	
}
