package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptBranchRechargeDao;
import com.hongyu.entity.ReceiptBranchRecharge;

@Repository("receiptBranchRechargeDaoImpl")
public class ReceiptBranchRechargeDaoImpl extends BaseDaoImpl<ReceiptBranchRecharge, Long>
		implements ReceiptBranchRechargeDao {
}