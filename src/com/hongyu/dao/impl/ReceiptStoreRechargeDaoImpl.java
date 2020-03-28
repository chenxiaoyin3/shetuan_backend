package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptStoreRechargeDao;
import com.hongyu.entity.ReceiptStoreRecharge;

@Repository("receiptStoreRechargeDaoImpl")
public class ReceiptStoreRechargeDaoImpl extends BaseDaoImpl<ReceiptStoreRecharge, Long>
		implements ReceiptStoreRechargeDao {
}