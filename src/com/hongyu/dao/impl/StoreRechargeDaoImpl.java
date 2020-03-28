package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.StoreRechargeDao;
import com.hongyu.entity.StoreRecharge;
@Repository("storeRechargeDaoImpl")
public class StoreRechargeDaoImpl extends BaseDaoImpl<StoreRecharge,Long> implements StoreRechargeDao{

}
