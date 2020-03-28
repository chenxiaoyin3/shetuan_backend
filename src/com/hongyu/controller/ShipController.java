package com.hongyu.controller;

import static com.hongyu.util.Constants.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;

@Controller
@RequestMapping({"/admin/business/ship/"})
public class ShipController {
	
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
	  
	  @Resource(name="hyAdminServiceImpl")
	  private HyAdminService hyAdminService;
	  
	  /**
	   * 待发货订单列表
	   * @param pageable
	   * @return
	   */
	  @RequestMapping({"list"})
	  @ResponseBody
	  public Json list(Pageable pageable, BusinessOrder businessOrder) {
		  Json j = new Json();
		  try{
			  HashMap<String, Object> result = new HashMap<>();
			  List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			  businessOrder.setOrderState(BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY);
			  List<Order> orders = new ArrayList<>();
			  orders.add(Order.desc("id"));
			  pageable.setOrders(orders);
			  Page<BusinessOrder> businessOrders = businessOrderService.findPage(pageable, businessOrder);
			  if(businessOrders.getTotal() > 0) {
				  for(BusinessOrder order : businessOrders.getRows()) {
					  HashMap<String, Object> hm = new HashMap<>();
					  hm.put("id", order.getId());
					  hm.put("orderCode", order.getOrderCode());
					  hm.put("receiverName", order.getReceiverName());
					  hm.put("receiverPhone", order.getReceiverPhone());
					  hm.put("receiverAddress", order.getReceiverAddress());
					  hm.put("receiveType", order.getReceiveType());
					  hm.put("receiverRemark", order.getReceiverRemark());
					  //0是平台发货，1是供应商发货
					  hm.put("isDivided", order.getIsDivided());
					  if (order.getOrderState().equals(Integer.valueOf(1))) {
						  hm.put("isAudit", false);
					  } else {
						  hm.put("isAudit", true);
					  }
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

	  @Autowired
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	  /**
	   * 发货详情
	   * @param pageable
	   * @param orderId
	   * @return
	   */
	  @RequestMapping({"detail"})
	  @ResponseBody
	  public Json detail(Pageable pageable, Long orderId) {
		  Json j = new Json();
		  try{
			  HashMap<String, Object> result = new HashMap<>();
			  List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			  BusinessOrder businessOrder = businessOrderService.find(orderId);
			  if(businessOrder == null) {
				  j.setMsg("查询失败");
				  j.setSuccess(false);
				  return j;
			  }
			  BusinessOrderItem orderItem = new BusinessOrderItem();
			  orderItem.setBusinessOrder(businessOrder);
			  Page<BusinessOrderItem> items = businessOrderItemService.findPage(pageable, orderItem);
			  for(BusinessOrderItem item : items.getRows()) {
				  HashMap<String, Object> hm = new HashMap<>();
				  if(item.getType() == 0) {
					  Specialty specialty = specialtyService.find(item.getSpecialty());
					  SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());
					  
					  if(specialty != null) {
						  hm.put("name", specialty.getName());
						  hm.put("specification", specification.getSpecification());
					  }
				  } else if(item.getType() == 1) {
					  HyGroupitemPromotion hyGroupitemPromotion = hyGroupitemPromotionServiceImpl
						  .find(item.getSpecialty());
					  if(hyGroupitemPromotion != null) {
						  hm.put("name", hyGroupitemPromotion.getPromotionId().getPromotionName());
					  }
				  }
				  hm.put("quantity", item.getQuantity());
				  lhm.add(hm);
			  }
			  result.put("rows", lhm);
			  result.put("pageNumber", pageable.getPage());
			  result.put("pageSize", pageable.getRows());
			  result.put("total", items.getTotal());
			  j.setMsg("查询详情成功");
			  j.setSuccess(true);
			  j.setObj(result);
		  } catch (Exception e) {
		      j.setSuccess(false);
		      j.setMsg(e.getMessage());
		  }
		  return j;
	  }
	  
	  /**
	   * 发货
	   * @param ship
	   * @param order
	   * @return
	   */
	  @RequestMapping({"add"})
	  @ResponseBody
	  public Json add(Ship ship, Long order, HttpSession session) {
		  Json j = new Json();
		  /**
		   * 获取当前用户
		   */
		  String username = (String) session.getAttribute(CommonAttributes.Principal);
		  HyAdmin admin = hyAdminService.find(username);
		  try{
			  BusinessOrder businessOrder = businessOrderService.find(order);
			  businessOrder.setOrderState(BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE);
			  businessOrder.setDeliveryTime(new Date());
			  
			  ship.setDeliverOperator(admin);
			  ship.setDeliveror(username);
			  ship.setOrderId(businessOrder);
			  ship.setType(0);
			  
			  businessOrder.setShip(ship);
			  businessOrderService.update(businessOrder);
			  j.setMsg("发货成功");
			  j.setSuccess(true);
		  } catch (Exception e) {
			  j.setSuccess(false);
			  j.setMsg(e.getMessage());
		  }
		  return j;
	  }
	  
	  
	  
//	  @RequestMapping({"/finishship"})
//	  @ResponseBody
//	  public Json finishShip(Long order) {
//		  Json j = new Json();
//		  try{
//			  BusinessOrder businessOrder = businessOrderService.find(order);
//			  businessOrder.setOrderState(BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE);
//			  businessOrderService.update(businessOrder);
//			  j.setMsg("结束发货成功");
//			  j.setSuccess(true);
//		  } catch (Exception e) {
//			  j.setSuccess(false);
//			  j.setMsg(e.getMessage());
//		  }
//		  return j;
//	  }
	  
	  @RequestMapping({"/shippedlist"})
	  @ResponseBody
	  public Json hasShippedOrder(BusinessOrder query, String deliveror, Integer status, Pageable pageable) {
		  //status 不传表示查找待收货和已发货的, 传0表示待收货的，传1表示已收货的
		  Json json =new Json();
		  
		  try {
			  //已发货物流按照订单号降序
			  List<Order> orders = new ArrayList<>();
			  orders.add(Order.desc("id"));
			  pageable.setOrders(orders);
			if (deliveror != null) {
				List<Filter> shipfs = new ArrayList<Filter>();
				shipfs.add(Filter.like("deliveror", deliveror));
//				List<HyAdmin> admins = hyAdminService.findList(null, fs, null);
				
				List<Ship> ships = shipService.findList(null, shipfs, null);
//				Set<BusinessOrder> orders = new HashSet<BusinessOrder>();
//				for (Ship ship : ships) {
//					orders.add(ship.getOrderId());
//				}
				List<Filter> filters=FilterUtil.getInstance().getFilter(query);
				if(status == null) {
					filters.add(Filter.ne("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));
					filters.add(Filter.isNotNull("ship"));
				} else if (status == 0) {
					filters.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));
				} else if (status == 1) {
					filters.add(Filter.isNotNull("ship"));
					filters.add(Filter.ne("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));

				} else {
					json.setSuccess(false);
					json.setMsg("参数不合法");
				}
				filters.add(Filter.in("ship", ships));
				pageable.setFilters(filters);
//				List<BusinessOrder> businessorders = businessOrderService.findList(null, filters, null);
//				List<BusinessOrder> total = new ArrayList<BusinessOrder>();
//				
//				for (BusinessOrder order : businessorders) {
//					if (orders.contains(order)) {
//						total.add(order);
//					}
//				}
//				
//				int pageSize = pageable.getRows();
//				int pageNumber = pageable.getPage();
//				
//				List<BusinessOrder> sublist = total.subList((pageNumber-1)*pageSize, 
//						pageNumber*pageSize>total.size()?total.size():pageNumber*pageSize);
//				List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
//				
//				for (BusinessOrder order : sublist) {
//					
//				}
				
//				Page<BusinessOrder> page = new Page<>(total.subList((pageNumber-1)*pageSize, 
//						pageNumber*pageSize>total.size()?total.size():pageNumber*pageSize), total.size(), pageable);
				
				Page<BusinessOrder> page = businessOrderService.findPage(pageable);
				List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
				for (BusinessOrder o : page.getRows()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", o.getId());
					map.put("orderCode", o.getOrderCode());
					map.put("deliveror", o.getShip().getDeliveror());
					map.put("deliveryTime", o.getDeliveryTime());
					map.put("shipCompany", o.getShip().getShipCompany());
					map.put("shipCode", o.getShip().getShipCode());
					map.put("receiverName", o.getReceiverName());
					map.put("receiverPhone", o.getReceiverPhone());
					map.put("receiverAddress", o.getReceiverAddress());
					map.put("orderState", o.getOrderState());
					maps.add(map);
				}
				Page<Map<String, Object>> result = new Page<>(maps, page.getTotal(), pageable);
				
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(result);
			} else {
				List<Filter> filters=FilterUtil.getInstance().getFilter(query);
				if(status == null) {
					filters.add(Filter.ne("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));
					filters.add(Filter.isNotNull("ship"));
				} else if (status == 0) {
					filters.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));
				} else if (status == 1) {
					filters.add(Filter.ne("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));
					filters.add(Filter.isNotNull("ship"));
				} else {
					json.setSuccess(false);
					json.setMsg("参数不合法");
				}
				
				pageable.setFilters(filters);
				Page<BusinessOrder> page = businessOrderService.findPage(pageable);
				
				List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
				for (BusinessOrder o : page.getRows()) {
					if(o.getShip()==null) {
						continue;
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", o.getId());
					map.put("orderCode", o.getOrderCode());
					map.put("deliveror", o.getShip().getDeliverOperator().getName());
					map.put("deliveryTime", o.getDeliveryTime());
					map.put("shipCompany", o.getShip().getShipCompany());
					map.put("shipCode", o.getShip().getShipCode());
					map.put("receiverName", o.getReceiverName());
					map.put("receiverPhone", o.getReceiverPhone());
					map.put("receiverAddress", o.getReceiverAddress());
					map.put("orderState", o.getOrderState());
					maps.add(map);
				}
				Page<Map<String, Object>> result = new Page<>(maps, page.getTotal(), pageable);
				
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(result);
			}
					
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		    
		  return json;
	  }
}
