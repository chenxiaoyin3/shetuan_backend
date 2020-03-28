package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketSubscribeDao;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.service.HyTicketSubscribeService;

@Service(value = "hyTicketSubscribeServiceImpl")
public class HyTicketSubscribeServiceImpl extends BaseServiceImpl<HyTicketSubscribe,Long> implements HyTicketSubscribeService {
	@Resource(name = "hyTicketSubscribeDaoImpl")
	HyTicketSubscribeDao dao;
	
	@Resource(name = "hyTicketSubscribeDaoImpl")
	public void setBaseDao(HyTicketSubscribeDao dao){
		super.setBaseDao(dao);		
	}
}
