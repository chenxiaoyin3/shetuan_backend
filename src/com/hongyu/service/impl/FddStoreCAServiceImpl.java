package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.FddStoreCA;
import com.hongyu.service.FddStoreCAService;
@Service("fddStoreCAServiceImpl")
public class FddStoreCAServiceImpl extends BaseServiceImpl<FddStoreCA,Long> implements FddStoreCAService{
	@Override
	@Resource(name="fddStoreCADaoImpl")
	public void setBaseDao(BaseDao<FddStoreCA, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
