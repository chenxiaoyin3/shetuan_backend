package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptDepositStoreBranchDao;
import com.hongyu.entity.ReceiptDepositStoreBranch;

@Repository("receiptDepositStoreBranchDaoImpl")
public class ReceiptDepositStoreBranchDaoImpl extends BaseDaoImpl<ReceiptDepositStoreBranch, Long>
		implements ReceiptDepositStoreBranchDao {
}