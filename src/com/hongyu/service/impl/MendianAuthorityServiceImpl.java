package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MendianAuthorityDao;
import com.hongyu.entity.MendianAuthority;
import com.hongyu.service.MendianAuthorityService;
@Service("mendianAuthorityServiceImpl")
public class MendianAuthorityServiceImpl extends BaseServiceImpl<MendianAuthority, Long> implements MendianAuthorityService {
	@Resource(name="mendianAuthorityDaoImpl")
	MendianAuthorityDao dao;
	
	@Resource(name = "mendianAuthorityDaoImpl")
	public void setBaseDao(MendianAuthorityDao dao){
		super.setBaseDao(dao);		
	}	
	
}
