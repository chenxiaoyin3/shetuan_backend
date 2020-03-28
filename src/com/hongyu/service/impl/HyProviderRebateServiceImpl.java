package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyProviderRebateDao;
import com.hongyu.entity.HyProviderRebate;
import com.hongyu.service.HyProviderRebateService;

@Service("hyProviderRebateServiceImpl")
public class HyProviderRebateServiceImpl extends BaseServiceImpl<HyProviderRebate, Long> implements HyProviderRebateService{

	@Resource(name = "hyProviderRebateDaoImpl")
	HyProviderRebateDao dao;
	
	@Resource(name = "hyProviderRebateDaoImpl")
	public void setBaseDao(HyProviderRebateDao dao){
		super.setBaseDao(dao);		
	}

}
