package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PurchaseDao;
import com.hongyu.entity.Purchase;
import com.hongyu.service.PurchaseService;

@Service("purchaseServiceImpl")
public class PurchaseServiceImpl extends BaseServiceImpl<Purchase, Long> implements PurchaseService {
	
	@Resource(name="purchaseDaoImpl")
	PurchaseDao purchaseDaoImpl;
	
	@Resource(name="purchaseDaoImpl")
	public void setBaseDao(PurchaseDao dao) {
		super.setBaseDao(dao);
	}

}
