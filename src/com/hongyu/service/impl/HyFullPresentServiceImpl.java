package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyFullPresentDao;
import com.hongyu.entity.HyFullPresent;
import com.hongyu.service.HyFullPresentService;
@Service(value = "hyFullPresentServiceImpl")
public class HyFullPresentServiceImpl extends BaseServiceImpl<HyFullPresent, Long>
		implements HyFullPresentService {
	@Resource(name = "hyFullPresentDaoImpl")
	HyFullPresentDao dao;
	
	@Resource(name = "hyFullPresentDaoImpl")
	public void setBaseDao(HyFullPresentDao dao){
		super.setBaseDao(dao);		
	}

}
