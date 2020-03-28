package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.TouristTypeDao;
import com.hongyu.entity.TouristType;
import com.hongyu.service.TouristTypeService;
@Service(value = "touristTypeServiceImpl")
public class TouristTypeServiceImpl extends BaseServiceImpl<TouristType, Long> 
implements TouristTypeService {

	
	@Resource(name = "touristTypeDaoImpl")
	TouristTypeDao dao;
	
	@Resource(name = "touristTypeDaoImpl")
	public void setBaseDao(TouristTypeDao dao){
		super.setBaseDao(dao);		
	}	
	
}