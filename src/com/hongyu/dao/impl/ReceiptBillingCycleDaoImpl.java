package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptBillingCycleDao;
import com.hongyu.entity.ReceiptBillingCycle;

@Repository("receiptBillingCycleDaoImpl")
public class ReceiptBillingCycleDaoImpl extends BaseDaoImpl<ReceiptBillingCycle, Long>
		implements ReceiptBillingCycleDao {
}