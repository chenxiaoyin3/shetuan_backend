package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponMoneyDao;
import com.hongyu.entity.CouponMoney;

@Repository("couponMoneyDaoImpl")
public class CouponMoneyDaoImpl extends BaseDaoImpl<CouponMoney, Long> implements CouponMoneyDao {
}