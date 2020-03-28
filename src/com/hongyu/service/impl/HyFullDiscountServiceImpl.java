package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyFullDiscountDao;
import com.hongyu.entity.HyFullDiscount;
import com.hongyu.service.HyFullDiscountService;
@Service(value = "hyFullDiscountServiceImpl")
public class HyFullDiscountServiceImpl extends BaseServiceImpl<HyFullDiscount, Long>
		implements HyFullDiscountService {
	@Resource(name = "hyFullDiscountDaoImpl")
	HyFullDiscountDao dao;
	
	@Resource(name = "hyFullDiscountDaoImpl")
	public void setBaseDao(HyFullDiscountDao dao){
		super.setBaseDao(dao);		
	}

}
