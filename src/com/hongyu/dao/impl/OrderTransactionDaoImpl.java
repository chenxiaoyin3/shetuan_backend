package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.OrderTransactionDao;
import com.hongyu.entity.OrderTransaction;

@Repository("orderTransactionDaoImpl")
public class OrderTransactionDaoImpl extends BaseDaoImpl<OrderTransaction, Long> implements OrderTransactionDao {

}
