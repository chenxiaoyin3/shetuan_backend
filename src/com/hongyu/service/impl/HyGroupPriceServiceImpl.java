package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupPriceDao;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.service.HyGroupPriceService;
@Service(value = "hyGroupPriceServiceImpl")
public class HyGroupPriceServiceImpl extends BaseServiceImpl<HyGroupPrice, Long> implements HyGroupPriceService {
	@Resource(name = "hyGroupPriceDaoImpl")
	HyGroupPriceDao dao;
	
	@Resource(name = "hyGroupPriceDaoImpl")
	public void setBaseDao(HyGroupPriceDao dao){
		super.setBaseDao(dao);		
	}
}
