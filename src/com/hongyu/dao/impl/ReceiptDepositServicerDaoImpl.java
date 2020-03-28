package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptDepositServicerDao;
import com.hongyu.entity.ReceiptDepositServicer;

@Repository("receiptDepositServicerDaoImpl")
public class ReceiptDepositServicerDaoImpl extends BaseDaoImpl<ReceiptDepositServicer, Long>
		implements ReceiptDepositServicerDao {
}