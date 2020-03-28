package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySupplierDeductQianzhengDao;
import com.hongyu.entity.HySupplierDeductQianzheng;
import com.hongyu.service.HySupplierDeductQianzhengService;

@Service(value = "hySupplierDeductQianzhengServiceImpl")
public class HySupplierDeductQianzhengServiceImpl extends BaseServiceImpl<HySupplierDeductQianzheng, Long>
		implements HySupplierDeductQianzhengService {
	@Resource(name = "hySupplierDeductQianzhengDaoImpl")
	HySupplierDeductQianzhengDao dao;
	
	@Resource(name = "hySupplierDeductQianzhengDaoImpl")
	public void setBaseDao(HySupplierDeductQianzhengDao dao){
		super.setBaseDao(dao);		
	}
}