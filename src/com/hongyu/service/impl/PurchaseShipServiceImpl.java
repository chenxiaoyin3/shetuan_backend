package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PurchaseShipDao;
import com.hongyu.entity.PurchaseShip;
import com.hongyu.service.PurchaseShipService;

@Service("purchaseShipServiceImpl")
public class PurchaseShipServiceImpl extends BaseServiceImpl<PurchaseShip, Long> implements PurchaseShipService {

	@Resource(name="purchaseShipDaoImpl")
	PurchaseShipDao purchaseShipDaoImpl;
	
	@Resource(name="purchaseShipDaoImpl")
	public void setBaseDao(PurchaseShipDao dao) {
		super.setBaseDao(dao);
	}
}
