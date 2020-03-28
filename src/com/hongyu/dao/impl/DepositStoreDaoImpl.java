package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.DepositStoreDao;
import com.hongyu.entity.DepositStore;

@Repository("depositStoreDaoImpl")
public class DepositStoreDaoImpl extends BaseDaoImpl<DepositStore, Long> implements DepositStoreDao {
}