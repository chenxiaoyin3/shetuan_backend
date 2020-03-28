package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupOtherpriceSwdDao;
import com.hongyu.entity.HyGroupOtherpriceSwd;
import com.hongyu.service.HyGroupOtherpriceSwdService;


@Service("hyGroupOtherpriceSwdServiceImpl")
public class HyGroupOtherpriceSwdServiceImpl extends BaseServiceImpl<HyGroupOtherpriceSwd, Long> implements HyGroupOtherpriceSwdService{

	@Resource(name = "hyGroupOtherpriceSwdDaoImpl")
	HyGroupOtherpriceSwdDao dao;
	
	@Resource(name = "hyGroupOtherpriceSwdDaoImpl")
	public void setBaseDao(HyGroupOtherpriceSwdDao dao){
		super.setBaseDao(dao);		
	}

}
