package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.TrafficTypeDao;
import com.hongyu.entity.TrafficType;
import com.hongyu.service.TrafficTypeService;
@Service(value = "trafficTypeServiceImpl")
public class TrafficTypeServiceImpl extends BaseServiceImpl<TrafficType, Long> 
implements TrafficTypeService {

	
	@Resource(name = "trafficTypeDaoImpl")
	TrafficTypeDao dao;
	
	@Resource(name = "trafficTypeDaoImpl")
	public void setBaseDao(TrafficTypeDao dao){
		super.setBaseDao(dao);		
	}	
	
}