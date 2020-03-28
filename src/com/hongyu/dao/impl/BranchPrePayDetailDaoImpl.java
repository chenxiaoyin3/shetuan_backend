package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BranchPrePayDetailDao;
import com.hongyu.entity.BranchPrePayDetail;

@Repository("branchPrePayDetailDaoImpl")
public class BranchPrePayDetailDaoImpl extends BaseDaoImpl<BranchPrePayDetail, Long> implements BranchPrePayDetailDao {

}
