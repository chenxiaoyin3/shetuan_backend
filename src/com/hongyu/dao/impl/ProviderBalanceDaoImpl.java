package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProviderBalanceDao;
import com.hongyu.entity.ProviderBalance;

@Repository("providerBalanceDaoImpl")
public class ProviderBalanceDaoImpl extends BaseDaoImpl<ProviderBalance, Long> implements ProviderBalanceDao {

}
