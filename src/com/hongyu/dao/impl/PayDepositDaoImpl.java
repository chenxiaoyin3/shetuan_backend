package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayDepositDao;
import com.hongyu.entity.PayDeposit;

@Repository("payDepositDaoImpl")
public class PayDepositDaoImpl extends BaseDaoImpl<PayDeposit, Long> implements PayDepositDao {
}