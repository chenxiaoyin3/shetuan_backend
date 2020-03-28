package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PurchaseInboundDao;
import com.hongyu.entity.PurchaseInbound;
import com.hongyu.service.PurchaseInboundService;

@Service("purchaseInboundServiceImpl")
public class PurchaseInboundServiceImpl extends BaseServiceImpl<PurchaseInbound, Long> implements PurchaseInboundService {
	@Resource(name="purchaseInboundDaoImpl")
	PurchaseInboundDao purchaseInboundDaoImpl;
	
	@Resource(name="purchaseInboundDaoImpl")
	public void setBaseDao(PurchaseInboundDao dao) {
		super.setBaseDao(dao);
	}
}
