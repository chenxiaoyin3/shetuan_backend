package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.StoreAccountDao;
import com.hongyu.entity.StoreAccount;

@Repository("storeAccountDaoImpl")
public class StoreAccountDaoImpl extends BaseDaoImpl<StoreAccount,Long> implements StoreAccountDao {

}
