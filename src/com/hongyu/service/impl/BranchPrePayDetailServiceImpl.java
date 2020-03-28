package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BranchPrePayDetailDao;
import com.hongyu.entity.BranchPrePayDetail;
import com.hongyu.service.BranchPrePayDetailService;

@Service("branchPrePayDetailServiceImpl")
public class BranchPrePayDetailServiceImpl extends BaseServiceImpl<BranchPrePayDetail, Long>
		implements BranchPrePayDetailService {
	@Resource(name = "branchPrePayDetailDaoImpl")
	BranchPrePayDetailDao dao;

	@Resource(name = "branchPrePayDetailDaoImpl")
	public void setBaseDao(BranchPrePayDetailDao dao) {
		super.setBaseDao(dao);
	}
}
