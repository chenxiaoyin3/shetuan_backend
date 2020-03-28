package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.AdvancePurchaseBalanceProviderDao;
import com.hongyu.entity.AdvancePurchaseBalanceProvider;
import com.hongyu.service.AdvancePurchaseBalanceProviderService;

@Service("advancePurchaseBalanceProviderServiceImpl")
public class AdvancePurchaseBalanceProviderServiceImpl extends BaseServiceImpl<AdvancePurchaseBalanceProvider, Long> implements AdvancePurchaseBalanceProviderService{
	@Resource(name="advancePurchaseBalanceProviderDaoImpl")
	private AdvancePurchaseBalanceProviderDao advancePurchaseBalanceProviderDaoImpl;
	

	
	@Resource(name="advancePurchaseBalanceProviderDaoImpl")
	public void setBaseDao(AdvancePurchaseBalanceProviderDao dao){
		super.setBaseDao(dao);
	}
}
