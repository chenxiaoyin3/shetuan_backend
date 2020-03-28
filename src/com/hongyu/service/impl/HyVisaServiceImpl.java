package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyVisaDao;
import com.hongyu.entity.HyVisa;
import com.hongyu.service.HyVisaService;

@Service("hyVisaServiceImpl")
public class HyVisaServiceImpl extends BaseServiceImpl<HyVisa, Long> implements HyVisaService{


	@Resource(name = "hyVisaDaoImpl")
	HyVisaDao dao;
	
	@Resource(name = "hyVisaDaoImpl")
	public void setBaseDao(HyVisaDao dao){
		super.setBaseDao(dao);		
	}

}
