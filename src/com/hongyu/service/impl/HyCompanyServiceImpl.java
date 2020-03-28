package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyCompanyDao;
import com.hongyu.entity.HyCompany;
import com.hongyu.service.HyCompanyService;

@Service(value = "hyCompanyServiceImpl")
public class HyCompanyServiceImpl extends BaseServiceImpl<HyCompany, Long> 
implements HyCompanyService {
	
	@Resource(name = "hyCompanyDaoImpl")
	HyCompanyDao dao;
	
	@Resource(name = "hyCompanyDaoImpl")
	public void setBaseDao(HyCompanyDao dao){
		super.setBaseDao(dao);		
	}	
}