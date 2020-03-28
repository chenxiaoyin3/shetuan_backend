package com.hongyu.controller;

import com.hongyu.*;
import com.hongyu.Order.Direction;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/admin/business/order_verify_deny")
public class BusinessOrderVerifyDenyController {
	
	
	@Resource(name="businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;
	
	
	//查询拒审单
	@RequestMapping(value = "/providerorder_page/view")
	@ResponseBody
	public Json providerOrder(@DateTimeFormat(iso=ISO.DATE) Date startdate, @DateTimeFormat(iso=ISO.DATE) Date enddate, String providername, String specialtyname, 
			String receivername, String receiverphone, Pageable pageable) {
		Json json = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			List<Filter> orderFilters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				filters.add(Filter.ge("createTime", yesterDayStart));
				filters.add(Filter.le("createTime", yesterDayEnd));
				orderFilters.add(Filter.ge("orderTime", yesterDayStart));
				orderFilters.add(Filter.le("orderTime", yesterDayEnd));
				
			} 
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
				orderFilters.add(Filter.ge("orderTime", start));
			} 
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
				orderFilters.add(Filter.le("orderTime", end));
			}
			
			//考虑供货商姓名
			if (StringUtils.isNotEmpty(providername)) {
				filters.add(Filter.like("deliverName", providername));
			}
			//商品
			filters.add(Filter.eq("type", 0));
			
			//订单处于已拒审状态
			orderFilters.add(Filter.eq("auditStatus", 2));
			//供货商发货
			//orderFilters.add(Filter.eq("isDivided", true));
			orderFilters.add(Filter.eq("isShow", false));
			if (StringUtils.isNotEmpty(receivername)) {
				orderFilters.add(Filter.like("receiverName", receivername));
			}
			if (StringUtils.isNotEmpty(receiverphone)) {
				orderFilters.add(Filter.like("receiverPhone", receiverphone));
			}
			List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, orderFilters, null);
			if (orders.isEmpty()) {
				Page<Map<String, Object>> page = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
				json.setObj(page);
				json.setMsg("查询成功");
				json.setSuccess(true);
				return json;
			} else {
				filters.add(Filter.in("businessOrder", orders));
			}
			pageable.setFilters(filters);
			pageable.setOrderProperty("id");
			pageable.setOrderDirection(Direction.desc);
			Page<BusinessOrderItem> page = businessOrderItemService.findPage(pageable);
			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
			for (BusinessOrderItem item :page.getRows()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("providerName", item.getDeliverName());
				Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
				if(StringUtils.isNotEmpty(specialtyname) && !StringUtils.contains(specialty.getName(), specialtyname)) {
					continue;
				}
				map.put("specialtyName", specialty.getName());
				SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
				map.put("specification", specification.getSpecification());
				map.put("quantity", item.getQuantity());
				map.put("orderCode", item.getBusinessOrder().getOrderCode());
				map.put("receiverName", item.getBusinessOrder().getReceiverName());
				map.put("receiverPhone", item.getBusinessOrder().getReceiverPhone());
				map.put("receiverAddress", item.getBusinessOrder().getReceiverAddress());
				map.put("costPrice", businessOrderItemService.getCostPriceOfOrderitem(item));
				
				/** wayne 180813*/
				map.put("salePrice", item.getSalePrice());	//单价
				
				BusinessOrder bOrder = item.getBusinessOrder();
				BigDecimal payMoney =  item.getSalePrice().multiply(
						bOrder.getPayMoney().divide(bOrder.getShouldPayMoney(),3,BigDecimal.ROUND_HALF_UP));
				map.put("payMoney",payMoney);	//现金支付
				BigDecimal balanceMoney = item.getSalePrice().multiply(
						bOrder.getBalanceMoney().divide(bOrder.getShouldPayMoney(),3,BigDecimal.ROUND_HALF_UP));
				map.put("balanceMoney", balanceMoney);	//余额支付
				map.put("couponMoney", item.getSalePrice().subtract(payMoney).subtract(balanceMoney));	//一次电子券
				map.put("payTime", bOrder.getPayTime());	//支付时间	
				map.put("weBusinessName", bOrder.getWeBusiness().getName());	//微商姓名
				map.put("storeName", bOrder.getWeBusiness().getNameOfStore());	//所属门店

				map.put("remark", item.getBusinessOrder().getReceiverRemark());
				maps.add(map);
			}
			Page<Map<String, Object>> mapPage = new Page<Map<String, Object>>(maps, page.getTotal(), pageable);
						
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(mapPage);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	//生成拒审单excel表格
	@RequestMapping(value = "/providerorder_excel/view")
	public String providerOrderExcel(@DateTimeFormat(iso=ISO.DATE)Date startdate, @DateTimeFormat(iso=ISO.DATE)Date enddate, 
			String providername, String specialtyname, String receivername, String receiverphone, 
			HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				filters.add(Filter.ge("createTime", yesterDayStart));
				filters.add(Filter.le("createTime", yesterDayEnd));
				
			} 
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
			} 
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
			}
			
			//考虑供货商姓名
			if (StringUtils.isNotEmpty(providername)) {
				filters.add(Filter.like("deliverName", providername));
			}
			//供货商发货
			//filters.add(Filter.le("deliverType", 1));
			//商品
			filters.add(Filter.eq("type", 0));
			List<Filter> orderFilters = new ArrayList<Filter>();
			//订单处于待发货状态
			orderFilters.add(Filter.eq("auditStatus", 2));

			//orderFilters.add(Filter.eq("isDivided", true));
			orderFilters.add(Filter.eq("isShow", false));
			if (StringUtils.isNotEmpty(receivername)) {
				orderFilters.add(Filter.like("receiverName", receivername));
			}
			if (StringUtils.isNotEmpty(receiverphone)) {
				orderFilters.add(Filter.like("receiverPhone", receiverphone));
			}
			
			
			List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, orderFilters, null);
			if (orders.isEmpty()) {
				List<ProviderOrder> results = new ArrayList<ProviderOrder>();
				// 生成Excel表标题
				StringBuffer sb2 = new StringBuffer();
				sb2.append("已拒审订单");
				String fileName = "已拒审订单.xls";  // Excel文件名
				String tableTitle = sb2.toString();   // Excel表标题
				String configFile = "productDeliver.xml"; // 配置文件
				com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
				excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
				return null;
			} else {
				filters.add(Filter.in("businessOrder", orders));
			}
			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(Order.desc("id"));
			List<BusinessOrderItem> list = businessOrderItemService.findList(null, filters, orderList);
			List<ProviderOrder> results = new ArrayList<ProviderOrder>();
			for (BusinessOrderItem item : list) {
				ProviderOrder map = new ProviderOrder();
				map.setProviderName(item.getDeliverName());
				Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
				if(StringUtils.isNotEmpty(specialtyname) && !StringUtils.contains(specialty.getName(), specialtyname)) {
					continue;
				}
				map.setSpecialtyName(specialty.getName());
				SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
				map.setSpecification(specification.getSpecification());
				map.setQuantity(item.getQuantity());
				map.setOrderCode(item.getBusinessOrder().getOrderCode());
				map.setReceiverName(item.getBusinessOrder().getReceiverName());
				map.setReceiverPhone(item.getBusinessOrder().getReceiverPhone());
				map.setReceiverAddress(item.getBusinessOrder().getReceiverAddress());
				map.setCostPrice(businessOrderItemService.getCostPriceOfOrderitem(item).setScale(2,BigDecimal.ROUND_HALF_UP));
				map.setRemark(item.getBusinessOrder().getReceiverRemark());
				
				/** wayne 180813*/
				map.setSalePrice(item.getSalePrice().setScale(2,BigDecimal.ROUND_HALF_UP));	//单价
				
				BusinessOrder bOrder = item.getBusinessOrder();
				BigDecimal payMoney =  item.getSalePrice().multiply(
						bOrder.getPayMoney().divide(bOrder.getShouldPayMoney(),3,BigDecimal.ROUND_HALF_UP));
				map.setPayMoney(payMoney.setScale(2,BigDecimal.ROUND_HALF_UP));	//现金支付
				BigDecimal balanceMoney = item.getSalePrice().multiply(
						bOrder.getBalanceMoney().divide(bOrder.getShouldPayMoney(),3,BigDecimal.ROUND_HALF_UP));
				map.setBalanceMoney(balanceMoney.setScale(2,BigDecimal.ROUND_HALF_UP));	//余额支付
				map.setCouponMoney((item.getSalePrice().subtract(payMoney).subtract(balanceMoney)).setScale(2,BigDecimal.ROUND_HALF_UP));	//一次电子券
				map.setPayTime(bOrder.getPayTime());	//支付时间	
				map.setWeBusinessName(bOrder.getWeBusiness().getName());	//微商姓名
				map.setStoreName(bOrder.getWeBusiness().getNameOfStore());	//所属门店

				results.add(map);
			}
			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			sb2.append("已拒审订单");
			String fileName = "已拒审订单.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "productDeliver.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			return null;
		}
		
		return null;
	}
	
	
	public class ProviderOrder {
		private String providerName;
		private String specialtyName;
		private String specification;
		private Integer quantity;
		private String orderCode;
		private String receiverName;
		private String receiverPhone;
		private String receiverAddress;
		private BigDecimal costPrice;
		private String remark;
		private BigDecimal salePrice;
		private BigDecimal payMoney;
		private BigDecimal balanceMoney;
		private BigDecimal couponMoney;
		private Date payTime;
		private String weBusinessName;
		private String storeName;
		
		public String getProviderName() {
			return providerName;
		}
		public void setProviderName(String providerName) {
			this.providerName = providerName;
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
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public String getOrderCode() {
			return orderCode;
		}
		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}
		public String getReceiverName() {
			return receiverName;
		}
		public void setReceiverName(String receiverName) {
			this.receiverName = receiverName;
		}
		public String getReceiverPhone() {
			return receiverPhone;
		}
		public void setReceiverPhone(String receiverPhone) {
			this.receiverPhone = receiverPhone;
		}
		public String getReceiverAddress() {
			return receiverAddress;
		}
		public void setReceiverAddress(String receiverAddress) {
			this.receiverAddress = receiverAddress;
		}
		public BigDecimal getCostPrice() {
			return costPrice;
		}
		public void setCostPrice(BigDecimal costPrice) {
			this.costPrice = costPrice;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public BigDecimal getSalePrice() {
			return salePrice;
		}
		public void setSalePrice(BigDecimal salePrice) {
			this.salePrice = salePrice;
		}
		public BigDecimal getPayMoney() {
			return payMoney;
		}
		public void setPayMoney(BigDecimal payMoney) {
			this.payMoney = payMoney;
		}
		public BigDecimal getBalanceMoney() {
			return balanceMoney;
		}
		public void setBalanceMoney(BigDecimal balanceMoney) {
			this.balanceMoney = balanceMoney;
		}
		public BigDecimal getCouponMoney() {
			return couponMoney;
		}
		public void setCouponMoney(BigDecimal couponMoney) {
			this.couponMoney = couponMoney;
		}
		public Date getPayTime() {
			return payTime;
		}
		public void setPayTime(Date payTime) {
			this.payTime = payTime;
		}
		public String getWeBusinessName() {
			return weBusinessName;
		}
		public void setWeBusinessName(String weBusinessName) {
			this.weBusinessName = weBusinessName;
		}
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}
		
	}

}
