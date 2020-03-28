package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.MhGroupOtherPrice;
import com.hongyu.service.MhGroupOtherPriceService;
@Service("mhGroupOtherPriceServiceImpl")
public class MhGroupOtherPriceServiceImpl extends BaseServiceImpl<MhGroupOtherPrice, Long> implements MhGroupOtherPriceService{

	@Override
	@Resource(name = "mhGroupOtherPriceDaoImpl")
	public void setBaseDao(BaseDao<MhGroupOtherPrice, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
