package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyTicketInbound;

public interface HyTicketInboundService extends BaseService<HyTicketInbound,Long> {
	
	void recoverTicketInboundByTicketOrder(HyOrder order) throws Exception;
	
	void recoverTicketInboundByTicketOrderItem(HyOrderItem item,Integer returnQuantity)throws Exception;

}
