package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FhyStoreRechargeDao;
import com.hongyu.entity.FhyStoreRecharge;


@Repository("fhyStoreRechargeDaoImpl")
public class FhyStoreRechargeDaoImpl extends BaseDaoImpl<FhyStoreRecharge, Long> implements FhyStoreRechargeDao{

}
