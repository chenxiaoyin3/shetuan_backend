package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptDepositStoreDao;
import com.hongyu.entity.ReceiptDepositStore;
import com.hongyu.service.ReceiptDepositStoreService;

@Service("receiptDepositStoreServiceImpl")
public class ReceiptDepositStoreServiceImpl extends BaseServiceImpl<ReceiptDepositStore, Long>
		implements ReceiptDepositStoreService {
	@Resource(name = "receiptDepositStoreDaoImpl")
	ReceiptDepositStoreDao dao;

	@Resource(name = "receiptDepositStoreDaoImpl")
	public void setBaseDao(ReceiptDepositStoreDao dao) {
		super.setBaseDao(dao);
	}
}