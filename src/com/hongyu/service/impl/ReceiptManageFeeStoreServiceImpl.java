package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptManageFeeStoreDao;
import com.hongyu.entity.ReceiptManageFeeStore;
import com.hongyu.service.ReceiptManageFeeStoreService;

@Service("receiptManageFeeStoreServiceImpl")
public class ReceiptManageFeeStoreServiceImpl extends BaseServiceImpl<ReceiptManageFeeStore, Long>
		implements ReceiptManageFeeStoreService {
	@Resource(name = "receiptManageFeeStoreDaoImpl")
	ReceiptManageFeeStoreDao dao;

	@Resource(name = "receiptManageFeeStoreDaoImpl")
	public void setBaseDao(ReceiptManageFeeStoreDao dao) {
		super.setBaseDao(dao);
	}
}