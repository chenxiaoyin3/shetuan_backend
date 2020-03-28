package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.StoreApplicationDao;
import com.hongyu.entity.StoreApplication;
import com.hongyu.service.StoreApplicationService;

@Service("storeApplicationServiceImpl")
public class StoreApplicationServiceImpl extends BaseServiceImpl<StoreApplication,Long> implements StoreApplicationService {

	@Resource(name="storeApplicationDaoImpl")
	@Override
	public void setBaseDao(BaseDao<StoreApplication, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
	
	
}
