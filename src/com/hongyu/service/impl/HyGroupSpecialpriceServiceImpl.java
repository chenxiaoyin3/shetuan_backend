package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupSpecialpriceDao;
import com.hongyu.entity.HyGroupSpecialprice;
import com.hongyu.service.HyGroupSpecialpriceService;
@Service(value = "hyGroupSpecialpriceServiceImpl")
public class HyGroupSpecialpriceServiceImpl extends BaseServiceImpl<HyGroupSpecialprice, Long>
		implements HyGroupSpecialpriceService {
	@Resource(name = "hyGroupSpecialpriceDaoImpl")
	HyGroupSpecialpriceDao dao;
	
	@Resource(name = "hyGroupSpecialpriceDaoImpl")
	public void setBaseDao(HyGroupSpecialpriceDao dao){
		super.setBaseDao(dao);		
	}
}
