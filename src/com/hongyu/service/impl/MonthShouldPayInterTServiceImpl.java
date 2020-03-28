package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.MonthShouldPayInterT;
import com.hongyu.service.MonthShouldPayInterTService;

@Service("monthShouldPayInterTServiceImpl")
public class MonthShouldPayInterTServiceImpl extends BaseServiceImpl<MonthShouldPayInterT, Long> implements MonthShouldPayInterTService{

	@Override
	@Resource(name = "monthShouldPayInterTDaoImpl")
	public void setBaseDao(BaseDao<MonthShouldPayInterT, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
