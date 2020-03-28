package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySupplierDeductRengouDao;
import com.hongyu.entity.HySupplierDeductRengou;
import com.hongyu.service.HySupplierDeductRengouService;

@Service(value = "hySupplierDeductRengouServiceImpl")
public class HySupplierDeductRengouServiceImpl extends BaseServiceImpl<HySupplierDeductRengou, Long>
		implements HySupplierDeductRengouService {
	@Resource(name = "hySupplierDeductRengouDaoImpl")
	HySupplierDeductRengouDao dao;
	
	@Resource(name = "hySupplierDeductRengouDaoImpl")
	public void setBaseDao(HySupplierDeductRengouDao dao){
		super.setBaseDao(dao);		
	}
}