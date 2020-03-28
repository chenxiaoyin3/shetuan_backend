package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptOtherDao;
import com.hongyu.entity.ReceiptOther;
import com.hongyu.service.ReceiptOtherService;

@Service("receiptOtherServiceImpl")
public class ReceiptOtherServiceImpl extends BaseServiceImpl<ReceiptOther, Long> implements ReceiptOtherService {
	@Resource(name = "receiptOtherDaoImpl")
	ReceiptOtherDao dao;

	@Resource(name = "receiptOtherDaoImpl")
	public void setBaseDao(ReceiptOtherDao dao) {
		super.setBaseDao(dao);
	}
}