package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PurchasePayDao;
import com.hongyu.entity.PurchasePay;

@Repository("purchasePayDaoImpl")
public class PurchasePayDaoImpl extends BaseDaoImpl<PurchasePay, Long> implements PurchasePayDao  {

}
