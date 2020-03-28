package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.FhyStoreAccountLogDao;
import com.hongyu.entity.FhyStoreAccountLog;
import com.hongyu.service.FhyStoreAccountLogService;

@Service("fhyStoreAccountLogServiceImpl")
public class FhyStoreAccountLogServiceImpl extends BaseServiceImpl<FhyStoreAccountLog, Long> implements FhyStoreAccountLogService{
	@Resource(name = "fhyStoreAccountLogDaoImpl")
	FhyStoreAccountLogDao dao;

	@Resource(name = "fhyStoreAccountLogDaoImpl")
	public void setBaseDao(FhyStoreAccountLogDao dao) {
		super.setBaseDao(dao);
	}
}
