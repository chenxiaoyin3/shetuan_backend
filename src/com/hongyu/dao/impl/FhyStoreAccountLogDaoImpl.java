package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FhyStoreAccountLogDao;
import com.hongyu.entity.FhyStoreAccountLog;

@Repository("fhyStoreAccountLogDaoImpl")
public class FhyStoreAccountLogDaoImpl extends BaseDaoImpl<FhyStoreAccountLog, Long> implements FhyStoreAccountLogDao{

}
