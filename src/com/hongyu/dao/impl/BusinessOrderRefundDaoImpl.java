package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessOrderRefundDao;
import com.hongyu.entity.BusinessOrderRefund;

@Repository("businessOrderRefundDaoImpl")
public class BusinessOrderRefundDaoImpl extends BaseDaoImpl<BusinessOrderRefund, Long>
		implements BusinessOrderRefundDao {
}