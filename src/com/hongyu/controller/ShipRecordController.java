package com.hongyu.controller;

import static com.hongyu.util.Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.HyPromotionService;
import com.hongyu.service.ShipService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;

@RestController
@RequestMapping({"/admin/business/shiprecord/"})
public class ShipRecordController {
	
	  @Resource(name="shipServiceImpl")
	  ShipService shipService;
	  
	  @Resource(name="businessOrderServiceImpl")
	  BusinessOrderService businessOrderService;
	  
	  @Resource(name="businessOrderItemServiceImpl")
	  BusinessOrderItemService businessOrderItemService;
	  
	  @Resource(name="specialtyServiceImpl")
	  SpecialtyService specialtyService;
	  
	  @Resource(name="hyPromotionServiceImpl")
	  HyPromotionService promotionService;
	  
	  @Resource(name="specialtySpecificationServiceImpl")
	  SpecialtySpecificationService specialtySpecificationService;
	  
	  @RequestMapping({"list"})
	  public Json list(Pageable pageable, BusinessOrder businessOrder) {
		  Json j = new Json();
		  try{
			  HashMap<String, Object> result = new HashMap<>();
			  List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			  List<Filter> filters = new ArrayList<>();
			  filters.add(Filter.gt("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY));
			  List<Order> orders = new ArrayList<>();
			  orders.add(Order.desc("deliveryTime"));
			  pageable.setOrders(orders);
			  pageable.setFilters(filters);
			  Page<BusinessOrder> businessOrders = businessOrderService.findPage(pageable, businessOrder);
			  if(businessOrders.getTotal() == 0){
				  j.setMsg("所查询数据不存在");
				  j.setSuccess(false);
				  return j;
			  }
			  for(BusinessOrder order : businessOrders.getRows()) {
				  HashMap<String, Object> hm = new HashMap<>();
				  hm.put("orderCode", order.getOrderCode());
				  hm.put("orderState", order.getOrderState());
//				  if(order.getShips().size() > 0 && order.getShips().get(0) != null & order.getShips().get(0).getDeliverOperator() != null){
//					  hm.put("deliverOperator", order.getShips().get(0).getDeliverOperator().getName());  
//				  }
				  if (order.getShip() != null && order.getShip().getDeliverOperator() != null) {
					  hm.put("deliverOperator", order.getShip().getDeliverOperator().getName());
				  }
				  
				  hm.put("receiverName", order.getReceiverName());
				  hm.put("receiverPhone", order.getReceiverPhone());
				  hm.put("receiverAddress", order.getReceiverAddress());
				  hm.put("receiveType", order.getReceiveType());
				  hm.put("receiverRemark", order.getReceiverRemark());
				  lhm.add(hm);
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
}
