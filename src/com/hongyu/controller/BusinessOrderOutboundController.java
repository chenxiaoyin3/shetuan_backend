package com.hongyu.controller;

import static com.hongyu.util.Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_INBOUND;
import static com.hongyu.util.Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.BusinessOrderOutbound;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderOutboundService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.InboundService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.util.DateUtil;

//@RestController
@Controller
@RequestMapping({"/admin/business/outbound/"})
public class BusinessOrderOutboundController {
	  @Resource(name="businessOrderOutboundServiceImpl")
	  BusinessOrderOutboundService businessOrderOutboundService;
	  
	  @Resource(name="businessOrderServiceImpl")
	  BusinessOrderService businessOrderService;
	 
	  @Resource(name="specialtyServiceImpl")
	  SpecialtyService specialtyService;
	  
	  @Resource(name="specialtySpecificationServiceImpl")
	  SpecialtySpecificationService specialtySpecificationService;
	  
	  @Resource(name="hyGroupitemPromotionServiceImpl")
	  HyGroupitemPromotionService hyGroupitemPromotionService;
	  
	  @Resource(name = "inboundServiceImpl")
	  InboundService inboundService;
	  
	  @Resource(name="hyAdminServiceImpl")
	  private HyAdminService hyAdminService;
	  
	  @Resource(name = "specialtyPriceServiceImpl")
	  SpecialtyPriceService specialtyPriceSrv;
	  
	  @RequestMapping({"list"})
	  @ResponseBody
	  public Json list(Pageable pageable, BusinessOrder businessOrder, Integer orderStatus) {
		  Json j = new Json();
		  try{
			  HashMap<String, Object> result = new HashMap<>();
			  List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();

			  List<Order> orders = new ArrayList<>();
			  orders.add(Order.desc("orderTime"));
			  orders.add(Order.asc("orderState"));
			  if(orderStatus == null) {
				  List<Filter> filters1 = new ArrayList<>();
				  filters1.add(Filter.ne("orderState", 0));
				  filters1.add(Filter.ne("orderState", 1));
				  pageable.setFilters(filters1);
			  } else if (orderStatus != null && orderStatus == -1) {
				  List<Filter> filters = new ArrayList<>();
				  filters.add(Filter.gt("orderState", 2));
				  pageable.setFilters(filters);
			  } else {
				  businessOrder.setOrderState(orderStatus);
			  }
			  pageable.setOrders(orders);
			  Page<BusinessOrder> businessOrders = businessOrderService.findPage(pageable, businessOrder);
			  if(businessOrders.getRows().size() > 0) {
				  for(BusinessOrder order : businessOrders.getRows()) {
					  HashMap<String, Object> hm = new HashMap<>();
					  hm.put("id", order.getId());
					  hm.put("orderCode", order.getOrderCode());
					  hm.put("receiverName", order.getReceiverName());
					  hm.put("receiverPhone", order.getReceiverPhone());
					  hm.put("orderTime", order.getOrderTime());
					  hm.put("orderStatus", order.getOrderState());
					  lhm.add(hm);
				  }
			  }
			  result.put("rows", lhm);
			  result.put("pageNumber", pageable.getPage());
			  result.put("pageSize", pageable.getRows());
			  result.put("total", businessOrders.getTotal());
			  j.setMsg("查询成功");
			  j.setSuccess(true);
			  j.setObj(result);
		  } catch (Exception e) {
		      j.setSuccess(false);
		      j.setMsg(e.getMessage());
		  }
		  return j;
	  }
	  
