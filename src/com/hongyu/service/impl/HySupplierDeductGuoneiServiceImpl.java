package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySupplierDeductGuoneiDao;
import com.hongyu.entity.HySupplierDeductGuonei;
import com.hongyu.service.HySupplierDeductGuoneiService;

@Service(value = "hySupplierDeductGuoneiServiceImpl")
public class HySupplierDeductGuoneiServiceImpl extends BaseServiceImpl<HySupplierDeductGuonei, Long>
		implements HySupplierDeductGuoneiService {
	@Resource(name = "hySupplierDeductGuoneiDaoImpl")
	HySupplierDeductGuoneiDao dao;
	
	@Resource(name = "hySupplierDeductGuoneiDaoImpl")
	public void setBaseDao(HySupplierDeductGuoneiDao dao){
		super.setBaseDao(dao);		
	}
}

