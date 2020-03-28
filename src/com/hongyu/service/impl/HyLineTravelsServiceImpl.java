package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyLineTravelsDao;
import com.hongyu.entity.HyLineTravels;
import com.hongyu.service.HyLineTravelsService;
@Service(value = "hyLineTravelsServiceImpl")
public class HyLineTravelsServiceImpl extends BaseServiceImpl<HyLineTravels, Long> implements HyLineTravelsService {
	@Resource(name = "hyLineTravelsDaoImpl")
	HyLineTravelsDao dao;
	
	@Resource(name = "hyLineTravelsDaoImpl")
	public void setBaseDao(HyLineTravelsDao dao){
		super.setBaseDao(dao);		
	}
}
