package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyAuthorityDao;
import com.hongyu.entity.HyAuthority;
import com.hongyu.service.HyAuthorityService;
@Service(value = "hyAuthorityServiceImpl")
public class HyAuthorityServiceImpl extends BaseServiceImpl<HyAuthority, Long> 
implements HyAuthorityService {

	
	@Resource(name = "hyAuthorityDaoImpl")
	HyAuthorityDao dao;
	
	@Resource(name = "hyAuthorityDaoImpl")
	public void setBaseDao(HyAuthorityDao dao){
		super.setBaseDao(dao);		
	}	
	
}
