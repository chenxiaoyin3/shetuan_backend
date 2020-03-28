package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ExposeDao;
import com.hongyu.entity.Expose;
import com.hongyu.service.ExposeService;

@Service("exposeServiceImpl")
public class ExposeServiceImpl extends BaseServiceImpl<Expose, Long> implements ExposeService{

	@Override
	@Resource(name="exposeDaoImpl")
	public void setBaseDao(BaseDao<Expose, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
	
}
