package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.service.SpecialtyPriceService;

@Service("specialtyPriceServiceImpl")
public class SpecialtyPriceServiceImpl extends BaseServiceImpl<SpecialtyPrice, Long> implements SpecialtyPriceService {
	
	@Resource(name="specialtyPriceDaoImpl")
	public void setBaseDao(BaseDao<SpecialtyPrice, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
