package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.DepositSupplierDao;
import com.hongyu.entity.DepositSupplier;
import com.hongyu.service.DepositSupplierService;

@Service("depositSupplierServiceImpl")
public class DepositSupplierServiceImpl extends BaseServiceImpl<DepositSupplier, Long>
		implements DepositSupplierService {
	@Resource(name = "depositSupplierDaoImpl")
	DepositSupplierDao dao;

	@Resource(name = "depositSupplierDaoImpl")
	public void setBaseDao(DepositSupplierDao dao) {
		super.setBaseDao(dao);
	}
}