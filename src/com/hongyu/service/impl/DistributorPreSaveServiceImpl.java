package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.DistributorPreSaveDao;
import com.hongyu.entity.DistributorPreSave;
import com.hongyu.service.DistributorPreSaveService;

@Service("distributorPreSaveServiceImpl")
public class DistributorPreSaveServiceImpl extends BaseServiceImpl<DistributorPreSave, Long>
		implements DistributorPreSaveService {
	@Resource(name = "distributorPreSaveDaoImpl")
	DistributorPreSaveDao dao;

	@Resource(name = "distributorPreSaveDaoImpl")
	public void setBaseDao(DistributorPreSaveDao dao) {
		super.setBaseDao(dao);
	}
}
