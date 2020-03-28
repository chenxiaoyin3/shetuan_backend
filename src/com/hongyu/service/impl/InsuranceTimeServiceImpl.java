package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.InsuranceTime;
import com.hongyu.service.InsuranceTimeService;

@Service("insuranceTimeServiceImpl")
public class InsuranceTimeServiceImpl extends BaseServiceImpl<InsuranceTime,Long> implements InsuranceTimeService {

	@Override
	@Resource(name="insuranceTimeDaoImpl")
	public void setBaseDao(BaseDao<InsuranceTime, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
	
}
