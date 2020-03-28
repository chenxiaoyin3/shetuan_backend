package com.hongyu.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.grain.service.BaseService;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.SpecialtySpecification;

public interface BusinessOrderItemService extends BaseService<BusinessOrderItem, Long>{
	public List<BusinessOrderItem> getItemsByBusinessOrder(BusinessOrder bOrder);
	
	public List<Map<String, Object>> getRefundItemMapList(BusinessOrder order);
	
	public String getSpecialtyName(BusinessOrderItem bItem);
	
	public String getSpecialtyCode(BusinessOrderItem bItem);
	
	public String getSpecificationName(BusinessOrderItem bItem);
	
	public List<BusinessOrderItem> getItemsOfSpecificationInDuration(SpecialtySpecification specification, Date start, Date end, Integer deliverType);
	
	public BigDecimal getCostPriceOfOrderitem(BusinessOrderItem item) throws Exception;
}
