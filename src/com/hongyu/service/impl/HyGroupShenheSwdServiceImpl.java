package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupShenheSwdDao;
import com.hongyu.entity.HyGroupShenheSwd;
import com.hongyu.service.HyGroupShenheSwdService;

@Service("hyGroupShenheSwdServiceImpl")
public class HyGroupShenheSwdServiceImpl extends BaseServiceImpl<HyGroupShenheSwd, Long> implements HyGroupShenheSwdService{

	@Resource(name = "hyGroupShenheSwdDaoImpl")
	HyGroupShenheSwdDao dao;
	
	@Resource(name = "hyGroupShenheSwdDaoImpl")
	public void setBaseDao(HyGroupShenheSwdDao dao){
		super.setBaseDao(dao);		
	}


}
