package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.HyTicketInboundDao;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.service.HyTicketInboundService;

@Service("hyTicketInboundServiceImpl")
public class HyTicketInboundServiceImpl extends BaseServiceImpl<HyTicketInbound,Long> implements HyTicketInboundService {
	@Resource(name = "hyTicketInboundDaoImpl")
	HyTicketInboundDao dao;	
	
	@Resource(name = "hyTicketInboundDaoImpl")
	public void setBaseDao(HyTicketInboundDao dao){
		super.setBaseDao(dao);		
    }

	@Override
	public void recoverTicketInboundByTicketOrder(HyOrder order) throws Exception{
		// TODO Auto-generated method stub
		List<HyOrderItem> items = order.getOrderItems();
		
		for(HyOrderItem item:items) {
			//恢复库存
			recoverTicketInboundByTicketOrderItem(item, item.getNumber());
		}
		
	}

	@Override
	public void recoverTicketInboundByTicketOrderItem(HyOrderItem item, Integer returnQuantity) throws Exception {
		// TODO Auto-generated method stub
		//恢复库存
		List<Filter> inboundFilters=new ArrayList<>();
		inboundFilters.add(Filter.eq("priceInboundId", item.getPriceId()));
		//inboundFilters.add(Filter.eq("type", 1));
		inboundFilters.add(Filter.ge("day", item.getStartDate()));
		inboundFilters.add(Filter.le("day", item.getEndDate()));
		List<HyTicketInbound> ticketInbounds=findList(null,inboundFilters,null);
		
		if(ticketInbounds.isEmpty()) {
			throw new Exception("该产品没有对应的库存");
		}

		synchronized(ticketInbounds){
			//恢复库存
			for(HyTicketInbound hyTicketInbound:ticketInbounds) {
				hyTicketInbound.setInventory(hyTicketInbound.getInventory()+returnQuantity);
				update(hyTicketInbound);
			}
		}
		
		
	}
}
