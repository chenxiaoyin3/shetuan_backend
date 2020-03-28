package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PurchaseInboundDao;
import com.hongyu.entity.PurchaseInbound;

@Repository("purchaseInboundDaoImpl")
public class PurchaseInboundDaoImpl extends BaseDaoImpl<PurchaseInbound, Long> implements PurchaseInboundDao {

}
