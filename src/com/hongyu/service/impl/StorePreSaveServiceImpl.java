package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.StorePreSaveDao;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.StorePreSaveService;

@Service("storePreSaveServiceImpl")
public class StorePreSaveServiceImpl extends BaseServiceImpl<StorePreSave, Long> implements StorePreSaveService {
	@Resource(name = "storePreSaveDaoImpl")
	StorePreSaveDao dao;

	@Resource(name = "storePreSaveDaoImpl")
	public void setBaseDao(StorePreSaveDao dao) {
		super.setBaseDao(dao);
	}
}
