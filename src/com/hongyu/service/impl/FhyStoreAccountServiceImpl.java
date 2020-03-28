package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.FhyStoreAccountDao;
import com.hongyu.entity.FhyStoreAccount;
import com.hongyu.service.FhyStoreAccountService;

@Service("fhyStoreAccountServiceImpl")
public class FhyStoreAccountServiceImpl extends BaseServiceImpl<FhyStoreAccount, Long> implements FhyStoreAccountService{
	@Resource(name = "fhyStoreAccountDaoImpl")
	FhyStoreAccountDao dao;

	@Resource(name = "fhyStoreAccountDaoImpl")
	public void setBaseDao(FhyStoreAccountDao dao) {
		super.setBaseDao(dao);
	}
}
