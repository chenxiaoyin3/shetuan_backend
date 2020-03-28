package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponBigCustomerDao;
import com.hongyu.entity.CouponBigCustomer;

@Repository("couponBigCustomerDaoImpl")
public class CouponBigCustomerDaoImpl extends BaseDaoImpl<CouponBigCustomer, Long> implements CouponBigCustomerDao {
}