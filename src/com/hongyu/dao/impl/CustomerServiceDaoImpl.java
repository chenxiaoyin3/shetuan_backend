package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CustomerServiceDao;
import com.hongyu.entity.CustomerService;

@Repository("customerServiceDaoImpl")
public class CustomerServiceDaoImpl extends BaseDaoImpl<CustomerService, Long> implements CustomerServiceDao{

}
