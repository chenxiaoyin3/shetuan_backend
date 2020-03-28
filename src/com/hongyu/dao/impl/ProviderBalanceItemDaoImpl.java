package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProviderBalanceItemDao;
import com.hongyu.entity.ProviderBalanceItem;

@Repository("providerBalanceItemDaoImpl")
public class ProviderBalanceItemDaoImpl extends BaseDaoImpl<ProviderBalanceItem, Long> implements ProviderBalanceItemDao {

}
