package com.hongyu.service;

import java.util.List;
import java.util.Map;

import com.grain.service.BaseService;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;

public interface BusinessOrderService extends BaseService<BusinessOrder, Long> {
	
	Map<String, Object> getOrderListItemMap(BusinessOrder order);
	
	BusinessOrder createSubOrder(BusinessOrder order,String deliverName,List<BusinessOrderItem> orderItems);
	
	Boolean havePromotions(BusinessOrder order);
	
	String getOrderCode();
}
