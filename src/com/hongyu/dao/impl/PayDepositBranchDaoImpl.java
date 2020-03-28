package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayDepositBranchDao;
import com.hongyu.entity.PayDepositBranch;

@Repository("payDepositBranchDaoImpl")
public class PayDepositBranchDaoImpl extends BaseDaoImpl<PayDepositBranch, Long> implements PayDepositBranchDao {
}