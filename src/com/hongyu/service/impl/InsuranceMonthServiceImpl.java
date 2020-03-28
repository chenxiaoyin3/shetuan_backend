package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.InsuranceMonth;
import com.hongyu.service.InsuranceMonthService;

@Service("insuranceMonthServiceImpl")
public class InsuranceMonthServiceImpl extends BaseServiceImpl<InsuranceMonth,Long> implements InsuranceMonthService {

	@Override
	@Resource(name="insuranceMonthDaoImpl")
	public void setBaseDao(BaseDao<InsuranceMonth, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
