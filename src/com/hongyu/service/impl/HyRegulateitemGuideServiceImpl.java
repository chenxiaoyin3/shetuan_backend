package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyRegulateitemGuideDao;
import com.hongyu.entity.HyRegulateitemGuide;
import com.hongyu.service.HyRegulateitemGuideService;

@Service(value = "hyRegulateitemGuideServiceImpl")
public class HyRegulateitemGuideServiceImpl extends BaseServiceImpl<HyRegulateitemGuide,Long> 
    implements HyRegulateitemGuideService {
	@Resource(name = "hyRegulateitemGuideDaoImpl")
	HyRegulateitemGuideDao dao;
	
	@Resource(name = "hyRegulateitemGuideDaoImpl")
	public void setBaseDao(HyRegulateitemGuideDao dao){
		super.setBaseDao(dao);		
	}
}
