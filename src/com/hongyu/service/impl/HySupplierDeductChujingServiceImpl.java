package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySupplierDeductChujingDao;
import com.hongyu.entity.HySupplierDeductChujing;
import com.hongyu.service.HySupplierDeductChujingService;
@Service(value = "hySupplierDeductChujingServiceImpl")
public class HySupplierDeductChujingServiceImpl extends BaseServiceImpl<HySupplierDeductChujing, Long>
		implements HySupplierDeductChujingService {
	@Resource(name = "hySupplierDeductChujingDaoImpl")
	HySupplierDeductChujingDao dao;
	
	@Resource(name = "hySupplierDeductChujingDaoImpl")
	public void setBaseDao(HySupplierDeductChujingDao dao){
		super.setBaseDao(dao);		
	}
}
