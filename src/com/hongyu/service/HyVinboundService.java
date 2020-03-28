package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyVinbound;

public interface HyVinboundService extends BaseService<HyVinbound, Long> {
	
	Integer abstractVinbound(Integer total,HyVinbound vinbound);
	void updateOrderItemVinbound(BusinessOrderItem item);
	void returnOrderItemVinbound(BusinessOrderItem item);

}
