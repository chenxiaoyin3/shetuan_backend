package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BranchPrePayDao;
import com.hongyu.entity.BranchPrePay;

@Repository("branchPrePayDaoImpl")
public class BranchPrePayDaoImpl extends BaseDaoImpl<BranchPrePay, Long> implements BranchPrePayDao {

}
