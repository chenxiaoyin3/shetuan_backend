package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyBusinessPV;
import com.hongyu.service.HyBusinessPVService;

@Service("hyBusinessPVServiceImpl")
public class HyBusinessPVServiceImpl extends BaseServiceImpl<HyBusinessPV, Long> implements HyBusinessPVService {

	
	@Resource(name="hyBusinessPVDaoImpl")
	public void setBaseDao(BaseDao dao){
		super.setBaseDao(dao);
	}
}
