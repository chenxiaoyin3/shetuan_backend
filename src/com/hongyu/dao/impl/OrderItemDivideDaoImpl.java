package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.OrderItemDivideDao;
import com.hongyu.entity.OrderItemDivide;
@Repository("orderItemDivideDaoImpl")
public class OrderItemDivideDaoImpl extends BaseDaoImpl<OrderItemDivide, Long> implements OrderItemDivideDao{

}
