package com.hongyu.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyStoreFhynewDao;
import com.hongyu.entity.HyStoreFhynew;
import com.hongyu.service.HyStoreFhynewService;

@Service("hyStoreFhynewServiceImpl")
public class HyStoreFhynewServiceImpl extends BaseServiceImpl<HyStoreFhynew,Long> implements HyStoreFhynewService{
	@Resource(name = "hyStoreFhynewDaoImpl")
	HyStoreFhynewDao dao;
	
	@Resource(name = "hyStoreFhynewDaoImpl")
	public void setBaseDao(HyStoreFhynewDao dao){
		super.setBaseDao(dao);		
	}	
}
