package com.hongyu.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.hongyu.Order.Direction;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/business/product_deliver")
public class ProductDeliverController {
	
	
	@Resource(name="businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;
	
	@Autowired
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	
	@Autowired
	SpecialtyPriceService specialtyPriceServiceImpl;
	
	//查询供应商采购单
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
			//filters.add(Filter.eq("type", 0));//组合优惠也要能供应商发货，这里不筛选。
			
			//订单处于待发货状态
			orderFilters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY));
			//供货商发货
			orderFilters.add(Filter.eq("isDivided", true));
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
			//2019-08-05改查page又过滤的问题，来不及考虑性能。
			List<Order> paixuOrders = new ArrayList<>();//paixu拼音排序
			paixuOrders.add(Order.desc("id"));
			List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null, filters, paixuOrders);
			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
			for (BusinessOrderItem item :businessOrderItems) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("providerName", item.getDeliverName());
				//如果是普通产品
				if(item.getType()==0){
					Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
					if(StringUtils.isNotEmpty(specialtyname) && !StringUtils.contains(specialty.getName(), specialtyname)) {
						continue;
					}
					map.put("specialtyName", specialty.getName());
					SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
					map.put("specification", specification.getSpecification());
				}else {//是组合优惠
					HyGroupitemPromotion groupitemPromotion =
							hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
					map.put("specialtyName", groupitemPromotion.getPromotionId().getPromotionName());
					map.put("specification", "");
				}
				map.put("quantity", item.getQuantity());
				map.put("orderCode", item.getBusinessOrder().getOrderCode());
				map.put("receiverName", item.getBusinessOrder().getReceiverName());
				map.put("receiverPhone", item.getBusinessOrder().getReceiverPhone());
				map.put("receiverAddress", item.getBusinessOrder().getReceiverAddress());
				//2019-08-07商贸采购部供应商待发货几个值改成总的。四舍五入保留两位小数。
				BigDecimal quantity = new BigDecimal(item.getQuantity());
				if(item.getType()==0){//普通产品调用getCostPriceOfOrderitem才正常
					map.put("costPrice", businessOrderItemService.getCostPriceOfOrderitem(item).multiply(quantity).setScale(2,BigDecimal.ROUND_HALF_UP));//总成本
				}else {	//如果是组合产品
					//获取组合优惠活动对象
					HyGroupitemPromotion groupitemPromotion =
						hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
					BigDecimal costPrice = BigDecimal.ZERO; //总成本价
					for(HyGroupitemPromotionDetail detail : groupitemPromotion.getHyGroupitemPromotionDetails()) {
						SpecialtySpecification s = detail.getItemSpecificationId();
						//找价格
						//先去价格变化表里面查
						List<Filter> priceFilters = new ArrayList<Filter>();
						priceFilters.add(Filter.eq("specification", s));
						priceFilters.add(Filter.eq("isActive", true));
						List<SpecialtyPrice> specialtyPrices = specialtyPriceServiceImpl.findList(null, priceFilters, null);

						if (specialtyPrices == null || specialtyPrices.isEmpty()) {
							continue;
						}
						SpecialtyPrice price = specialtyPrices.get(0);
						costPrice = costPrice.add(price.getCostPrice());						
					}
					map.put("costPrice",costPrice.multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,BigDecimal.ROUND_HALF_UP)); //订单条目总的成本
				}
				/** wayne 180813*/
				map.put("salePrice", item.getSalePrice().setScale(2,BigDecimal.ROUND_HALF_UP));	//单价
				map.put("totalSalePrice", item.getSalePrice().multiply(quantity).setScale(2,BigDecimal.ROUND_HALF_UP));	//总的叫应付金额
				
				BusinessOrder bOrder = item.getBusinessOrder();
				BigDecimal payMoney =  item.getSalePrice().multiply(
						bOrder.getPayMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.put("payMoney",payMoney);	//现金支付
				map.put("payMoney",payMoney.multiply(quantity).setScale(2,BigDecimal.ROUND_HALF_UP));	//现金支付
				BigDecimal balanceMoney = item.getSalePrice().multiply(
						bOrder.getBalanceMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.put("balanceMoney", balanceMoney);	//余额支付
				map.put("balanceMoney", balanceMoney.multiply(quantity).setScale(2,BigDecimal.ROUND_HALF_UP));	//余额支付
//				map.put("couponMoney", item.getSalePrice().subtract(payMoney).subtract(balanceMoney));	//一次电子券
				map.put("couponMoney", (item.getSalePrice().subtract(payMoney).subtract(balanceMoney)).multiply(quantity).setScale(2,BigDecimal.ROUND_HALF_UP));	//一次电子券
				map.put("payTime", bOrder.getPayTime());	//支付时间	
				map.put("weBusinessName", bOrder.getWeBusiness().getName());	//微商姓名
				map.put("storeName", bOrder.getWeBusiness().getNameOfStore());	//所属门店

				map.put("remark", item.getBusinessOrder().getReceiverRemark());
				maps.add(map);
			}
			Page<Map<String, Object>> mapPage = null;
			if (maps.size() == 0) {
				mapPage = new Page<>(maps, 0, pageable);
			} else {
				Integer pageIndex = pageable.getPage();
				Integer pageSize = pageable.getRows();
				int endIndex = maps.size()<pageIndex*pageSize?maps.size():pageIndex*pageSize;
				mapPage = new Page<Map<String, Object>>(maps.subList((pageIndex-1)*pageSize, endIndex), maps.size(), pageable);
			}			
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
	
	//生成供应商采购单excel表格
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
				startdate = yesterDayStart;
				enddate = yesterDayEnd;
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
			filters.add(Filter.le("deliverType", 1));
			//商品
			//filters.add(Filter.eq("type", 0));//组合优惠也要能供应商发货，这里不筛选。
			List<Filter> orderFilters = new ArrayList<Filter>();
			//订单处于待发货状态
			orderFilters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY));
			orderFilters.add(Filter.eq("isDivided", true));
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
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				sb2.append("待发货订单_"+format.format(startdate)+"-"+format.format(enddate));
				String fileName = "待发货订单_"+format.format(startdate)+"-"+format.format(enddate)+".xls";  // Excel文件名
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
				//如果是普通产品
				if(item.getType()==0){
					Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
					if(StringUtils.isNotEmpty(specialtyname) && !StringUtils.contains(specialty.getName(), specialtyname)) {
						continue;
					}
					map.setSpecialtyName(specialty.getName());
					SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
					map.setSpecification(specification.getSpecification());
				}else {
					HyGroupitemPromotion groupitemPromotion =
			                 hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
					 map.setSpecialtyName(groupitemPromotion.getPromotionId().getPromotionName());
					 map.setSpecification("");
				}
				map.setQuantity(item.getQuantity());
				map.setOrderCode(item.getBusinessOrder().getOrderCode());
				map.setReceiverName(item.getBusinessOrder().getReceiverName());
				map.setReceiverPhone(item.getBusinessOrder().getReceiverPhone());
				map.setReceiverAddress(item.getBusinessOrder().getReceiverAddress());
				map.setRemark(item.getBusinessOrder().getReceiverRemark());
				//2019-08-07商贸采购部供应商待发货几个值改成总的。四舍五入保留两位小数。
				BigDecimal quantity = new BigDecimal(item.getQuantity());

				
				if(item.getType()==0){//普通产品调用getCostPriceOfOrderitem才正常
					map.setCostPrice(businessOrderItemService.getCostPriceOfOrderitem(item).multiply(quantity).setScale(2,RoundingMode.HALF_UP));//总成本
				}else {	//如果是组合产品
					//获取组合优惠活动对象
					HyGroupitemPromotion groupitemPromotion =
						hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
					BigDecimal costPrice = BigDecimal.ZERO; //总成本价
					for(HyGroupitemPromotionDetail detail : groupitemPromotion.getHyGroupitemPromotionDetails()) {
						SpecialtySpecification s = detail.getItemSpecificationId();
						//找价格
						//先去价格变化表里面查
						List<Filter> priceFilters = new ArrayList<Filter>();
						priceFilters.add(Filter.eq("specification", s));
						priceFilters.add(Filter.eq("isActive", true));
						List<SpecialtyPrice> specialtyPrices = specialtyPriceServiceImpl.findList(null, priceFilters, null);

						if (specialtyPrices == null || specialtyPrices.isEmpty()) {
							continue;
						}
						SpecialtyPrice price = specialtyPrices.get(0);
						costPrice = costPrice.add(price.getCostPrice());						
					}
					map.setCostPrice(costPrice.multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP)); //订单条目总的成本
				}
				
				/** wayne 180813*/
				map.setSalePrice(item.getSalePrice().setScale(2,RoundingMode.HALF_UP));	//单价
				map.setTotalSalePrice(item.getSalePrice().multiply(quantity).setScale(2,RoundingMode.HALF_UP));	//总的，应支付金额
				
				BusinessOrder bOrder = item.getBusinessOrder();
				BigDecimal payMoney =  item.getSalePrice().multiply(
						bOrder.getPayMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.setPayMoney(payMoney.setScale(2,RoundingMode.HALF_UP));	//现金支付
				map.setPayMoney(payMoney.multiply(quantity).setScale(2,RoundingMode.HALF_UP));	//现金支付
				BigDecimal balanceMoney = item.getSalePrice().multiply(
						bOrder.getBalanceMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.setBalanceMoney(balanceMoney.setScale(2,RoundingMode.HALF_UP));	//余额支付
				map.setBalanceMoney(balanceMoney.multiply(quantity).setScale(2,RoundingMode.HALF_UP));	//余额支付
//				map.setCouponMoney((item.getSalePrice().subtract(payMoney).subtract(balanceMoney)).setScale(2,RoundingMode.HALF_UP));	//一次电子券
				map.setCouponMoney((item.getSalePrice().subtract(payMoney).subtract(balanceMoney)).multiply(quantity).setScale(2,RoundingMode.HALF_UP));	//一次电子券
				map.setPayTime(bOrder.getPayTime());	//支付时间	
				map.setWeBusinessName(bOrder.getWeBusiness().getName());	//微商姓名
				map.setStoreName(bOrder.getWeBusiness().getNameOfStore());	//所属门店

				results.add(map);
			}
			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			sb2.append("待发货订单_"+format.format(startdate)+"-"+format.format(enddate));
			String fileName = "待发货订单_"+format.format(startdate)+"-"+format.format(enddate)+".xls";  // Excel文件名
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
	
	
	public static class ProviderOrder {
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
		private BigDecimal salePrice;//单价
		private BigDecimal totalSalePrice;//应支付金额
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
		public BigDecimal getTotalSalePrice() {
			return totalSalePrice;
		}
		public void setTotalSalePrice(BigDecimal totalSalePrice) {
			this.totalSalePrice = totalSalePrice;
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
