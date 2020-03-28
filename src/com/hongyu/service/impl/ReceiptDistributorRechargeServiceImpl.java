package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptDistributorRechargeDao;
import com.hongyu.entity.ReceiptDistributorRecharge;
import com.hongyu.service.ReceiptDistributorRechargeService;

@Service("receiptDistributorRechargeServiceImpl")
public class ReceiptDistributorRechargeServiceImpl extends BaseServiceImpl<ReceiptDistributorRecharge, Long>
		implements ReceiptDistributorRechargeService {
	@Resource(name = "receiptDistributorRechargeDaoImpl")
	ReceiptDistributorRechargeDao dao;

	@Resource(name = "receiptDistributorRechargeDaoImpl")
	public void setBaseDao(ReceiptDistributorRechargeDao dao) {
		super.setBaseDao(dao);
	}
}