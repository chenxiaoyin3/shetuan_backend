package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptDepositStoreBranchDao;
import com.hongyu.entity.ReceiptDepositStoreBranch;
import com.hongyu.service.ReceiptDepositStoreBranchService;

@Service("receiptDepositStoreBranchServiceImpl")
public class ReceiptDepositStoreBranchServiceImpl extends BaseServiceImpl<ReceiptDepositStoreBranch, Long>
		implements ReceiptDepositStoreBranchService {
	@Resource(name = "receiptDepositStoreBranchDaoImpl")
	ReceiptDepositStoreBranchDao dao;

	@Resource(name = "receiptDepositStoreBranchDaoImpl")
	public void setBaseDao(ReceiptDepositStoreBranchDao dao) {
		super.setBaseDao(dao);
	}
}