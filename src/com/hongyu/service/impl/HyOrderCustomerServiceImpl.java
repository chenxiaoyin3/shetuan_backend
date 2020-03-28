package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupOtherpriceSwdDao;
import com.hongyu.dao.HyOrderCustomerDao;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.service.HyOrderCustomerService;

@Service("hyOrderCustomerServiceImpl")
public class HyOrderCustomerServiceImpl extends BaseServiceImpl<HyOrderCustomer,Long> implements HyOrderCustomerService {
	@Resource(name = "hyOrderCustomerDaoImpl")
	HyOrderCustomerDao dao;
	
	@Resource(name = "hyOrderCustomerDaoImpl")
	public void setBaseDao(HyOrderCustomerDao dao){
		super.setBaseDao(dao);		
	}
}
