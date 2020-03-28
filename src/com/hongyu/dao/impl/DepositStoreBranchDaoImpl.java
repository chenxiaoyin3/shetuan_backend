package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.DepositStoreBranchDao;
import com.hongyu.entity.DepositStoreBranch;

@Repository("depositStoreBranchDaoImpl")
public class DepositStoreBranchDaoImpl extends BaseDaoImpl<DepositStoreBranch, Long> implements DepositStoreBranchDao {
}