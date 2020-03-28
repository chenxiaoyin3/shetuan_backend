package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProviderDao;
import com.hongyu.entity.Provider;
import com.hongyu.service.ProviderService;

@Service("providerServiceImpl")
public class ProviderServiceImpl
extends BaseServiceImpl<Provider, Long>
implements ProviderService{
	
	@Resource(name="providerDaoImpl")
	ProviderDao providerDaoImpl;
	
	@Resource(name="providerDaoImpl")
	public void setBaseDao(ProviderDao dao) {
		super.setBaseDao(dao);
	}
}
