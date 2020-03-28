package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptManageFeeStoreDao;
import com.hongyu.entity.ReceiptManageFeeStore;

@Repository("receiptManageFeeStoreDaoImpl")
public class ReceiptManageFeeStoreDaoImpl extends BaseDaoImpl<ReceiptManageFeeStore, Long>
		implements ReceiptManageFeeStoreDao {
}