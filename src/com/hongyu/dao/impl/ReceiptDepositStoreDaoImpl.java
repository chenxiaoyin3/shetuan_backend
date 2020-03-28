package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptDepositStoreDao;
import com.hongyu.entity.ReceiptDepositStore;

@Repository("receiptDepositStoreDaoImpl")
public class ReceiptDepositStoreDaoImpl extends BaseDaoImpl<ReceiptDepositStore, Long>
		implements ReceiptDepositStoreDao {
}