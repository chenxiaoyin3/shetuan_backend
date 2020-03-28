package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketSubscribeDao;
import com.hongyu.dao.HyVisaPricesDao;
import com.hongyu.entity.HyVisaPrices;
import com.hongyu.service.HyVisaPricesService;

@Service("hyVisaPricesServiceImpl")
public class HyVisaPricesServiceImpl extends BaseServiceImpl<HyVisaPrices, Long> implements HyVisaPricesService{
	
	@Resource(name = "hyVisaPricesDaoImpl")
	HyVisaPricesDao dao;
	
	@Resource(name = "hyVisaPricesDaoImpl")
	public void setBaseDao(HyVisaPricesDao dao){
		super.setBaseDao(dao);		
	}
}
