package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReceiptDetailsDao;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.service.ReceiptDetailsService;

@Service("receiptDetailsServiceImpl")
public class ReceiptDetailsServiceImpl extends BaseServiceImpl<ReceiptDetail, Long> implements ReceiptDetailsService {
	@Resource(name = "receiptDetailsDaoImpl")
	ReceiptDetailsDao dao;

	@Resource(name = "receiptDetailsDaoImpl")
	public void setBaseDao(ReceiptDetailsDao dao) {
		super.setBaseDao(dao);
	}
}