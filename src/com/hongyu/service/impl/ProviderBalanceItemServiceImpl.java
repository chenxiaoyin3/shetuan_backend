package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProviderBalanceDao;
import com.hongyu.dao.ProviderBalanceItemDao;
import com.hongyu.entity.ProviderBalanceItem;
import com.hongyu.service.ProviderBalanceItemService;

@Service("providerBalanceItemServiceImpl")
public class ProviderBalanceItemServiceImpl extends BaseServiceImpl<ProviderBalanceItem, Long> implements ProviderBalanceItemService {
	@Resource(name = "providerBalanceItemDaoImpl")
	ProviderBalanceItemDao dao;

	@Resource(name = "providerBalanceItemDaoImpl")
	public void setBaseDao(ProviderBalanceItemDao dao) {
		super.setBaseDao(dao);
	}
}
