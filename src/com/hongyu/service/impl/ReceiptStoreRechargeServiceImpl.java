package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptStoreRechargeDao;
import com.hongyu.entity.ReceiptStoreRecharge;
import com.hongyu.service.ReceiptStoreRechargeService;

@Service("receiptStoreRechargeServiceImpl")
public class ReceiptStoreRechargeServiceImpl extends BaseServiceImpl<ReceiptStoreRecharge, Long>
		implements ReceiptStoreRechargeService {
	@Resource(name = "receiptStoreRechargeDaoImpl")
	ReceiptStoreRechargeDao dao;

	@Resource(name = "receiptStoreRechargeDaoImpl")
	public void setBaseDao(ReceiptStoreRechargeDao dao) {
		super.setBaseDao(dao);
	}
}