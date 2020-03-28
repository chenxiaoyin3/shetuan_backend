package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessOrderDao;
import com.hongyu.entity.BusinessOrder;

@Repository("businessOrderDaoImpl")
public class BusinessOrderDaoImpl extends BaseDaoImpl<BusinessOrder, Long> implements BusinessOrderDao {

}
