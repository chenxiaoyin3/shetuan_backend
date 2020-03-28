package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayDetailsBranchDao;
import com.hongyu.entity.PayDetailsBranch;

@Repository("payDetailsBranchDaoImpl")
public class PayDetailsBranchDaoImpl extends BaseDaoImpl<PayDetailsBranch, Long> implements PayDetailsBranchDao {
}