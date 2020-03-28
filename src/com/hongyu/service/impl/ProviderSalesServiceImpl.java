package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProviderSalesDao;
import com.hongyu.entity.ProviderSales;
import com.hongyu.service.ProviderSalesService;

@Service("providerSalesServiceImpl")
public class ProviderSalesServiceImpl extends BaseServiceImpl<ProviderSales, Long> implements ProviderSalesService {
	
	@Resource(name="providerSalesDaoImpl")
	ProviderSalesDao providerSalesDaoImpl;
	
	@Resource(name="providerSalesDaoImpl")
	public void setBaseDao(ProviderSalesDao dao) {
		super.setBaseDao(dao);
	}
}
