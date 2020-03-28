package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProviderBalanceDao;
import com.hongyu.entity.ProviderBalance;
import com.hongyu.service.ProviderBalanceService;

@Service("providerBalanceServiceImpl")
public class ProviderBalanceServiceImpl extends BaseServiceImpl<ProviderBalance, Long> implements ProviderBalanceService {
	@Resource(name = "providerBalanceDaoImpl")
	ProviderBalanceDao dao;

	@Resource(name = "providerBalanceDaoImpl")
	public void setBaseDao(ProviderBalanceDao dao) {
		super.setBaseDao(dao);
	}
}
