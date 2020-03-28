package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyCreditFhyDao;
import com.hongyu.entity.HyCreditFhy;
import com.hongyu.service.HyCreditFhyService;

@Service("hyCreditFhyServiceImpl")
public class HyCreditFhyServiceImpl extends BaseServiceImpl<HyCreditFhy,Long> implements HyCreditFhyService {
	@Resource(name = "hyCreditFhyDaoImpl")
	HyCreditFhyDao dao;
	
	@Resource(name = "hyCreditFhyDaoImpl")
	public void setBaseDao(HyCreditFhyDao dao){
		super.setBaseDao(dao);		
	}	
}