	  @RequestMapping({"outboundDetail"})
	  @ResponseBody
	  public Json outboundDetail(Long id) {
		  Json j = new Json();
		  try{
			  HashMap<String, Object> result = new HashMap<>();
			  BusinessOrder businessOrder = businessOrderService.find(id);
			  if(businessOrder == null) {
				  j.setMsg("订单不存在");
				  j.setSuccess(false);
				  return j;
			  }
			  WechatAccount wechatAccount = businessOrder.getWechatAccount();
			  result.put("orderCode", businessOrder.getOrderCode());
			  result.put("receiverPhone", businessOrder.getReceiverPhone());
			  if(wechatAccount != null) {
				  result.put("wechat", wechatAccount.getWechatName());
			  }
			  result.put("orderTime", businessOrder.getOrderTime());
			  result.put("orderStatus", businessOrder.getOrderState());
			  List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			  Set<BusinessOrderItem> orderItems = businessOrder.getBusinessOrderItems();
			  if(orderItems.size() > 0) {
				  for(BusinessOrderItem orderItem : orderItems) {
					  HashMap<String, Object> hm = new HashMap<>();
					  if(orderItem.getType() == 0) {
						  if(orderItem.getSpecialty() != null && orderItem.getSpecialtySpecification() != null) {
							  Specialty specialty = specialtyService.find(orderItem.getSpecialty());
							  SpecialtySpecification specification = specialtySpecificationService.find(orderItem.getSpecialtySpecification());
							  if(specialty != null && specification != null) {
								  hm.put("specialty", specialty.getName());
								  hm.put("specification", specification.getSpecification());
							  }
						  }
					  } else if (orderItem.getType() == 1) {
						  if(orderItem.getSpecialty() != null) {
							  HyGroupitemPromotion groupItemPromotion = hyGroupitemPromotionService.find(orderItem.getSpecialty());
							  if(groupItemPromotion != null && groupItemPromotion.getPromotionId() != null) {
								  hm.put("specialty", groupItemPromotion.getPromotionId().getPromotionName());
							  }
						  }
					  }
					  hm.put("buyNum", orderItem.getQuantity());
					  lhm.add(hm);
				  }
			  }
			  result.put("data", lhm);
			  j.setMsg("查询成功");
			  j.setSuccess(true);
			  j.setObj(result);
		  } catch (Exception e) {
		      j.setSuccess(false);
		      j.setMsg(e.getMessage());
		  }
		  return j;
	  }
	  
		@Resource(name="businessOrderItemServiceImpl")
		BusinessOrderItemService businessOrderItemService;
		

	  
	  @RequestMapping({"submit"})
	  @ResponseBody
	  public Json submit(Long id, HttpSession session) {
		  Json j = new Json();
		  String username = (String) session.getAttribute(CommonAttributes.Principal);
		  HyAdmin user = hyAdminService.find(username);
		  try {
			  BusinessOrder businessOrder = businessOrderService.find(id);
			  if(businessOrder == null) {
				  j.setMsg("订单不存在");
				  j.setSuccess(false);
				  return j;
			  }
			  businessOrder.setOrderState(BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY);
			  if(businessOrder.getBusinessOrderItems().size() > 0) {

				  BusinessOrderItem item = inboundService.isInboundEnough(new ArrayList(businessOrder.getBusinessOrderItems()));
					if(item!=null){
						//判断库存，如果库存不足
						j.setSuccess(false);
						j.setMsg("库存不足");
						j.setObj(item);
						return j;
					}
					
				  for(BusinessOrderItem orderItem : businessOrder.getBusinessOrderItems()) {
					  
					 
					//更新该订单条目的平台库存
					inboundService.updateOrderItemInbound(orderItem, user);
					  
					orderItem.setOutboundQuantity(orderItem.getQuantity());
					orderItem.setOperator(username);
					  
					  
					businessOrderItemService.update(orderItem);
//					  if(orderItem.getType() == 0) {
//						  if(orderItem.getSpecialtySpecification() != null) {
//							  SpecialtySpecification specification = specialtySpecificationService.find(orderItem.getSpecialtySpecification());
//							  if(specification != null) {
//								 submitHelper(specification, orderItem, username, 0);
//							  }
//						  }
//					  } else if (orderItem.getType() == 1) {
//						  if(orderItem.getSpecialty() != null) {
//							  HyGroupitemPromotion groupItemPromotion = hyGroupitemPromotionService.find(orderItem.getSpecialty());
//							  if(groupItemPromotion == null) {
//								  j.setMsg("优惠活动不存在");
//								  j.setSuccess(false);
//								  return j;
//							  }
//							  Set<HyGroupitemPromotionDetail> details = groupItemPromotion.getHyGroupitemPromotionDetails();
//							  if(details.size() > 0) {
//								  for(HyGroupitemPromotionDetail detail : details) {
//									  SpecialtySpecification specification = detail.getItemSpecificationId();
//									  if(specification != null) {
//										  submitHelper(specification, orderItem, username, detail.getBuyNumber());
//									  }
//								  }
//							  } 
//						  }
//					  }
				  }
			  }
			  j.setMsg("出货成功");
			  j.setSuccess(true);		  
		  } catch (Exception e) {
		      j.setSuccess(false);
		      j.setMsg(e.getMessage());
		  }
		  return j;
	  }
	  
	  
	  @RequestMapping({"outboundedList"})
	  @ResponseBody
	  public Json outboundedList(Long id, Pageable pageable) {
		  Json j = new Json();
		  try {
			  BusinessOrder businessOrder = businessOrderService.find(id);
			  HashMap<String, Object> result = new HashMap<>();
			  List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			  Set<BusinessOrderItem> orderItems = businessOrder.getBusinessOrderItems();
			  List<Filter> filters = new ArrayList<>();
			  filters.add(Filter.in("businessOrderItem", orderItems));
			  List<BusinessOrderOutbound> outbounds = businessOrderOutboundService.findList(null, filters, null);
			  if(outbounds.size() > 0) {
				  for(BusinessOrderOutbound outbound : outbounds) {
					  HashMap<String, Object> hm = new HashMap<>();
					  hm.put("specialty", outbound.getInbound().getSpecification().getSpecialty().getName());
					  hm.put("specification", outbound.getInbound().getSpecification().getSpecification());
					  hm.put("outboundQuantity", outbound.getOutboundQuantity());
					  hm.put("productDate", outbound.getInbound().getProductDate());
					  hm.put("depotCode", outbound.getDepotCode());
					  hm.put("outboundTime", outbound.getOutboundTime());
					  hm.put("operator", outbound.getOperator().getName());
					  lhm.add(hm);
				  }
			  }
			  
			  result.put("rows", lhm);
			  result.put("pageNumber", pageable.getPage());
			  result.put("pageSize", pageable.getRows());
			  result.put("total", outbounds.size());
			  j.setMsg("查询成功");
			  j.setSuccess(true);
			  j.setObj(result);
		  } catch (Exception e) {
		      j.setSuccess(false);
		      j.setMsg(e.getMessage());
		  }
		  return j;
	  }
	  
