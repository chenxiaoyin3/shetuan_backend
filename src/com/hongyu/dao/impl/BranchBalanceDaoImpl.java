package com.hongyu.dao.impl;import org.springframework.stereotype.Repository;import com.grain.dao.impl.BaseDaoImpl;import com.hongyu.dao.BranchBalanceDao;import com.hongyu.entity.BranchBalance;@Repository("branchBalanceDaoImpl")public class BranchBalanceDaoImpl extends BaseDaoImpl<BranchBalance, Long> implements BranchBalanceDao {}