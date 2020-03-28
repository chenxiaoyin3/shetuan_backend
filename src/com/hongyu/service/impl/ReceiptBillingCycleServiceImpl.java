package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptBillingCycleDao;
import com.hongyu.entity.ReceiptBillingCycle;
import com.hongyu.service.ReceiptBillingCycleService;

@Service("receiptBillingCycleServiceImpl")
public class ReceiptBillingCycleServiceImpl extends BaseServiceImpl<ReceiptBillingCycle, Long>
		implements ReceiptBillingCycleService {
	@Resource(name = "receiptBillingCycleDaoImpl")
	ReceiptBillingCycleDao dao;

	@Resource(name = "receiptBillingCycleDaoImpl")
	public void setBaseDao(ReceiptBillingCycleDao dao) {
		super.setBaseDao(dao);
	}
}