package com.hongyu.service;

import java.util.List;
import java.util.Map;

import com.grain.service.BaseService;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.Provider;
import com.hongyu.entity.SpecialtySpecification;

public interface SpecialtySpecificationService extends BaseService<SpecialtySpecification, Long> {
	
	SpecialtySpecification getParentSpecification(SpecialtySpecification s);
	
	List<SpecialtySpecification> getSpecificationsOfProvider(Provider provider);
	
	Boolean isBaseInboundEnough(List<Map<String, Object>> orderItems)throws Exception;
	
	void updateBaseInboundAndHasSold(BusinessOrderItem orderItem,Boolean isSale)throws Exception;
 
}
