package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyFullSubstractDao;
import com.hongyu.entity.HyFullSubstract;
import com.hongyu.service.HyFullSubstractService;
@Service(value = "hyFullSubstractServiceImpl")
public class HyFullSubstractServiceImpl extends BaseServiceImpl<HyFullSubstract, Long>
		implements HyFullSubstractService {
	@Resource(name = "hyFullSubstractDaoImpl")
	HyFullSubstractDao dao;
	
	@Resource(name = "hyFullSubstractDaoImpl")
	public void setBaseDao(HyFullSubstractDao dao){
		super.setBaseDao(dao);		
	}

}
