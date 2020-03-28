package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySupplierDeductQicheDao;
import com.hongyu.entity.HySupplierDeductQiche;
import com.hongyu.service.HySupplierDeductQicheService;

@Service(value = "hySupplierDeductQicheServiceImpl")
public class HySupplierDeductQicheServiceImpl extends BaseServiceImpl<HySupplierDeductQiche, Long>
		implements HySupplierDeductQicheService {
	@Resource(name = "hySupplierDeductQicheDaoImpl")
	HySupplierDeductQicheDao dao;
	
	@Resource(name = "hySupplierDeductQicheDaoImpl")
	public void setBaseDao(HySupplierDeductQicheDao dao){
		super.setBaseDao(dao);		
	}
}