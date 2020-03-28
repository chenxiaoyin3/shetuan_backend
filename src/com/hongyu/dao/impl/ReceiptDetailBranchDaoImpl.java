package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptDetailBranchDao;
import com.hongyu.entity.ReceiptDetailBranch;

@Repository("receiptDetailBranchDaoImpl")
public class ReceiptDetailBranchDaoImpl extends BaseDaoImpl<ReceiptDetailBranch, Long>
		implements ReceiptDetailBranchDao {
}