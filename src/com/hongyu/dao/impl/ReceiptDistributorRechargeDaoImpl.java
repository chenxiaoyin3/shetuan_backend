package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptDistributorRechargeDao;
import com.hongyu.entity.ReceiptDistributorRecharge;

@Repository("receiptDistributorRechargeDaoImpl")
public class ReceiptDistributorRechargeDaoImpl extends BaseDaoImpl<ReceiptDistributorRecharge, Long>
		implements ReceiptDistributorRechargeDao {
}