package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PurchaseItemDao;
import com.hongyu.entity.PurchaseItem;
import com.hongyu.service.PurchaseItemService;

@Service("purchaseItemServiceImpl")
public class PurchaseItemServiceImpl extends BaseServiceImpl<PurchaseItem, Long> implements PurchaseItemService {
	@Resource(name="purchaseItemDaoImpl")
	PurchaseItemDao purchaseItemDaoImpl;
	
	@Resource(name="purchaseItemDaoImpl")
	public void setBaseDao(PurchaseItemDao dao) {
		super.setBaseDao(dao);
	}
}
