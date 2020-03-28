package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BranchPreSaveDao;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.service.BranchPreSaveService;

@Service("branchPreSaveServiceImpl")
public class BranchPreSaveServiceImpl extends BaseServiceImpl<BranchPreSave, Long> implements BranchPreSaveService {
	@Resource(name = "branchPreSaveDaoImpl")
	BranchPreSaveDao dao;

	@Resource(name = "branchPreSaveDaoImpl")
	public void setBaseDao(BranchPreSaveDao dao) {
		super.setBaseDao(dao);
	}
}
