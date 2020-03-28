package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.OrderTransactionDao;
import com.hongyu.entity.OrderTransaction;
import com.hongyu.service.OrderTransactionService;

@Service("orderTransactionServiceImpl")
public class OrderTransactionServiceImpl extends BaseServiceImpl<OrderTransaction, Long> implements OrderTransactionService {
	
	@Resource(name = "orderTransactionDaoImpl")
	OrderTransactionDao dao;
	
	@Resource(name = "orderTransactionDaoImpl")
	public void setBaseDao(OrderTransactionDao dao){
		super.setBaseDao(dao);		
	}
}
