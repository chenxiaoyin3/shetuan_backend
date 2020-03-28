package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyOrderCustomerDao;
import com.hongyu.entity.HyOrderCustomer;

@Repository("hyOrderCustomerDaoImpl")
public class HyOrderCustomerDaoImpl extends BaseDaoImpl<HyOrderCustomer, Long> implements HyOrderCustomerDao {

}
