package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.DepositStoreBranchDao;
import com.hongyu.entity.DepositStoreBranch;
import com.hongyu.service.DepositStoreBranchService;

@Service("depositStoreBranchServiceImpl")
public class DepositStoreBranchServiceImpl extends BaseServiceImpl<DepositStoreBranch, Long>
		implements DepositStoreBranchService {
	@Resource(name = "depositStoreBranchDaoImpl")
	DepositStoreBranchDao dao;

	@Resource(name = "depositStoreBranchDaoImpl")
	public void setBaseDao(DepositStoreBranchDao dao) {
		super.setBaseDao(dao);
	}
}