	  public class OutboundEntity implements Serializable {
		  public String productName;
		  public String specificationName;
		  public String costPrice;
		  public String platformPrice;
		  public String shipFee;
		  public Integer outboundQuantity;
		  public String depotCode;
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getSpecificationName() {
			return specificationName;
		}
		public void setSpecificationName(String specificationName) {
			this.specificationName = specificationName;
		}
		public String getCostPrice() {
			return costPrice;
		}
		public void setCostPrice(String costPrice) {
			this.costPrice = costPrice;
		}
		public String getPlatformPrice() {
			return platformPrice;
		}
		public void setPlatformPrice(String platformPrice) {
			this.platformPrice = platformPrice;
		}
		public String getShipFee() {
			return shipFee;
		}
		public void setShipFee(String shipFee) {
			this.shipFee = shipFee;
		}
		public Integer getOutboundQuantity() {
			return outboundQuantity;
		}
		public void setOutboundQuantity(Integer outboundQuantity) {
			this.outboundQuantity = outboundQuantity;
		}
		public String getDepotCode() {
			return depotCode;
		}
		public void setDepotCode(String depotCode) {
			this.depotCode = depotCode;
		}
		  
		  
	  }
	  
	  
	  @RequestMapping({"/outboundedListByDate"})
	  @ResponseBody
	  public Json outboundedList(@DateTimeFormat(iso=ISO.DATE)Date date, Pageable pageable) {
		  Json j = new Json();
		  try {
			  HashMap<String, Object> result = new HashMap<>();
			  List<Filter> filters = new ArrayList<Filter>();
			  Date start = DateUtil.getStartOfDay(date);
			  Date end = DateUtil.getEndOfDay(date);
			  filters.add(Filter.ge("outboundTime", start));
			  filters.add(Filter.le("outboundTime", end));
			  filters.add(Filter.eq("isValid", true));
			  pageable.setFilters(filters);
			  List<Order> orders = new ArrayList<Order>();
			  orders.add(Order.desc("id"));
			  pageable.setOrders(orders);
			  Page<BusinessOrderOutbound> page = businessOrderOutboundService.findPage(pageable);
			  List<BusinessOrderOutbound> list = page.getRows();
			  List<OutboundEntity> entities = new ArrayList<>();
			  for (BusinessOrderOutbound outbound : list) {
				  OutboundEntity entity = new OutboundEntity();
				  Inbound inbound = outbound.getInbound();
				  SpecialtySpecification specification = inbound.getSpecification();
				  Specialty specialty = specification.getSpecialty();
				  entity.setProductName(specialty.getName());
				  entity.setSpecificationName(specification.getSpecification());
				  List<Filter> priceFilters = new ArrayList<Filter>();
				  priceFilters.add(Filter.eq("specification", specification));
				  priceFilters.add(Filter.eq("isActive", true));
				  List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, priceFilters, null);
				  entity.setPlatformPrice(prices.get(0).getPlatformPrice().toString());
				  entity.setCostPrice(prices.get(0).getCostPrice().toString());
				  entity.setDepotCode(inbound.getDepotCode());
				  entity.setShipFee(outbound.getBusinessOrderItem().getBusinessOrder().getShipFee().toString());
				  entity.setOutboundQuantity(outbound.getOutboundQuantity());
				  entities.add(entity);
			  }
			  
			  result.put("rows", entities);
			  result.put("pageNumber", pageable.getPage());
			  result.put("pageSize", pageable.getRows());
			  result.put("total", entities.size());
			  j.setMsg("查询成功");
			  j.setSuccess(true);
			  j.setObj(result);
		  } catch (Exception e) {
		      j.setSuccess(false);
		      j.setMsg(e.getMessage());
		  }
		  return j;
	  }
	  
	  
	  @RequestMapping("/excel/view")
		public String outboundExcel(@DateTimeFormat(iso=ISO.DATE)Date date,HttpServletRequest request, HttpServletResponse response) {
		  
		  try {
			  List<OutboundEntity> entities = new ArrayList<>();
			  List<Filter> filters = new ArrayList<Filter>();
			  Date start = DateUtil.getStartOfDay(date);
			  Date end = DateUtil.getEndOfDay(date);
			  filters.add(Filter.ge("outboundTime", start));
			  filters.add(Filter.le("outboundTime", end));
			  filters.add(Filter.eq("isValid", true));
			  List<BusinessOrderOutbound> outbounds = businessOrderOutboundService.findList(null, filters, null);
			  for (BusinessOrderOutbound outbound : outbounds) {
				  OutboundEntity entity = new OutboundEntity();
				  Inbound inbound = outbound.getInbound();
				  SpecialtySpecification specification = inbound.getSpecification();
				  Specialty specialty = specification.getSpecialty();
				  entity.setProductName(specialty.getName());
				  entity.setSpecificationName(specification.getSpecification());
				  List<Filter> priceFilters = new ArrayList<Filter>();
				  priceFilters.add(Filter.eq("specification", specification));
				  priceFilters.add(Filter.eq("isActive", true));
				  List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, priceFilters, null);
				  entity.setPlatformPrice(prices.get(0).getPlatformPrice().toString());
				  entity.setCostPrice(prices.get(0).getCostPrice().toString());
				  entity.setDepotCode(inbound.getDepotCode());
				  entity.setShipFee(outbound.getBusinessOrderItem().getBusinessOrder().getShipFee().toString());
				  entity.setOutboundQuantity(outbound.getOutboundQuantity());
				  entities.add(entity);
			  }
			  
			// 生成Excel表标题
			  DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			  StringBuilder sb2 = new StringBuilder();
			  sb2.append("当日出库商品报表-");
			  sb2.append(sdf.format(date));
			  String tableTitle = sb2.toString();
			  String fileName = sb2.toString()+".xls";
			  String configFile = "businessOutbound.xml"; // 配置文件
			  
			  com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			  excelCon.export2Excel(request, response, entities, fileName, tableTitle, configFile);
		  } catch (Exception e) {
			  e.printStackTrace();
			  return null;
		  }
		  return null;
	  }
	  
	  
	  
	  
	  
