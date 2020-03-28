package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponMoneyHistoryDao;
import com.hongyu.entity.CouponMoneyHistory;

@Repository("couponMoneyHistoryDaoImpl")
public class CouponMoneyHistoryDaoImpl extends BaseDaoImpl<CouponMoneyHistory, Long> implements CouponMoneyHistoryDao {
}