package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.service.StoreAccountLogService;
@Service("storeAccountLogServiceImpl")
public class StoreAccountLogServiceImpl extends BaseServiceImpl<StoreAccountLog,Long> implements StoreAccountLogService{

	@Resource(name="storeAccountLogDaoImpl")
	@Override
	public void setBaseDao(BaseDao<StoreAccountLog, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
