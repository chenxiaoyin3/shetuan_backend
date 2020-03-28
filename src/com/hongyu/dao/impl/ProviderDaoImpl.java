package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProviderDao;
import com.hongyu.entity.Provider;

@Repository("providerDaoImpl")
public class ProviderDaoImpl extends BaseDaoImpl<Provider, Long> implements ProviderDao {

}
