package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BranchBalanceDao;
import com.hongyu.entity.BranchBalance;
import com.hongyu.service.BranchBalanceService;

@Service("branchBalanceServiceImpl")
public class BranchBalanceServiceImpl extends BaseServiceImpl<BranchBalance, Long> implements BranchBalanceService {
	@Resource(name = "branchBalanceDaoImpl")
	BranchBalanceDao dao;

	@Resource(name = "branchBalanceDaoImpl")
	public void setBaseDao(BranchBalanceDao dao) {
		super.setBaseDao(dao);
	}
}