package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.InsurancePrice;
import com.hongyu.service.InsurancePriceService;

@Service("insurancePriceServiceImpl")
public class InsurancePriceServiceImpl extends BaseServiceImpl<InsurancePrice,Long> implements InsurancePriceService{

	@Override
	@Resource(name="insurancePriceDaoImpl")
	public void setBaseDao(BaseDao<InsurancePrice, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
	
}