//	  public void submitHelper(SpecialtySpecification specification, BusinessOrderItem orderItem, String username, int number) {
//		  List<Filter> filters = new ArrayList<>();
//		  filters.add(Filter.eq("specification", specification));
//		  filters.add(Filter.gt("inboundNumber", 0));
//		  List<Order> orders = new ArrayList<>();
//		  orders.add(Order.asc("productDate"));
//		  List<Inbound> inbounds = inboundService.findList(null, filters, orders);
//		  int buyNumber = orderItem.getType() == 0 ? orderItem.getQuantity() : orderItem.getQuantity() * number ;
//		  for(Inbound inbound : inbounds) {
//			  if(buyNumber == 0) break;
//			  BusinessOrderOutbound orderOutbound = new BusinessOrderOutbound();
//			  orderOutbound.setBusinessOrderItem(orderItem);
//			  orderOutbound.setInbound(inbound);
//			  orderOutbound.setDepotCode(inbound.getDepotCode());
//			  if(inbound.getInboundNumber() >= buyNumber) {
//				  inbound.setInboundNumber(inbound.getInboundNumber() - buyNumber);
//				  inbound.setSaleNumber(inbound.getSaleNumber() + buyNumber);
//				  orderOutbound.setOutboundQuantity(buyNumber);
//				  buyNumber = 0;
//			  } else {
//				  buyNumber = buyNumber - inbound.getInboundNumber();
//				  orderOutbound.setOutboundQuantity(inbound.getInboundNumber());
//				  inbound.setSaleNumber(inbound.getSaleNumber() + inbound.getInboundNumber());
//				  inbound.setInboundNumber(0);
//			  }
//			  inbound.setUpdateTime(new Date());
//			  inboundService.update(inbound);  
//			  orderOutbound.setOutboundTime(new Date());
//			  orderOutbound.setOperator(hyAdminService.find(username));
//			  businessOrderOutboundService.save(orderOutbound);
//		  }
//	  }
}
