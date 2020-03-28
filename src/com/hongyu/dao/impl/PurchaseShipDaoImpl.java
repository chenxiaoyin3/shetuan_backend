package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PurchaseShipDao;
import com.hongyu.entity.PurchaseShip;

@Repository("purchaseShipDaoImpl")
public class PurchaseShipDaoImpl extends BaseDaoImpl<PurchaseShip, Long> implements PurchaseShipDao {

}
