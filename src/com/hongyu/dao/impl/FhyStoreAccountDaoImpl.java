package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FhyStoreAccountDao;
import com.hongyu.entity.FhyStoreAccount;

@Repository("fhyStoreAccountDaoImpl")
public class FhyStoreAccountDaoImpl extends BaseDaoImpl<FhyStoreAccount, Long> implements FhyStoreAccountDao{

}
