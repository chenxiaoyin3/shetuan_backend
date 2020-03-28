package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.InsuranceAttach;
import com.hongyu.service.InsuranceAttachService;

@Service("insuranceAttachServiceImpl")
public class InsuranceAttachServiceImpl extends BaseServiceImpl<InsuranceAttach,Long> implements InsuranceAttachService {

	@Override
	@Resource(name="insuranceAttachDaoImpl")
	public void setBaseDao(BaseDao<InsuranceAttach, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
	
}
