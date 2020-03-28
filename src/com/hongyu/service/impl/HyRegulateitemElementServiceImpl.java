package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyRegulateitemElementDao;
import com.hongyu.entity.HyRegulateitemElement;
import com.hongyu.service.HyRegulateitemElementService;

@Service(value = "hyRegulateitemElementServiceImpl")
public class HyRegulateitemElementServiceImpl extends BaseServiceImpl<HyRegulateitemElement,Long>
    implements HyRegulateitemElementService{
	@Resource(name = "hyRegulateitemElementDaoImpl")
	HyRegulateitemElementDao dao;
	
	@Resource(name = "hyRegulateitemElementDaoImpl")
	public void setBaseDao(HyRegulateitemElementDao dao){
		super.setBaseDao(dao);		
	}
}
