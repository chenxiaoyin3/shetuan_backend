package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PriceTypeDao;
import com.hongyu.entity.PriceType;
import com.hongyu.service.PriceTypeService;
@Service(value = "priceTypeServiceImpl")
public class PriceTypeServiceImpl extends BaseServiceImpl<PriceType, Long> 
implements PriceTypeService {

	
	@Resource(name = "priceTypeDaoImpl")
	PriceTypeDao dao;
	
	@Resource(name = "priceTypeDaoImpl")
	public void setBaseDao(PriceTypeDao dao){
		super.setBaseDao(dao);		
	}	
	
}