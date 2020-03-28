package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProviderSalesDao;
import com.hongyu.entity.ProviderSales;

@Repository("providerSalesDaoImpl")
public class ProviderSalesDaoImpl extends BaseDaoImpl<ProviderSales, Long> implements ProviderSalesDao {
	

}
