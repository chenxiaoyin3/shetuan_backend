package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketRefundDao;
import com.hongyu.entity.HyTicketRefund;
import com.hongyu.service.HyTicketRefundService;

@Service("hyTicketRefundServiceImpl")
public class HyTicketRefundServiceImpl extends BaseServiceImpl<HyTicketRefund,Long> implements HyTicketRefundService {
	@Resource(name = "hyTicketRefundDaoImpl")
	HyTicketRefundDao dao;	
	
	@Resource(name = "hyTicketRefundDaoImpl")
	public void setBaseDao(HyTicketRefundDao dao){
		super.setBaseDao(dao);		
    }
}
