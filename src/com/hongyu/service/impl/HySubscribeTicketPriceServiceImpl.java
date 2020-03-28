package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySubscribeTicketPriceDao;
import com.hongyu.entity.HySubscribeTicketPrice;
import com.hongyu.service.HySubscribeTicketPriceService;

@Service(value = "hySubscribeTicketPriceServiceImpl")
public class HySubscribeTicketPriceServiceImpl extends BaseServiceImpl<HySubscribeTicketPrice,Long>
    implements HySubscribeTicketPriceService{
	@Resource(name = "hySubscribeTicketPriceDaoImpl")
	HySubscribeTicketPriceDao dao;
	
	@Resource(name = "hySubscribeTicketPriceDaoImpl")
	public void setBaseDao(HySubscribeTicketPriceDao dao){
		super.setBaseDao(dao);		
	}	
}
