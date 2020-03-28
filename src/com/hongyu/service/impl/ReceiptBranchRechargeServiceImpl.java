package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptBranchRechargeDao;
import com.hongyu.entity.ReceiptBranchRecharge;
import com.hongyu.service.ReceiptBranchRechargeService;

@Service("receiptBranchRechargeServiceImpl")
public class ReceiptBranchRechargeServiceImpl extends BaseServiceImpl<ReceiptBranchRecharge, Long>
		implements ReceiptBranchRechargeService {
	@Resource(name = "receiptBranchRechargeDaoImpl")
	ReceiptBranchRechargeDao dao;

	@Resource(name = "receiptBranchRechargeDaoImpl")
	public void setBaseDao(ReceiptBranchRechargeDao dao) {
		super.setBaseDao(dao);
	}
}