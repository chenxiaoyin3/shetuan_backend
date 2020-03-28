package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProfitShareStoreDao;
import com.hongyu.entity.ProfitShareStore;
import com.hongyu.service.ProfitShareStoreService;

@Service("profitShareStoreServiceImpl")
public class ProfitShareStoreServiceImpl extends BaseServiceImpl<ProfitShareStore, Long>
		implements ProfitShareStoreService {
	@Resource(name = "profitShareStoreDaoImpl")
	ProfitShareStoreDao dao;

	@Resource(name = "profitShareStoreDaoImpl")
	public void setBaseDao(ProfitShareStoreDao dao) {
		super.setBaseDao(dao);
	}
}