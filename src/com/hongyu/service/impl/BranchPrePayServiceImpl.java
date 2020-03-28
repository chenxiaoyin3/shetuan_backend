package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BranchPrePayDao;
import com.hongyu.entity.BranchPrePay;
import com.hongyu.service.BranchPrePayService;

@Service("branchPrePayServiceImpl")
public class BranchPrePayServiceImpl extends BaseServiceImpl<BranchPrePay, Long> implements BranchPrePayService {
	@Resource(name = "branchPrePayDaoImpl")
	BranchPrePayDao dao;

	@Resource(name = "branchPrePayDaoImpl")
	public void setBaseDao(BranchPrePayDao dao) {
		super.setBaseDao(dao);
	}
	
	
	
}
