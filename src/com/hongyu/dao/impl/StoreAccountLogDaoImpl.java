package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.StoreAccountLogDao;
import com.hongyu.entity.StoreAccountLog;

@Repository("storeAccountLogDaoImpl")
public class StoreAccountLogDaoImpl extends BaseDaoImpl<StoreAccountLog,Long> implements StoreAccountLogDao{

}
