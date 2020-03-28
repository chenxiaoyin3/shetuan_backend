package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyVisaPicDao;
import com.hongyu.entity.HyVisaPic;
import com.hongyu.service.HyVisaPicService;

@Service("hyVisaPicServiceImpl")
public class HyVisaPicServiceImpl extends BaseServiceImpl<HyVisaPic,Long> implements HyVisaPicService {
	@Resource(name = "hyVisaPicDaoImpl")
	HyVisaPicDao dao;
	
	@Resource(name = "hyVisaPicDaoImpl")
	public void setBaseDao(HyVisaPicDao dao){
		super.setBaseDao(dao);		
	}
}
