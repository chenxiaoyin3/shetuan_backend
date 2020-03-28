package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessOrderItemDao;
import com.hongyu.entity.BusinessOrderItem;
@Repository("businessOrderItemDaoImpl")
public class BusinessOrderItemDaoImpl extends BaseDaoImpl<BusinessOrderItem, Long> implements BusinessOrderItemDao {

}
