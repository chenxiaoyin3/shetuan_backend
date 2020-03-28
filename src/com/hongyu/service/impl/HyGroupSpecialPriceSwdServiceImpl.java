package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupSpecialPriceSwdDao;
import com.hongyu.entity.HyGroupSpecialPriceSwd;
import com.hongyu.service.HyGroupSpecialPriceSwdService;

@Service("hyGroupSpecialPriceSwdServiceImpl")
public class HyGroupSpecialPriceSwdServiceImpl extends BaseServiceImpl<HyGroupSpecialPriceSwd, Long> implements HyGroupSpecialPriceSwdService{

	@Resource(name = "hyGroupSpecialPriceSwdDaoImpl")
	HyGroupSpecialPriceSwdDao dao;
	
	@Resource(name = "hyGroupSpecialPriceSwdDaoImpl")
	public void setBaseDao(HyGroupSpecialPriceSwdDao dao){
		super.setBaseDao(dao);		
	}

}
