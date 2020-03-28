package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptDepositServicerDao;
import com.hongyu.entity.ReceiptDepositServicer;
import com.hongyu.service.ReceiptDepositServicerService;

@Service("receiptDepositServicerServiceImpl")
public class ReceiptDepositServicerServiceImpl extends BaseServiceImpl<ReceiptDepositServicer, Long>
		implements ReceiptDepositServicerService {
	@Resource(name = "receiptDepositServicerDaoImpl")
	ReceiptDepositServicerDao dao;

	@Resource(name = "receiptDepositServicerDaoImpl")
	public void setBaseDao(ReceiptDepositServicerDao dao) {
		super.setBaseDao(dao);
	}
}