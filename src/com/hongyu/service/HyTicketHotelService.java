package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyTicketHotel;

public interface HyTicketHotelService extends BaseService<HyTicketHotel,Long> {

	public HyTicketHotel getTicketHotelByHyOrder(HyOrder order);
}
