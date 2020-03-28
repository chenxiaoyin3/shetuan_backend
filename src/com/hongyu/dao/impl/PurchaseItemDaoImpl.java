package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PurchaseItemDao;
import com.hongyu.entity.PurchaseItem;

@Repository("purchaseItemDaoImpl")
public class PurchaseItemDaoImpl extends BaseDaoImpl<PurchaseItem, Long> implements PurchaseItemDao {

}
