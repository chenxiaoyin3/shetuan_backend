package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.Weight;
import com.hongyu.service.WeightService;

@Service("weightServiceImpl")
public class WeightServiceImpl extends BaseServiceImpl<Weight,Long> implements WeightService {

	@Override
	@Resource(name="weightDaoImpl")
	public void setBaseDao(BaseDao<Weight, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
