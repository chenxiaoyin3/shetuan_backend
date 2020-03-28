package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupOtherpriceDao;
import com.hongyu.entity.HyGroupOtherprice;
import com.hongyu.service.HyGroupOtherpriceService;
@Service(value = "hyGroupOtherpriceServiceImpl")
public class HyGroupOtherpriceServiceImpl extends BaseServiceImpl<HyGroupOtherprice, Long>
		implements HyGroupOtherpriceService {
	@Resource(name = "hyGroupOtherpriceDaoImpl")
	HyGroupOtherpriceDao dao;
	
	@Resource(name = "hyGroupOtherpriceDaoImpl")
	public void setBaseDao(HyGroupOtherpriceDao dao){
		super.setBaseDao(dao);		
	}
}
