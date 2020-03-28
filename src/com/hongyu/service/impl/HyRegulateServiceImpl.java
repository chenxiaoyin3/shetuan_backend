package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyRegulateDao;
import com.hongyu.entity.HyRegulate;
import com.hongyu.service.HyRegulateService;


@Service(value = "hyRegulateServiceImpl")
public class HyRegulateServiceImpl extends BaseServiceImpl<HyRegulate,Long> 
     implements HyRegulateService{
	@Resource(name = "hyRegulateDaoImpl")
	HyRegulateDao dao;
	
	@Resource(name = "hyRegulateDaoImpl")
	public void setBaseDao(HyRegulateDao dao){
		super.setBaseDao(dao);		
	}
}
