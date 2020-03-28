package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BusinessOrderDepotDao;
import com.hongyu.entity.BusinessOrderDepot;
import com.hongyu.service.BusinessOrderDepotService;
@Service(value = "businessOrderDepotServiceImpl")
public class BusinessOrderDepotServiceImpl extends BaseServiceImpl<BusinessOrderDepot, Long>
		implements BusinessOrderDepotService {
	
	@Resource(name = "businessOrderDepotDaoImpl")
	BusinessOrderDepotDao dao;
	
	@Resource(name = "businessOrderDepotDaoImpl")
	public void setBaseDao(BusinessOrderDepotDao dao){
		super.setBaseDao(dao);		
	}	
}
