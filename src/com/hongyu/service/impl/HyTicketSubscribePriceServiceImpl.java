package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketSubscribePriceDao;
import com.hongyu.entity.HyTicketSubscribePrice;
import com.hongyu.service.HyTicketSubscribePriceService;

@Service("hyTicketSubscribePriceServiceImpl")
public class HyTicketSubscribePriceServiceImpl extends BaseServiceImpl<HyTicketSubscribePrice,Long> implements HyTicketSubscribePriceService {
	@Resource(name = "hyTicketSubscribePriceDaoImpl")
	HyTicketSubscribePriceDao dao;
	
	@Resource(name = "hyTicketSubscribePriceDaoImpl")
	public void setBaseDao(HyTicketSubscribePriceDao dao){
		super.setBaseDao(dao);		
	}
}
