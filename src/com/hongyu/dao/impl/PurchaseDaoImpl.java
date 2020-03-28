package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PurchaseDao;
import com.hongyu.entity.Purchase;

@Repository("purchaseDaoImpl")
public class PurchaseDaoImpl extends BaseDaoImpl<Purchase, Long> implements PurchaseDao {

}
