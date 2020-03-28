package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayDepositBranchDao;
import com.hongyu.entity.PayDepositBranch;
import com.hongyu.service.PayDepositBranchService;

@Service("payDepositBranchServiceImpl")
public class PayDepositBranchServiceImpl extends BaseServiceImpl<PayDepositBranch, Long>
		implements PayDepositBranchService {
	@Resource(name = "payDepositBranchDaoImpl")
	PayDepositBranchDao dao;

	@Resource(name = "payDepositBranchDaoImpl")
	public void setBaseDao(PayDepositBranchDao dao) {
		super.setBaseDao(dao);
	}
}