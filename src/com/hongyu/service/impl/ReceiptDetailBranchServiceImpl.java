package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptDetailBranchDao;
import com.hongyu.entity.ReceiptDetailBranch;
import com.hongyu.service.ReceiptDetailBranchService;

@Service("receiptDetailBranchServiceImpl")
public class ReceiptDetailBranchServiceImpl extends BaseServiceImpl<ReceiptDetailBranch, Long>
		implements ReceiptDetailBranchService {
	@Resource(name = "receiptDetailBranchDaoImpl")
	ReceiptDetailBranchDao dao;

	@Resource(name = "receiptDetailBranchDaoImpl")
	public void setBaseDao(ReceiptDetailBranchDao dao) {
		super.setBaseDao(dao);
	}
}