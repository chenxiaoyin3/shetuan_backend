package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketPriceInboundDao;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.service.HyTicketPriceInboundService;

@Service("hyTicketPriceInboundServiceImpl")
public class HyTicketPriceInboundServiceImpl extends BaseServiceImpl<HyTicketPriceInbound,Long>
    implements HyTicketPriceInboundService {
	@Resource(name = "hyTicketPriceInboundDaoImpl")
	HyTicketPriceInboundDao dao;	
	
	@Resource(name = "hyTicketPriceInboundDaoImpl")
	public void setBaseDao(HyTicketPriceInboundDao dao){
		super.setBaseDao(dao);		
    }
}
