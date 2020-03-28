package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ServicePromiseDao;
import com.hongyu.entity.ServicePromise;
import com.hongyu.service.ServicePromiseService;

@Service(value = "servicePromiseServiceImpl")
public class ServicePromiseServiceImpl extends BaseServiceImpl<ServicePromise, Long> implements ServicePromiseService {
	
	@Resource(name = "servicePromiseDaoImpl")
	ServicePromiseDao dao;
	
	@Resource(name = "servicePromiseDaoImpl")
	public void setBaseDao(ServicePromiseDao dao){
		super.setBaseDao(dao);		
	}
}
