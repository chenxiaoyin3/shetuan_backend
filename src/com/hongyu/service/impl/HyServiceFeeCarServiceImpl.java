package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyServiceFeeCar;
import com.hongyu.service.HyServiceFeeCarService;

@Service("hyServiceFeeCarServiceImpl")
public class HyServiceFeeCarServiceImpl extends BaseServiceImpl<HyServiceFeeCar,Integer> implements HyServiceFeeCarService{

	@Resource(name="hyServiceFeeCarDaoImpl")
	@Override
	public void setBaseDao(BaseDao<HyServiceFeeCar, Integer> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
