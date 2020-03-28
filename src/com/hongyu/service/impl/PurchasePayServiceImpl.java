package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PurchasePayDao;
import com.hongyu.entity.PurchasePay;
import com.hongyu.service.PurchasePayService;

@Service("purchasePayServiceImpl")
public class PurchasePayServiceImpl extends BaseServiceImpl<PurchasePay, Long> implements PurchasePayService {
	@Resource(name="purchasePayDaoImpl")
	PurchasePayDao purchasePayDaoImpl;
	
	@Resource(name="purchasePayDaoImpl")
	public void setBaseDao(PurchasePayDao dao) {
		super.setBaseDao(dao);
	}
}
