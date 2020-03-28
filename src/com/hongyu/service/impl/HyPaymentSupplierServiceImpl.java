package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.dao.HyPaymentSupplierDao;
import com.hongyu.entity.HyPaymentSupplier;
import com.hongyu.service.HyPaymentSupplierService;

@Service("hyPaymentSupplierServiceImpl")
public class HyPaymentSupplierServiceImpl extends BaseServiceImpl<HyPaymentSupplier,Long>
          implements HyPaymentSupplierService{
	@Resource(name = "hyPaymentSupplierDaoImpl")
	HyPaymentSupplierDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "hyPaymentSupplierDaoImpl")
	public void setBaseDao(HyPaymentSupplierDao dao){
		super.setBaseDao(dao);	
	}
	
}
