package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.DepositStoreDao;
import com.hongyu.entity.DepositStore;
import com.hongyu.service.DepositStoreService;

@Service("depositStoreServiceImpl")
public class DepositStoreServiceImpl extends BaseServiceImpl<DepositStore, Long> implements DepositStoreService {
	@Resource(name = "depositStoreDaoImpl")
	DepositStoreDao dao;

	@Resource(name = "depositStoreDaoImpl")
	public void setBaseDao(DepositStoreDao dao) {
		super.setBaseDao(dao);
	}
}