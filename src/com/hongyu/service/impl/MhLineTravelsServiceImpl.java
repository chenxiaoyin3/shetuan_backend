package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.MhLineTravels;
import com.hongyu.service.MhLineTravelsService;
@Service("mhLineTravelsServiceImpl")
public class MhLineTravelsServiceImpl extends BaseServiceImpl<MhLineTravels,Long> implements MhLineTravelsService{

	@Override
	@Resource(name = "mhLineTravelsDaoImpl")
	public void setBaseDao(BaseDao<MhLineTravels, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
}
