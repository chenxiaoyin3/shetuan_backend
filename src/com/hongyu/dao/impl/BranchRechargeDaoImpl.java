package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BranchRechargeDao;
import com.hongyu.entity.BranchRecharge;

@Repository("branchRechargeDaoImpl")
public class BranchRechargeDaoImpl extends BaseDaoImpl<BranchRecharge, Long> implements BranchRechargeDao {

}
