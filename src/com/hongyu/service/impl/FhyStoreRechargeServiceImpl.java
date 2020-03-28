package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.FhyStoreRechargeDao;
import com.hongyu.entity.FhyStoreRecharge;
import com.hongyu.service.FhyStoreRechargeService;


@Service("fhyStoreRechargeServiceImpl")
public class FhyStoreRechargeServiceImpl extends BaseServiceImpl<FhyStoreRecharge, Long> implements FhyStoreRechargeService{
	@Resource(name = "fhyStoreRechargeDaoImpl")
	FhyStoreRechargeDao dao;

	@Resource(name = "fhyStoreRechargeDaoImpl")
	public void setBaseDao(FhyStoreRechargeDao dao) {
		super.setBaseDao(dao);
	}
}
