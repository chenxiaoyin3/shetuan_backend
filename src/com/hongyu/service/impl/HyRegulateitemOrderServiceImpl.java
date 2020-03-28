package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyRegulateitemOrderDao;
import com.hongyu.entity.HyRegulateitemOrder;
import com.hongyu.service.HyRegulateitemOrderService;

@Service(value = "hyRegulateitemOrderServiceImpl")
public class HyRegulateitemOrderServiceImpl extends BaseServiceImpl<HyRegulateitemOrder,Long>
    implements HyRegulateitemOrderService{
	@Resource(name = "hyRegulateitemOrderDaoImpl")
	HyRegulateitemOrderDao dao;
	
	@Resource(name = "hyRegulateitemOrderDaoImpl")
	public void setBaseDao(HyRegulateitemOrderDao dao){
		super.setBaseDao(dao);		
	}
}
