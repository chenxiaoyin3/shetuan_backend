package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupPriceSwdDao;
import com.hongyu.entity.HyGroupPriceSwd;
import com.hongyu.service.HyGroupPriceSwdService;

@Service("hyGroupPriceSwdServiceImpl")
public class HyGroupPriceSwdServiceImpl extends BaseServiceImpl<HyGroupPriceSwd, Long> implements HyGroupPriceSwdService{

	@Resource(name = "hyGroupPriceSwdDaoImpl")
	HyGroupPriceSwdDao dao;
	
	@Resource(name = "hyGroupPriceSwdDaoImpl")
	public void setBaseDao(HyGroupPriceSwdDao dao){
		super.setBaseDao(dao);		
	}


}
