package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySupplierDeductPiaowuDao;
import com.hongyu.entity.HySupplierDeductPiaowu;
import com.hongyu.service.HySupplierDeductPiaowuService;

@Service(value = "hySupplierDeductPiaowuServiceImpl")
public class HySupplierDeductPiaowuServiceImpl extends BaseServiceImpl<HySupplierDeductPiaowu, Long>
		implements HySupplierDeductPiaowuService {
	@Resource(name = "hySupplierDeductPiaowuDaoImpl")
	HySupplierDeductPiaowuDao dao;
	
	@Resource(name = "hySupplierDeductPiaowuDaoImpl")
	public void setBaseDao(HySupplierDeductPiaowuDao dao){
		super.setBaseDao(dao);		
	}
}
