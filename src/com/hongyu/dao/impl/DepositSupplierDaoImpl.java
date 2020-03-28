package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.DepositSupplierDao;
import com.hongyu.entity.DepositSupplier;

@Repository("depositSupplierDaoImpl")
public class DepositSupplierDaoImpl extends BaseDaoImpl<DepositSupplier, Long> implements DepositSupplierDao {
}