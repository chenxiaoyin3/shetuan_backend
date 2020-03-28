package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyServiceFeeNoncar;
import com.hongyu.service.HyServiceFeeNoncarService;

@Service("hyServiceFeeNoncarServiceImpl")
public class HyServiceFeeNoncarServiceImpl extends BaseServiceImpl<HyServiceFeeNoncar,Integer> implements HyServiceFeeNoncarService {

	@Resource(name="hyServiceFeeNoncarDaoImpl")
	@Override
	public void setBaseDao(BaseDao<HyServiceFeeNoncar, Integer> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
}
