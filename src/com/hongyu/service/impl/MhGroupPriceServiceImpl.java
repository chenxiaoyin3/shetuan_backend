package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.MhGroupPrice;
import com.hongyu.service.MhGroupPriceService;
@Service("mhGroupPriceServiceImpl")
public class MhGroupPriceServiceImpl extends BaseServiceImpl<MhGroupPrice,Long> implements MhGroupPriceService{

	@Override
	@Resource(name = "mhGroupPriceDaoImpl")
	public void setBaseDao(BaseDao<MhGroupPrice, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
