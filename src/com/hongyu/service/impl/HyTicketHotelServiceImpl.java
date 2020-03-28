package com.hongyu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketHotelDao;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.service.HyTicketHotelService;

@Service("hyTicketHotelServiceImpl")
public class HyTicketHotelServiceImpl extends BaseServiceImpl<HyTicketHotel,Long> implements HyTicketHotelService {
	@Resource(name = "hyTicketHotelDaoImpl")
	HyTicketHotelDao dao;
	
	@Resource(name = "hyTicketHotelDaoImpl")
	public void setBaseDao(HyTicketHotelDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public HyTicketHotel getTicketHotelByHyOrder(HyOrder order) {
		// TODO Auto-generated method stub
		if(order.getType()!=3) {
			return null;
		}
		List<HyOrderItem> orderItems = order.getOrderItems();
		if(orderItems==null || orderItems.isEmpty()) {
			return null;
		}
		HyOrderItem orderItem = orderItems.get(0);
		HyTicketHotel hyTicketHotel = this.find(orderItem.getProductId());
		if(hyTicketHotel==null) {
			return null;
		}
		return hyTicketHotel;
	}	
}
